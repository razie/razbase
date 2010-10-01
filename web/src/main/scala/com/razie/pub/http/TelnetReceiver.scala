/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http;

import java.io._
import java.net.ServerSocket;
import java.net.Socket;
import razie.base._
import scala.util.matching.Regex
import razie.SM
import com.razie.pub.comms._

object S {
   val logger = razie.Log
}

object T {
   // see RFC854
  val IAC  = 255
  val DONT = 254
  val DO   = 253
  val WONT = 252
  val WILL = 251
  val SB   = 250
  val SE   = 240
  
  // RFC 857
  val ECHO = 1
 
  // RFC 858
  val SUPPRESS_GO_AHEAD = 3
 
  // RFC 1073
  val NAWS = 31
 
  // RFC 1184
  val LINEMODE = 34
  val MODE = 1
  val MASK_EDIT = 1
  val MASK_LIT_ECHO = 16
  val FORWARDMASK = 2
  val SLC = 3
  val SLC_VALUE = 2
  val SLC_FORW2 = 18
}

/** the generic telnet state machine */
abstract class TelnetSM extends SM.StateMachine {
  import SM._
   
   val SB = 250
   val WILL = 251
   val WONT = 252
   val DO = 253
   val DONT = 254
   val IAC = 255
   val CR = IEvent('\r')
  
   val wwdd = Seq(WILL, WONT, DO, DONT)

   // negociated options, see http://support.microsoft.com/kb/231866
   val modes = scala.collection.mutable.HashMap (
         1 -> false, // echo - indicates single char echo mode...
         34 -> true  // linemode
         )
         
   /** set the 'just' negociated option on/off */
   def mode (f:Boolean) (sm:StateMachine, t:Transition, e:Event) {
     val i = e.asInstanceOf[IEvent].i
     modes.put (i,f)
     logger trace ("TELNET option "+i+" turned "+(if(f) "ON" else "OFF"))
  }

   // based on diagram at http://tomi.vanek.sk/index.php?page=telnet
   
   implicit val sm = this
   
   val sstates @ (data, cmd, app, param, neg, subneg) = ("data", "cmd", "app", "param", "neg", "subneg")
   override def start = state("data")
   
   override val transitions : Seq[Transition] = 
         (data, IAC)             -> cmd ::
         (data, 0)               -> data :: // NOP - don't know why i get these after CR
         (data, 10)              -> data :: // NOP - LF ignored?
         (data, 13)              -> data + eatLine + echo ("") :: // CR
         (data, {_:Event=>true}) -> data + eatChar + echo ("") :: // remaining chars
         (cmd, IAC)              -> data ::
         (cmd, Seq(WILL, WONT, DO, DONT)) -> neg + push ::
         // TODO 3-2 should negociate stuff, i.e. reply with will/won't
         (neg, {_:Event=>last==SM(DO)})   -> data + mode(true) + pop :: 
         (neg, {_:Event=>last==SM(DONT)}) -> data + mode(false) + pop ::
         (neg, AnyEvent) -> data + echo("interesting sequence...") + pop ::  // What is this?
         (cmd, SB) -> subneg :: 
         (subneg, 240) -> data + echo("done subneg...") ::  // What is this?
         (subneg, AnyEvent) -> subneg + echo("supposedly subnegotiating...") ::  // What is this?
         (""".*""".r, CR) -> data :: // it's important to reset the thing on ENTRE
          Nil

    def eatChar (sm:StateMachine, t:Transition, e:Event) 
    def eatLine (sm:StateMachine, t:Transition, e:Event) 
}

/** the useful part: content assist etc */
class MyTelnetSM (val p:Puffer, val session:SessionControl, val socket:MyServerSocket, val cs:ContentServer) 
extends TelnetSM {
   import SM._

   val useMultiple = true
   
   def shouldEcho = modes(1)//.getOrElse(true)
   var selection = razie.Listi[(Char, ActionItem)] ()

   /** eat a char at a time and for TAB popup the content assisst options */
   override def eatChar (sm:StateMachine, t:Transition, e:Event) {
      val c = e.asInstanceOf[IEvent].i.toChar
      if (c == '\t') {
         cassist (c)
      } else if (c.toInt == 127) { // backspace
          p.del
          session write 8.toByte // BS
      } else if (c.toInt == 3) { // ^C - dump this line
        p.clear
        session print "\n\r"
      } else if (selection.size > 0) {
         // I'm inside the "popup" with the options
         if (c == '0') { // abandon selection
            selection = razie.Listi[(Char, ActionItem)] ()
         } else if (c == '0' || c.isLetter || c.isControl ) { // abandon selection
            // smarts - find the longest matching section from all options
            if (c != '0') bigu (c.toString)
            selection = razie.Listi[(Char, ActionItem)] ()
         } else selection.filter (_._1 == c) headOption match {
            case Some((_,x)) => {
               bigu (completion (p.acc , x.name))
               selection = razie.Listi[(Char, ActionItem)] ()
            }
            case None => {
               session println ""
               session println "GRESHKA - try again :)"
            printsel
            }
         }
      } else {
         bigu(c.toString)
      }
   }

   def bigu (c:String) {
     p eat c
     if (shouldEcho) session print c.toString 
   }
   
   def completion (curr:String, option:String) : String = {
      if (curr.length <= 0 || curr(curr.length-1).isControl) option
      else {
         // find the last ID
         var i = curr.length-1
         while (i > 0 && curr(i).isLetterOrDigit) i -= 1
         var (j,k) = if (i==0) (i,0) else (i+1, 0)
         while (j < curr.length && k < option.length && curr(j) == option (k)) { j+=1; k +=1 }
         option.substring(k, option.length)
      }
   }
   
   /** find actual content assisst options */
   def cassist (c:Char) : Boolean = {
      S.logger trace "char: " + c + "   code: " + c.toInt
      if (c == '\t') {
         val opt = cs.options(p.acc, session.sessionId)
         if (opt.size == 1) {
            val x = opt.head
            S.logger log "option: " + x; 
            session print x.name
            p eat x.name
         } else if (opt.size > 1 && useMultiple) {
    var i = 0
            opt. foreach { x => selection.append ((('1'+i).toChar, x)); i+=1 }
            session println ""
            session println "--> Selection: "
            printsel
         }
         true
      } 
     false
   }

  private def printsel {
    var i = 0
    selection foreach (t => if (i < 9) { session println "      " + t._1 + ") " + t._2.label; i+=1})
    session println "      " + 0 + ") " + " - quit selection"
    session print p.acc
  }
   
   override def eatLine (sm:StateMachine, t:Transition, e:Event) {
      p.lineReady
      p.b = ! eatLine (p.line)
      p.clear
   }
   
   def eatLine (line:String) = {
      S.logger trace "line: " + line
      session print "\r\n"
      
      if (line == "exit" || line == "quit") {
         session print "stopping..\r\n"
         session.stop  // done
      } else {
         val parms=  new java.util.Properties()
         parms.put ("sessionId", session.sessionId)
         val reply = cs.exec (p.line, "telnet", parms, socket, razie.AA())
         if (reply != null){
            session print reply.toString
            session print "\r\n"
         }
         true
      }
      false
   }
}

/** decouple from actual socket stuff all we need to callback to control the session */
trait SessionControl {
   def write (byte:Byte)
   def print (s:String)
   def println (s:String)
   def stop
   def sessionId : String
}

/** the state (buffer) ? */
class Puffer {
   var acc = ""
   var line = ""
   
   var b=true
   var ignoreCnt=0
   
   def eat (c:Char) = acc = acc+c
   def del = if (acc.length >= 1)  acc = acc.substring (0, acc.length-1)
   
   def eat (s:String) = acc = acc+s
   def lineReady {line=acc}
   def ignore (i:Int) = ignoreCnt = i
   def clear {acc=""; line=""; b=true; ignoreCnt=0}
}

/** hook to razie's web server */
class TelnetReceiver (val socket:MyServerSocket, cs:ContentServer) extends SocketReceiver with SessionControl with Runnable {
//   val in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
   val in = socket.getInputStream();
   val out = new PrintStream(socket.getOutputStream());
   
   override def write   (b:Byte) = out write b
   override def print   (s:String) = out print s
   override def println (s:String) = out print s + "\r\n"
   override def stop = keepReading = false
   val sessionId : String = cs.mkSession("scala")
   // TODO scripting support for other languages: switch language

   var keepReading = true
   
   val sm = new MyTelnetSM (new Puffer(), this, socket, cs)

   // from http://github.com/jrudolph/scala-stuff/blob/master/telnet/src/main/scala/net/virtualvoid/scala/tools/JLineTelnet.scala
   { // - set client in character mode
      def write(bs: Int*) = {
      out.write(bs.map(_.toByte).toArray)
    }
   // request WindowSize handling
    write(T.IAC, T.DO, T.NAWS)
    // don't wait for GO_AHEAD before doing anything
    write(T.IAC, T.WILL, T.SUPPRESS_GO_AHEAD)
    // jline will echo all visible characters
    write(T.IAC, T.WILL, T.ECHO)
   }
   
   override def run () {
      S.logger log "receiving"
   while (keepReading) { // one loop per "session

      // echo mode - 
//      if (! sm.modes.getOrElse(1, false)) {
         while (keepReading) { // one loop per character
            val b = in.read
            if (b == -1)
               keepReading = false
            else
               sm move SM(b)
         }
   }
   
   socket.close();
   }
}


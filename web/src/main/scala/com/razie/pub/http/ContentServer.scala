/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

import razie.base.ActionItem;
import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.base.data._
import com.razie.pub.comms._

/**
 * generic&simplified content server - content may be served over many protocols and rendering technologies.
 * 
 * @author razvanc99
 */
trait ContentServer {
    /* main callback from the server to handle a command send via the socket in the form "COMMAND
     * ARGS"
     * 
     * @param cmd the command sent in, "GET" is used in http for instance
     * @param protocol the protocol - at this level it is not used actually
     * @param parms
     * @param socket the command was received with, normaly you have to write something back...
     * @return the result of the command - or StreamedConsumedReply if the stream was filled
     * @throws AuthException
     */
   def exec(cmdLine:String, protocol:String, parms:Properties, socket:MyServerSocket, httpattrs:AttrAccess) : AnyRef
  
   /** content assist for interactive sessions */
   def options (currLine:String, sessionId:String) : Seq[ActionItem]
   
   def mkSession (lang:String): String
}

/** a light content server has a default handler, which is called when no other handlers match. 
 * 
 * this is very useful for instance to rnu scripts over telnet, when telnet is mapped on the same port.
 *
 */
class LightContentServer (var dflt:SocketCmdHandler = null) extends ContentServer {
   
   override def exec(cmdLine:String, protocol:String, parms:Properties, socket:MyServerSocket, httpattrs:AttrAccess) : AnyRef = {
      var (cmd,args) = ("","")
      
      // will be null if no listeners found...
      var reply : AnyRef = null

      // Now write to the client
      if (cmdLine != null) {
        if (cmdLine.indexOf(' ') > 0) {
          cmd = cmdLine.substring(0, cmdLine.indexOf(' '));
          args = cmdLine.substring( cmdLine.indexOf(' ') + 1);
        } else {
          cmd = cmdLine;
          args = "";
        }

        // TODO document these - are they used anywhere?
        httpattrs.set("lightsoa.methodname", cmd);
        httpattrs.set("lightsoa.path", args);
        socket.setHttp(args, httpattrs);
                
        Log.logThis("HTTP_CLIENT_RQ: " + cmd + " args=" + args);

        val hd = razie.M(getHandlers()).filter(_.getSupportedActions().contains(cmd)).toList.headOption
        hd.foreach (
          c => {
            razie.Debug("HTTP_FOUND_LISTENER: " + c.getClass().getName());
            try {
              reply = c.execServer(cmd, "", args, new Properties(), socket);
            } catch {
               case e:Throwable=> {
                  // TODO 3-2 security breach showing stack trace to user
                  razie.Log.error("ERR_HTTP_RECEIVER_EXCEPTION: ", e);
                  reply = HtmlRenderUtils.textToHtml(Log.getStackTraceAsString(e));
               }
            }
          })
          
          if (! hd.isDefined && dflt != null)
             reply = dflt.execServer(cmd, "", args, parms, socket);
      }
    reply
   }
   
   override def options (s:String, sessionId:String) : Seq[ActionItem] = EMPTY
   val EMPTY = razie.Listi[ActionItem]()
   
   override def mkSession (lang:String): String = ""
   
    def registerHandler(c:SocketCmdHandler ) {
       synchronized {
        getHandlers().add(c);
       }
    }

    def removeHandler(c:SocketCmdHandler ) {
       synchronized {
        getHandlers().remove(c);
       }
    }

    /**
     * @return the listeners
     */
    def getHandlers () : java.util.List[SocketCmdHandler] = {
        return listeners;
    }

    // TODO 2-2 make MTSafe - there's java.util.ConcurrentModificationException on this...during startup
    val listeners : java.util.List[SocketCmdHandler] = new java.util.ArrayList[SocketCmdHandler]();
}

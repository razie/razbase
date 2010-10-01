package razie.draw.widgets

import razie.draw._
import com.razie.pub.comms._
import razie.base._
import razie.draw._
import java.awt.event.KeyEvent
import razie.draw.Drawable.Widget

/** statics */
object K {
   // the states of the respective keys
   var ctrl=false
   var alt=false
   var shift=false
   
   import java.awt.event.KeyEvent._

   // (keycode, (char, shift+char))
   val kl1 = List (
         (VK_ESCAPE , ("ESC", "ESC")),
         
         (VK_1 , ("1", "!")),
         (VK_2 , ("2", "@")),
         (VK_3 , ("3", "#")),
         (VK_4 , ("4", "$")),
         (VK_5 , ("5", "%")),
         (VK_6 , ("6", "^")),
         (VK_7 , ("7", "&")),
         (VK_8 , ("8", "*")),
         (VK_9 , ("9", "(")),
         (VK_0 , ("0", ")")),
         (VK_MINUS , ("-", "_")),
         (VK_EQUALS , ("=", "+")),
         
         (VK_BACK_SPACE , ("<<<", "<<<"))
   )
   val kl2 = List (
         (VK_TAB , ("TAB", "TAB")),

         (VK_Q , ("q", "Q")),
         (VK_W , ("w", "W")),
         (VK_E , ("e", "E")),
         (VK_R , ("r", "R")),
         (VK_T , ("t", "T")),
         (VK_Y , ("y", "Y")),
         (VK_U , ("u", "U")),
         (VK_I , ("i", "I")),
         (VK_O , ("o", "O")),
         (VK_P , ("p", "P")),
         (VK_BRACELEFT , ("[", "{")),
         (VK_BRACERIGHT , ("]", "}")),
         
         (VK_BACK_SLASH , ("\\", "|"))
   )
   val kl3 = List (
         (VK_CAPS_LOCK , ("CAPS", "CAPS")),
         
         (VK_A , ("a", "A")),
         (VK_S , ("s", "S")),
         (VK_D , ("d", "D")),
         (VK_F , ("f", "F")),
         (VK_G , ("g", "G")),
         (VK_H , ("h", "H")),
         (VK_J , ("j", "J")),
         (VK_K , ("k", "K")),
         (VK_L , ("l", "L")),
         (VK_SEMICOLON , (";", ":")),
         (VK_QUOTE , ("'", "\"")),
         (-1 , ("", "")),
         
         (VK_ENTER , ("ENTER", "ENTER"))
   )
   val kl4 = List (
         (VK_SHIFT , ("SHIFT", "SHIFT")),
         
         (VK_Z , ("z", "Z")),
         (VK_X , ("x", "X")),
         (VK_C , ("c", "C")),
         (VK_V , ("v", "V")), 
         (VK_B , ("b", "B")), 
         (VK_N , ("n", "N")), 
         (VK_M , ("m", "M")), 
         (VK_COMMA , (",", "<")), 
         (VK_PERIOD , (".", ">")), 
         (VK_SLASH -> ("/", "?")), 
         (-1 , ("", "")),
         (-1 , ("", "")),
         
         (VK_SHIFT , ("SHIFT", "SHIFT"))
   )
   
   val kl5 = List (
         (VK_SHIFT , ("SHIFT", "SHIFT")),
         
         (VK_ALT , ("ALT", "ALT")),
         (VK_CONTROL , ("CTRL", "CTRL")),
         (VK_SPACE , ("   sp   ", "   sp   ")),
         (VK_CONTROL , ("CTRL", "CTRL")),
         (VK_ALT , ("ALT", "ALT")),
         
         (VK_SHIFT , ("SHIFT", "SHIFT"))
   )

   def findChar (key:String) : Option[Int] = {
      var ret:Option[Int] = None
      for (l <- K.lines) 
         for ((k, (n,s)) <- l)
            if (n==key || s==key) ret = Some(k) // TODO optimize - stop loop
      ret
   }

   // if you want this in a differnet language, modify this and run it to dump
   // to stdout
   def main (argv:Array[String]) {
      for (l <- K.lines) 
         for ((k, (n,s)) <- l)
           println ("Code: "+k+", char="+n+", with shift makes="+s)
   }

   // the rows of keys
   val lines = Array (kl1, kl2, kl3, kl4)
}

// you need to implement this and bind it back to call the keyboard methods below
// see example in class AgentRobotService
trait KeyCallback {
   // make an action to invoke for keyboard pressed
   def k(args:AnyRef*) : ActionToInvoke

   // make an action to invoke for mouse delta move
   def m(args:AnyRef*) : ActionToInvoke
}

/** simple keyboard control
 * 
 * WARNING - there's an anomaly...please render this explicitely rather than return it, i.e. call 
 * */
class Keyboard (val factory:KeyCallback) extends Drawable {

   override def render (t:Technology, out:DrawStream) : AnyRef = {
       if (out.isInstanceOf[HttpDrawStream]) {
          val s = out.asInstanceOf[HttpDrawStream]
          s.addMeta("<script type=\"text/javascript\" src=\"/public/keyboard.js\" charset=\"UTF-8\"></script>")
          s.addMeta("<link rel=\"stylesheet\" type=\"text/css\" href=\"/public/keyboard.css\">")
       } else {
         razie.Log ("WARN-KEYB rendering on unkonwn stream: " + out.getClass.getName)
      }
       
     razie.Draw.toString ("")
    }

    
    def robokey (key:String) =  {
       val r = new java.awt.Robot()
       val c = Integer.parseInt(key)
       c match {
          case KeyEvent.VK_SHIFT => K.shift = pressc(K.shift, KeyEvent.VK_SHIFT)
          case KeyEvent.VK_ALT => K.alt = pressc(K.alt, KeyEvent.VK_ALT)
          case KeyEvent.VK_CONTROL => K.ctrl = pressc(K.ctrl, KeyEvent.VK_CONTROL)
          case _ => {
             r.keyPress(c)
             r.keyRelease(c)
          }
       }
    }

   /** this is called from the keyboard.js via some urls */
    def razKeyboard (key:String, state:String) =  {
       val r = new java.awt.Robot()
       key match {
          case "Caps" => {
             r.keyPress(KeyEvent.VK_CAPS_LOCK)
             r.keyRelease(KeyEvent.VK_CAPS_LOCK)
          }
          case "Shift" => K.shift = pressc(state.toBoolean, KeyEvent.VK_SHIFT)
          case "Alt" => K.alt = pressc(state.toBoolean, KeyEvent.VK_ALT)
          case "Ctrl" => K.ctrl = pressc(state.toBoolean, KeyEvent.VK_CONTROL)
          case _ => K.findChar (key).foreach { kt:Int => {
             r.keyPress(kt)
             r.keyRelease(kt)
            }}
       }
    }

    def pressc (b:Boolean, c:Int) : Boolean = {
       val r = new java.awt.Robot()
       if (b) r.keyRelease(c)
       else r.keyPress(c)
       !b
   }
}

/** this renders to web as basic text withlinks - may be useful for browsers that have no JS capability...
TODO - hookup with device capabilities later on */
class UglyKeyboard (factory:KeyCallback) extends Keyboard (factory) {

   override def render (t:Technology, out:DrawStream) : AnyRef = {
      val t = new razie.draw.DrawTable () 
      for (l <- K.lines) {
         for ((k, (n,s)) <- l){
            t write (razie.Draw.cmd (razie.AI(" "+n+" "), factory.k("key", k.toString)).style(NavLink.Style.JUST_LABEL))
         }
         t closeRow
      }
      
      val lastRow = new razie.draw.DrawTable ()
      for ((k, (n,s)) <- K.kl5){
            lastRow write (razie.Draw.cmd (razie.AI(" "+n+" "), factory.k( "key", k.toString)).style(NavLink.Style.JUST_LABEL))
         }

      val tm = new razie.draw.DrawTable () 
      
      tm write (razie.Draw.link (razie.AI(" ^ "), factory.m("dx", "0", "dy", "-1")).style(NavLink.Style.JUST_LABEL))
      tm write (razie.Draw.link (razie.AI(" v "), factory.m("dx", "0", "dy", "1")).style(NavLink.Style.JUST_LABEL))
      tm write (razie.Draw.link (razie.AI(" < "), factory.m("dx", "0", "dy", "-1")).style(NavLink.Style.JUST_LABEL))
      tm write (razie.Draw.link (razie.AI(" > "), factory.m("dx", "0", "dy", "1")).style(NavLink.Style.JUST_LABEL))
      
     razie.Draw.seq(t, lastRow, "Mouse:\n", tm)
    }
}

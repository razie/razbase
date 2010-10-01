package razie.draw.widgets

import razie.draw._
import com.razie.pub.comms._
import razie.base._
import razie.draw._
import java.awt._
import java.awt.event.KeyEvent
import java.awt.event.InputEvent
import razie.draw.Drawable.Widget

/** statics */
object KMPad {
   // the states of the respective keys
   var ldown=false
   var rdown=false
}

/** simple keyboard control
 * 
 * WARNING - there's an anomaly...please render this explicitely rather than return it, i.e. call 
 * */
class MousePad () extends Drawable {
   val gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()(0).getDefaultConfiguration()
   val bounds = gc.getBounds();
   
   override def render (t:Technology, out:DrawStream) : AnyRef = {
       if (out.isInstanceOf[HttpDrawStream]) {
          val s = out.asInstanceOf[HttpDrawStream]
          s.addMeta("<script type=\"text/javascript\" src=\"/public/mousepad.js\" charset=\"UTF-8\"></script>")
       } else {
         razie.Log ("WARN-KEYB rendering on unkonwn stream: " + out.getClass.getName)
      }
       
     razie.Draw.toString ("")
    }
    
    def mouseclick (which:String,dx:String, dy:String) =  {
       mousedown(which, dx, dy)
       mouseup(which, dx, dy)
    }
    def mouseup (which:String,dx:String, dy:String) =  {
       val r = new java.awt.Robot()
       //movemouse (dx, dy)
       which match {
          case "left" => r.mouseRelease (InputEvent.BUTTON1_MASK);
          case "middle" => r.mouseRelease (InputEvent.BUTTON3_MASK);
          case "right" => r.mouseRelease (InputEvent.BUTTON2_MASK);
       }
    }
    def mousedown (which:String,dx:String, dy:String) =  {
       val r = new java.awt.Robot()
       //movemouse (dx, dy)
       which match {
          case "left" => r.mousePress (InputEvent.BUTTON1_MASK);
          case "middle" => r.mousePress (InputEvent.BUTTON3_MASK);
          case "right" => r.mousePress (InputEvent.BUTTON2_MASK);
       }
    }
    // dx/dy are 0-1000 proportional
    def movemouse (dx:String, dy:String) =  {
       val r = new java.awt.Robot()
       val x = (dx.toFloat*bounds.width)/1000.0
       val y = (dy.toFloat*bounds.height)/1000.0
       razie.Debug ("bounds w="+bounds.width+", h="+bounds.height)
       razie.Debug("movemaoues x="+x+", y="+y)
       r.mouseMove (x.toInt, y.toInt)
    }
}

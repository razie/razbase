package razie.draw.widgets

import razie.draw._
import com.razie.pub.comms._
import razie.base._
import razie.draw._
import java.awt.event.KeyEvent
import razie.draw.Drawable.Widget
import razie.draw.Drawable

/** based on the web idea of matching div classes to CSS */
class DrawDiv (val name:String, val d:Drawable) extends Drawable {

   override def render (t:Technology, out:DrawStream) : AnyRef = {
      out write ("<div class=\""+name+"\">")
      out write d.render(t,out)
      out write ("</div>")
      Drawable.nothing
    }
}



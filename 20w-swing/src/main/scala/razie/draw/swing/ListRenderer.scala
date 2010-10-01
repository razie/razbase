package razie.draw.swing;

import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

class ListRenderer extends Renderer[DrawList] {
   
   override def render(o:DrawList , t:Technology , out:DrawStream ) :AnyRef = {
     val panel = new BoxPanel (if (o.isVertical) Orientation.Vertical else Orientation.Horizontal) {
       def add (c:Component) { contents += c }
     }

     for (e <- razie.M(o.getList)) 
        panel add Init.swingify (e, t, out)

     panel
     }

 }

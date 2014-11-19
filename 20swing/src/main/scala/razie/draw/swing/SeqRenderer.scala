package razie.draw.swing;

import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

class SeqRenderer extends Renderer[DrawSequence] {
//   import scala.collection.JavaConversions._
   
   override def render(o:DrawSequence , t:Technology , out:DrawStream ) :AnyRef = {
     val panel = new BoxPanel (Orientation.Vertical) {
       def add (c:Component) { contents += c }
     }

     for (e <- razie.M(o.elements)) 
        panel add Init.swingify (e.o, t, out)

     panel
     }

 }

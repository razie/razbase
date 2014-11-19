
package razie.draw.swing.test;

import razie.draw._
import razie.draw.samples._
import razie.draw.swing._
import scala.swing._
import razie.Draw

object TestSwing extends SimpleSwingApplication {
  def top = new MainFrame {
     
     razie.draw.swing.Init.init
     
    title = "Hello, World!"
       
    val panel = new BoxPanel (Orientation.Vertical) {
      def add (c:Component) { contents += c }
    }

    val stream = new SwingDrawStream ({ c:Component => panel.add(c) })
    stream write SimpleModel.model
    
    contents = panel
  }
  
}

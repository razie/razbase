package razie.draw.swing;

import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

class TableRenderer extends DrawTable.MyRenderer {
//    public static class MyRenderer implements ContainerRenderer {

        override def canRender(o:Object , t:Technology ) : Boolean = {
            o.isInstanceOf[DrawTable]
        }

   override def render(o:AnyRef , t:Technology , out:DrawStream ) :AnyRef = {
      val table = o.asInstanceOf[DrawTable]
      
     val panel = new GridPanel (table.getRows.size, table.prefCols) {
       def add (c:Component) { contents += c }
     }

     for (r <- razie.M(table.getRows)) 
        r match {
        case l : DrawList => for (e <- razie.M(l.getList)) panel add Init.swingify (e, t, out)
        case _ => panel add Init.swingify (r, t, out)
     }

     panel
     }

 }

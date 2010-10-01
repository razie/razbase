package razie.draw.swing;

import razie.base._
import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

// for DrawString, DrawText and DrawToString
class FormRenderer extends Renderer[DrawForm] {
   import scala.collection.JavaConversions._
   
   override def render(f:DrawForm , t:Technology , out:DrawStream ) :AnyRef = {
      
     val panel = new GridPanel (f.parms.size, 2) {
       def add (c:Component) { contents += c }
     }

        
     val widgets = new razie.AA()
     
        for (a <- f.parms.getPopulatedAttr()) {
            val value = f.parms.getAttr(a);
            val svalue = if (value == null)  "" else value.toString();
            
            val ttype = if (f.parms.getAttrType(a) != null) f.parms.getAttrType(a)
            else AttrType.STRING
            
            val w = ttype match {
              case AttrType.STRING |AttrType.INT|AttrType.FLOAT|AttrType.DATE => 
                 new EditorPane("text/plain", svalue )
              case AttrType.MEMO | AttrType.SCRIPT => 
                 new EditorPane("text/plain",  svalue)
                 
              case _ => null
            }
            
            if (w != null) {
               panel add Init.swingify (razie.Draw.label(a),t,out)
               panel add w
               widgets set (a, w)
            }
        }
     
            val b = razie.Draw.button(razie.AI("haha")) { 
               println ("collecting values...")
               val aa = widgets.map[EditorPane,String] ((x,y)=>y.text)
               }
            
     val panel2 = new BoxPanel (Orientation.Vertical) {
       def add (c:Component) { contents += c }
     }

        panel2 add Init.swingify (razie.Draw.label(f.name.name),t,out)
        panel2 add panel
        panel2 add Init.swingify (b, t,out)

       panel2
     }

 }

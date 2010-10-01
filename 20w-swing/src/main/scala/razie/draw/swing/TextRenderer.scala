package razie.draw.swing;

import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

// for DrawString, DrawText and DrawToString
class TextRenderer extends Renderer[Drawable] {

  override def render(b:Drawable, t:Technology , out:DrawStream ) :AnyRef = 
     b match {
     case _ : DrawTextMemo => new TextArea (b.toString, 3, 0)
     case _ : DrawToStringMemo => new TextArea (b.toString, 3, 0)
     case _ => new Label (b.toString);
  }

 }

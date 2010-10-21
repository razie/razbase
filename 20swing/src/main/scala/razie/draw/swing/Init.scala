package razie.draw.swing

import razie.draw._
import razie.draw.widgets._
import scala.swing._

object Init {
   def init {
   Renderer.Helper.register(classOf[NavLink],       Technology.SWING, new NavLinkRenderer())
   Renderer.Helper.register(classOf[NavButton],     Technology.SWING, new NavLinkRenderer())
   Renderer.Helper.register(classOf[DrawTextMemo],  Technology.SWING, new TextRenderer())
   Renderer.Helper.register(classOf[DrawText],      Technology.SWING, new TextRenderer())
   Renderer.Helper.register(classOf[DrawToStringMemo], Technology.SWING, new TextRenderer())
   Renderer.Helper.register(classOf[DrawToString],  Technology.SWING, new TextRenderer())
   Renderer.Helper.register(classOf[DrawSequence],  Technology.SWING, new SeqRenderer())
   Renderer.Helper.register(classOf[DrawList],      Technology.SWING, new ListRenderer())
   Renderer.Helper.register(classOf[DrawTable],     Technology.SWING, new TableRenderer())
   Renderer.Helper.register(classOf[DrawTableScala],Technology.SWING, new TableRenderer())
   Renderer.Helper.register(classOf[DrawForm],      Technology.SWING, new FormRenderer())
   Renderer.Helper.register(classOf[ScriptPad],     Technology.SWING, new ScriptPadRenderer())
   }

   // TODO doesn't work if object is streamed in renderer...
   def swingify (e:Any, t:Technology, out:DrawStream):Component = Renderer.Helper.draw(e, t, out) match {
      case c:Component => c
      case d:Drawable => swingify (d, t, out) // watchout recursive...
      case _ => Renderer.Helper.draw(razie.Draw.toString(e), t, out).asInstanceOf[Component]
      }

}

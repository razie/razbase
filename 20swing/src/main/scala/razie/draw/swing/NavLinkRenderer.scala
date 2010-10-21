/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.swing;

import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._

class NavLinkRenderer extends Renderer[NavLink] {

   override def render(b:NavLink , t:Technology , out:DrawStream ) :AnyRef = {

      var icon = RazIconRes.getIconFile(b.action.iconProp);

      // if not std property, then it's full url
      if (icon == null || icon.length() <= 0) {
         icon = b.action.iconProp;
      }

      
      val but = new Button (Action(b.action.label) {
         if (b.ati!=null) b.ati.act(null)
         });
      
      if (b.ati.actionItem.tooltip != null && b.ati.actionItem.tooltip != b.ati.actionItem.name)  
         but.tooltip = b.ati.actionItem.tooltip
      but
     }

 }

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.widgets;

import razie.draw.DrawStream;
import razie.draw.Technology;
import razie.draw.Drawable.Widget;

import com.razie.pub.base.data.HtmlRenderUtils;

/**
 * Draw a text with proper toHTML formatting, including escaping of special html chars
 * 
 * @author razvanc99
 */
public class DrawText extends Widget {
    String o;

    public DrawText(String o) {
        this.o = o;
    }

    @Override
        public Object render(Technology technology, DrawStream stream) {
            if (Technology.HTML.equals(technology)) {
                return HtmlRenderUtils.textToHtml(o);
            }
            
            return o.toString();
        }

    public String toString() {
        return o == null ? "" : o.toString();
    }
}

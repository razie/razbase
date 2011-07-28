/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.widgets;

import razie.draw.DrawStream;
import razie.draw.Technology;
import razie.draw.Drawable.Widget;

/**
 * just a wrapper to turn any object into a drawable string, via toString(). One use is to stream
 * exceptions for instance.
 * 
 * this is useful also to avoid default formatting of strings - this will not replace a \n with <br>
 * for instance. IF you actually want that, just use HtmlRenderUtils.textToHtml(s) or DrawText
 * 
 * @author razvanc99
 * 
 */
public class DrawToString extends Widget {
    Object o;

    public DrawToString(Object o) {
        this.o = o;
    }

    @Override
        public Object render(Technology technology, DrawStream stream) {
            return o == null ? "" : o.toString();
        }

    public String toString() {
        return o == null ? "" : o.toString();
    }
}
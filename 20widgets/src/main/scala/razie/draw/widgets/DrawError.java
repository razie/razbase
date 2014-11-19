package razie.draw.widgets;

import razie.draw.DrawStream;
import razie.draw.Drawable.Widget;
import razie.draw.Technology;

import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.log.Log;

/**
 * dedicated error object (popups, info bars etc)
 *  
 *  TODO set server error code when returning DrawError
 *  
 * @author razvanc99
 */
public class DrawError extends Widget {
    String    msg;
    Throwable t;

    public DrawError(String msg, Throwable t) {
        this.msg = msg;
        this.t = t;
    }

    public DrawError(String msg) {
        this.msg = msg;
    }

    public DrawError(Throwable t) {
        this.t = t;
    }

    @Override
    public Object render(Technology technology, DrawStream stream) {
       if (Technology.HTML.equals(technology)) {
          return new DrawToString ("<p><b><font color=red>Opa! Not work...err...nice red, though!</font></b><p>" + toString());
       }
            return toString();
        }

    public String toString() {
        return (msg == null ? "<no msg>" : msg)
                + (t == null ? "" : "\n" + HtmlRenderUtils.textToHtml(Log.getStackTraceAsString(t)));
    }
}
/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw.widgets;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.draw.DrawStream;
import razie.draw.Drawable.Widget;
import razie.draw.Technology;

import com.razie.pub.resources.RazIconRes;

/**
 * a link with an action that navigates elsewhere (i.e. open a new window or do GET in http
 * 
 * @author razvanc99
 * @version $Id$
 * 
 */
public class NavLink extends Widget {

    public ActionItem action;
    public ActionToInvoke ati;
    public String     link;

    public enum Style {
        ONELINE, JUST_LABEL, JUST_ICON, TEXT_UNDER
    };

    public enum Size {
        TINY, SMALL, NORMAL, LARGE
    };

    Style style = Style.ONELINE;
    Size  size  = Size.TINY;

    /** display a, execute link */
    public NavLink(ActionItem a, String link) {
        this.action = a;
        this.link = link;
    }

    /** command is for both display and execution */
    public NavLink(ActionToInvoke a) {
        this.action = a.actionItem;
        this.link = a.makeActionUrl();
        this.ati=a;
    }

    /** display item, execute a */
    public NavLink(ActionItem item, ActionToInvoke a) {
        this.action = item;
        this.link = a.makeActionUrl();
        this.ati=a;
    }

    public NavLink style(Style st, Size... sz) {
        this.style = st;
        if (sz.length > 0)
            this.size = sz[0];
        return this;
    }

    public void setTiny(boolean tiny) {
        this.style = tiny ? Style.JUST_ICON : Style.TEXT_UNDER;
        this.size = tiny ? Size.SMALL : Size.NORMAL;
    }

    @Override
        public Object render(Technology technology, DrawStream stream) {
           return irender("<a", null, this, technology, stream);
        }

        protected Object irender(String atype, String func, NavLink b, Technology technology, DrawStream stream) {
            String icon = RazIconRes.getIconFile(b.action.iconProp);

            String lnk = b.link != null && b.link.length() > 0 ? b.link : "";
            lnk = func == null ? lnk : func+"('"+lnk+"')";
                
            // if not std property, then it's full url
            if (icon == null || icon.length() <= 0) {
                icon = b.action.iconProp;
            }

            if (Technology.HTML.equals(technology)) {
                String s = b.link != null && b.link.length() > 0 ? atype + " href=\"" + lnk + "\">" : "";
                if (!b.style.equals(Style.JUST_LABEL) && icon != null && icon.length() > 0 && !icon.equals(b.action.name) 
                      && !icon.equals("UNKNOWN")
                      ) {
                    String tip = b.action.tooltip != null ? b.action.tooltip : b.action.label;
                    if (Size.TINY.equals(b.size)) {
                        s += "<img border=0 width=21 height=21 src=\"" + icon + "\" alt=\"" + tip + "\"/>";
                    } else if (Size.SMALL.equals(b.size)) {
                        s += "<img border=0 width=30 height=30 src=\"" + icon + "\" alt=\"" + tip + "\"/>";
                    } else if (Size.NORMAL.equals(b.size)) {
                        s += "<img border=0 width=80 height=80 src=\"" + icon + "\" alt=\"" + tip + "\"/>";
                    } else if (Size.LARGE.equals(b.size)) {
                        s += "<img border=0 width=180 height=180 src=\"" + icon + "\" alt=\"" + tip + "\"/>";
                    }

                    if (Style.ONELINE.equals(b.style)) {
                        s += b.action.label;
                    } else if (Style.JUST_ICON.equals(b.style)) {
                    } else if (Style.TEXT_UNDER.equals(b.style)) {
                        s += "<br>" + b.action.label;
                    }
                } else {// Style.JUST_LABEL
                    s += b.action.label;
                }
                s += b.link != null && b.link.length() > 0 ? "</a>" : "";
                return s;
            }

            // default rendering
            return b.action.label;
        }

}

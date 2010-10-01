/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw.widgets;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

/**
 * a button with an action and image that navigates elsewhere (i.e. open a new window or do GET in
 * http
 * 
 * @author razvanc99
 * @version $Id$
 * 
 */
public class NavButton extends NavLink {

    /** display a, execute link */
    public NavButton(ActionItem a, String link) {
        super(a, link);
        this.style = Style.TEXT_UNDER;
        this.size = Size.NORMAL;
    }

    /** command is for both display and execution */
    public NavButton(ActionToInvoke a) {
        super(a);
        this.style = Style.TEXT_UNDER;
        this.size = Size.NORMAL;
    }

    /** display item, execute a */
    public NavButton(ActionItem item, ActionToInvoke a) {
        super(item, a);
        this.style = Style.TEXT_UNDER;
        this.size = Size.NORMAL;
    }
}

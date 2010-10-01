/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw.widgets;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.draw.DrawStream;
import razie.draw.Technology;

import razie.draw.Renderer;

/**
 * a button with an action and image that just does it's action and no change on the screen (POST in
 * html)
 * 
 * @author razvanc99
 * @version $Id$
 * 
 */
public class SimpleButton extends NavButton {

    public SimpleButton(ActionItem a, String link) {
        super(a, link);
    }
    
    /** command is for both display and execution */
    public SimpleButton(ActionItem d, ActionToInvoke a) {
        super(d, a);
    }

    /** command is for both display and execution */
    public SimpleButton(ActionToInvoke a) {
        super(a);
    }

        @Override
        public Object render(Technology technology, DrawStream stream) {
            return irender("<a ", "javascript:razInvoke", this, technology, stream);
        }
}

/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base;

import razie.draw.Drawable;

/**
 * this is an instance of an action, meant to be invoked. It is prepared by someone and can be
 * executed on the spot OR presented to the user as a menu or some other invokable and invoked
 * later. It contains everything needed to invoke itself. It can be invoked in the same JVM or
 * remotely (from a web page etc).
 *
 * basically think of an URL that does something: it includes everything needed to invoke itself
 * (function name, parameters bound with values etc)....
 *
 * it can be placed on a menu, web page, dialog as a button etc - it generally represents a menu
 * item or a button.
 *
 * NB: any application should use some unified abstraction to invoke controllers from the views.
 * This simple technique allows swapping views very easily. It also allows to easily offer APIs
 * without writing a new invocation layer. Since the web is ubiquitous, tying this to URLs does make some sense...
 *
 * In our case (pretty much everywhere, really), as you'll see later, it generally is about calling
 * either a service or an object.
 * 
 * @author razvanc99
 */
public abstract class ActionToInvoke extends AttrAccessImpl implements IActionable, AttrAccess, Drawable {
    /** this is the action, contains the actual command name and label to display */
    public ActionItem actionItem;

    /**
     * the prefix used depending on the drawing technology - for http, it's the URL to append to.
     * This will identify the target of the action
     */
    public String     target;

    // TODO presentation in model, not nice
    public boolean    drawTiny  = false;

    /**
     * does it navigate or just invoke something in the background? navigation means GET for
     * instance as opposed to POST/ACT
     */
    public ActionItem.ActionType actionType = ActionItem.ActionType.R;

    /**
     * constructor
     * 
     * @param target the prefix used depending on the drawing technology - for http, it's the URL to
     *        append to
     * @param item this is the action, contains the actual command name and label to display
     * @param pairs
     */
    public ActionToInvoke(String target, ActionItem item, Object... pairs) {
        super(pairs);
        this.target = target;
        this.actionItem = item;
        this.actionType = item.actionType;
    }

    /**
     * should not tie this to actual technology, but URLs are the most common form of invoking
     * actions
     */
    public abstract String makeActionUrl();

    /** clone this one wiht the new args - often it's like a function call */
    public abstract ActionToInvoke args(Object...pairs);
}

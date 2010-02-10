/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base;


/**
 * the simplest interface for an actionable...similar to f: ActionContext => Any in scala
 * 
 * @author razvanc99
 */
public interface IActionable {
    /**
     * execute this action in a given context. The context must include me as well?
     * 
     * default implementation assumes i need to call an url and get the first line of response
     */
    public Object act(ActionContext ctx);
}

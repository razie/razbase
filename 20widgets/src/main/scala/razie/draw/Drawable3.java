/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw;

/** this is a remnant of illogical thinking. Use Drawable instead 
 * 
 * @author razvanc
 */
public interface Drawable3 extends Drawable {
   // TODO 1-2 get rid of this method and have everyone render oneself
   @SuppressWarnings("rawtypes")
public Renderer getRenderer(Technology technology);

   /** @deprecated replace with Renderer.DefaultRenderer */
   public static class DefaultRenderer extends Renderer.DefaultRenderer { }
}

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
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

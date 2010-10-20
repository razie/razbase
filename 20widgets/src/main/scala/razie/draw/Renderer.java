/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;

import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.data.IndexedMemDb;

/**
 * a renderer can render an object on a display...
 * 
 * Note that you can inject renderers for objects by registering with the RendererUtils below...
 * 
 * @author razvanc99
 * 
 */
public interface Renderer<T> {

   /**
    * @label draws
    */

   /* #com.razie.sdk.draw.Drawable Dependency_Link */

   /**
    * the return object is technology specific...it could be a swing dialog reference :)
    * 
    * The convention here is: the caller prepares a stream. The implementation will either use the stream or
    * just return an object. Note that the stream is always present!
    * 
    * @param o
    *           - not null, the object to draw
    * @param technology
    *           - the technology to draw on
    * @param stream
    *           - not null, a stream to draw on. Use DrawSequence if you don't know what to do and draw that.
    * @return null if it was drawn on the stream, a drawable object otherwise
    */
   public Object render(T o, Technology technology, DrawStream stream);

   /** simple renderer utils */
   @SuppressWarnings("unchecked")
   public static class Helper {
      private static IndexedMemDb<Class, Technology, Renderer> renderers = new IndexedMemDb<Class, Technology, Renderer>();

      /** overwrite or define a new renderer for a drawable class and technology combination */
      public static void register(Class<? extends Drawable> c, Technology t, Renderer r) {
         renderers.put(c, t, r);
      }

      private static Renderer findRenderer(Drawable d, Technology technology) {
         Renderer r = renderers.get(d.getClass(), technology);
         if (r == null) {
            if (d instanceof Drawable3) {
               r = ((Drawable3) d).getRenderer(technology);
            } else if (d instanceof Drawable) {
               r = DefaultRenderer.singleton;
            }
         }
         return r;
      }

      /** draw an object */
      public static Object draw(Object o, Technology t, DrawStream stream) {
         if (o == null) {
            return "";
         }

         if (o instanceof Drawable || o instanceof Drawable3) {
            return findRenderer((Drawable) o, t).render(o, t, stream);
         } else if (o instanceof DrawableSource) {
            Drawable d = ((DrawableSource) o).makeDrawable();
            return draw(d, t, stream);
         } else {
            if (Technology.HTML.equals(t)) {
               return HtmlRenderUtils.textToHtml(o.toString());
            }
         }

         return o.toString();
      }
      
      /** rendering widgets may result in other drawables...recurse until either get null or the same thing */
      public static Object recursiveDraw(Object d, Technology technology, DrawStream stream) {
         Object r1 = d;
         Object r2 = Renderer.Helper.draw(d, technology, stream);
         while (r2 != null && r2 != r1 && r2 instanceof Drawable) {
            r1 = r2;
            r2 = Renderer.Helper.draw(r1, technology, stream);
         }
         
         return r2 == null ? "" : r2;
      }
   }

   /**
    * rendering containers involves hearder/body (elements)/footer.
    * <p>
    * Note that this also extends the Renderer, so there's also a method to render everything in one shot.
    * That may or may not make sense...
    * <p>
    * NOTE that if you choose to stream anything, you have to stream everything. What that means is that if
    * you choose to return "x" for the header but write the footer directly to the stream, you're in
    * trouble... :)
    */
   public static interface ContainerRenderer extends Renderer<Object> {
      /**
       * the return object is technology specific. NOTE that if you choose to stream anything, you have to
       * stream everything.
       * 
       * @param o
       *           the container providing the footer
       * @param technology
       * @return
       */
      public Object renderHeader(Object o, Technology technology, DrawStream stream);

      /**
       * the return object is technology specific NOTE that if you choose to stream anything, you have to
       * stream everything.
       * 
       * @param o
       *           the container
       * @param technology
       * @return
       */
      public Object renderElement(Object container, Object element, Technology technology, DrawStream stream);

      /**
       * the return object is technology specific NOTE that if you choose to stream anything, you have to
       * stream everything.
       * 
       * @param o
       *           the container providing the footer
       * @param technology
       * @return
       */
      public Object renderFooter(Object o, Technology technology, DrawStream stream);
   }
   
   /** regular smart Drawables */
   public static class DefaultRenderer implements Renderer<Drawable> {
      // no state, MT-safe
      public static DefaultRenderer singleton = new DefaultRenderer();

      public boolean canRender(Drawable o, Technology technology) {
         return true;
      }

      public Object render(Drawable o, Technology technology, DrawStream stream) {
         return o.render(technology, stream);
      }
   }
}

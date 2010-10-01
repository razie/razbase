/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw;

import razie.draw.Renderer.Helper;


/**
 * basic drawable objects, which are rendered by Renderers for different technologies
 * 
 * Convention: each drawable MUST support at least HTML and TEXT. It SHOULD also support XML and
 * JSON. For other rendering technologies, you should create basic drawable objects in this package,
 * which will render themselves onto those respective technologies, depending on their availability
 * in a given environment.
 * 
 * The rendering as TEXT is for logging.
 * 
 * The rendering as HTML is for display in an old HTML page (form or something)
 * 
 * The rendering as XML/JSON is for marshalling/unmarshalling.
 * 
 * The other rendering formats are for display in other technologies (SWING, SVG etc).
 * 
 * There is a NATIVE rendering which means you use only native drawing elements (all defined in this
 * package). These can then render to any other technologies.
 * 
 * The preferred approach for supporting another technology is: make sure the technology is
 * XML-based. Render yourself in NATIVE and implement the xslt transformation from native to the
 * other technology.
 * 
 * Note that rendering of an item includes actionables for that item (user interaction instructions
 * and actions/commands back to the model element). Controllers are optional for managing
 * interaction (there is a defualt controller).
 * 
 * TODO Also NOTE that most of the above is to do
 * 
 * @author razvanc99
 */
public interface Drawable {
   /**
    * shortcut to render self - don't like controllers that much
    * 
    * the return object is technology specific...it could be a swing dialog reference :)
    * 
    * The convention here is: the caller prepares a stream. The implementation will either use the
    * stream or just return an object. Note that the stream is always present!
    * 
    * @param technology - the technology to draw in
    * @param stream - not null, a stream to draw to
    * @return null if it was drawn on the stream, a drawable object otherwise
    */
   public Object render(Technology t, DrawStream stream);
   
   /** just a syntax marker - not sure i'll ever use this... */
   public static abstract class Widget implements Drawable {}
   
   /** nothing - can return from render() if you've written to the stream */
   public static class Nothing implements Drawable {
      @Override
      public Object render(Technology t, DrawStream stream) { return ""; }
   }
 
   public static Nothing nothing = new Nothing();
}


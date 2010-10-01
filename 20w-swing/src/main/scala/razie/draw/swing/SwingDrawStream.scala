/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package razie.draw.swing

import razie.draw._

import scala.swing._

/**
 * simple stream to write in any technology to an output stream. Just translates from Draw to java
 * streams...
 * 
 * wrap this for instance in the http stream to wrap in http
 * 
 * @author razvanc99
 * @version $Id$
 * 
 */
class SwingDrawStream (paint: Component => Unit) extends DrawStream (Technology.SWING) {
   
//    protected void writeBytes(byte[] b) {
//        try {
//            out.write(b);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    override def close() {}

    override def renderObjectToStream(d:AnyRef) = {
       val o = d match {
          case r:Drawable => Renderer.Helper.draw(r, technology, this);
          case r:DrawableSource => Renderer.Helper.draw(r, technology, this);
          case _ => Renderer.Helper.draw(razie.Draw.toString(d), technology, this);
       }

       val newo:Component = o match {
          case c:Component => c
          case _ => new Button ("UNKNOWN: " + o)
       }
       
       paint(newo)
    }

    override def renderElement(d:StreamableContainer, e:AnyRef) { renderObjectToStream (e) }
    override def renderFooter(d:StreamableContainer) { }
    override def renderHeader(d:StreamableContainer) { }

//    @Override
//    public void renderElement(StreamableContainer container, Object element) {
//        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderElement(
//                container, element, technology, this);
//        writeBytes(result.toString().getBytes());
//    }
//
//    @Override
//    protected void renderFooter(StreamableContainer container) {
//        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderFooter(
//                container, technology, this);
//        writeBytes(result.toString().getBytes());
//    }
//
//    @Override
//    protected void renderHeader(StreamableContainer container) {
//        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderHeader(
//                container, technology, this);
//        writeBytes(result.toString().getBytes());
//    }
//
//    public String toString() {
//        return out.toString();
//    }
}

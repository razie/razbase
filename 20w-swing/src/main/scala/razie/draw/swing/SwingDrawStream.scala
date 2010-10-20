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
class SwingDrawStream(paint: Component => Unit) extends DrawStream(Technology.SWING) {

  override def close() {}

  def renderObjectToStream(d: AnyRef) = {
    val o = d match {
      case r: Drawable => Renderer.Helper.draw(r, technology, this);
      case r: DrawableSource => Renderer.Helper.draw(r, technology, this);
      case _ => Renderer.Helper.draw(razie.Draw.toString(d), technology, this);
    }

    val newo: Component = o match {
      case c: Component => c
      case _ => new Button("UNKNOWN: " + o)
    }

    paint(newo)
  }

  def renderElement(d: StreamableContainer, e: AnyRef) { renderObjectToStream (e) }
  def renderFooter(d: StreamableContainer) {}
  def renderHeader(d: StreamableContainer) {}
  //    public String toString() {
  //        return out.toString();
  //    }
}

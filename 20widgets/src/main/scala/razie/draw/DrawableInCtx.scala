package razie.draw

import razie.base.AttrAccess

/** this is a context-aware drawable 
 * 
 * TODO do i need both Drawable3 and this context-aware version or just this one?
 */
trait DrawableInCtx {
   
    def getRenderer(t : Technology, ctx : AttrAccess) : Renderer[Drawable] = 
       new CtxAwareRenderer (ctx)

    /** shortcut to render self - don't like controllers that much 
     * 
     * the return object is technology specific...it could be a swing dialog reference :) 
     * 
     * The convention here is: the caller prepares a stream. The implementation will either use 
     * the stream or just return an object. Note that the stream is always present! 
     * 
     * @param technology -  the technology to draw in
     * @param stream - not null, a stream to draw to
     * @return null if it was drawn on the stream, a drawable object otherwise
     */
    def render(t : Technology, stream : DrawStream, ctx : AttrAccess) : AnyRef
}

    /** Use this for model drawables that just use widgets, see SampleDrawable */
class CtxAwareRenderer (ctx:AttrAccess) extends Renderer[Drawable] {

    override def render(o : Drawable, t : Technology, stream : DrawStream) : AnyRef = 
       o match {
       case a : DrawableInCtx => a.render ( t, stream, ctx)
       case b : Drawable => b.render ( t, stream)
    }
}

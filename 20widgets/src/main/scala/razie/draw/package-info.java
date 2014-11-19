/**
 * Basic Drawables and streamables
 * 
 * <p>
 * This package has two main ideas: technology-independent rendering and streaming of objects.
 * 
 * <h1>Drawables and rendering</h1>
 * 
 * <p>
 * The idea is that all objects are Drawables and as such, can be rendered onto some technology. The
 * rendering logic rests with the developer of the object (Drawable3.render()) and can also be added
 * from the outside, when a new rendering technology is added for existing classes:
 * Renderer.Helper.register().
 * <p>
 * A set of basic widgets is offered, which you can use to render thyself. It is highly recommended
 * that all Drawables use only widgets to draw themselves. The widgets can then be mapped onto new
 * technologies, transparent to your code.
 * <p>
 * Rendering can be considered the same as Serialization (render onto a JSON stream for instance).
 * <p>
 * To add a renderer for an existing object, just register it in the Renderer with the class name
 * you want to render and technology.
 * <p>
 * Wrap streams in the order of the protocol stack. For instance, to stream a DIDL to a HTTP reply
 * socket, use <code>new DIDLStream (HttpStream(Socket))</code>.
 * <p>
 * Main rendering helper is Renderer.Helper.draw()
 * 
 * 
 * <h2>Streamables</h2>
 * 
 * <p>
 * The usual functional approach <code>result = search(x)</code> implies that clients wait for all
 * the processing to complete before receiving anything. Often times, that is both unacceptable and
 * wasteful, for no good reason, other than suitable for lazy/junior programmers.
 * <p>
 * Instead, we propose a simple streaming model, where the equivalent is
 * <code>search (resultStream, x)</code> which doesn't complicate either implementation nor
 * client. Since we're talking multiple results, the client code can be a simple for loop but now it
 * has the option of mounting processors on the stream and do more stuff, including parallel
 * processing etc. Likewise, the results for instance can be streamed on screen (or html page) as
 * they're found, so the clients don't have to wait for all 1000 movies to be found when they care
 * about the second...
 * <p>
 * Note that this is not about iterating or page up/down/next functionality. That can be implemented
 * into a specific stream. All we want here is to make sure all code streams results as efficiently
 * and as soon as possible, for a good user experience.
 * 
 * <p>
 * The next direction that I see for this is updatable containers, where the streaming is combined
 * with lazy updating. This is useful let's say when grabbing remote data grouped by artists and
 * rendering it grouped by genre. The rendered page should update itself as more results are
 * available.
 * 
 * 
 * <h2>Model View Controller</h2>
 * 
 * <p>
 * Models are Drawables and can normally render themselves using the basic widgets. If you want to
 * change their View, just register a different renderer which will overwrite theirs.
 * <p>
 * If you want to split model/view, then have your models implement the IDrawableModel instead and
 * then they have to be able to create a view(). Note that the default viewFactory can be used, with
 * registration. There are three use cases for views:
 * 
 * 1. you simply want to display a model...just use model.view().render() or write the model to the
 * stream. This is the default behavior
 * 
 * 2. you want to display it differently in a new composite view - meaning you have a new composite
 * model you're managing...in that case, just use your own Drawable3 to re-draw the basic models. or
 * hardcode in place.
 * 
 * 3. you want to change how a particular model is displayed - then register a new view/renderer for
 * that class
 * 
 * 4. you want to change how it's displayed in a particular screen - can't really help with that...
 * 
 * <p>
 * I don't have a Controller right now, since most widgets contain only control actions.
 * 
 * STATE: concept, changes often
 * 
 * @version $Id: package-info.java,v 1.1 2007-10-02 11:54:36 razvanc Exp $
 */
package razie.draw;


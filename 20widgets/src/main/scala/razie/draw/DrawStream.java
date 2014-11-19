/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import razie.comms.CommsEndPoint;

import com.razie.pub.base.log.Log;

/**
 * trying to setup a stream drawing framework. this could also be called DrawCanvas or panel in
 * Witcky?
 * 
 * 
 * <p>
 * A stream is like a panel or a web page. The difference is that it will stream completed objects
 * right away to the client rather than wait for the entire contents to be available. This normally
 * applies to containers (trees, tables, lists) as individual items do not need to be streamed.
 * 
 * <p>
 * The user won't have to wait for the entire operation to be over, will simply see items as they're
 * generated/found.
 * 
 * <p>
 * A stream implementation should only bother with the writeObjectToStream() - everything else is
 * implemented here...
 * 
 * <p>Lots of drawing methods use a stream. Use DrawSequence if you don't have one...
 * 
 * @author razvanc99
 * @version $Id$
 * @param <EndPoint>
 * 
 */
public abstract class DrawStream implements DrawAccumulator {
    public List<Element>           elements = new ArrayList<Element>();
    protected Technology    technology;
    private CommsEndPoint endPoint;
    protected int countObjectBytes = 0; // counts actual bytes written for all objects, except header/footer

    public CommsEndPoint getEndPoint() {
        return endPoint;
    }

    protected void setEndPoint(CommsEndPoint ep) {
        this.endPoint = ep;
    }

    /**
     * objects are rendered using this technology...you can change the technology, but make sure you
     * know what you're doing i.e. (nothing was written to the stream)
     */
    public DrawStream(Technology technology) {
        this.technology = technology;
    }

    /** @return how many elements were written (or pending) on this stream until now */
    public int size() {
        return elements.size();
    }

    /** add a completed object to the stream */
    public void write(Object d) {
        if (d != null) {
            elements.add(new Element(d, ElementState.WAITING));
            close(d);
        } else  {
           Log.logThis("WARN_STREAM_WRITE_NULL someone wanted to write a null...");
           Log.traceThis("WARN_STREAM_WRITE_NULL stacktrace:", new RuntimeException().fillInStackTrace());
        }
    }

    public Technology getTechnology() {
        return this.technology;
    }

    /**
     * WARNING - do not switch technology on an open stream unless you know what you're doing. my
     * only use case right now is switching between html to json on a soamethoddrawable
     * 
     * @return if it switched
     */
    protected boolean switchTechnology(Technology t) {
        this.technology = t;
        return true;
    }

    /** add an object to the stream */
    public void open(Object d) {
        boolean recursiveFromContainer = false;

        if (d instanceof StreamableContainer) {
            StreamableContainer container = (StreamableContainer) d;
            if (this == container.getStream())
                recursiveFromContainer = true;
        }

        if (!recursiveFromContainer) {
            elements.add(new Element(d, ElementState.OPEN));
            boolean canWrite = canWrite(d);

            if (d instanceof StreamableContainer.Impl) {
                StreamableContainer.Impl container = (StreamableContainer.Impl) d;
                // if (!this.equals(container.getStream()))
                if (this != container.getStream())
                    container.open(this);

                if (canWrite) {
                    renderHeader(container);
                    container.wroteHeader = true;
                }
            }
        }
    }

    /** d is complete - write to stream... */
    private boolean canWrite(Object d) {
        int index = find(d);

        // find if i can print it or not...
        int i = index - 1;
        for (i = index - 1; i > 0; i--) {
            if (!this.elements.get(i).state.equals(ElementState.CLOSED))
                break;
        }

        return i <= 0;
    }

    /** d is complete - write to stream... */
    public void close(Object d) {
        int index = find(d);

        if (this.elements.get(index).state == ElementState.CLOSED) {
            // already closed - don't recurse
            return;
        }

        this.elements.get(index).state = ElementState.CLOSED;

        // find if i can print it or not...
        int i = index - 1;
        for (i = index - 1; i > 0; i--) {
            if (!this.elements.get(i).state.equals(ElementState.CLOSED))
                break;
        }

        if (i > 0) {
            /** nothing to paint - something still open */
            return;
        }

        // writing myself may add to the stream !!!
        int lastIdxNow = this.elements.size();
        for (int paintingIdx = index; paintingIdx < lastIdxNow; paintingIdx++) {
            Element e = this.elements.get(paintingIdx);
            if (!e.state.equals(ElementState.CLOSED)) {
                break;
            }

            if (d instanceof StreamableContainer) {
                if (((StreamableContainer) d).getState().equals(ElementState.OPEN)) {
                    StreamableContainer.Impl container = (StreamableContainer.Impl) d;
                    container.close();

                    if (!container.wroteFooter) {
                        this.renderFooter(container);
                    }
                } else {
                    this.renderObjectToStream(e.o);
                }
            } else {
                this.renderObjectToStream(e.o);
            }
        }
    }

    /**
     * =======================================================
     * 
     * stupid renderers - i would implement them all right here, but there are some stream proxies
     * that mess with these...
     */

    /**
     * this is used directly by streamable containers when they need to write stuff - knowledge
     * about the container is added as opposed to the simple write(). You must use the container's
     * renderer, to retain any container-specific formatting, i.e.
     * <tr>
     * <td> etc.
     */
    public abstract void renderElement(StreamableContainer container, Object element);

    protected abstract void renderHeader(StreamableContainer container);

    protected abstract void renderFooter(StreamableContainer container);

    /** this is the main method of a stream */
    protected abstract void renderObjectToStream(Object d);

    /** not using logical equals but pointer equals */
    protected int find(Object d) {
        for (int i = 0; i < this.elements.size(); i++) {
            Element e = this.elements.get(i);
            if (e.o == d) {
                return i;
            }
        }

        throw new IllegalArgumentException("object d not in stream yet...");
    }

    /**
     * nothing new to put in the stream - can close conncetions, flush etc. note that there may be
     * open objects registered with the stream - it should wait for those to complete as well...
     */
    public abstract void close();

    /**
     * simple proxy to a different stream. proxies can add format translation, change formatting
     * etc, wrap in protocol etc
     * 
     * @author razvanc99
     * @version $Id$
     * 
     */
    public abstract static class DrawStreamWrapper extends DrawStream {

        /** the actual output socket */
        protected DrawStream proxied;

        public DrawStreamWrapper(DrawStream proxied) throws IOException {
            super(proxied.technology);
            this.proxied = proxied;
            super.setEndPoint(proxied.getEndPoint());
        }

        protected void setEndPoint(CommsEndPoint ep) {
            this.proxied.setEndPoint(ep);
            super.setEndPoint(ep);
        }

        /** add a completed object to the stream */
        public void write(Object d) {
            proxied.write(d);
        }

        /** add an object to the stream */
        public void open(Object d) {
            proxied.open(d);
        }

        /** d is complete */
        public void close(Object d) {
            proxied.close(d);
        }

        @Override
        public void close() {
            proxied.close();
        }

        @Override
        protected void renderObjectToStream(Object d) {
            proxied.renderObjectToStream(d);
            this.countObjectBytes = proxied.countObjectBytes;
        }

        @Override
        public void renderElement(StreamableContainer container, Object element) {
            proxied.renderElement(container, element);
        }

        @Override
        protected void renderFooter(StreamableContainer container) {
            proxied.renderFooter(container);
        }

        @Override
        protected void renderHeader(StreamableContainer container) {
            proxied.renderHeader(container);
        }

        @Override
        public int size() {
            return proxied.size();
        }

        @Override
        protected boolean switchTechnology(Technology t) {
            if (proxied.switchTechnology(t)) {
                this.technology = t;
                return true;
            }
            return false;
        }

    }

    static enum ElementState {
            /** this is open - began writing to stream, not done yet */
            OPEN,
            /** this item has been completely writen to the stream */
            CLOSED,
            /** waiting to be allowed to write to stream */
            WAITING
        };
        
    /** internal helper - keeps tabs on the objects written to the stream */
    public static class Element {
        public Object o;
        ElementState  state;

        Element(Object o, ElementState state) {
            this.o = o;
            this.state = state;
        }

    }
}

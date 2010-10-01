/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import razie.draw.Renderer.ContainerRenderer;
import razie.draw.Renderer.Helper;

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
public class SimpleDrawStream extends DrawStream {

    /** the actual output socket */
    protected OutputStream out;

    public SimpleDrawStream(Technology tech, OutputStream out) throws IOException {
        super(tech);
        this.out = out;
    }

    public SimpleDrawStream(Technology tech) throws IOException {
        super(tech);
        this.out = new ByteArrayOutputStream();
    }

    public SimpleDrawStream() throws IOException {
        super(Technology.TEXT);
        this.out = new ByteArrayOutputStream();
    }

    protected void writeBytes(byte[] b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void renderObjectToStream(Object d) {
        Object r2 = Renderer.Helper.recursiveDraw(d, technology, this);
        if (r2 != null) {
           String s = r2.toString();
           writeBytes(s.getBytes());
           this.countObjectBytes += s.getBytes().length;
        }
    }

    @Override
    public void renderElement(StreamableContainer container, Object element) {
        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderElement(
                container, element, technology, this);
        writeBytes(result.toString().getBytes());
    }

    @Override
    protected void renderFooter(StreamableContainer container) {
        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderFooter(
                container, technology, this);
        writeBytes(result.toString().getBytes());
    }

    @Override
    protected void renderHeader(StreamableContainer container) {
        Object result = ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderHeader(
                container, technology, this);
        writeBytes(result.toString().getBytes());
    }

    public String toString() {
        return out.toString();
    }
}

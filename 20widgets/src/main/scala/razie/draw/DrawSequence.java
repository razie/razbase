/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;


import razie.draw.Renderer.ContainerRenderer;

/**
 * a drawable sequence of objects. Objects must implement Drawable3 or else we'll use toString()
 * 
 * this is different from DrawList in that there's no special formatting across elements...and it's
 * a stream not a container
 * 
 * @author razvanc99
 */
public class DrawSequence extends DrawStream implements Drawable {

    public DrawSequence() {
        super(Technology.ANY);
    }

    public DrawSequence(Object... objects) {
        super(Technology.ANY);
        if (objects != null) {
            for (Object o : objects) {
                if (o != null) this.write(o);
            }
        }
    }

    /** shortcut to render self - don't like controllers that much */
    public Object render(Technology t, DrawStream stream) {
            String reply = "";
            for (Element element : elements) {
                reply += (String) Renderer.Helper.recursiveDraw(element.o, t, stream);
            }

            return reply;
    }

    @Override
    public void close() {
    }

    @Override
    protected void renderObjectToStream(Object d) {
    }

    @Override
    public void renderElement(StreamableContainer container, Object element) {
        ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderElement(container,
                element, technology, this);
    }

    @Override
    protected void renderFooter(StreamableContainer container) {
        ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderFooter(container,
                technology, this);
    }

    @Override
    protected void renderHeader(StreamableContainer container) {
        ((ContainerRenderer) ((Drawable3) container).getRenderer(technology)).renderHeader(container,
                technology, this);
    }

}

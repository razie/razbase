/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;



/**
 * just wrap a container and present it as a stream - this is to separate model logic from
 * presentation: the model logic knows to stream and you just just stream it into a presentation
 * container (i.e. table)
 * 
 * note again, that the objects are not rendered yet, in either this stream or its container...
 * 
 * @author razvanc99
 */
public class ContainerStream extends DrawStream {
    StreamableContainer c;

    public ContainerStream(StreamableContainer c) {
        super(Technology.ANY);
        this.c=c;
    }

    @Override
    public void close() {
        c.close();
    }

    @Override
    protected void renderObjectToStream(Object d) {
        // just forward to my target - it will actually render whenever however it pleases
        this.c.write(d);
    }

    @Override
    public void renderElement(StreamableContainer container, Object element) {
        // nothing to do - that element container has already been forwarded to my target
    }

    @Override
    protected void renderFooter(StreamableContainer container) {
        // nothing to do - that element container has already been forwarded to my target
    }

    @Override
    protected void renderHeader(StreamableContainer container) {
        // nothing to do - that element container has already been forwarded to my target
    }

    // TODO DrawList is streamable but if you used it as below it doesn't close containerd containers
    @SuppressWarnings("unused")
	private void fixme () {
        DrawList c=new DrawList();
        ContainerStream s=new ContainerStream(new DrawList());
        s.open (c);
        //...// write to c
       c.close(); 
        // it doesn't close it properly - try to use it instead of a draw sequence and 
 
    }
}

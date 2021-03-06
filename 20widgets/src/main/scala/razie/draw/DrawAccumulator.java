/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;


/**
 * many methods just get result sets, without caring about the form in which these will be
 * presented. Clients can feed them whichever accumulator they want. Normal accumulators are
 * DrawStreams and DrawContainers.
 * 
 * streams will process results as they're accumulated while containers just pile them up and wait
 * for someone to process the results
 * 
 * $
 * @author razvanc99
 * 
 */
public interface DrawAccumulator {
    /**
     * accumulate a new object
     */
    public void write(Object o);

    /**
     * accumulator is closed, either by stream or by client. IF the object was open on the stream,
     * this was its last chance to flush everything down the stream, see DrawTable
     */
    public void close();
}

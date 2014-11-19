/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;

/**
 * this is really an object input stream...
 * 
 * TODO find use and finalize
 * 
 * $
 * @author razvanc99
 * 
 */
public interface DrawableInputStream {
    public Object readNext();
    public boolean hasMore();
    
    public static class Impl implements DrawableInputStream {

        public boolean hasMore() {
            // TODO Auto-generated method stub
            return false;
        }

        public Object readNext() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

}

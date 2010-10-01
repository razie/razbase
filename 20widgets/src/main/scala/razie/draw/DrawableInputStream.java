/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
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

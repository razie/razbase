/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package razie.draw;


/**
 * this is for decoupling of code - not everyone needs to be a drawable...
 * 
 * @author razvanc99
 * @stereotype factory
 * @version $Id$
 */
public interface DrawableSource {

    /**
     * @stereotype makes
     */

    /*#com.razie.pub.hframe.draw.Drawable Dependency_Link */

    public Drawable makeDrawable();
}

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
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

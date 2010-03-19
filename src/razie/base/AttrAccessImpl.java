/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base;


/** moving from java to scala... */
public class AttrAccessImpl extends JavaAttrAccessImpl {

   /** dummy */
   public AttrAccessImpl() {
   };

   /**
    * build from sequence of parm/value pairs or other stuff
    * 
    * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another
    *       AttrAccess OR a Map<String,String>. Note the parm names can contain type:
    *       "name:string"
    */
   public AttrAccessImpl(Object... pairs) {
       this.setAttr(pairs);
   }

}

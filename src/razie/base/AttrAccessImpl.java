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


        /**
         * build from sequence of parm/value pairs or other stuff
         * 
         * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another
         *       AttrAccess OR a Map<String,String>. Note the parm names can contain type:
         *       "name:string"
         */
//       def this(pairs:Any*) = { this(); setAttr(pairs) }

//       def sgetPopulatedAttr = (razie.RJS apply (this.getPopulatedAttr))

//        public Iterable<String> getPopulatedAttr() {
//            return this.parms == null ? Collections.EMPTY_LIST : this.order;
//        }
//
//        def size() : Int {
//            return this.parms == null ? 0 : this.parms.size();
//        }
//
//        public boolean isPopulated(String name) {
//            return this.parms != null && this.parms.containsKey(name);
//        }

//   override def toString() : String = 
//      (for (a <- this.sgetPopulatedAttr) yield a + (if (this.hasAttrType(a)) (":"+getAttrType(a)) else "") + "=" + getAttr(a).toString()).mkString(",")
       
//   override def toXml() : String = {
//      var s = "";
//      for (name <- this.sgetPopulatedAttr) {
//        s += "<" + name + ">" + this.getAttr(name) + "</" + name + ">";
//         }
//      s;
//     }

}


    /** simple base implementation */
//class ProxyAttrAccessImpl (val proxy:AttrAccess) extends AttrAccess {




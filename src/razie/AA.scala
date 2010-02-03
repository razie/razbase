/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

import com.razie.pub.base._

/** simplify usage of AttrAccess - attribute management
 * 
 * @author razvanc
 */
object AA {
   def apply (s:AnyRef*):AA = {val x = new AA(); x.setAttr(s:_*); x }
   def apply ():AA = new AA()

   /** simplify accessing attributes of asset classes 
    * 
    * @param s asset, of any class
    * @param name identifies the attribute
    * @return the attribute value, if we can find something yielding attribute values...
    */
   def a (s:Any, name:String):Option[AnyRef] = s match {
      case x:AttrAccess => Some(x a name)
      case x:HasAttrAccess => Some(x.attr a name)
      case _ => None // TODO 2-3 to try reflection... both scala and java versions :)
   }
   
   /** simplify accessing attributes of asset classes 
    * 
    * @param s asset, of any class
    * @param name identifies the attribute
    * @return the attribute value, if we can find something yielding attribute values...
    */
   def sa (s:Any, name:String):Option[String] = a (s,name) match {
      case Some(null) => None
      case Some(x) => Some(x.toString)
      case _ => None 
   }
   
   def foreach (a:AttrAccess, f : (String, AnyRef) => Unit) = 
      (razie.M apply (a.getPopulatedAttr)).foreach (x => f(x, a a x))
}

/** 
 * simplified AttributeAccess :)
 * 
 * @author razvanc
 */
class AA extends AttrAccessImpl {
   def this (xx : AnyRef*) = { this(); setAttr (xx); }
   
   def foreach (f : (String, AnyRef) => Unit) = 
      this.sgetPopulatedAttr.foreach (x => f(x, this a x))
   def filter (f : (String, AnyRef) => Boolean) = 
      this.sgetPopulatedAttr.filter (x => f(x, this a x))

   def toXmlWithChildren (me:Any, tag:String)(contents:Any=>String) = {
      var s = "<"+tag + " "
      s += (for (a <- this.sgetPopulatedAttr) yield a+"=\"" + this.getAttr(a) + "\"").mkString(" ")
      s += ">\n"
      s += contents (me)
      s += "</>\n"
         s
   }
}

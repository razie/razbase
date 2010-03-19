package razie.xp

import razie._
import org.json._

/** resolving JSON structures */
object XpJsonSolver extends XpSolver[AnyRef,List[AnyRef]] {

   // TODO 2-2 need to simplify - this is just mean...
   override def getNext[T>:AnyRef,U>:List[AnyRef]] (o:(T,U),tag:String, assoc:String) : List[(T,U)]={
      val ret:List[(T,U)] = 
         
      if (! o._2.asInstanceOf[List[AnyRef]].isEmpty) {
        if (tag != "*") 
          o._2.asInstanceOf[List[AnyRef]].filter (b => b.asInstanceOf[JSONObject].has(tag)).map (b=>(b.asInstanceOf[JSONObject].get(tag), Nil)).toList
        else {
          // tricky: match any object
          o._2.asInstanceOf[List[AnyRef]].filter (
                b => b.isInstanceOf[JSONObject]
                ).flatMap (
                      b => values (b.asInstanceOf[JSONObject]).filter (c => c.isInstanceOf[JSONObject]).
                      map (d => (d,Nil))
                      ).toList
        }
      } else {
      o._1 match {
         case j : JSONObject => 
           if (tag != "*") 
              (j.get (tag), List()) :: Nil
           else {
              // tricky: match any object
              for (s <- razie.MOLD(j.keys); if j.get(s.toString).isInstanceOf[JSONObject]) 
                 yield (j.get(s.toString), Nil)
           }
         case a : JSONArray => Nil
         case _ => Nil
      }
      }
      ret
   }

   private def values (b:JSONObject) = 
     for (s <- razie.MOLD(b.keys); if b.get(s.toString).isInstanceOf[JSONObject])  
       yield b.get(s.toString).asInstanceOf[JSONObject]

   override def getAttr[T>:AnyRef] (o:T,attr:String) : String = {
      val ret = o match {
         case o : JSONObject => o.get (attr)
         case _ => null
      }
      ret.toString
   }
   
//   override def reduce[T>:scala.xml.Elem,U>:List[scala.xml.Elem]] (o:Iterable[(T,U)],cond:XpCond) : Iterable[(T,U)] = 

   // attr can be: field name, method name (with no args) or property name */
   def resolve (o:AnyRef,attr:String) : Any = {
   }
   
   private[this] def toZ (attr:String) = attr.substring(0,1).toUpperCase + (if (attr.length > 1) attr.substring (1, attr.length-1) else "")
}
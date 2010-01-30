/*
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie.xp

import razie._

/** 
 * Implement this resolution in smart objects...otherwise inject it with an XPSolver
 */
trait OXP {
   /** find one element */
   def xpe (path:String) : OXP
   /** find a list of elements */
   def xpl (path:String) : List[OXP]
   /** find one attribute */
   def xpa (path:String) : String
   /** find a list of attributes */
   def xpla (path:String) : List[String]
}

/** 
 * XP resolution from a given node
 */
trait XPFrom[T] {
   /** find one element */
   def xpe (root:T) : T
   /** find a list of elements */
   def xpl (root:T) : List[T]
   /** find one attribute */
   def xpa (root:T) : String
   /** find a list of attributes */
   def xpla (root:T) : List[String]
}

    
class Person {}
class SuperOrder {}

object People {
   def xpe (path:String) : Person = null
}

object Samples {
   val john = People xpe ("/Person[@areGroovy=='nah!']")
   val me = xpe ("/{hasFriends}Person[@areGroovy=='yeah, baby!']") from john
   val minime = XP[Person] ("/Person[@areGroovy=='yeah, baby!']")
}

trait XPathType {
//   def from[T] (root:T) : T
//   def apply[T] = from(null)
   def from[T] (root:T) (implicit m:scala.reflect.Manifest[T]) : T
}

case class xpe (expr:String) extends XPathType {
    override def from [T] (root:T) (implicit m:scala.reflect.Manifest[T]) : T = (XP[T] (expr) using null) xpe (root)
//    override def apply [T] : T = from(null)
//    def apply [T] = XP[T] (expr) 
   
}



trait SuperMan_ager {
   def xpl (path:String) : List[SuperOrder]  = XP[SuperOrder] (path).xpl(null, null) 
   def openOrders () : List[SuperOrder]  = xpl ("/SuperOrder[@state=='Open']")
}




/** 
 * This is a simplified API so the calls are transparent
 */
trait GXPLike[T] {
   /** find one element */
   def xpe (root:T) : T
   /** find a list of elements */
   def xpl (root:T) : List[T]
   /** find one attribute */
   def xpa (root:T) : String
   /** find a list of attributes */
   def xpla (root:T) : List[String]
}

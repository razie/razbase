/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

import scala.collection._

/** conversions from java to scala collections
 * 
 * probably the thing I hate the most about scala: interacting with Java collections 
 * 
 * use like this: RJS(javalist).foreach -OR- RJS apply javalist foreach -OR- RJS list javalist sort 
 * OR for (x <- RJS.apply(whatever-java-threw-at-you))...
 */
object RJS {
  def apply[A](ij:java.lang.Iterable[A]) : scala.collection.Iterable[A] = JavaConversions.asIterable(ij)
  
  def apply[A](ij:java.util.List[A]) : scala.collection.mutable.Buffer[A] = JavaConversions.asBuffer(ij)
  
  def list[A](ij:java.util.List[A]) : scala.List[A] = JavaConversions.asBuffer(ij).toList
  
  def apply[A, B](ij : java.util.Map[A, B]) : scala.collection.mutable.Map[A,B] = JavaConversions.asMap(ij)
}

/** conversions from scala to java collections. TRY NOT TO do this, unless you absolutely have to :)
 * 
 * probably the thing I hate the most about scala: interacting with Java collections 
 * 
 * use like this RSJ(scalalist)
*/
object RSJ {
   def apply[A](ij:scala.collection.Iterable[A]) : java.lang.Iterable[A] = JavaConversions.asIterable(ij)
   
   def apply[A](ij:scala.collection.mutable.Buffer[A]) : java.util.List[A] = JavaConversions.asList(ij)
   
//   def apply[A](ij:scala.List[A]) : java.util.List[A] = JavaConversions.asList(ij)
   
//   def apply[A, B](ij : scala.collection.mutable.Map[A, B]) : java.util.Map[A,B] = JavaConversions.asMap(ij)
}

/** it sucks to have to import the stupid long package name all the time... */
class Map [K,V] extends scala.collection.mutable.HashMap [K, V] {}

object Map {
   def apply [K,V] () = new razie.Map[K,V]()
}

/** it sucks to have to import the stupid long package name all the time... */
//class List[V] extends scala.collection.mutable.MutableList[V] {}


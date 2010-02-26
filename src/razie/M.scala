/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

import scala.collection._
import java.{ lang => jl, util => ju }

/** I kept looking for a generic collection/iterable thing...and found that I was chasing Monads. 
 * 
 * This is my monadic playground. This is the commonality between all monads (mostly lists/collections), specifically taylored for scala's for()
 * 
 * use like this: razie.M(javalist).foreach -OR- razie.M apply javalist foreach
 * OR for (x <- razie.M.apply(whatever-java--r-scala-threw-at-you))...
 */
trait M[+A] {
   def map[B]     (f: A => B)       : M[B] 
   def flatMap[B] (f: A => M[B])    : M[B] 
   def filter     (p: A => Boolean) : M[A] 
   def foreach[U] (f: A => U)

   def iterator: Iterator[A] 
   def toList: List[A] = M.toList (this)
  
   override def equals (y:Any) : Boolean = tryConvertingToM[A] (y) match {
      case m : M[A] => M.equals(this, m) (_==_)
      case _ => false
   }

   def tryConvertingToM[A] (y:Any) : M[A] = {
      y match {
         case l : java.util.List[A] => M.apply (l)
         case i : java.util.Iterator[A] => M.apply(i)
         case b : java.lang.Iterable[A] => M.apply(b)
         case m : java.util.Map[_,A] => M.apply (m.values) 
         case a : Array[A] => M.apply(a)
         case s : scala.Seq[A] => M.apply(s)
         case _ => y.asInstanceOf[M[A]]
      }

   }
   def sort (lt:(A,A) => Boolean) : List[A] = M.sort(this, lt)
}

// special chars: a : 

/** helper class - has all the conversions */
object M {
//   implicit def apply[A] (l:Iterator[A]) : M[A] = new MonaTravestita[A] (l.toList)
   implicit def apply[A] (l:Iterator[A]) : M[A] = new MonaItera[A] (l)
   class MonaItera[A] (val l:Iterator[A]) extends M[A] {
     override def map[B](f: A => B): M[B] = new MonaItera(l.map(f))
     override def flatMap[B](f: A => M[B]): M[B] = new MonaItera (l.flatMap(x => f(x).iterator))
     override def filter(p: A => Boolean): M[A] = new MonaItera(l.filter(p))
     override def foreach[U](f: A => U) = l.foreach(f)

     override def iterator: Iterator[A]  = l
     override def toList: List[A] = l.toList
   }
   
   implicit def apply[A] (l:Traversable[A]) : M[A] = new MonaTravestita[A] (l)
   class MonaTravestita[A] (val l:Traversable[A]) extends M[A] {
     override def map[B](f: A => B): M[B] = new MonaTravestita(l.map(f))
     override def flatMap[B](f: A => M[B]): M[B] = new MonaTravestita (l.flatMap(x => f(x).toList))
     override def filter(p: A => Boolean): M[A] = new MonaTravestita(l.filter(p))
     override def foreach[U](f: A => U) = l.foreach(f)

     override def iterator: Iterator[A]  = l.toIterable.iterator
     override def toList: List[A] = l.toList
   }
   
   implicit def apply[A] (l:Option[A]) : M[A] = new MonaOpta[A] (l)
   class MonaOpta[A] (val l:Option[A]) extends M[A] {
     override def map[B](f: A => B): M[B] = new MonaOpta(l.map(f))
     override def flatMap[B](f: A => M[B]): M[B] = new MonaOpta (l.flatMap((x:A) => toOption (f(x))))
     override def filter(p: A => Boolean): M[A] = new MonaOpta(l.filter(p))
     override def foreach[U](f: A => U) = l.foreach(f)

     override def iterator: Iterator[A]  = l.iterator
     override def toList: List[A] = l.toList
     
     def toOption[B] (m:M[B]) : Option[B] = firstOpt (m)
   }
  
   implicit def apply[A] (l:ju.List[A]) : M[A] = apply (JavaConversions.asBuffer(l))
   implicit def apply[A] (l:ju.Iterator[A]) : M[A] = apply (JavaConversions.asIterator(l))
   implicit def apply[A] (l:jl.Iterable[A]) : M[A] = apply (JavaConversions.asIterable(l))
   implicit def apply[A] (l:ju.Map[_,A]) : M[A] = apply (JavaConversions.asIterable(l.values))
   
  
   // ------------------- common monad stuff

   /** apply f on each pair (A,B) and contain result 
    * 
    * TODO return a nice non-strict monad
    */
   def parmap[A,B,C] (x:M[A], y:M[B]) (f:(A,B)=>C) : M[C] = {
      // TODO optimize
    val i1 = x.iterator
    val i2 = y.iterator
    var b = false
    val res = razie.Listi[C]()

    while (i1.hasNext && i2.hasNext && !b) {
      val x1 = i1.next
      val x2 = i2.next
      res += f(x1, x2)
    }

    // TODO see quals - what if the lists are not equals? get pissed?
    M(res)
   }

   /** compare two monads, given a comparison function 
    * 
    * @param x - to compare
    * @param y - to compare
    * @param eeq - eq function 
    */
   def equals[A,B] (x:M[A], y:M[B]) (eeq:(A,B)=>Boolean) = {
      // TODO optimize
    val i1 = x.iterator
    val i2 = y.iterator
    var bad = false

    while (i1.hasNext && i2.hasNext && !bad) {
      val x1 = i1.next
      val x2 = i2.next

      if (! eeq(x1,x2)) {
        bad = true
      }
    }

    !(bad || i1.hasNext || i2.hasNext)
   }
   
   def first[A] (x:M[A]) : M[A] = {
      // TODO optimize - how to stop filter ?
      class State[A] {var s:Option[A] = None}
      val state = new State[A]()
      val f = (s:State[A], y:A) => {if (s.s.isDefined) false else {s.s=Some(y); true} }
      val m = x.filter (f (state, _))
      m
   }

   def firstOpt[A] (x:M[A]) : Option[A] = {
      // TODO optimize - how to stop filter ?
      class State[A] {var s:Option[A] = None}
      val state = new State[A]()
      val f = (s:State[A], y:A) => {if (s.s.isDefined) false else {s.s=Some(y); true} }
      val m = x.filter (f (state, _))
      state.s
   }

   def firstThat[A] (x:M[A])(cond:A=>Boolean) : Option[A] = {
      // TODO optimize - how to stop filter ?
      class State[A] {var s:Option[A] = None}
      val state = new State[A]()
      val f = (s:State[A], y:A) => {if (s.s.isDefined) false else {if (cond(y)) {s.s=Some(y); true} else false} }
      val m = x.filter (f (state, _))
      state.s
   }

   def count[A] (x:M[A]) : Int = {
      // TODO optimize - how to stop foreach ?
      class State {var c:Int = 0}
      val state = new State()
      val f = (s:State, y:A) => { s.c +=1 }
      val m = x.foreach (f (state, _))
      state.c
   }
   
   def toList[A] (x:M[A]) : List[A] = {
      // TODO optimize 
      val state = new scala.collection.mutable.ListBuffer[A] ()
      val f = (y:A) => { state.append(y) }
      val m = x.foreach (f)
      state.toList
   }
   
   def sort[A] (x:M[A], lt:(A,A) => Boolean) : List[A] = x.toList.sort(lt)
}

/** my first ugly attempt - not type-safe */
object MOLD {
   import JavaConversions._
   
   def apply (f:Any) : List[_] = {
      f match {
         case l : java.util.List[_] => (for (x <- l) yield x).toList
         case i : java.util.Iterator[_] => (for (x <- i) yield x).toList
         case b : java.lang.Iterable[_] => (for (x <- b) yield x).toList
         case m : java.util.Map[_,_] => (for (x <- m.values) yield x).toList
         case a : Array[_] => (for (x <- a) yield x).toList
         case s : scala.Seq[_] => (for (x <- s) yield x).toList
         case null => (for (x <- None) yield x).toList
         case _ => (for (x <- Some(f)) yield x).toList
      }
   }
   def f (x: => Any) = apply (x)
}



/** it sucks to have to import the stupid long package name all the time... */
object Mapi {
   def apply [K,V] () = new scala.collection.mutable.HashMap [K, V] ()
}

/** it sucks to have to import the stupid long package name all the time...when retrofitting old code */
object Listi {
   def apply [K] () = new scala.collection.mutable.ListBuffer [K] ()
}

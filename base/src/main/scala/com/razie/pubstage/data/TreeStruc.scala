package com.razie.pubstage.data;

/**
 * a tree structure, with nodes and leafs etc
 * 
 * @author razvanc
 * @param [T]
 */
trait TreeStruc[T] extends Structure[T] {
   def children : List[TreeStruc[T]]
   def isLeaf : Boolean
}

class TreeImplNode[T] (c:T) extends StrucImpl[T] (c) with TreeStruc[T] {
   val kids = new scala.collection.mutable.ListBuffer[TreeStruc[T]]()

   override def children : List[TreeStruc[T]] = kids.toList
   override def isLeaf : Boolean = false

   def addLeaf (t:T) = this.kids += new TreeImplLeaf[T](t)

   def addNode (t:T) : TreeStruc[T] = {
     val newT = new TreeImplNode[T](t);
     this.kids += newT
     newT
     }
}
    
class TreeImplLeaf[T] (c:T) extends StrucImpl[T](c) with TreeStruc[T] {
   override def children : List[TreeStruc[T]] = scala.collection.immutable.List[TreeStruc[T]]()
   override def isLeaf : Boolean = true
}


class TreeBuilder[T] (t:T)(f: =>List[TreeStruc[T]]) extends TreeImplNode[T] (t) {
   override val children : List[TreeStruc[T]] = f
}

case class INode (t:Int)(f: =>List[TreeStruc[Int]]) extends TreeBuilder[Int] (t)(f) {}
case class ILeaf (t:Int) extends TreeImplLeaf[Int] (t) {}


object TestTree1 {

   val tree =  INode (0) { 
      ILeaf(1) :: ILeaf(2) :: Nil
   }
}


/** the f should create also LazyTreeBuiled nodes or leafs */
class LazyTreeBuilder[T] (t:T)(f: TreeStruc[T] => List[TreeStruc[T]]) extends TreeImplNode[T] (t) {
   override lazy val children : List[TreeStruc[T]] = f (this)
}


// morph test

//class TreeMorph[T] (t:T)(f: =>List[TreeMorph[T]] = Nil) extends TreeImplNode[T] (t) {
//}
//
//class TreeMorphAny extends TreeImplNode[Any] {
//   
//}
//
//case class IT (t:Int)(f: =>List[TreeMorph[Int]] = Nil) extends TreeMorph[Int] (i) (f) {
//}
//
//
//object TestTree2 {
//
//   val tree =  IT (0) { 
////      IT(1) :: IT(2)
//   }
//   
//   
//}

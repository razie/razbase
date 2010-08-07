/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.g

//-------------------------- simple (set) graphs, with structure induced on an existing set

/** a graph is a set of nodes and links between these nodes */
trait SGraph [N <: GSNode[_], L <:GLink[N]] {
  def gnodes : Seq[N]
  def glinks : Seq[L]
}

/** a node is... anything */
trait GSNode[A] extends GNode[GSNode[A], GLink[GSNode[A]]] {
  def content:A
//trait GSNode {
}

/** a link between two nodes */
trait GLink [N <: GNode[_,_]] {
//trait GLink [N <: GSNode] {
  def a : N
  def z : N
}


//---------------------------- smart graph of nodes, each node is a sub-graph with embedded structure

/** a graph is a set of nodes and links between these nodes */
trait Graph [N <: GNode[_,_], L <:GLink[N]] {
  def gnodes : Seq[N]
  def glinks : Seq[L] // by convention, all links with a == this
}

/** a modifyable graph */
trait WRGraph [N <: GNode[_,_], L <:GLink[N]] extends Graph[N,L] {
  this : N =>
 
  type LFactory = (N, N) => L
  
  def gnodes_= (s:Seq[N])
  def glinks_= (s:Seq[L]) // by convention, all links with a == this
  
  /** reroute */
  def --> [T<:N] (z:T)(implicit linkFactory: LFactory) : N = {
    glinks = linkFactory(this,z) :: Nil
    this
    }
  /** reroute */
//  def <-- [T<:WRGraph[N,L]] (z:T)(implicit linkFactory: LFactory) : N =  z --> (this)
//  def <-+ [T<:WRGraph[N,L]] (z:T)(implicit linkFactory: LFactory) : N =  z +-> (this)
  /** add a new dependency */
  def +-> [T<:N](z:T)(implicit linkFactory: LFactory) : N = {
    glinks = glinks.toList.asInstanceOf[List[L]] ::: List(linkFactory (this, z))
    this
  }
  /** par depy a -> (b,c) */
  def --> [T<:N] (z:Seq[T])(implicit linkFactory: LFactory) : N = {
    glinks = z.map (linkFactory(this,_)).toList
    this
  } 
  /** par depy a -> (b,c) */
  def +-> [T<:N] (z:Seq[T])(implicit linkFactory: LFactory) : N = {
    glinks = glinks.toList.asInstanceOf[List[L]] ::: z.map (linkFactory(this,_)).toList
    this
  } 
}

/** smart graph, each node is a sub-graph of "child" nodes and links */
trait GNode[N<:GNode[N,L], L<:GLink[N]] extends Graph[N, L] {
//trait GNode[N<:GNode[N,L], L<:GLink[N]] extends GSNode with Graph[N, L] {
  this : N =>
  
  def mkString = Graphs.mkString[N,L] (this)
  
//  def filtered (f: GNode[N, L] => Boolean) : GNode[N, L] = Filtered (this, f)
}

//case class Filtered [N<:GNode[N,L], L<:GLink[N]] (base:GNode[N, L], f: GNode[N, L] => Boolean) 
//extends GNode[N, L] {
//   
//}

/** traversal helpers */
object Graphs {
  /** you better give me a DAG :) */
  def mkString [N<:GNode[N,L], L<:GLink[N]] (n:N) : String = {
    var s = new StringBuffer()
    s.append("Graph: \n")
    s.append(pt(" ", 0) + n.toString+"\n")
    foreach (n,
          (x:N,v:Int)=>{}, // nothing - I print nodes as part of the assocs pointing to them
          (l:L,v:Int)=>{s.append(pt(" ", v) + "->" + l.z.toString+"\n")},
          0
          )
    s.toString
  }

  /** you better give me a DAG :) */
  def mkString2 [N<:GNode[N,L], L<:GLink[N]] (n:N) : String = {
    var s = new StringBuffer()
    s.append("Graph: \n")
    foreach (n,
          (x:N,v:Int)=>{s.append(pt(" ", v) + x.toString+"\n")},
          (l:L,v:Int)=>{s.append(pt(" ", v) + "->" + l.z.toString+"\n")},
          0
          )
    s.toString
  }

  def pt (s:String, i:Int) = (Range(0,i).map(k=>s)).mkString
     
//  def foreach [L <:GLink[_]] (n:GNode[_,_], fn:(GNode[_,_], Int)=>Unit, fl:(GLink[_<:GNode[_,_]], Int)=>Unit, level:Int = 0) {
  def foreach [N<:GNode[N,L], L<:GLink[N]] (n:N, fn:(N, Int)=>Unit, fl:(L, Int)=>Unit, level:Int = 0) {
    fn(n,level)
    n.glinks.foreach(l=>{ fl(l, level); foreach(l.z, fn, fl, level+1) })
  }
  
  def filterNodes [N<:GNode[N,L], L<:GLink[N]] (n:N) (f: N => Boolean) : Seq[N] = {
    val ret = razie.Listi[N]
    Graphs.foreach (n,
      (x:N,v:Int)=>{if (f(x)) ret append x},
      (l:L,v:Int)=>{},
      0
      )
    ret
    }
}

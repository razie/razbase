/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.g

object Graphs {
}

//-------------------------- simple (set) graphs, with structure induced on an existing set

/** a graph is a set of nodes and links between these nodes */
trait SGraph [N <: GSNode, L <:GLink[N]] {
  def gnodes : Seq[N]
  def glinks : Seq[L]
}

/** a node is... anything */
trait GSNode {
}

/** a link between two nodes */
trait GLink [N <: GSNode] {
  def a : N
  def z : N
}


//---------------------------- smart graph of nodes, each node is a sub-graph with embedded structure

/** a graph is a set of nodes and links between these nodes */
trait Graph [N <: GNode[_,_], L <:GLink[N]] {
  def gnodes : Seq[N]
  def glinks : Seq[L] // by convention, all links with a == this
}

/** smart graph, each node is a sub-graph of "child" nodes and links */
trait GNode[N<:GNode[N,L], L<:GLink[N]] extends GSNode with Graph[N, L] {
  this : N =>
  
  def mkString = GStuff.mkString[N,L] (this)
}

/** traversal helpers */
object GStuff {
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
}

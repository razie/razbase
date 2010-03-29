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
//  def gnodes : Seq[GNode[L]]
//  def glinks : Seq[L] // by convention, all links with a == this
}


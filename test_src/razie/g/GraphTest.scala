/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.g

import org.scalatest.junit._
import org.scalatest.SuperSuite
import org.scalatest.matchers._
import razie.{G}
import razie.g._

class MyL (val a:MyN, val z:MyN) extends razie.g.GLink[MyN]
class MyLV (aaa:MyN, zzz:MyN, val selector:Any) extends MyL (aaa,zzz)                                            

class MyN (val color:String) extends razie.g.GNode[MyN, MyL] with razie.g.WRGraph[MyN, MyL] {         
//  override var gnodes : Seq[MyN] = Nil 
  override var glinks : Seq[MyL] = Nil // links 

  /** bound: point all leafs to z, an end node, while avoiding z --> z */
  def --| (z:MyN)(implicit linkFactory: LFactory)  = {
    ( razie.g.Graphs.filterNodes[MyN,MyL](this) {
      a => a.glinks.isEmpty && a != z
    } ) foreach (i => i +-> z)                 
    this
  }

  /** par depy a -> (b,c) */
  def -->[T <: MyN](z: Map[Any,T])(implicit linkFactory: LFactory): MyN = {
    glinks = z.map(m => new MyLV(this, m._2, m._1)).toList
    this
  }
  /** par depy a -> (b,c) */
  def +->[T <: MyN](z: Map[Any,T])(implicit linkFactory: LFactory): MyN = {
    glinks = glinks.toList.asInstanceOf[List[MyL]] ::: z.map(m => new MyLV(this, m._2, m._1)).toList
    this
  }
 
  override def toString = color

}

/**
 * testing the assets
 * 
 * @author razvanc99
 */
class GraphTest extends JUnit3Suite with ShouldMatchers {
  import razie.g.Graphs._  // get the implicits
  
  // for the nice inherited --> operators                             
  implicit val linkFactory = (x,y) => new MyL(x,y)

  def x = new MyN ("x")
  def y = new MyN ("y")

  val g1 = x --> List(x,x,y) --| y

  class Counter { 
    var c = 0; 
//    val traversed = new scala.collection.mutable.HashSet[MyN]() 
    def count (n:MyN, i:Int) {
//      if (!(traversed contains n)) { 
        c+=1; 
//        traversed += n
//      }
    }
  }

  implicit def toGraphLike[N <: GNode[N, L], L <: GLink[N]] 
    (root:N) (implicit mn:Manifest[N], ml:Manifest[L]) = entire[N,L] (root)
    
  // leaf counted 3 times...
  def test1 = expect (7) {
     val c1 = new Counter
    (Graphs.entire[MyN, MyL] (g1).foreachNode (c1.count), c1)._2.c
  }

  // all nodes counted JUST one time
  def test2 = expect (5) {
     val c1 = new Counter
    println (Graphs.entire[MyN, MyL] (g1).indexed.index)
    (Graphs.entire[MyN, MyL] (g1).indexed.foreachNode (c1.count), c1)._2.c
  }

}

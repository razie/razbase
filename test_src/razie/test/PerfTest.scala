/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.test

import org.scalatest.junit._
import org.scalatest._
import razie.Near
import razie.Threads
import razie.Perf

class PerfTest extends JUnit3Suite {

   def test1  = expect (false) { Near (10.0::10.0::Nil) == Near (15.0::10.0::Nil) }
   def test1a = expect (true) { Near (10.0::10.0::Nil) == Near (11.9::8.1::Nil) }
   def test1b = expect (false) { Near (10.0::10.0::Nil) == 15.0::10.0::Nil }
   def test1c = expect (Near (10.0::10.0::Nil)) { Near (11.9::8.1::Nil) }
//   def test1d = expect (Near (10.0::10.0::Nil)) { 11.9::8.1::Nil }
   
   def test2 = expect (Near (10.0::10.0::0.1::Nil, 50)) {
      val g1 = Perf.runmt(1, 10) { 
         (thread:Int, loop:Int) => {
               Thread.sleep(10)
         }
      }
   println (g1)
   Near (List(g1._1, g1._2, g1._3))
   }
  
   // same response time per thread but throughpout goes up
   def test3 = expect (Near (10.0::10.0::0.2::Nil)) {
      val g1 = Perf.runmt(2, 10) { 
         (thread:Int, loop:Int) => {
               Thread.sleep(10)
         }
      }
   println (g1)
   Near (List(g1._1, g1._2, g1._3))
   }

   // at 5 threads, it's not times 5 anymore, just times 4...
   def test4 = expect (Near (10.0::0.1::10.0::0.2::10.0::0.4::Nil)) {
    val g3 = (for (i <- List(1,2,5)) yield (i, 
        Perf.runmt(i, 10) { (thread:Int, loop:Int) => {
               Thread.sleep(10)
//               println ("test " + thread + "-"+loop)
        }} 
    )).toList
   Near (g3.flatMap(x => List(x._2._2, x._2._3)))
   }
   
}

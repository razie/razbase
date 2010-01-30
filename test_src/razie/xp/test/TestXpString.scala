/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.xp.test

import org.scalatest.junit._
import org.scalatest.SuperSuite
import razie._

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class TestXpString extends JUnit3Suite {

 def test11a = expect (List("root")) {
  XP("/root").xpl(StringXpSolver, "/root")
  }

 def test11b = expect (List("root")) {
  XP("/root").xpl(StringXpSolver, "root")
  }

 def test12 = expect (List("s1")) {
  XP("/root/s1").xpl(StringXpSolver, "/root/s1")
  }

 def test21 = expect (List(("a",List(null)))) { xp("/a","a") }
 def test22 = expect (List(("a",List(null)))) { xp("a","a") }
 def test23 = expect (List(("a",List("/b")))) { xp("/a/b","a") }
 def test24 = expect (List(("a",List("/b")))) { xp("a/b","a") }

 def xp(src:String,path:String) = StringXpSolver.getNext((src,List(src)),path,null)
}

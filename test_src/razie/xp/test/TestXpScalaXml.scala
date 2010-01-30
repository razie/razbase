/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.xp.test

import org.scalatest.junit._
import org.scalatest.SuperSuite
import razie.XP

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class TestXpScalaXml extends JUnit3Suite {
   
   def test41 = expect (List("a")) { xpl("/a").map(_.label) }
   def test42 = expect (List("a")) { xpl("a").map(_.label) }
   def test43 = expect (List("b1","b2")) { xpl("/a/b").map(_ \ "@ba") }
   def test44 = expect (List("b1","b2")) { xpla("/a/b/@ba") }
   def test45 = expect (List("c11","c12","c13")) { xpla("/a/b[@ba=='b1']/c/@ca") }
 
   def xpl (path:String) = XP forScala (path) xpl (TXXmls.x) 
   def xpla(path:String) = XP forScala (path) xpla (TXXmls.x) 
 
// def test31 = expect (List("a")) { sx("/a").map(_.name) }
// def test32 = expect (List("a")) { sx("a").map(_.name) }
// def test33 = expect (List("b1","b2")) { sx("/a/b").map(_ a "ba") }
// 
// def sx(path:String) = new XP[RazElement] (path).xpl(new DomXqSolver, TXXmls.x) 
}

object TXXmls {
  def x = {
    <a aa="a1">
      <b ba="b1">
        <c ca="c11"/>
        <c ca="c12"/>
        <c ca="c13"/>
      </b>
      <b ba="b2">
        <c ca="c21"/>
        <c ca="c22"/>
        <c ca="c23"/>
      </b>
    </a>
    }
}

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.xp.test

import org.scalatest.junit._
import org.scalatest.SuperSuite
import razie.{ XP, DomXpSolver }
import razie.base.data.RazElement._

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class TestXpScalaXml extends JUnit3Suite {

  def test40 = expect(List("a")) { xpl("/").map(_.label) }
  def test41 = expect(List("a")) { xpl("/a").map(_.label) }
  def test42 = expect(List("a")) { xpl("a").map(_.label) }
  def test43 = expect(List("b1", "b2")) { xpl("/a/b").map(x => (x \ "@ba").toString) }
  def test43a = expect(List("b1", "b2")) { xpl("a/b").map(x => (x \ "@ba").toString) }
//  def test43b = expect(List("b1", "b2")) { xpl("/a").flatMap(XP.forScala("b").xpl(_)).map(x => (x \ "@ba").toString) }
  def test44 = expect(List("b1", "b2")) { xpla("/a/b/@ba") }
  def test45 = expect(List("c11", "c12", "c13")) { xpla("/a/b[@ba=='b1']/c/@ca") }
  def test46 = expect(List("b1", "b2")) { xpla("/a/*/@ba") }
  def test47 = expect(List("b1", "b2")) { xpla("/*/*/@ba") }

  def xpl(path: String) = XP forScala (path) xpl (TXXmls.x)
  def xpla(path: String) = XP forScala (path) xpla (TXXmls.x)

  def test31 = expect(List("a")) { sx("/a").map(_.name) }
  def test32 = expect(List("a")) { sx("a").map(_.name) }
  def test33 = expect(List("b1", "b2")) { sx("/a/b").map(_ a "ba") }

  def sx(path: String) = XP[razie.base.data.RazElement](path).xpl(new DomXpSolver, TXXmls.x)
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

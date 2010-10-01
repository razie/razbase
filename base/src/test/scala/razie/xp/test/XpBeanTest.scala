/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
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
class XpBeanTest extends JUnit3Suite {

  def test1 = expect(List(root)) { xpl("/") }
  def test2 = expect("s") { xpa("/@s") }
  def test3 = expect("s") { xpe("/s") }
  def test4 = expect("t") { xpa("/@t") }
  def test5 = expect("u") { xpe("/u") }
  def test6 = expect(List(new JavaB("j"))) { xpl("/j") }
  def test7 = expect(List(new JavaB("a"), new JavaB("b"))) { xpl("/l") }
  def test8 = expect("a") { xpa("/l[@value=='a']/@value") }
  def test9 = expect(List("a", "b")) { xpla("/l/@value") }
  def test0 = expect("s") { xpe("/j/a/s") }
  def testa = expect("s") { xpa("/j/@s") }
  def testb = expect("s") { xpe("/j/*/s") }
  def testc = expect(List("s", "s")) { xpl("/j/*/s") }


  def xpe(path: String) = XP[Any](path) using BeanXpSolver xpe root
  def xpl(path: String) = XP[Any](path).xpl(BeanXpSolver, root)
  def xpla(path: String) = XP[Any](path).xpla(BeanXpSolver, root)
  def xpa(path: String) = XP[Any](path).xpa(BeanXpSolver, root)

  val root = new ScalaB("root")
}

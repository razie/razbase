/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.xp.test

import org.junit.Test
import org.scalatest.junit.MustMatchersForJUnit
import razie._

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class TestXpString extends MustMatchersForJUnit {

  def test11a = expect(List("root")) {
    XP("/root").xpl(StringXpSolver, "/root")
  }

  def test11b = expect(List("root")) {
    XP("/root").xpl(StringXpSolver, "root")
  }

  def test12 = expect(List("s1")) {
    XP("/root/s1").xpl(StringXpSolver, "/root/s1")
  }

  def test21 = expect(List(("a", List(null)))) { xp("/a", "a") }
  def test22 = expect(List(("a", List(null)))) { xp("a", "a") }
  def test23 = expect(List(("a", List("/b")))) { xp("/a/b", "a") }
  def test24 = expect(List(("a", List("/b")))) { xp("a/b", "a") }

  def xp(src: String, path: String) = StringXpSolver.getNext((src, List(src)), path, null)
}

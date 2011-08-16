package razie.dsl.test

import org.scalatest.junit._
import razie.dsl._

/** simple collector example */
class DslCollectorTest extends JUnit3Suite {

  case class move(i: Int) extends DslCollectable

  def test1 = expect(move(1) :: move(2) :: Nil) {
    val collected = new collection.mutable.ListBuffer[Any]()

    DslCollector.collect { collected += _ }(1) { // start a collector level
      move(1) //collects itself
      move(2) //collects itself
    } // collector ends

    collected
  }
}


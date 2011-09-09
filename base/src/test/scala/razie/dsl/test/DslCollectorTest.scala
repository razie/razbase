package razie.dsl.test

import org.scalatest.junit._
import razie.dsl._
import org.junit.Test

/** simple collector example */
class DslCollectorTest extends MustMatchersForJUnit {

  case class move(i: Int) extends DslCollectable

  @Test def test1 = expect(move(1) :: move(2) :: Nil) {
    val collected = new collection.mutable.ListBuffer[Any]()

    DslCollector.collect { collected += _ }(1) { // start a collector level
      move(1) //collects itself
      move(2) //collects itself
    } // collector ends

    collected
  }
}


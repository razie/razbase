package razie.dsl.test

import org.junit.Test
import org.scalatest.junit._
import razie.dsl._

/** simple if/else implementation, with else_if */
object DslCollectorIfElseSimple {
  val vars = new collection.mutable.HashMap[String, Any]() // var values

  val expr1 = $if(true) {
    v("a") := 11
    v("b") := 12
  }
  val expr2 = $if(false) {
    v("a") := 11
    v("b") := 12
  } $else {
    v("a") := 41
    v("b") := 42
  }

  var branch = 1
  val expr =
    $if(branch == 1) {
      v("a") := 11
      v("b") := 12
    } $else $if(branch == 2) {
      v("a") := 21
      v("b") := 22
    } $else $if(branch == 3) {
      v("a") := 31
      v("b") := 32
    } $else {
      v("a") := 41
      v("b") := 42
    }

  def v(s: String) = new TVar(s)
  def $if(cond: => Boolean)(body: => Unit) = TIf(() => cond, () => body)

  /** a token has a name and kids - and can evaluate itself, at "runtime" */
  class Token(var name: String) {
    def kids: Seq[Any] = Nil
    def eval { kids map { case t: Token => t.eval } }

    override def toString = name
    def print(indent: String) { println(indent + name); kids map { case t: Token => t.print(indent + " ") } }
  }

  // vars can be assigned to
  case class TVar(varname: String) extends Token("TVar " + varname) {
    def :=(expr: => Any) = new TAssign(this, () => expr)
  }

  case class TAssign(v: TVar, e: () => Any) extends Token(":=") with DslCollectable { //collects itself 
    override def eval { vars.put(v.varname, e()) }
  }

  /**
   * a scope encloses the construction of other tokens - using the DSL collecting pattern
   *
   *  http://blog.razie.com/2011/08/scala-dsl-technique-collecting.html
   */
  case class TScope(body: () => Unit) extends Token("SCOPE ") {
    override val kids = DslCollector.simpleCollect(1) { body() }
  }

  case class TIf(cond: () => Boolean, body: () => Unit, var elsebranch: Option[TElse] = None) extends Token("IF ") {
    val scope = TScope(body)
    //    def $else[T] (body: => Unit) = { this.lastif.elsebranch = Some(Else(() => body)); this }
    def $else[T](body: => T) = { this.lastif.elsebranch = Some(TElse(() => body)); this }
    def $else(i: TIf) = {
      this.lastif.elsebranch = Some(TElseIf(i))
      this
    }
    def lastif = elsebranch.map(_.lastif).getOrElse(this)
    override def kids = scope :: elsebranch.toList
    override def eval { if (cond()) scope.eval else elsebranch.map(_.eval) }
  }

  // the last else clause
  case class TElse(body: () => Unit) extends Token("ELSE ") {
    override def kids: Seq[Any] = TScope(body) :: Nil
    def lastif: TIf = throw new IllegalStateException("this is a final else - no more else")
  }

  case class TElseIf(i: Token) extends TElse(() => ()) {
    name = "ELSE-IF "
    override def kids: Seq[Any] = i :: Nil
    override def lastif: TIf = i match {
      case i: TIf => i.lastif
      case _ => throw new IllegalStateException("this else cannot have an else")
    }
  }
}

class DslCollectorIfElseSimpleTest extends MustMatchersForJUnit {
  import DslCollectorIfElseSimple._

  @Test def testa1 = expect(11) {
    expr1.print("1 ")
    expr1.eval
    vars("a")
  }

  @Test def testa2 = expect(41) {
    expr2.print("2 ")
    expr2.eval
    vars("a")
  }

  def run(b: Int) = {
    branch = b
    expr.print(branch + " ")
    expr.eval
    vars("a")
  }

  @Test def test1 = expect(11) { run(1) }
  @Test def test2 = expect(21) { run(2) }
  @Test def test3 = expect(31) { run(3) }
  @Test def test4 = expect(41) { run(4) }
}

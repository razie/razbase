package razie.dsl.test

import org.scalatest.junit._
import razie.dsl._

object Expr {
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

  val expr = $if(false) {
    v("a") := 11
    v("b") := 12
  } $else $if(true) {
    v("a") := 21
    v("b") := 22
    //  } $else $if(true) {
    //    v("a") := 31
    //    v("b") := 32
    //  } $else {
    //    v("a") := 41
    //    v("b") := 42
  }

  def v(s: String) = new TVar(s)
  def $if(cond: => Boolean)(body: => Unit) = If(() => cond, () => body)

  class Token(var s: String) {
    def kids: Seq[Any] = Nil
    override def toString = s
    def print(indent: String) { println(indent + s); kids map { case t: Token => t.print(indent + " ") } }
    def eval { kids map { case t: Token => t.eval } }
  }

  trait Collected { // collects others
    var collected = false
    DslCollector.current.map { _ collect this }
    def collect() : Unit
  }

  case class TVar(name: String) extends Token("TVar " + name) {
    def :=(expr: => Any) = new TAssign(this, () => expr)
  }

  case class TAssign(v: TVar, e: () => Any) extends Token(":=") with DslCollectable { //collects itself 
    override def eval { vars.put(v.name, e()) }
  }

  var thisIsAnElse = false

  // this is the starting IF
  case class If(cond: () => Boolean, body: () => Unit) extends Token("IF ") {
    //    DslCollector.collect(1) { body() }
    def $else[T](body: => Unit) = IfElse(this, Else(() => body))
    def $else$If(n: If) = IfElse(this, ElseIf(n))
    //    def $else(n: IfElse) = IfElse(this, ElseIf(n))
    override val kids = DslCollector.simpleCollect(1) { body() }
    override def eval { if (cond()) kids map { case t: Token => t.eval } }
  }
  // the last else clause
  case class Else(body: () => Unit) extends Token("ELSE ") {
    thisIsAnElse = true
    override val kids = DslCollector.simpleCollect(1) { body() }
    thisIsAnElse = true
  }
  case class ElseIf(i: Token) extends Token("ELSE-IF ") {
    override val kids = i :: Nil
  }
  case class IfElse(prev: If, next: Token) extends Token("IF-ELSE ") {
    override val kids: Seq[Any] = prev :: next :: Nil
    override def eval {
      if (prev.cond())
        prev.kids map { case t: Token => t.eval }
      else
        next.kids map { case t: Token => t.eval }
    }
    // handles the second else in an if-else-if-else
    //    def $else(body: => Unit) = {
    //      lastIfElse.$else (body)
    //    }
    //    def $else(n: If) = IfElse(this, ElseIf(n))
    //    def last[T](n:Token):T = next.kids.first match {
    //      case ElseIf(i) => i
    //      case x:IfElse => x.lastIf
    //      case x:If => x
    //      case p:Token => { p.print(">>>>>");  throw new IllegalStateException ("can only have an $else follow an $if")}
    //    }
  }
}

class DslCollectorIfElseTest extends JUnit3Suite {
  def test1 = expect(21) {
    Expr.expr.print(" ")
    Expr.expr.eval
    Expr.vars("a")
  }
}


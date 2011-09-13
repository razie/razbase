/**
 * ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie
import razie.base.data.RazElement
import java.lang.reflect.Method
import java.lang.reflect.Field

/**
 * a simple resolver for x path like stuff. note the limitation at the bottom
 *
 * can resolve the following expressions
 *
 * /a/b/c
 * /a/b/@c
 * /a/b[cond]/...
 * /a/{assoc}b[cond]/...
 *
 * / a / * / c    - ignore one level: explore all possibilities for just that level
 *
 * One difference from classic xpath is that the root node can be specified, see "a" above
 *
 * It also differs from a classic xpath by having the {assoc} option. Useful when
 * navigating models that use assocations as well as composition (graphs). Using
 * "/a/{assoc}b" means that it will use association {assoc} to find the b starting
 * from a...
 *
 * TODO the type system here is all gefuckt...need better understanding of variance in scala.
 * See this http://www.nabble.com/X-String--is-not-a-subtype-of-X-AnyRef--td23428970.html
 *
 * Example usage:
 * <ul>
 * <li> on Strings: XP("/root").xpl(new StringXpSolver, "/root")
 * <li> on scala xml: XP[scala.xml.Elem] ("/root").xpl(new ScalaDomXpSolver, root)
 * </ul>
 *
 * NOTE - this is stateless with respect to the parsed object tree - it only keeps the pre-compiled xpath
 * expression so you should reuse them as much as possible
 *
 * Note that this is a limited play-type thing. There are full XPATH implementations to browse stuff,
 * like Apache's JXpath.
 *
 * The main features of this implementation are: 1) small and embeddable 2) works for most every-day things and
 * 3) extensiblity: you can easily plugin resolvers.
 */
case class XP[T](val gp: GPath) {
  razie.Debug("building XP with GPath: " + gp.elements.mkString("/"))

  /** return the matching list - solve this path starting with the root and the given solving strategy */
  def xpl(ctx: XpSolver[T, Any], root: T): List[T] = {
    gp.requireNotAttr
    ixpl(ctx, root)
  }

  /** return the matching single element - solve this path starting with the root and the given solving strategy */
  def xpe(ctx: XpSolver[T, Any], root: T): T = xpl(ctx, root).head

  /** return the matching attribute - solve this path starting with the root and the given solving strategy */
  def xpa(ctx: XpSolver[T, Any], root: T): String = {
    gp.requireAttr
    ctx.getAttr(ixpl(ctx, root).head, gp.elements.last.name)
  }

  /** return the list of matching attributes - solve this path starting with the root and the given solving strategy */
  def xpla(ctx: XpSolver[T, Any], root: T): List[String] = {
    gp.requireAttr
    ixpl(ctx, root).map(ctx.getAttr(_, gp.elements.last.name))
  }

  /** if you'll keep using the same context there's no point dragging it around */
  def using(ctx: XpSolver[T, Any]) = new XPSolved(this, ctx)

  /** internal implementation - a simple fold */
  private def ixpl(ctx: XpSolver[T, Any], root: T): List[T] =
    if (gp.nonaelements.size == 0)
      root :: Nil
    else
      ctx.unwrap(
        if (gp.nonaelements.size == 1) {
          val c = ctx.children(root) // TODO this approach means we always introspec all to find one...
          val ret = ctx.reduce(List(c), gp.head).toList.map(_._1)
          // if unlucky, try children
          if (ret.size > 0) ret
          else solve(gp.head, ctx, List(c)).toList.map(_._1)
        } else for (
          e <- (if (gp.head.name == "**") gp.nonaelements else gp.exceptFirst).foldLeft(
            ctx.reduce(List(ctx.children(root)), gp.head).toList)((x, xe) => solve(xe, ctx, x).asInstanceOf[List[(T, Any)]])
        ) yield e._1)

  /** from res get the path and then reduce with condition looking for elements */
  private def solve(xe: XpElement, ctx: XpSolver[T, Any], res: List[(T, Any)]): List[(T, Any)] = {
    if ("**" == xe.name)
      ctx.reduce(recurseSS(xe, gp.afterSS, ctx, res), xe).toList
    else
      for (e <- res; x <- ctx.reduce(ctx.getNext(e, xe.name, xe.assoc), xe)) yield x
  }

  // must collect all possibilities recursively
  private def recurseSS(xe: XpElement, next: XpElement, ctx: XpSolver[T, Any], res: List[(T, Any)]): List[(T, Any)] = {
    val m = for (
      e <- res;
      x <- {
        if (!ctx.getNext(e, next.name, next.assoc).isEmpty) e :: Nil
        else recurseSS(xe, next, ctx, ctx.getNext(e, "*", "").toList)
      }
    ) yield x
    m
  }

  /* looking for an attribute */
  private def solvea(xe: XpElement, ctx: XpSolver[Any, Any], res: Any): String =
    ctx.getAttr(res, xe.name)
}

/** Example of creating a dedicated solver */
object XP {
  def forScala(xpath: String) = XP[scala.xml.Elem](xpath) using ScalaDomXpSolver
  def forString(xpath: String) = XP[String](xpath) using StringXpSolver
  def forBean(xpath: String) = XP[Any](xpath) using BeanXpSolver

  def apply[T](expr: String) = { new XP[T](GPath(expr)) }

  /** TODO make it private */
  def stareq(what: String, tag: String) =
    if ("*" == tag || "**" == tag) true
    else what == tag
}

/**
 * Simple helper to simplify client code when the context doesn't change:
 * it pairs an XP with a particular context/solver.
 *
 * So you can just use it after creation and not worry about carrying both arround.
 */
class XPSolved[T](val xp: XP[T], val ctx: XpSolver[T, Any]) {
  /** find one element */
  def xpe(root: T): T = xp.xpe(ctx, root)
  /** find a list of elements */
  def xpl(root: T): List[T] = xp.xpl(ctx, root)
  /** find one attribute */
  def xpa(root: T): String = xp.xpa(ctx, root)
  /** find a list of attributes */
  def xpla(root: T): List[String] = xp.xpla(ctx, root)
}

/** simple base class to decouple parsing the elements from their actual functionality */
case class GPath(val expr: String) {
  // list of parsed elements
  lazy val elements =
    for (e <- (expr split "/").filter(_ != "")) yield new XpElement(e)

  lazy val nonaelements = elements.filter(_.attr != "@")

  def head = nonaelements.head
  def exceptFirst = if (nonaelements.size > 0) nonaelements drop 1 else nonaelements

  lazy val startsFromRoot = expr.startsWith("/")

  def requireAttr =
    if (elements.last.attr != "@")
      throw new IllegalArgumentException("ERR_XP result should be attribute but it's an entity...")

  def requireNotAttr =
    if (elements.size > 0 && elements.last.attr == "@")
      throw new IllegalArgumentException("ERR_XP result should be entity but it's an attribute...")

  /** the first element after a ** */
  def afterSS: XpElement = elements(elements.indexWhere(_.name == "**") + 1)
}

/** overwrite this if you want other scriptables for conditions...it's just a syntax marker */
object XpCondFactory {
  def make(s: String) = if (s == null) null else new XpCond(s)
}

/**
 * the condition of an element in the path.
 *
 * TODO 3-2 maybe i should extract a trait and use it?
 *
 * this default implementation supports something like "[@attrname==15]"
 */
class XpCond(val expr: String) {
  // TODO 1-2 implement something better
  val parser = """\[[@]*(\w+)[ \t]*([=!~]+)[ \t]*[']*([^']*)[']*\]""".r
  val parser(a, eq, v) = expr

  /** returns all required attributes, in the AA format */
  def attributes: Iterable[String] = List(a)

  def passes[T](o: T, ctx: XpSolver[T, Any]): Boolean = {
    lazy val temp = ctx.getAttr(o, a)

    eq match {
      case "==" => v == temp
      case "!=" => v != temp
      case "~=" => temp.matches(v)
      case _ => throw new IllegalArgumentException("ERR_XPCOND operator unknown: " + eq + " in expr \"" + expr + "\"")
    }
  }

  override def toString = expr
}

/** the strategy to break down the input based on the current path element. The solving algorithm is: apply current sub-path to current sub-nodes, get the results and RESTs. Filter by conditions and recurse.  */
trait XpSolver[+A, +B] {
  /**
   * prepare to start from a node, figure out the continuations
   *
   * @param root the node we'll start resolving from
   * @return
   */
  def children[T >: A, U >: B](root: T): (T, U)

  /**
   * finally unwrap whatever and serve plain objects
   *
   * @param root the node we'll start resolving from
   * @return
   */
  def unwrap[T >: A](root: List[T]): List[T] = root

  /**
   * get the next list of nodes at the current position in the path.
   * For each, return a tuple with the respective value and the REST to continue solving
   *
   * @param curr the list of (currelement, continuation) to analyze
   * @return
   */
  def getNext[T >: A, U >: B](curr: (T, U), tag: String, assoc: String): Iterable[(T, U)]

  /**
   * get the value of an attribute from the given node
   *
   * @param curr the current element
   * @return the value, toString, of the attribute
   */
  def getAttr[T >: A](curr: T, attr: String): String

  /**
   * reduce the current set of possible nodes based on the given condition.
   * Note that the condition may be null - this is still called to give you a chance to cleanup?
   *
   * This default implementation may be ok for most resolvers
   *
   * @param curr the list of (currelement, continuation) to reduce
   * @param cond the condition to use for filtering - may be null if there's no condition at this point
   * @return
   */
  def reduce[T >: A, U >: B](curr: Iterable[(T, U)], xe: XpElement): Iterable[(T, U)] =
    xe.cond match {
      case null => curr.asInstanceOf[List[(T, U)]]
      case _ => curr.asInstanceOf[List[(T, U)]].filter(x => xe.cond.passes(x._1, this))
    }
}

/** an element in the path */
protected class XpElement(val expr: String) {
  val parser = """(\{.*\})*([@])*([\w]+\:)*([\$|\w]+|\**)(\[.*\])*""".r
  val parser(assoc_, attr, prefix, name, scond) = expr
  val cond = XpCondFactory.make(scond)

  def assoc = assoc_ match {
    // i don't know patterns very well this is plain ugly... :(
    case s: String => { val p = """\{(\w)\}""".r; val p(aa) = s; aa }
    case _ => assoc_
  }

  override def toString = List(assoc_, attr, prefix, name, scond) mkString ","
}

/** this example resolves strings with the /x/y/z format */
object StringXpSolver extends XpSolver[String, List[String]] {
  override def children[T >: String, U >: List[String]](root: T): (T, U) = (root, List(root).asInstanceOf[U])

  override def getNext[T >: String, U >: List[String]](o: (T, U), tag: String, assoc: String): Iterable[(T, U)] = {
    val pat = """/*(\w+)(/.*)*""".r
    val pat(result, next) = o._2.asInstanceOf[List[String]].head
    List((result, List(next)))
  }

  override def getAttr[T >: String](o: T, attr: String): String = attr match {
    case "name" => o.toString
    case _ => throw new UnsupportedOperationException("can't really get attrs from a str...")
  }

  // there is no solver in a string, eh?
  override def reduce[T >: String, U >: List[String]](o: Iterable[(T, U)], x: XpElement): Iterable[(T, U)] =
    o.filter { zz: ((T, U)) => XP.stareq(zz._1.asInstanceOf[String], x.name) }

}

/** this resolves dom trees*/

class DomXpSolver extends XpSolver[RazElement, List[RazElement]] {
  override def children[T >: RazElement, U >: List[RazElement]](root: T): (T, U) =
    (root, root.asInstanceOf[RazElement].children.asInstanceOf[U])

  override def getNext[T >: RazElement, U >: List[RazElement]](o: (T, U), tag: String, assoc: String): Iterable[(T, U)] = {
    val n = o._2.asInstanceOf[List[RazElement]] filter (zz => XP.stareq(zz.name, tag))
    for (e <- n) yield children(e)
  }

  override def getAttr[T >: RazElement](o: T, attr: String): String = o.asInstanceOf[RazElement] a attr

  //  override def reduce[T >: RazElement, U >: List[RazElement]](o: Iterable[(T, U)], cond: XpCond): Iterable[(T, U)] = 
  //    o.asInstanceOf[List[(T, U)]]
}

/** this resolves dom trees*/
object ScalaDomXpSolver extends XpSolver[scala.xml.Elem, List[scala.xml.Elem]] {
  /** TODO can't i optimize this? how do i inline it at least? */
  override def children[T >: scala.xml.Elem, U >: List[scala.xml.Elem]](root: T): (T, U) =
    (root, root.asInstanceOf[scala.xml.Elem].child.filter(_.isInstanceOf[scala.xml.Elem]).toList.asInstanceOf[U])

  override def getNext[T >: scala.xml.Elem, U >: List[scala.xml.Elem]](o: (T, U), tag: String, assoc: String): Iterable[(T, U)] =
    o._2.asInstanceOf[List[scala.xml.Elem]].filter(zz => XP.stareq(zz.label, tag)).map(x => children(x)).toList

  // return node values as well, if proper atribute not found...
  override def getAttr[T >: scala.xml.Elem](o: T, attr: String): String = {
    val ns = (o.asInstanceOf[scala.xml.Elem] \ ("@" + attr))
    if (!ns.isEmpty) ns.text
    else (o.asInstanceOf[scala.xml.Elem] \ attr) text
  }

  override def reduce[T >: scala.xml.Elem, U >: List[scala.xml.Elem]](curr: Iterable[(T, U)], xe: XpElement): Iterable[(T, U)] =
    (xe.cond match {
      case null => curr.asInstanceOf[List[(T, U)]]
      case _ => curr.asInstanceOf[List[(T, U)]].filter(x => xe.cond.passes(x._1, this))
    }).filter(gaga => XP.stareq(gaga._1.asInstanceOf[scala.xml.Elem].label, xe.name))
}

/**
 * reflection resolved for java/scala objects
 */
object BeanXpSolver extends MyBeanXpSolver

/**
 * reflection implementation
 *
 * @param excludeMatches - custom exclusion rules: nodes and attributes with these names won't be browsed
 */
class MyBeanXpSolver(val excludeMatches: List[String => Boolean] = Nil) extends XpSolver[AnyRef, (String, String) => List[AnyRef]] {
  trait LazyB { def eval: Any }
  abstract class BeanWrapper(val j: Any, val label: String = "root") extends LazyB {
    override def equals(other: Any) =
      other.isInstanceOf[BeanWrapper] && this.label == other.asInstanceOf[BeanWrapper].label
    override def toString = "BW(" + label + ")"
  }
  case class RootWrapper(override val j: Any, override val label: String = "root") extends BeanWrapper(j, label) with LazyB { override def eval: Any = j }
  case class FieldWrapper(override val j: Any, val f: Field, override val label: String = "root") extends BeanWrapper(j, label) with LazyB { override def eval: Any = f.get(j) }
  case class MethodWrapper(override val j: Any, val m: Method, override val label: String = "root") extends BeanWrapper(j, label) with LazyB {
    override def eval: Any = { if (debug) razie.Debug(3, "invoke: " + m); m.invoke(j) }
  }
  def WrapO(j: Any, label: String = "root") = new RootWrapper(j, label)
  def WrapF(j: Any, f: Field, label: String) = new FieldWrapper(j, f, label)
  def WrapM(j: Any, m: Method, label: String) = new MethodWrapper(j, m, label)

  var debug = true

  type CONT = (String, String) => List[BeanWrapper]

  override def children[T >: BeanWrapper, U >: CONT](root: T): (T, U) = {
    val r = root match {
      case r1: BeanWrapper => r1
      case _ => WrapO(root, "root")
    }
    (r, ((a: String, b: String) => resolve(r.j.asInstanceOf[AnyRef], a, b)).asInstanceOf[U])
  }

  import razie.Debug._

  override def getNext[T >: BeanWrapper, U >: CONT](o: (T, U), tag: String, assoc: String): List[(T, U)] = {
    if (debug) razie.Debug(3, "getNext " + tag)
    o._2.asInstanceOf[CONT].apply(tag, assoc).asInstanceOf[List[Any]].asInstanceOf[List[BeanWrapper]].
      filter(zz => XP.stareq(zz.asInstanceOf[BeanWrapper].label, tag)).teeIf(debug, 3, "before").
      flatMap(src => {
        val res = src.eval
        if (debug) println("DDDDDDDDDDDDD-" + res)
        for (y <- razie.MOLD(res))
          yield (WrapO(y.asInstanceOf[T], src.label), ((a: String, b: String) => resolve(y.asInstanceOf[AnyRef], a, b)).asInstanceOf[U])
      }).teeIf(debug, 3, "after").toList
  }

  override def getAttr[T >: BeanWrapper](o: T, attr: String): String = {
    val oo = if (o.isInstanceOf[BeanWrapper]) o else WrapO(o, "root")
    resolve(oo.asInstanceOf[BeanWrapper].eval.asInstanceOf[AnyRef], attr).head.eval.toString
  }

  override def reduce[T >: BeanWrapper, U >: CONT](curr: Iterable[(T, U)], xe: XpElement): Iterable[(T, U)] =
    (xe.cond match {
      case null => curr.asInstanceOf[List[(T, U)]]
      case _ => curr.asInstanceOf[List[(T, U)]].filter(x => xe.cond.passes(x._1, this))
    }).filter(gaga => XP.stareq(gaga._1.asInstanceOf[BeanWrapper].label, xe.name))

  override def unwrap[T >: BeanWrapper](root: List[T]): List[T] =
    (root map (_.asInstanceOf[BeanWrapper].j)).asInstanceOf[List[T]]

  // exclude all methods from Object and some others
  lazy val meth = resolve(new Object(), "*", "", false).map(x => (x.label, x.label)).toMap ++
    List("productArity", "productElements", "productPrefix", "productIterator").map(x => (x, x)).toMap

  // some exclusion rules
  val nomatch: List[String => Boolean] =
    { x: String => x.endsWith("$outer") } ::
      { x: String => x.equals("MODULE$") } ::
      { x: String => x.equals("hashCode") } ::
      // these is because the REPL, in Scripster, adds this to any case class: apply,class $line4.$read$$iw$$iw$C1
      // and generates Malformed class name
      //      { x: String => x.equals("apply") } :: 
      { x: String => x.equals("readResolve") } ::
      Nil

  // completely skip these classes
  val nogo: List[Any] = List(
    classOf[String], classOf[Int], classOf[Boolean], classOf[Float],
    classOf[Integer])

  // attr can be: field name, method name (with no args) or property name */
  private def resolve(o: AnyRef, attr: String, assoc: String = "", check: Boolean = true): List[BeanWrapper] = {
    if (debug) razie.Debug(3, "Resolving: " + attr + " from root: " + o)

    // introspection 
    def fields(crit: Field => Boolean) = o.getClass.getFields.filter(f => FILTER(f.getName)).filter(crit(_)).map(f => WrapO(f.get(o), f.getName()))
    def getters(crit: Method => Boolean) = o.getClass.getDeclaredMethods.filter(m =>
      m.getName.startsWith("get") &&
        m.getName != "getClass" &&
        FILTER(m.getName) &&
        m.getParameterTypes.isEmpty).filter(crit(_)).map(f => WrapM(o, f, fromZ(f.getName)))
    def scalas(crit: Method => Boolean) = o.getClass.getDeclaredMethods.filter(m =>
      m.getParameterTypes.size == 0 &&
        m.getReturnType.getName != "void" &&
        !m.getName.startsWith("get") &&
        FILTER(m.getName) &&
        m.getDeclaringClass() == o.getClass()).filter(crit(_)).map(f => WrapM(o, f, f.getName))

    def ALL[T](x: T) = true // filter all

    def FILTER(name: String) = {
      check &&
        !meth.contains(name) &&
        !nomatch.foldLeft(false)((x, f) => x || f(name)) &&
        !excludeMatches.foldLeft(false)((x, f) => x || f(name))
    }

    val result = if ("*" == attr) {
      // TODO restrict them by type or patter over type
      if (nogo.contains(o.getClass())) Nil
      else {
        // java getX scala x or member x while dropping duplicates
        (scalas(ALL) ++ fields(ALL) ++ getters(ALL)).map(
          p => (p.label, p)).toMap.values.toList
      }
    } else {
      // java getX scala x or member x by name
      val m: java.lang.reflect.Method = try {
        o.getClass.getMethod("get" + toZ(attr))
      } catch {
        case _ => try {
          o.getClass.getMethod(attr)
        } catch {
          case _ => null
        }
      }

      val result2 = try {
        if (m != null) List(WrapM(o, m, attr))
        //      if (m != null) m.invoke(o)
        else {
          val f = try {
            o.getClass.getField(attr)
          } catch {
            case _ => null
          }

          if (f != null) List(WrapO(f.get(o), attr))
          else Nil // TODO should probably log or debug?
        }
      } catch {
        case _ => Nil
      }

      if (result2.isEmpty) {
        // ===================== last chance - try by type
        def also(s: String) = (assoc == null || assoc.length <= 0 || assoc.equals(s))
        val s = scalas(m => attr == getSimpleName(m.getReturnType()) && also(m.getName()))
        val m = getters(m => attr == getSimpleName(m.getReturnType()) && also(m.getName()))
        val f = fields(f => attr == getSimpleName(f.getType()) && also(f.getName()))

        if (debug) {
          println("FFFFFields: " + o.getClass.getFields.map(f => (f.getName(), f.getType)).mkString("-"))
          println("FFFFMethods: " + o.getClass.getDeclaredMethods.filter(m =>
            m.getName.startsWith("get") &&
              m.getName != "getClass" &&
              m.getParameterTypes.isEmpty).map(f => (fromZ(f.getName), f.getReturnType)).mkString("-"))
          println("FFFFFScalas: " + o.getClass.getDeclaredMethods.filter(m =>
            m.getParameterTypes.size == 0 &&
              m.getReturnType.getName != "void" &&
              !m.getName.startsWith("get") &&
              m.getDeclaringClass() == o.getClass()).map(f => (f.getName, f.getReturnType)).mkString("-"))
        }

        def opt[T <: BeanWrapper](x1: => Seq[T]): Option[Seq[BeanWrapper]] = { val x = x1; if (x.isEmpty) None else Some(x) }

        opt(s).getOrElse(opt(m).getOrElse(opt(f).getOrElse(Nil))).collect(
          {
            case x1 @ RootWrapper(j, label) => RootWrapper(j, attr) // i'm using RootWrapper instead of FieldWrapper
            case FieldWrapper(j, f, l) => FieldWrapper(j, f, attr)
            case MethodWrapper(j, m, l) => MethodWrapper(j, m, attr)
          }).toList
      } else result2
    }

    if (debug) razie.Debug("resolved: " + result.mkString(","))
    result
  }

  private[this] def toZ(attr: String) = attr.substring(0, 1).toUpperCase + (if (attr.length > 1) attr.substring(1, attr.length - 1) else "")
  private[this] def fromZ(getter: String) = if (getter.length > 3) getter.substring(3).substring(0, 1).toLowerCase + (if (getter.length > 4) getter.substring(4, getter.length - 1) else "") else getter

  /** protect against scala interpreter screwy names, must hack this Class.getSimpleName method */
  private[this] def getSimpleName(c: java.lang.Class[_]): String = {
    var n = getSimpleClassName(c)
    // this is specifically for the interpreter - it adds this to some ofthe types can can't do beans by type in Scripster
    val REPL_PREFIX = "$read$$iw$$iw$"
    if (n.isEmpty()) n = c.getName.substring(c.getName.lastIndexOf(".") + 1); // strip the package name
    if (n.startsWith(REPL_PREFIX))
      n.substring(REPL_PREFIX.length())
    else n
  }
  /** protect against scala interpreter screwy names, must hack this Class.getSimpleName method */
  private[this] def getSimpleClassName(c: java.lang.Class[_]): String = {
    if (c.isArray())
      return getSimpleName(c.getComponentType()) + "[]";

    var simpleName = getSimpleBinaryName(c);
    if (simpleName == null) { // top level class
      simpleName = c.getName();
      return simpleName.substring(simpleName.lastIndexOf(".") + 1); // strip the package name
    }
    val length = simpleName.length();
    if (length < 1) // || simpleName.charAt(0) != '$')
      throw new InternalError("Malformed class name");
    var index = 1;
    while (index < length && isAsciiDigit(simpleName.charAt(index)))
      index = index + 1;
    // Eventually, this is the empty string iff this is an anonymous class
    return simpleName.substring(index);
  }

  private def getSimpleBinaryName(c: Class[_]): String = {
    val enclosingClass = c.getEnclosingClass();
    if (enclosingClass == null) // top level class
      return null;
    // Otherwise, strip the enclosing class' name
    try {
      return c.getName().substring(enclosingClass.getName().length());
    } catch {
      case ex: IndexOutOfBoundsException => throw new InternalError("Malformed class name");
    }
  }
  private def isAsciiDigit(c: Char) = {
    '0' <= c && c <= '9';
  }
}

// TODO 2-2 build a hierarchical context/solver structure - to rule the world. It would include registration

//class MyFailTypes {
//   def getAttr[T>:AnyRef] (o:T,attr:String) : String = {
//      resolve(o, attr).toString
//   }
//   
//   def resolve[T>:AnyRef] (o:T,attr:String) : Any = o.getClass.getField(attr) 
//}

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie

/** Example of creating a dedicated solver */
object XP {
   def forScala  (xpath:String) = XP[scala.xml.Elem] (xpath) using ScalaDomXpSolver
   def forString (xpath:String) = XP[String] (xpath) using StringXpSolver
   def forBean   (xpath:String) = XP[Any] (xpath) using BeanXpSolver
   
   def apply[T] (expr : String) = { new XP[T] (GPath (expr)) }
}

/** 
 * Simple helper to simplify client code when the context doesn't change: it pairs an XP with a particular context/solver.
 * 
 * So you can just use it after creation and not worry about carrying both arround. 
 */
class XPSolved [T] (val xp : XP[T], val ctx:XpSolver[T,Any]) {
   /** find one element */
   def xpe (root:T) : T = xp.xpe (ctx, root)
   /** find a list of elements */
   def xpl (root:T) : List[T] = xp.xpl (ctx, root)
   /** find one attribute */
   def xpa (root:T) : String = xp.xpa (ctx, root)
   /** find a list of attributes */
   def xpla (root:T) : List[String] = xp.xpla (ctx, root)
}

/** simple base class to decouple parsing the elements from their actual functionality */
case class GPath (val expr:String) {
  // list of parsed elements
  lazy val elements =  
    for (val e <- (expr split "/").filter(_!="")) 
      yield new XpElement(e)
  
  lazy val nonaelements = elements.filter(_.attr!="@")
   
  def requireAttr = 
    if (elements.last.attr != "@") 
      throw new IllegalArgumentException ("ERR_XP result should be attribute but it's an entity...")
   
  def requireNotAttr = 
    if (elements.size > 0 && elements.last.attr == "@") 
      throw new IllegalArgumentException ("ERR_XP result should be entity but it's an attribute...")
}

/** a simple resolver for x path like stuff 
 * 
* can resolve the following expressions
* 
* /a/b/c
* /a/b/@c
* /a/b[cond]/...
* /a/{assoc}b[cond]/...
* 
* It differs from a classic xpath by having the {assoc} option. Useful when 
* navigating models that use assocations as well as composition. Using 
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
* NOTE - this is stateless with respect to the parsed object tree - it only keeps the pre-compiled xpath expression so you should reuse them as much as possible
*/
case class XP[T] (val gp:GPath) {

   /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpl (ctx:XpSolver[T,Any], root:T) : List[T] = {
         gp.requireNotAttr
         ixpl(ctx, root)
      }

      /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpe (ctx:XpSolver[T,Any], root:T) : T = xpl (ctx, root).head
      
      /** return the matching attribute solve this path starting with the root and the given solving strategy */
      def xpa (ctx:XpSolver[T,Any], root:T) : String = {
         gp.requireAttr
         ctx.getAttr(ixpl(ctx,root).head, gp.elements.last.name)
      }
          
      /** return the list of matching attribute solve this path starting with the root and the given solving strategy */
      def xpla (ctx:XpSolver[T,Any], root:T) : List[String] = {
         gp.requireAttr
         ixpl(ctx,root).map (ctx.getAttr(_, gp.elements.last.name))
      }
          
   /** if you'll keep using the same context there's no point dragging it around */
   def using (ctx:XpSolver[T,Any]) = new XPSolved (this, ctx)

   /** return the matching list solve this path starting with the root and the given solving strategy */
   private def ixpl (ctx:XpSolver[T,Any], root:T) : List[T] = 
      for (e <- gp.nonaelements.foldLeft (List ((root,List(root).asInstanceOf[Any])) ) ( (x,xe) => solve(xe, ctx, x).asInstanceOf[List[(T,Any)]]) ) 
        yield e._1

   // -------------------------- these were in XPElement 
   
   /** from res get the path and then reduce with condition looking for elements */
   private def solve (xe:XpElement, ctx:XpSolver[T,Any], res:List[(T,Any)]):List[(T,Any)] = 
      for (e <- res; x <- ctx.reduce (ctx.getNext(e,xe.name,xe.assoc),xe.cond)) 
         yield x
   
   /* looking for an attribute */
   private def solvea (xe:XpElement, ctx:XpSolver[Any,Any], res:Any) : String = 
     ctx.getAttr(res,xe.name)

}

/** overwrite this if you want other scriptables for conditions...it's just a syntax marker */
object XpCondFactory {
   def make (s:String) = if (s == null) null else new XpCond(s)
}

/** the condition of an element in the path. 
 * 
 * TODO 3-2 maybe i should extract a trait and use it? 
 * 
 * this default implementation supports something like "[@attrname==15]"
 */
class XpCond (val expr:String) {
   // TODO 1-2 implement something better
   val parser = """\[[@]*(\w+)[ \t]*([=!~]+)[ \t]*[']*([^']*)[']*\]""".r
   val parser(a, eq, v) = expr
    
   /** returns all required attributes, in the AA format */
   def attributes : Iterable[String] = List(a)
   
   def passes[T] (o:T, ctx:XpSolver[T,Any]) : Boolean = {
      lazy val temp = ctx.getAttr(o, a)
      
      eq match {
         case "==" => v == temp
         case "!=" => v != temp
         case "~=" =>  temp.matches (v)
         case _ => throw new IllegalArgumentException ("ERR_XPCOND operator unknown: "+eq+" in expr \""+expr+"\"")
      }
   }
   
   override def toString = expr
}

/** the strategy to break down the input based on the current path element. The solving algorithm is: apply current sub-path to current sub-nodes, get the results and RESTs. Filter by conditions and recurse.  */
trait XpSolver[+A,+B] {
   /** get the next list of nodes at the current position in the path. 
    * For each, return a tuple with the respective value and the REST to continue solving 
    * 
    * @param curr the list of (currelement, continuation) to analyze
    * @return
    */
   def getNext[T>:A,U>:B](curr:(T,U),tag:String, assoc:String) : Iterable[(T,U)]

   /** get the value of an attribute from the given node 
    * 
    * @param curr the current element
    * @return the value, toString, of the attribute
    */
   def getAttr[T>:A](curr:T,attr:String) : String

   /** reduce the current set of possible nodes based on the given condition. 
    * Note that the condition may be null - this is still called to give you a chance to cleanup?
    * 
    * This default implementation may be ok for most resolvers 
    * 
    * @param curr the list of (currelement, continuation) to reduce
    * @param cond the condition to use for filtering - may be null if there's no condition at this point
    * @return
    */
   def reduce[T>:A,U>:B](curr:Iterable[(T,U)],cond:XpCond) : Iterable[(T,U)] =
      cond match {
         case null => curr.asInstanceOf[List[(T,U)]]
         case _ => curr.asInstanceOf[List[(T,U)]].filter(x => cond.passes(x._1, this))
      }
}

/** an element in the path */
protected class XpElement (val expr:String){
   val parser = """(\{.*\})*([@])*(\w+|\*)(\[.*\])*""".r
   val parser(assoc_, attr, name, scond) = expr
   val cond = XpCondFactory.make (scond)

   def assoc = assoc_ match {
         // i don't know patterns very well this is plain ugly... :(
         case s:String => { val p = """\{(\w)\}""".r; val p(aa) = s; aa}
         case _=> assoc_
      }
}

/** this example resolves strings with the /x/y/z format */
object StringXpSolver extends XpSolver[String,List[String]] {
  override def getNext[T>:String,U>:List[String]](o:(T,U),tag:String, assoc:String) : Iterable[(T,U)]={
    val pat = """/*(\w+)(/.*)*""".r
    val pat(result,next) = o._2.asInstanceOf[List[String]].head
    List((result,List(next)))
   } 
  
  override def getAttr[T>:String] (o:T,attr:String) : String = attr match {
     case "name" => o.toString
     case _ => throw new UnsupportedOperationException("can't really get attrs from a str...")
  }

  // there is no solver in a string, eh?
  override def reduce[T>:String,U>:List[String]] (o:Iterable[(T,U)],cond:XpCond) : Iterable[(T,U)] = 
    o
}

/** this resolves dom trees*/
//class DomXpSolver extends XpSolver[RazElement,List[RazElement]] {
//   override def getNext[T>:RazElement,U>:List[RazElement]] (o:(T,U),tag:String, assoc:String) : List[(T,U)]={
//      val n = o._1.asInstanceOf[RazElement] xpl tag
//      for (e <- n) yield (e,e.asInstanceOf[U])
//   } 
//
//   override def getAttr[T>:RazElement] (o:T,attr:String) : String = o.asInstanceOf[RazElement] a attr
//   override def reduce[T>:RazElement,U>:List[RazElement]] (o:List[(T,U)],cond:XpCond) : List[(T,U)] = o.asInstanceOf[List[(T,U)]]
//}

/** this resolves dom trees*/
object ScalaDomXpSolver extends XpSolver[scala.xml.Elem,List[scala.xml.Elem]] {
   override def getNext[T>:scala.xml.Elem,U>:List[scala.xml.Elem]] (o:(T,U),tag:String, assoc:String) : Iterable[(T,U)]=
      o._2.asInstanceOf[List[scala.xml.Elem]].filter(_.label==tag).map(x => { val t = (x.asInstanceOf[T],children(x).toList.asInstanceOf[U]); println ("t=" + t); t}).toList

   override def getAttr[T>:scala.xml.Elem] (o:T,attr:String) : String = 
	   (o.asInstanceOf[scala.xml.Elem] \ ("@"+attr)) text

   /** TODO can't i optimize this? how do i inline it at least? */
   private def children (e:scala.xml.Elem) = 
      e.child.filter(_.isInstanceOf[scala.xml.Elem])
}

/** reflection resolved for java/scala objects 
 * "/Student/@name" or "Students/getStudents */
object BeanXpSolver extends XpSolver[AnyRef,List[AnyRef]] {
   override def getNext[T>:AnyRef,U>:List[AnyRef]] (o:(T,U),tag:String, assoc:String) : List[(T,U)]={
      val n = resolve (o._1.asInstanceOf[AnyRef], tag)
      
      for (x <- razie.MOLD(n)) yield (x.asInstanceOf[T], List())
   }

   override def getAttr[T>:AnyRef] (o:T,attr:String) : String = {
      resolve(o.asInstanceOf[AnyRef], attr).toString
   }
   
//   override def reduce[T>:scala.xml.Elem,U>:List[scala.xml.Elem]] (o:Iterable[(T,U)],cond:XpCond) : Iterable[(T,U)] = 

   // attr can be: field name, method name (with no args) or property name */
   def resolve (o:AnyRef,attr:String) : Any = {
      // java getX scala x or member x
      val m : java.lang.reflect.Method = try {
         o.getClass.getMethod("get"+toZ(attr))
      } catch {
         case _ => try {
            o.getClass.getMethod(attr)
            } catch {
               case _ => null
            }
      }
      
      val result = try {
         if (m != null) m.invoke (o)
         else {
      val f = try {
         o.getClass.getField(attr)
      } catch {
         case _ => null
      }

            if (f != null) f.get(o)
         else null // TODO should probably log or debug?
         }
      } catch {
         case _ => null
      }
         
      result
   }
   
   private[this] def toZ (attr:String) = attr.substring(0,1).toUpperCase + (if (attr.length > 1) attr.substring (1, attr.length-1) else "")
}

// TODO 2-2 build a hierarchical context/solver structure - to rule the world. It would include registration

//class MyFailTypes {
//   def getAttr[T>:AnyRef] (o:T,attr:String) : String = {
//      resolve(o, attr).toString
//   }
//   
//   def resolve[T>:AnyRef] (o:T,attr:String) : Any = o.getClass.getField(attr) 
//}

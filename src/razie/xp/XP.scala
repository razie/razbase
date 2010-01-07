/*
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie.xp

/** Example of creating a dedicated solver */
object XP {
   def forScala (xpath:String) = 
      new XPSolved[scala.xml.Elem] (new XP[scala.xml.Elem] (xpath), new ScalaDomXqSolver) 
}

/** 
 * Simple helper to simplify client code when the context doesn't change: it pairs an XP with a particular context/solver.
 * 
 * So you can just use it after creation and not worry about carrying both arround. 
 */
class XPSolved [T] (val xp : XP[T], val ctx:XqSolver[T,Any]) {
   /** find one element */
   def xpe (root:T) : T = xp.xpe (ctx, root)
   /** find a list of elements */
   def xpl (root:T) : List[T] = xp.xpl (ctx, root)
   /** find one attribute */
   def xpa (root:T) : String = xp.xpa (ctx, root)
   /** find a list of attributes */
   def xpla (root:T) : List[String] = xp.xpla (ctx, root)
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
* <li> on Strings: new XP("/root").xpl(new StringXqSolver, "/root")
* <li> on scala xml: new XP[scala.xml.Elem] ("/root").xpl(new ScalaDomXqSolver, root) 
* </ul>
* 
* NOTE - this is stateless with respect to the parsed object tree - it only keeps the pre-compiled xpath expression so you should reuse them as much as possible
*/
class XP[T] (val expr:String){

   // list of parsed elements
   val elements =  
      for (val e <- (expr split "/").filter(_!="")) 
    	  yield new XqElement[T] (e)
   lazy val nonaelements = elements.filter(_.attr!="@")

   /** return the matching list solve this path starting with the root and the given solving strategy */
   private def ixpl (ctx:XqSolver[T,Any], root:T) : List[T] = 
      for (e <- nonaelements.foldLeft (List ((root,List(root).asInstanceOf[Any])) ) ( (x,y) => y.solve(ctx, x).asInstanceOf[List[(T,Any)]]) ) 
        yield e._1

   /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpl (ctx:XqSolver[T,Any], root:T) : List[T] = {
         requireNotAttr
         ixpl(ctx, root)
      }

      /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpe (ctx:XqSolver[T,Any], root:T) : T = xpl (ctx, root).head
      
      /** return the matching attribute solve this path starting with the root and the given solving strategy */
      def xpa (ctx:XqSolver[T,Any], root:T) : String = {
         requireAttr
         ctx.getAttr(ixpl(ctx,root).head, elements.last.name)
      }
          
      /** return the list of matching attribute solve this path starting with the root and the given solving strategy */
      def xpla (ctx:XqSolver[T,Any], root:T) : List[String] = {
         requireAttr
         ixpl(ctx,root).map (ctx.getAttr(_, elements.last.name))
      }
          
      def requireAttr = if (elements.last.attr != "@") throw new IllegalArgumentException ("ERR_XP result should be attribute but it's an entity...")
      def requireNotAttr = if (elements.last.attr == "@") throw new IllegalArgumentException ("ERR_XP result should be entity but it's an attribute...")
}

/** overwrite this if you want other scriptables for conditions...it's just a syntax marker */
object XqCondFactory {
   def make (s:String) = if (s == null) null else new XqCond(s)
}

/** the condition of an element in the path. 
 * 
 * TODO 3-2 maybe i should extract a trait and use it? 
 * 
 * this default implementation supports something like "[@attrname==15]"
 */
class XqCond (val expr:String) {
   // TODO 1-2 implement something better
   val parser = """\[[@]*(\w+)[ \t]*([=!~]+)[ \t]*[']*([^']*)[']*\]""".r
   val parser(a, eq, v) = expr
    
   /** returns all required attributes, in the AA format */
   def attributes : Iterable[String] = List(a)
   
   def passes[T] (o:T, ctx:XqSolver[T,Any]) : Boolean = {
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
trait XqSolver[+A,+B] {
   /** get the next list of nodes at the current position in the path. For each, return a tuple with the respective value and the REST to continue solving */
   def getNext[T>:A,U>:B](o:(T,U),tag:String, assoc:String) : Iterable[(T,U)]

   /** get the value of an attribute from the given node */
   def getAttr[T>:A](o:T,attr:String) : String

   /** reduce the current set of possible nodes based on the given condition. Note that the condition may be null - this is still called to give you a chance to cleanup?
    * 
    * @param o the list of (currelement, continuation) to reduce
    * @param cond the condition to use for filtering - may be null if there's no condition at this point
    * @return
    */
   def reduce[T>:A,U>:B](o:Iterable[(T,U)],cond:XqCond) : Iterable[(T,U)]
}

/** an element in the path */
protected class XqElement[T] (val expr:String){
   val parser = """(\{.*\})*([@])*(\w+)(\[.*\])*""".r
   val parser(assoc_, attr, name, scond) = expr
   val cond = XqCondFactory.make (scond)

   /** from res get the path and then reduce with condition looking for elements */
   def solve (ctx:XqSolver[T,Any], res:List[(T,Any)]):List[(T,Any)] = 
      for (e <- res; x <- ctx.reduce (ctx.getNext(e,name,assoc),cond)) 
         yield x
   
   /* looking for an attribute */
   def solvea (ctx:XqSolver[Any,Any], res:Any) : String = 
     ctx.getAttr(res,name)
     
   def assoc = assoc_ match {
         // i don't know patterns very well this is plain ugly... :(
         case s:String => { val p = """\{(\w)\}""".r; val p(aa) = s; aa}
         case _=> assoc_
      }
}

/** this example resolves strings with the /x/y/z format */
class StringXqSolver extends XqSolver[String,List[String]] {
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
  override def reduce[T>:String,U>:List[String]] (o:Iterable[(T,U)],cond:XqCond) : Iterable[(T,U)] = 
    o
}

/** this resolves dom trees*/
//class DomXqSolver extends XqSolver[RazElement,List[RazElement]] {
//   override def getNext[T>:RazElement,U>:List[RazElement]] (o:(T,U),tag:String, assoc:String) : List[(T,U)]={
//      val n = o._1.asInstanceOf[RazElement] xpl tag
//      for (e <- n) yield (e,e.asInstanceOf[U])
//   } 
//
//   override def getAttr[T>:RazElement] (o:T,attr:String) : String = o.asInstanceOf[RazElement] a attr
//   override def reduce[T>:RazElement,U>:List[RazElement]] (o:List[(T,U)],cond:XqCond) : List[(T,U)] = o.asInstanceOf[List[(T,U)]]
//}

/** this resolves dom trees*/
class ScalaDomXqSolver extends XqSolver[scala.xml.Elem,List[scala.xml.Elem]] {
   override def getNext[T>:scala.xml.Elem,U>:List[scala.xml.Elem]] (o:(T,U),tag:String, assoc:String) : Iterable[(T,U)]=
      o._2.asInstanceOf[List[scala.xml.Elem]].filter(_.label==tag).map(x => { val t = (x.asInstanceOf[T],children(x).toList.asInstanceOf[U]); println ("t=" + t); t}).toList

   /** TODO can't i optimize this? how do i inline it at least? */
   private def children (e:scala.xml.Elem) = 
      e.child.filter(_.isInstanceOf[scala.xml.Elem])
    
   override def getAttr[T>:scala.xml.Elem] (o:T,attr:String) : String = 
	   (o.asInstanceOf[scala.xml.Elem] \ ("@"+attr)) text

   override def reduce[T>:scala.xml.Elem,U>:List[scala.xml.Elem]] (o:Iterable[(T,U)],cond:XqCond) : Iterable[(T,U)] = 
      cond match {
         case null => o.asInstanceOf[List[(T,U)]]
         case _ => o.asInstanceOf[List[(T,U)]].filter(x => cond.passes(x._1, this))
   }
}

/** reflection resolved for java/scala objects */
//class BeanXqSolver extends XqSolver[AnyRef,List[RazElement]] {
//   override def getNext[T>:AnyRef,U>:List[AnyRef]] (o:(T,U),tag:String, assoc:String) : List[(T,U)]={
//      val n = o._1
//      n.getClass
//      for (e <- n) yield (e,e.asInstanceOf[U])
//   }
//
//   override def getAttr[T>:AnyRef] (o:T,attr:String) : String = o.asInstanceOf[RazElement] a attr
//   override def reduce[T>:AnyRef,U>:List[AnyRef]] (o:List[(T,U)],cond:XqCond) : List[(T,U)] = o.asInstanceOf[List[(T,U)]]
//}

// TODO 2-2 build a hierarchical context/solver structure - to rule the world. It would include registration


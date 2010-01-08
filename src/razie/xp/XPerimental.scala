/*
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie.xp

    
class Person {}
class SuperOrder {}

object People {
   def xpe (path:String) : Person = null
}

object Samples {
   val john = People xpe ("/Person[@areGroovy=='nah!']")
   val me = xpe ("/{hasFriends}Person[@areGroovy=='yeah, baby!']") from john
   val minime = XP[Person] ("/Person[@areGroovy=='yeah, baby!']")
}

trait applicator {
   def from[T] (root:T) (implicit m:scala.reflect.Manifest[T]) : T
//   def apply[T] = from(null)
}

case class xpe (expr:String) extends applicator {
    override def from [T] (root:T) (implicit m:scala.reflect.Manifest[T]) : T = (XP[T] (expr) using null) xpe (root)
//    override def apply [T] : T = from(null)
//    def apply [T] = XP[T] (expr) 
   
}



trait SuperMan_ager {
   def xpl (path:String) : List[SuperOrder]  = XP[SuperOrder] (path).xpl(null, null) 
   def openOrders () : List[SuperOrder]  = xpl ("/SuperOrder[@state=='Open']")
}




/** 
 * This is a simplified API so the calls are transparent
 */
trait GXPLike[T] {
   /** find one element */
   def xpe (root:T) : T
   /** find a list of elements */
   def xpl (root:T) : List[T]
   /** find one attribute */
   def xpa (root:T) : String
   /** find a list of attributes */
   def xpla (root:T) : List[String]
}

/** Example of creating a dedicated solver */
object GXP {
   def forScala (xpath:String) = 
      new XPSolved[scala.xml.Elem] (new XP[scala.xml.Elem] (xpath), new ScalaDomXpSolver) 
}

/** 
 * Simple helper to simplify client code when the context doesn't change: it pairs an XP with a particular context/solver.
 * 
 * So you can just use it after creation and not worry about carrying both arround. 
 */
class GXPSolved [T] (val xp : XP[T], val ctx:XpSolver[T,Any]) {
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
* <li> on Strings: new XP("/root").xpl(new StringXpSolver, "/root")
* <li> on scala xml: new XP[scala.xml.Elem] ("/root").xpl(new ScalaDomXpSolver, root) 
* </ul>
* 
* NOTE - this is stateless with respect to the parsed object tree - it only keeps the pre-compiled xpath expression so you should reuse them as much as possible
*/
class GXP[T] (val expr:String) {

   // list of parsed elements
   val elements =  
      for (val e <- (expr split "/").filter(_!="")) 
    	  yield new XpElement[T] (e)
   lazy val nonaelements = elements.filter(_.attr!="@")

   /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpl (ctx:XpSolver[T,Any], root:T) : List[T] = {
         requireNotAttr
         ixpl(ctx, root)
      }

      /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpe (ctx:XpSolver[T,Any], root:T) : T = xpl (ctx, root).head
      
      /** return the matching attribute solve this path starting with the root and the given solving strategy */
      def xpa (ctx:XpSolver[T,Any], root:T) : String = {
         requireAttr
         ctx.getAttr(ixpl(ctx,root).head, elements.last.name)
      }
          
      /** return the list of matching attribute solve this path starting with the root and the given solving strategy */
      def xpla (ctx:XpSolver[T,Any], root:T) : List[String] = {
         requireAttr
         ixpl(ctx,root).map (ctx.getAttr(_, elements.last.name))
      }
          
   /** return the matching list solve this path starting with the root and the given solving strategy */
   private def ixpl (ctx:XpSolver[T,Any], root:T) : List[T] = 
      for (e <- nonaelements.foldLeft (List ((root,List(root).asInstanceOf[Any])) ) ( (x,y) => y.solve(ctx, x).asInstanceOf[List[(T,Any)]]) ) 
        yield e._1

      def requireAttr = if (elements.last.attr != "@") throw new IllegalArgumentException ("ERR_XP result should be attribute but it's an entity...")
      def requireNotAttr = if (elements.last.attr == "@") throw new IllegalArgumentException ("ERR_XP result should be entity but it's an attribute...")
}

/** this is a "compiled" path expression - you can store it in this form */
case class gpath[T] (val expr:String) {

   // list of parsed elements
   val elements =  
      for (val e <- (expr split "/").filter(_!="")) 
        yield new XpElement[T] (e)
   lazy val nonaelements = elements.filter(_.attr!="@")

}

class GGPath[T] (val p:gpath[T]) {

//   def this (expr:String) = this (gpath(expr))

   /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpl (ctx:XpSolver[T,Any], root:T) : List[T] = {
         requireNotAttr
         ixpl(ctx, root)
      }

      /** return the matching list solve this path starting with the root and the given solving strategy */
      def xpe (ctx:XpSolver[T,Any], root:T) : T = xpl (ctx, root).head
      
      /** return the matching attribute solve this path starting with the root and the given solving strategy */
      def xpa (ctx:XpSolver[T,Any], root:T) : String = {
         requireAttr
         ctx.getAttr(ixpl(ctx,root).head, p.elements.last.name)
      }
          
      /** return the list of matching attribute solve this path starting with the root and the given solving strategy */
      def xpla (ctx:XpSolver[T,Any], root:T) : List[String] = {
         requireAttr
         ixpl(ctx,root).map (ctx.getAttr(_, p.elements.last.name))
      }
          
   /** return the matching list solve this path starting with the root and the given solving strategy */
   private def ixpl (ctx:XpSolver[T,Any], root:T) : List[T] = 
      for (e <- p.nonaelements.foldLeft (List ((root,List(root).asInstanceOf[Any])) ) ( (x,y) => y.solve(ctx, x).asInstanceOf[List[(T,Any)]]) ) 
        yield e._1

      def requireAttr = if (p.elements.last.attr != "@") throw new IllegalArgumentException ("ERR_XP result should be attribute but it's an entity...")
      def requireNotAttr = if (p.elements.last.attr == "@") throw new IllegalArgumentException ("ERR_XP result should be entity but it's an attribute...")
}

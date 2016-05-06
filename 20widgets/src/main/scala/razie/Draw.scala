/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie

import razie.draw._
import razie.draw.widgets._
import razie.base._

//// TODO when Drawable3 looses this stupid method, drop this class (or move Drawable3 here)
//trait Drawable extends razie.draw.Drawable3 {
//   override def getRenderer(t:Technology) : Renderer[_] = Drawable.DefaultRenderer.singleton
//}

/** condense most useful drawing primitives */
object Draw {
   import scala.collection.JavaConversions._
         
   /** put some objects in a sequence - sequence is not formatted */
   def seq (stuff:Any*) = {
      val s = new DrawSequence()
      stuff.foreach (x => s.write(x))
      s
   }
  
   /** put some objects in a list - lists are html table */
   def list (stuff:Any*) = hlist (stuff:_*)
   def hlist (stuff:Any*) = {
      val s = new DrawList()
      stuff.foreach {
         case l:List[Any] => for (x <- l) s.write(x)
         case i:Iterator[Any] => for (x <- i) s.write(x)
         case t:Iterable[Any] => for (x <- t) s.write(x)
         case a:Array[Any] => for (x <- a) s.write(x)
         case z@_ => s.write(z)  
      }
      s
   }
   
   def vlist (stuff:Any*) =  hlist(stuff:_*) vertical true

   def table : DrawTableScala = table (-1)()
   def table (cols:Int)(stuff:Any*) : DrawTableScala = {
      val s = layoutTable (cols)(stuff)
      s.rowColor = "#292929"
      s
   }
   def layoutTable : DrawTableScala = layoutTable (-1)()
   def layoutTable (cols:Int)(stuff:Any*) : DrawTableScala = {
      val s = new DrawTableScala()
      s.cols(cols)
      stuff.foreach {
         case l:List[Any] => for (x <- l) s.write(x)
         case i:Iterator[Any] => for (x <- i) s.write(x)
         case t:Iterable[Any] => for (x <- t) s.write(x)
         case a:Array[Any] => for (x <- a) s.write(x)
         case z@_ => s.write(z)  
      }
      s
   }
   
   /** make a link - it displays a different ActionItem than the actual link to call */
   def link (ai:ActionItem, ati:ActionToInvoke) = new NavLink (ai, ati)
   def link (ati:ActionToInvoke) = new NavLink (ati)
   def link (ai:ActionItem, url:String) = new NavLink (ai, url)
   /** make a button - it displays a different ActionItem than the actual link to call */
   def button (ai:ActionItem, ati:ActionToInvoke) = new NavButton (ai, ati)
   def button (ati:ActionToInvoke) = new NavButton (ati)
   def button (ai:ActionItem, url:String) = new NavButton (ai, url)
   def button (ai:ActionItem) (f: => Unit) = new NavButton (new FActionToInvoke(ai, f))
   /** make a button - it displays a different ActionItem than the actual link to call */
   def cmd (ai:ActionItem, ati:ActionToInvoke) = new SimpleButton (ai, ati)
   def cmd (ai:ActionItem, url:String) = new SimpleButton (ai, url)
   def cmd (ai:ActionItem) (f: => Unit) = new SimpleButton (new FActionToInvoke(ai, f))

   // ------------ these are label style
   def label (s:String) = new DrawText (s)
   // text, formatted accordingly, escaped and everything
   def text (s:String) = new DrawText (s)
   // xml - in the future we'll find a nice colored formatter
   def xml (s:String) = new DrawText (s)
   // html section - best avoided since only works nice with some clients (web)
   def html (s:String) = new DrawToString (s)
   // any object to String - no other html formatting
   def toString (s:Any) = new DrawToString (s)
   def hBar (lab:String) = new razie.draw.widgets.HorizBar(lab)
   def hBar = new razie.draw.widgets.HorizBar("")
   
   // ------------ these are memo style
   
   // memo, formatted accordingly, escaped and everything
   def memo (s:String) = new DrawTextMemo (s)
   // html section - best avoided since only works nice with some clients (web)
   def htmlMemo (s:String) = new DrawToStringMemo (s)

   /** make sure the first item has all the attributes - it will dictate the columns */
   def attrTable (o:Iterable[AttrAccess]) : DrawTable = {
      // the headings are the name of the attributes
     import scala.collection.JavaConverters._
      val whatamess:Iterable[String]= /*scala.collection.JavaConverters.iterableAsScalaIterable(*/o.head.getPopulatedAttr
      val t = table(whatamess.size) ()
      t.headers = whatamess

      for (x <- o; a <- t.headers) t.write (x.a(a))
             
      t
   }
   
   /** make sure the first item has all the attributes - it will dictate the columns */
   def attrList (o:AttrAccess) : DrawTable = {
      val t = table(2)().align (Align.LEFT)
      // the headings are the name of the attributes
      t.headers = List("name", "value")

      for (x <- o.getPopulatedAttr) { t write x; t write o.a(x)}
             
      t
   }
   
   def div (name:String, d:Drawable) = new DrawDiv (name, d)
   
   def form (name:ActionItem, ati:ActionToInvoke, parms:AttrAccess) = new DrawForm (name, ati, parms)
   def form (name:ActionItem, ati:ActionToInvoke, parms:AnyRef*) = new DrawForm (name, ati, razie.AA(parms:_*))
   def form (name:ActionItem, parms:AnyRef*) (f: AttrAccess => Unit) = 
      new DrawForm (name, new FActionToInvoke2(name, f), razie.AA(parms:_*))
   
   def error (msg:String, t:Throwable=null) = new DrawError (msg, t)
}
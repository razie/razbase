/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.base;

/**
 * this is a local f() action
 * 
 * TODO 1-2 make it usable in web apps - cache callback service
 * 
 * @author razvanc99
 */
class FActionToInvoke (ai:ActionItem, f: => Unit) 
   extends BaseActionToInvoke ("", ai) {
  
   // TODO this is implemented just cause i'm lazy and reused NavLink from NavButton...
     override def makeActionUrl() : String = ("TODO use remote...")
     override def act(ctx:ActionContext ) : AnyRef = { f; "" }
     
    override def args(pairs:AnyRef*) = new FActionToInvoke (ai, f);
}

/**
 * this is a local f() action
 * 
 * TODO 1-2 make it usable in web apps - cache callback service
 * 
 * @author razvanc99
 */
class FActionToInvoke2 (ai:ActionItem, f: AttrAccess => Unit) 
   extends BaseActionToInvoke ("", ai) {
  
   // TODO this is implemented just cause i'm lazy and reused NavLink from NavButton...
     override def makeActionUrl() : String = ("TODO use remote...")
     override def act(ctx:ActionContext ) : AnyRef = { f (this); "" }
     
    override def args(pairs:AnyRef*) = 
       { val x =new FActionToInvoke2 (ai, f); x.setAttr(pairs:_*); x}
}

/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package razie.base.scripting

import razie.base.ActionContext;

/**
 * actions execute in a context of objects available at that time in that environment.
 * 
 * a script context is more complex than just AttributeAccess, it may include a hierarchy of
 * contexts, hardcode mappings etc. This class may go away and be replaced with the jdk1.6
 * scriptables.
 * 
 * it's used to run activities and scripts $
 * 
 * You can define functions, which are evaluated every time
 * 
 * @author razvanc99
 * deprecated move code to ActContext
 */
trait ScriptContext extends ActionContext {
   /**
    * deine a new function - these are evaluated every time they are invoked. these also overwrite
    * another symbol, so you can redefine a symbol to do something else.
    * 
    * DO NOT forget to seal a context before passing it to untrusted plugins
    */
   def define(fun:String , expr:String )

   /** remove a function */
   def undefine(macro:String )

   /** TODO 3 FUNC use guards, document */
   def guard(name:String , condition:String , expr:String )

   /** TODO 3 FUNC use guards, document */
   def unguard(name:String , condition:String , expr:String )

   /** TODO remove */
   def xscrewscala28(name:String , v:Object )

   /** make execution verbose or not */
   def verbose(v:Boolean )

   /** content assist options */
   def options (script:String ) : java.util.List[String]
  
   /** the last error message - NOT reset after each...*/
   def lastError : String
}

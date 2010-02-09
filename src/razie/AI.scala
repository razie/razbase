/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

import razie.base._

/** simplified ActionItem 
 * 
 * i really should drop the stupid java base classes and move it all to scala 
 */
object AI {
   implicit def stoai (s:String) = new AI (s, s, s)
   
   def apply (cmd:String, label:String=null, tooltip:String=null) = new AI (cmd,label, tooltip)
   def cmdicon (cmd:String, icon:String=null) = new AI (cmd,cmd, cmd, icon)
}

/** simplified ActionItem 
 * 
 *  i really should drop the stupid java base classes and move it all to scala 
 */
class AI (
      cmd:String, label:String, tooltip:String, iconP:String=razie.Icons.UNKNOWN.name) 
   extends ActionItem (cmd, iconP, (if (label==null)cmd else label), (if(tooltip==null)cmd else tooltip)) {
}

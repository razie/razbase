/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

import com.razie.pub.base.{log => pblog}

/** some logging basics 
 * 
 * @author razvanc
 */
trait Log {
   def trace (f : => Any) 
   def log   (msg:String, e:Throwable=null)
   def alarm (msg:String, e:Throwable=null)
   def audit (msg:String, t:Throwable=null)
   
   def apply (msg:String, e:Throwable=null) = log (msg, e)
} 

/** some logging basics 
 * 
 * @author razvanc
 */
object Log extends Log {
   override def trace (f : => Any) = traceThis (f) 
   override def log   (msg:String, t:Throwable=null) = logThis (msg, t)
   override def alarm   (msg:String, t:Throwable=null) = alarmThis (msg, t)
   override def audit   (msg:String, t:Throwable=null) = auditThis (msg, t)

   def auditThis   (msg:String, t:Throwable=null) =
      if (t == null)
         pblog.Log.audit (msg)
      else
         pblog.Log.audit (msg, t)

   def logThis (msg:String) = pblog.Log.logThis (msg)
   def apply   (msg:String) = pblog.Log.logThis (msg)
   def logThis (msg:String, e:Throwable) = pblog.Log.logThis (msg, e)
   
   /** @return the same message, so you can return it */
   def alarmThis (msg:String) = {
      pblog.Log.alarmThis (msg) 
      msg
	   }
	   
   /** @return the same message, so you can return it */
   def alarmThis (msg:String, e:Throwable) = {
      pblog.Log.alarmThis (msg, e) 
      msg
	   }

   /** optimized so the code is not even invoked if tracing is off ... don't suppose this will cause side-effects? */
   def traceThis (f : => Any) = {
      if (pblog.Log.isTraceOn()) {
         val p = f
         p match {
            case s:String => pblog.Log.traceThis (s) 
            case (s:String,e:Throwable) => pblog.Log.traceThis (s,e) 
            case _ => pblog.Log.traceThis (p.toString)
         }
      }
   }
}

object Debug {
   def apply (f : => Any) = Log.traceThis (f)
}

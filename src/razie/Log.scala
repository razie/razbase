/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie
 import com.razie.pub.base.{log => pblog}

/** 
 * some logging basics 
 * 
 * You can customize the entire logging system by providing an implementation for 
 * com.razie.pub.base.log.Factory and set that: com.razie.pub.base.log.Log.factory = new MyFactory().
 * 
 * See example in razpub:com.razie.pub.base.log.Log4j
 * 
 * @author razvanc
 */
trait Log {
   def trace (f : => Any) 
   def log   (msg:String, e:Throwable=null)
   def alarm (msg:String, e:Throwable=null) // implicit audit
   def audit (msg:String, t:Throwable=null)
   def error (msg:String, t:Throwable=null) // alarm and throw
   
   def apply (msg:String, e:Throwable=null) = log (msg, e)
} 

class RaziepbLog extends Log {
   override def trace (f : => Any) = pblog.Log.traceThis ({val x = f; x.toString}) 
   override def log   (msg:String, t:Throwable=null) = pblog.Log.logThis (msg, t)
   override def alarm   (msg:String, t:Throwable=null) = pblog.Log.alarmThis (msg, t)
   override def audit   (msg:String, t:Throwable=null) = pblog.Log.logThis ("AUDIT:"+msg, t)
   override def error   (msg:String, t:Throwable=null) = pblog.Log.alarmThisAndThrow(msg, t)
}

class StupidLog extends Log {
   override def trace (f : => Any) = println ("DEBUG: " + f) 
   override def log   (msg:String, t:Throwable=null) = println ("LOG: " + msg + t)
   override def alarm   (msg:String, t:Throwable=null) = println ("ALARM: " + msg, t)
   override def audit   (msg:String, t:Throwable=null) = println ("AUDIT: " + msg, t)
   override def error   (msg:String, t:Throwable=null) = { println(msg, t); throw t }
}

/** some logging basics 
 * 
 * @author razvanc
 */
object Log extends Log {
   // overwrite/change this to use different logging mechanism
   var impl = new RaziepbLog()
   
   override def trace (f : => Any) = impl.trace(f)
   override def log   (msg:String, t:Throwable=null) = impl.log(msg, t)
   override def alarm   (msg:String, t:Throwable=null) = impl.alarm(msg, t)
   override def audit   (msg:String, t:Throwable=null) = impl.audit(msg, t)
   override def error   (msg:String, t:Throwable=null) = impl.error(msg, t)

   def auditThis   (msg:String, t:Throwable=null) =
      if (t == null)
         pblog.Log.audit (msg)
      else
         pblog.Log.audit (msg, t)

   def logThis (msg:String) = log (msg)
   def apply   (msg:String) = log (msg)
   def logThis (msg:String, e:Throwable) = log(msg, e)
   
   /** @return the same message, so you can return it */
   def alarmThis (msg:String) = {
      impl.alarm(msg) 
      msg
	   }
	   
   /** @return the same message, so you can return it */
   def alarmThis (msg:String, e:Throwable) = {
      impl.alarm (msg, e) 
      msg
	   }

   /** optimized so the code is not even invoked if tracing is off ... don't suppose this will cause side-effects? */
   def traceThis (f : => Any) = {
      if (pblog.Log.isTraceOn()) {
         val p = f
         p match {
            case s:String => pblog.Log.traceThis (s) 
            case (s:String,e:Throwable) => pblog.Log.traceThis (s,e) 
            case _ => impl.trace(p.toString)
         }
      }
   }
}

object Debug {
   def apply (f : => Any) = Log.traceThis (f)
}

object Audit {
   def apply (f : => Any) = Log.audit (f.toString)
}

object Alarm {
   def apply (f : => Any) = Log.alarm (f.toString)
}

object Error {
   def apply (f : => Any) = Log.error (f.toString)
}

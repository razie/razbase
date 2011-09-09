/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie

import com.razie.pub.base.{ log => pblog }

/** 
 * some logging basics 
 * 
 * read more in com.razie.pub.base.log._
 * 
 * @author razvanc
 */
trait Log {
  def trace(f: => Any)
  def log(msg: String, e: Throwable = null)
  def alarm(msg: String, e: Throwable = null) // implicit audit
  def audit(msg: String, t: Throwable = null)
  def error(msg: String, t: Throwable = null) // alarm and throw

  def apply(msg: String, e: Throwable = null) = log (msg, e)

  /** set debuggin on or off */
  def silent (d:Boolean)
}

class PbLog(component: String, category: String) extends Log {
  val pbl = pblog.Log.factory.create(component, category);

  def trace(level: Int)(f: => Any) =
    if (pbl.isTraceLevel(level)) pbl.trace(level, { val x = f; x.toString })
  override def trace(f: => Any) =
    if (pbl.isTraceLevel(1)) pbl.trace(1, { val x = f; x.toString })
  override def log(msg: String, t: Throwable = null) = pbl.log(msg, t)
  override def alarm(msg: String, t: Throwable = null) = pbl.alarm(msg, t)
  override def audit(msg: String, t: Throwable = null) = pbl.log("AUDIT: " + msg, t)
  override def error(msg: String, t: Throwable = null) = pblog.Log.alarmThisAndThrow(msg, t)
  
  def silent (d:Boolean) { pblog.Log.SILENT = d }
}

class RaziepbLog extends Log {
  override def trace(f: => Any) = pblog.Log.traceThis ({ val x = f; x.toString })
  override def log(msg: String, t: Throwable = null) = pblog.Log.logThis (msg, t)
  override def alarm(msg: String, t: Throwable = null) = pblog.Log.alarmThis (msg, t)
  override def audit(msg: String, t: Throwable = null) = pblog.Log.logThis ("AUDIT: " + msg, t)
  override def error(msg: String, t: Throwable = null) = pblog.Log.alarmThisAndThrow(msg, t)
  def silent (d:Boolean) { pblog.Log.SILENT = d}
}

class StupidLog extends Log {
  private def th = Thread.currentThread.getName + " "
  override def trace(f: => Any) = println ("DEBUG: " + th + f)
  override def log(msg: String, t: Throwable = null) = println ("LOG: " + th + msg + t)
  override def alarm(msg: String, t: Throwable = null) = println ("ALARM: " + th + msg, t)
  override def audit(msg: String, t: Throwable = null) = println ("AUDIT: " + th + msg, t)
  override def error(msg: String, t: Throwable = null) = { println("ERROR: " + th + msg, t); throw t }
  def silent (d:Boolean) { }
}

class SILENTLOG extends Log {
  private def th = Thread.currentThread.getName + " "
  override def trace(f: => Any) = {}
  override def log(msg: String, t: Throwable = null) = {}
  override def alarm(msg: String, t: Throwable = null) = println ("ALARM: " + th + msg, t)
  override def audit(msg: String, t: Throwable = null) = println ("AUDIT: " + th + msg, t)
  override def error(msg: String, t: Throwable = null) = { println("ERROR: " + th + msg, t); throw t }
  def silent (d:Boolean) { }
}

// TODO WTF - I can't use the Log object directly WTF...F..F..F..
object NewLog {

  val fuckers = 3

  // respect the Log.factory.create signature
  val factory = new Object {
    def create(component: String, category: String) = new PbLog(component, category)
  }
  def create(component: String, category: String) = new PbLog(component, category)

}

/** some logging basics 
 * 
 * @author razvanc
 */
object Log extends Log {
  // overwrite/change this to use different logging mechanism
  var impl : Log = new RaziepbLog()
 
  def silent (d:Boolean) { impl.silent(d)}

  val fuckers = 3

  // respect the Log.factory.create signature
  var factory = new Object {
    def create(component: String, category: String) = new PbLog(component, category)
  }
  def create(component: String, category: String) = new PbLog(component, category)

  override def trace(f: => Any) = impl.trace(f)
  override def log(msg: String, t: Throwable = null) = impl.log(msg, t)
  override def alarm(msg: String, t: Throwable = null) = impl.alarm(msg, t)
  override def audit(msg: String, t: Throwable = null) = impl.audit(msg, t)
  override def error(msg: String, t: Throwable = null) = impl.error(msg, t)

  def auditThis(msg: String, t: Throwable = null) =
    if (t == null)
      pblog.Log.audit (msg)
    else
      pblog.Log.audit (msg, t)

  def logThis(msg: String) = log (msg)
  def apply(msg: String) = log (msg)
  def logThis(msg: String, e: Throwable) = log(msg, e)

  /** @return the same message, so you can return it */
  def alarmThis(msg: String) = {
    impl.alarm(msg)
    msg
  }

  /** @return the same message, so you can return it */
  def alarmThis(msg: String, e: Throwable) = {
    impl.alarm (msg, e)
    msg
  }

  /** optimized so the code is not even invoked if tracing is off ... don't suppose this will cause side-effects? */
  def traceThis(f: => Any) = {
    if (pblog.Log.isTraceOn()) {
      val p = f
      p match {
        case s: String => pblog.Log.traceThis (s)
        case (s: String, e: Throwable) => pblog.Log.traceThis (s, e)
        case _ => impl.trace(p.toString)
      }
    }
  }

}

object Audit {
  def apply(f: => Any) = Log.audit (f.toString)
}

object Debug {
  def apply(f: => Any) = Log.trace(f)
  
  implicit def toTee[T](l: Seq[T]): TeeSeq[T] = new TeeSeq[T](l)
  class TeeSeq[T](l: Seq[T]) {
    def tee: Seq[T] = {
      razie.Debug("TEE- " + l.mkString(", "))
      l
    }
    def tee(level: Int, prefix: String): Seq[T] = {
      razie.Debug("TEE-" + prefix + " - " + l.mkString(", "))
      l
    }
    def teeIf(should:Boolean, level: Int, prefix: String): Seq[T] = {
      if (should) razie.Debug("TEE-" + prefix + " - " + l.mkString(", "))
      l
    }
  }
}

object Warn {
  def apply(f: => Any) = Log.alarm (f.toString)
}

object Alarm {
  def apply(f: => Any) = Log.alarm (f.toString)
}

object Error {
  def apply(f: => Any) = Log.error (f.toString)
}


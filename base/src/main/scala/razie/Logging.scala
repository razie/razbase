package razie

import org.fusesource.scalate.{ util => sfu }

/** A Logging trait you can mix into an implementation class without affecting its public API
 *
 *  NOTE: IF you get an anon class name, you can override the logger:
 *  protected override val logger = newlog (classOf)
 *
 *  NOTE: the formatting uses java.lang.String.format NOT slf4j formatting by reason of clusterfuck
 */
trait Logging {

  protected val logger = sfu.Log(getClass)

  protected def newlog(clazz: Class[_]) = sfu.Log(clazz)
  protected def newlog(s:String) = sfu.Log(s)

  /** use this if you want to log with slf4j conventions instead of the formatting conventions implemented here (String.format).
   *
   *  Printf rules!
   */
  protected def slf4j: org.slf4j.Logger = logger.log

  @inline protected def tee(message: => String): String = { val m = message; logger.trace(m); m}
  
  @inline protected def error(message: => String): Unit = logger.error(message)
  @inline protected def error(message: => String, e: Throwable): Unit = logger.error(e, message)

  @inline protected def warn(message: => String): Unit = logger.warn(message)
  @inline protected def warn(message: => String, e: Throwable): Unit = logger.warn(e, message)

  @inline protected def info(message: => String): Unit = logger.info(message)
  @inline protected def info(message: => String, e: Throwable): Unit = logger.info(e, message)
  @inline protected def log(message: => String): Unit = logger.info(message)
  @inline protected def log(message: => String, e: Throwable): Unit = logger.info(e, message)
  
  // TODO audit shoudl go in log no matter what
  @inline protected def audit(message: => String): Unit = logger.info("AUDIT "+message)
  @inline protected def audit(message: => String, e: Throwable): Unit = logger.info(e, "AUDIT "+message)

  @inline protected def debug(message: => String): Unit = logger.debug(message)
  @inline protected def debug(message: => String, e: Throwable): Unit = logger.debug(e, message)

  @inline protected def trace(message: => String): Unit = logger.trace(message)
  @inline protected def trace(message: => String, e: Throwable): Unit = logger.trace(e, message)

  // TODO i won't expose these most likely - just hate to loose
  
  //  @inline protected def error(m: => String, args:Any*): Unit = logger.error (m, args:_*)
  //  @inline protected def error(e: Throwable, m: => String, args:Any*): Unit = logger.error (e, m, args:_*)
  //  @inline protected def error(m: => String, e: Throwable): Unit = logger.error (e, m)
  //
  //  @inline protected def warn(m: => String, args:Any*): Unit = logger.warn (m, args:_*)
  //  @inline protected def warn(e: Throwable, m: => String, args:Any*): Unit = logger.warn (e, m, args:_*)
  //
  //  @inline protected def log(m: => String, args:Any*): Unit = logger.info (m, args:_*)
  //  @inline protected def log(e: Throwable, m: => String, args:Any*): Unit = logger.info (e, m, args:_*)
  //  @inline protected def log(m: => String, e: Throwable): Unit = logger.info (e, m)
  //  @inline protected def info(m: => String, args:Any*): Unit = logger.info (m, args:_*)
  //  @inline protected def info(e: Throwable, m: => String, args:Any*): Unit = logger.info (e, m, args:_*)
  //
  //  @inline protected def debug(m: => String, args:Any*): Unit = logger.debug (m, args:_*)
  //  @inline protected def debug(e: Throwable, m: => String, args:Any*): Unit = logger.debug (e, m, args:_*)
  //
  //  @inline protected def trace(m: => String, args:Any*): Unit = logger.trace (m, args:_*)
  //  @inline protected def trace(e: Throwable, m: => String, args:Any*): Unit = logger.trace (e, m, args:_*)
  //  @inline protected def trace(m: => String, e: Throwable): Unit = logger.trace (e, m)

}


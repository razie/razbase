/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie

import com.razie.pub.base.ExecutionContext
import com.razie.pub.base.NoStatics

/**
 * thread local static object - ThreadLocal is easy to implement, not neccessarily to use
 * 
 * Note this is not quite per-thread only...it is per ExecutionContext, so there can be multiple "statics" per JVM - see that class
 * 
 * Use like
 * <code> public static NoStatic<MyClass> myStatic = new NoStatic<MyClass>("myStatic", new MyClass(...))</code>
 * .
 * 
 * On a thread, reset the default value if needed:
 * <code>myStatic.set(newValue)</code>
 * 
 * In code, access like a static, don't worry about thread:
 * <code>myStatic.get</code> or <code>myStatic()</code>
 * 
 * see TLNoStatic for why ThreadLocal is not usable
 * 
 * @mtsafe
 * @see com.razie.pub.base.test.TestNoStatic
 * @see com.razie.pub.base.ExecutionContext
 * @author razvanc99
 */
class NoStatic[T >: Null <: AnyRef](val id: String, initialValue: => T) {
  private var _value: T = null

  private def value = if (_value == null) this.synchronized {
    // classic anti-pattern mostly working easy-peasy
    if (_value == null) set (initialValue) else _value
  }

  def get = thisThreads.value // TODO read a pointer is atomic, right?
  def apply() = get

  /**
   * here's the tricky part... will set only on the particular thread ... IF
   * it has a context...
   */
  def set(newValue: T): T = this.synchronized { thisThreads._value = newValue; newValue }

  private def thisThreads: NoStatic[T] = {
    val tx = ExecutionContext.instance();

    if (tx.isPopulated(id)) {
      (ExecutionContext.instance().getAttr(id)).asInstanceOf[NoStatic[T]]
    } else tx.synchronized {
      // classic anti-pattern mostly working easy-peasy
      if (tx.isPopulated(id)) {
        (ExecutionContext.instance().getAttr(id)).asInstanceOf[NoStatic[T]]
      } else {
        // here's the magic: clone itself for new context with new value
        // TODO explicit samples/tests for this
        val newInst =
          if (tx == ExecutionContext.DFLT_CTX) {
            // resetJVM was performed
            //               this._value=initialValue; 
            this._value = null
            this
          } else new NoStatic[T](id, initialValue)
        tx.set(id, newInst)
        newInst
      }
    }
  }
}

object NoStatic {
  /** this will cleanup all the execution contexts in this JVM...as if you'd kill and restart the JVM */
  def resetJVM = ExecutionContext.resetJVM()
}

/** TODO 1-1 copy NoStatics docs here and ditch old one? */
object NoStaticS {
  /**
   * create a static for the current thread for the given class
   * 
   * @param o
   *            the instance to use in this and related threads
   * @return the same object you put in
   */
  def put[A](o: A)(implicit m: scala.reflect.Manifest[A]): A =
    NoStatics.put(m.erasure, o).asInstanceOf[A]

  /**
   * get the instance/static for this thread of the given class on this thread
   */
  def get[A](implicit m: scala.reflect.Manifest[A]): Option[A] = {
    val cl = NoStatics.get(m.erasure)
    if (cl != null) Some(cl.asInstanceOf[A]) else None
  }

}

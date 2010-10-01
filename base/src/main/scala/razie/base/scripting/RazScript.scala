/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.base.scripting

import razie.base.ActionContext

/**
 * some statics
 * 
 * @author razvanc
 */
object RazScript {
   // the result of running a smart script
  class RSResult[+A] { 
    def map[B>:A] (f:A=>B) : RSResult[B] = RSUnsupported ("by default")
    def getOrElse[B >: A] (f: => B) : B = f
    def getOrThrow:A = throw new IllegalArgumentException (this.toString)
    def jgetOrElse (f:Any) : Any = getOrElse(f)
  }

  case class RSSucc[A] (res:A) extends RSResult[A] { 
    override def map[B] (f:A=>B) : RSResult[B] = RSSucc(f(res))
    override def getOrElse[B >: A] (f: => B) : B = res
    override def getOrThrow:A = res
  }

  case class RSError (err:String) extends RSResult[String]
      object RSIncomplete  extends RSResult[Any]   // expression is incomplete...
  case class RSUnsupported (what:String) extends RSResult[Nothing] // interactive mode unsupported
      object RSUnsupported extends RSUnsupported ("todo") // interactive mode unsupported
      object RSSuccNoValue extends RSResult[Any] // successful, but no value returned
  
  def err (msg:String) = RSError(msg)
  def succ (res:AnyRef) = RSSucc(res)
}

/**
 * minimal script interface
 * 
 * TODO use JSR 223 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
trait RazScript {
  import RazScript._

  /** try to compile - optimization usually. If SIUnsupported, it will still be able to evaluate it
   * 
   * @return SError or SSuccNoValue...or others
   */
  def compile (ctx:ActionContext) : RSResult[Any]
  
  /** strait forward evaluation and return result of expression */
  def eval (ctx:ActionContext) : RSResult[Any]
 
  /** interactive evaluation - more complex interaction */ 
  def interactive (ctx:ActionContext) : RSResult[Any]
}

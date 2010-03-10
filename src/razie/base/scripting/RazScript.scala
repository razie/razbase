/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.base.scripting

/**
 * some statics
 * 
 * @author razvanc
 */
object RazScript {
   // the result of running a smart script
  class RSResult { 
     def map (f:Any=>RSResult) : RSResult = RSUnsupported 
     def getOrElse (f: => Any) :Any = f
     }

  case class RSSucc (res:Any) extends RSResult { 
     override def map (f:Any=>RSResult): RSResult = f(res)
     override def getOrElse (f: => Any) : Any = res
     }

  case class RSError (err:String) extends RSResult
      object RSIncomplete  extends RSResult   // expression is incomplete...
      object RSUnsupported extends RSResult // interactive mode unsupported
      object RSSuccNoValue extends RSResult // successful, but no value returned
  
  def err (msg:String) = RSError(msg)
  def succ (res:AnyRef) = RSSucc(res)
}

/**
 * minimal script interface
 * 
 * TODO use JSR 264 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
trait RazScript {
  import RazScript._

  /** try to compile - optimization usually. If SIUnsupported, it will still be able to evaluate it
   * 
   * @return SError or SSuccNoValue...or others
   */
  def compile (ctx:ScriptContext) : RSResult
  
  /** strait forward evaluation and return result of expression */
  def eval (ctx:ScriptContext) : RSResult
 
  /** interactive evaluation - more complex interaction */ 
  def interactive (ctx:ScriptContext) : RSResult
}

/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package razie.base.scripting

/**
 * minimal script interface
 * 
 * TODO use JSR 264 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
object RazScript {
  class SResult
  case class SSucc (res:Any) extends SResult
  case class SError (err:String) extends SResult
  object SIncomplete extends SResult
  object SIUnsupported extends SResult // interactive mode unsupported
  object SSuccNoValue extends SResult // successful, but no value returned
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

  /** strait forward evaluation and return result of expression */
  def eval(ctx:ScriptContext) : AnyRef
 
  /** interactive evaluation - more complex interaction */ 
  def interactive (ctx:ScriptContext) : SResult
}

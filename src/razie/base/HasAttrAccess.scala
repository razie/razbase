package razie.base

/** to avoid proxying the thing - although that way may be cleaner? 
 * 
 * @see {}
 */
trait HasAttrAccess {
   def attr : AttrAccess
}

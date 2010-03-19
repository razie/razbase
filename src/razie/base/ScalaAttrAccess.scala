/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base;

/**
 * simple attribute access interface and implementation - a bunch of name-value pairs with many
 * different constructors - everything these days has attributes.
 * 
 * it is used throughout to access parms in a unified manner: from http requests, method arguments,
 * properties etc
 * 
 * <p>
 * It has a skeleton type definition.
 * 
 * <p>
 * Note the funny behavior of setAttr ("attrname:type", value)...
 * 
 * <p>
 * Note the funny behavior of setAttr ("attrname:type=value,attrname2:type=value")...
 * 
 * @author razvanc99
 */
trait ScalaAttrAccess {

  def sgetPopulatedAttr : Iterable[String]
       
  def foreach (f : (String, AnyRef) => Unit) : Unit 
   
  def filter (f : (String, AnyRef) => Boolean) : Iterable[String]

  def map [A,B] (f : (String, A) => B) : ScalaAttrAccess 
  
  def mapValues [A,B] (f : (A) => B) : Seq[B]
}
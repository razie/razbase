/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.test

import org.scalatest.junit._
import org.scalatest.SuperSuite

// sample of how to implement a domain model for XP access

// TODO 

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class MTest extends JUnit3Suite {
  
   def testL = expect (List("a")) { razie.M apply { val l = new java.util.ArrayList[String](); l.add("a"); l } }
//   def testI = expect (razie.M("a"::Nil)) { razie.M apply { val l = new java.util.ArrayList[String](); l.add("a"); l }.iterator }
//   def testB = expect (List("a")) { (razie.M apply { val l = new java.util.HashMap[String, String](); l.put("a", "a"); l.values }).toList }
//   def testM = expect (List("a")) { (razie.M apply { val l = new java.util.HashMap[String, String](); l.put("a", "a"); l}).toList }
//   def testA = expect (List("a")) { (razie.M apply { Array("a") }).toList }
//   def testS = expect (List("a")) { (razie.M apply { for (x <- Array("a")) yield x }).toList }
//   def testN = expect (List()) { razie.MOLD apply { null } }
//   def testV = expect (List("a")) { razie.MOLD apply { "a" } }
//   
//   def testL1 = expect ("a"::Nil) { razie.M apply ala }
//   def testL2 = expect ("a"::Nil) { razie.M (ala) }
   val ala = { val l = new java.util.ArrayList[String](); l.add("a"); l }

   import razie.M._
   def testEq = expect (true) {razie.M.equals (1::2::Nil, 1::2::Nil) (_==_)}
   
}

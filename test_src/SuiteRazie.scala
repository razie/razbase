/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */

import org.scalatest.junit._
import org.scalatest.SuperSuite

/** main test suite */
class SuiteRazieIWishItWorked extends SuperSuite (
  List (
    new razie.xp.test.TestXpString,
    new razie.xp.test.TestXpScalaXml
  )
)

/** TODO this is sooooooooooooo messed up... */
class SuiteRazie () extends junit.framework.TestSuite(classOf[XNada]) {
  
  // this is where you list the tests...
   addTestSuite(classOf[razie.xp.test.TestXpString])
   addTestSuite(classOf[razie.xp.test.TestXpScalaXml])
   
   def test1() = 
     // don't touch this line
     addTest(new junit.framework.TestSuite(classOf[razie.xp.test.TestXpString]))
     
}

// this is here to convince eclipse to run as/junit...
class XNada extends junit.framework.TestCase {
 def testNada : Unit =  {}
}
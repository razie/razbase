/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base.test

import org.scalatest.junit._
import razie.base._
import org.scalatest.SuperSuite

/**
 * testing the assets
 * 
 * @author razvanc99
 */
class ScalaAttrAccessTest extends JUnit3Suite {
    val aa = new AttrAccessImpl("attr1:string=val1,attr2:int=2");

   def testString() = expect ("2") { JavaAttrAccessImpl.fromString(aa.toString).a("attr2") }

}

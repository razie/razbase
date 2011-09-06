/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.xp.test

import org.scalatest.junit._
import org.scalatest.SuperSuite
import razie._
import razie.xp.XpJsonSolver

import org.json._

/**
 * junit tests for the XP stuff
 * 
 * @author razvanc99
 */
class XpJsonTest extends JUnit3Suite {
     val json = """
{
  "errorCode": 203, 
  "errorMessage": "You must be authenticated to access recent", 
  "statusCode": "ERROR",
  "a" : { 
    "value": "a1" ,
    "b" : { 
      "value": "b1" 
    }
  }
}
"""
        // TODO how to handle json arrays?
//          "["+
//          "\"b\" : { \"value\": \"b1\" }," +
//          "\"b\" : { \"value\": \"b2\" }," +
//          "]"+
       
   def test1  = expect ("203") { xpa("/root/@errorCode")}
   def test2  = expect ("a1") { xpa("/root/a/@value")}
   def test2a = expect ("a1") { xpa("/root/*/@value")}
   def test2b = expect ("a1") { xpa("root/a/@value")}
   def test3  = expect ("b1") { xpa("/root/a/b/@value")}
   def test4  = expect ("b1") { xpa("/root/*/b/@value")}
 
   def xpe(path:String) = XP[Any] (path) using XpJsonSolver xpe root
   def xpl (path:String) = XP[Any] (path).xpl(XpJsonSolver, root) 
   def xpla(path:String) = XP[Any] (path).xpla(XpJsonSolver, root) 
   def xpa(path:String) = XP[Any] (path).xpa(XpJsonSolver, root) 

   val root = XpJsonSolver.WrapO(new JSONObject(json))
}


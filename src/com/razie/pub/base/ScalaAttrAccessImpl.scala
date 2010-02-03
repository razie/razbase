/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

/** simple base implementation */
abstract class ScalaAttrAccessImpl extends AttrAccess {
    
   def hasAttrType(name:String):Boolean 

        /**
         * build from sequence of parm/value pairs or other stuff
         * 
         * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another
         *       AttrAccess OR a Map<String,String>. Note the parm names can contain type:
         *       "name:string"
         */
//       def this(pairs:Any*) = { this(); setAttr(pairs) }

       def sgetPopulatedAttr = (razie.RJS apply (this.getPopulatedAttr))

//        public Iterable<String> getPopulatedAttr() {
//            return this.parms == null ? Collections.EMPTY_LIST : this.order;
//        }
//
//        def size() : Int {
//            return this.parms == null ? 0 : this.parms.size();
//        }
//
//        public boolean isPopulated(String name) {
//            return this.parms != null && this.parms.containsKey(name);
//        }

   override def toString() : String = 
      (for (a <- this.sgetPopulatedAttr) 
         yield a + (if (this.hasAttrType(a)) (":"+getAttrType(a)) else "") + "=" + getAttr(a).toString()).mkString(",")
       
   override def toXml() : String = 
      (for (a <- this.sgetPopulatedAttr)  
         yield "<" + a + ">" + this.getAttr(a) + "</" + a + ">").mkString("")

   override def addToUrl(url:String) : String = {
      val s = (for (a <- sgetPopulatedAttr) 
         yield java.net.URLEncoder.encode(a, "UTF-8") + "=" + java.net.URLEncoder.encode(toStr(getAttr(a)), "UTF-8")).mkString("&")
      
      if (s.length<=0)
         url
      else if (url!= null && url != "" && !url.endsWith("?") && !url.endsWith("&")) 
         url + (if (url.contains("=")) "&" else "?") + s
      else if (url!= null)
         url+s
     else 
        s
   }

   private def toStr (o:Object):String = if (o != null) o.toString() else ""
      
   def getOrElse(name:String, dflt:AnyRef) : AnyRef = if (isPopulated (name)) getAttr(name) else dflt
   def sgetOrElse(name:String, dflt:String) : String = getOrElse (name, dflt).toString


}





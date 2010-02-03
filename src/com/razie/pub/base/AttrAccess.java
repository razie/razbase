/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base;

import org.json.JSONObject;

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
public interface AttrAccess {

   /** these types MUST be supported by forms for capture, not necessarily by displays */
   public static enum AttrType {
      STRING, MEMO, SCRIPT, INT, FLOAT, DATE
   };

   // TODO 3-2 protect this against idiot code
   public static AttrAccess EMPTY = new AttrAccessImpl();
   
   /** @return the value of the named attribute or null */
   public Object getAttr(String name);

   /** @return the value of the named attribute or null */
   public Object getOrElse(String name, Object dflt);
   
   /** I'm really starting to hate typing... shortcut for getAttr */
   public Object a(String name);

   /** most of the time they're just strings - i'll typecast here... this is a() typcast to String */
   public String sa(String name);

   /** set the value of the named attribute + the name can be of the form name:type */
   public void set(String name, Object value);
   
   /** set the value of the named attribute + the name can be of the form name:type */
   public void set(String name, Object value, AttrType t);

   /** set the value of the named attribute + the name can be of the form name:type */
   public void setAttr(String name, Object value);

   /**
    * set the value of one or more attributes
    * 
    * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another AttrAccess
    *       OR a Map<String,String>. Note that the parm name can contain the type, i.e.
    *       "name:string".
    */
   public void setAttr(Object... pairs);

   /** the number of populated attributes */
   public int size();

   /** iterate through the populated attributes */
   public Iterable<String> getPopulatedAttr();

   /** check if an attribute is populated */
   public boolean isPopulated(String name);

   /**
    * @return the type of the named attribute OR null if not known. Default is by convention String
    */
   public AttrType getAttrType(String name);

   /**
    * set the type of the named attribute
    */
   public void setAttrType(String name, AttrType type);

   /** some random xml format */
   public String toXml();

   /** same pairs format name,value,name,value... */
   public Object[] toPairs();

   /**
    * add my attributes to the JSONObject passed in. If null passed in, empty object is created
    * first
    * 
    * @param obj an json object to add to or null if this is a single element
    * @return
    */
   public JSONObject toJson(JSONObject obj);

   /**
    * add these attributes to an url, respecting the url parm format, i.e.
    * getMovie?name=300.divx&producer=whoknows
    */
   public String addToUrl(String url);

   /** hierarchical implementation */
   public class TreeImpl extends AttrAccessImpl {
      AttrAccess parent;

      /** dummy */
      public TreeImpl(AttrAccess parent) {
         super();
         this.parent = parent;
      };

      /**
       * build from sequence of parm/value pairs or other stuff
       * 
       * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another
       *       AttrAccess OR a Map<String,String>
       */
      public TreeImpl(AttrAccess parent, Object... pairs) {
         this(parent);
         this.setAttr(pairs);
      }

      @Override
      public Object getAttr(String name) {
         Object o = this.parms != null ? this.parms.get(name) : null;
         return o != null ? o : (parent != null ? parent.getAttr(name) : null);
      }

      @Override
      public boolean isPopulated(String name) {
         boolean b = this.parms != null && this.parms.containsKey(name);
         return b ? true : (parent != null ? parent.isPopulated(name) : false);
      }

      @Override
      public String toString() {
         String ret = parent != null ? parent.toString() : "";
         return ret + super.toString();
      }
   }
}
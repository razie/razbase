/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package razie.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

    /** simple base implementation */
    public class JavaAttrAccessImpl extends ScalaAttrAccessImpl implements AttrAccess {
        // lazy - using underscore since many classes may derive from here...
        public Map<String, Object>   _attrs = null;
        protected Map<String, AttrType> _types = null;
        protected List<String>          _order = null;

        /** dummy */
        public JavaAttrAccessImpl() {
        };

        /**
         * build from sequence of parm/value pairs or other stuff
         * 
         * @parm pairs are pais of name/value, i.e. "car", "lexus" OR a Properties, OR another
         *       AttrAccess OR a Map<String,String>. Note the parm names can contain type:
         *       "name:string"
         */
        public JavaAttrAccessImpl(Object... pairs) {
            this.setAttr(pairs);
        }

        /* TODO should setAttr(xx,null) remove it so it's not populated? */
        public void set(String name, Object value) { setAttr(name,value); }
        public void set(String name, Object value, AttrAccess.AttrType t) { 
           setAttr(name,value); setAttrType (name, t);}


        // TODO 3-2 implement nice scala pattern matching
        /** parse "name:type=val" into a spec */
        public static AttrSpec parseSpec (String spec) {
           String[] ss = spec.split("=", 2);

           String val = null;
           if (ss.length > 1)
               val = ss[1];

           String name = ss[0];
          
           // - parse the rest
           AttrType t = AttrType.DEFAULT;
           
           // check name for type definition
           if (name.contains(":")) {
               String n[];

               // type defn can be escaped by a \
               int idx = name.indexOf("\\:");
               if (idx >= 0 && idx == name.indexOf(":") - 1) {
                   n = new String[2];
                   
                   // let's see if it does have a type...
                   String s2 = name.substring(idx + 2);
                   int idx2 = s2.indexOf(":");
                   if (idx2 >= 0) {
                       n[0] = name.substring(0, idx+2+idx2);
                       n[1] = name.substring(idx+2+idx2+1);
                   } else {
                       n[0] = name;
                       n[1] = null;
                   }
                   
                   name = n[0] = n[0].replaceAll("\\\\:", ":");
               } else
                   n = name.split(":", 2);

               // basically, IF there's a ":" AND what's after is a recognied type...otherwise i'll
               // assume the parm name is "a:b"
               if (n.length > 1 && n[1] != null) {
                   AttrType tt = AttrType.valueOf(n[1].toUpperCase());
                   if (tt != null) {
                       name = n[0];
                       t = tt;
                   }
               }
           }
           
           return AttrSpec$.MODULE$.factory1(name, t, val);
        }
        
        /* TODO should setAttr(xx,null) remove it so it's not populated? */
        public void setAttr(String name, Object value) {
            checkMap();
            AttrSpec s = parseSpec (name);
            
            if (s.t() != AttrType.DEFAULT)
               this.setAttrType(s.n(), s.t());
            
            if (!this._attrs.containsKey(s.n()))
                this._order.add(s.n());
            this._attrs.put(s.n(), value);
        }

        public Object getAttr(String name) {
            return this._attrs != null ? this._attrs.get(name) : null;
        }

        public Object a(String name) {
            return getAttr(name);
        }

        public String sa(String name) {
            return (String)a(name);
        }

        /**
         * @parm pairs are pais of name/value, i.e. setAttr("car", "lexus") OR a Properties, OR
         *       another AttrAccess OR a Map<String,String>
         */
        @SuppressWarnings("unchecked")
      public void setAttr(Object... pairs) {
            if (pairs != null && pairs.length == 1 && pairs[0] instanceof Map) {
                Map<String, String> m = (Map<String, String>) pairs[0];
                for (Map.Entry<String,String> entry : m.entrySet()) {
                    this.setAttr(entry.getKey(), m.get(entry.getKey()));
                }
            } else if (pairs != null && pairs.length == 1 && pairs[0] instanceof Properties) {
                Properties m = (Properties) pairs[0];
                for (Map.Entry<Object,Object> entry : m.entrySet()) {
                    this.setAttr((String)entry.getKey(), m.get((String)entry.getKey()));
                }
            } else if (pairs != null && pairs.length == 1 && pairs[0] instanceof JavaAttrAccessImpl) {
                JavaAttrAccessImpl m = (JavaAttrAccessImpl) pairs[0];
                for (String s : m.getPopulatedAttr()) {
                    this.setAttr((String) s, m.getAttr((String) s));
                }
            } else if (pairs != null && pairs.length == 1 && pairs[0] instanceof String) {
                /* one line defn of a bunch of parms */
                /*
                 * Note the funny behavior of setAttr
                 * ("attrname:type=value,attrname2:type=value")...
                 */
                String m = (String) pairs[0];
                String[] n = m.split("[,&]");
                for (String s : n) {
                   // TODO 3-2 share with parseSpec above
//                   AttrSpec as = parseSpec (s);
                    String[] ss = s.split("=", 2);

                    String val = null;
                    if (ss.length > 1)
                        val = ss[1];

                    String nametype = ss[0];
                    this.setAttr(nametype, val);
                }
            } else if (pairs != null && pairs.length > 1) {
                for (int i = 0; i < pairs.length / 2; i++) {
                    String name = (String) pairs[2 * i];
                    if (name != null)
                        this.setAttr(name, pairs[2 * i + 1]);
                }
            }
        }

        private void checkMap() {
            if (this._attrs == null) {
                this._attrs = new HashMap<String, Object>();
                this._types = new HashMap<String, AttrType>();
                this._order = new ArrayList<String>();
            }
        }

        @SuppressWarnings("unchecked")
        public Iterable<String> getPopulatedAttr() {
            return this._attrs == null ? Collections.EMPTY_LIST : this._order;
        }

        public int size() {
            return this._attrs == null ? 0 : this._attrs.size();
        }

        public boolean isPopulated(String name) {
            return this._attrs != null && this._attrs.containsKey(name);
        }

        public JSONObject toJson(JSONObject obj) {
            try {
                if (obj == null)
                    obj = new JSONObject();
                for (String name : this.getPopulatedAttr()) {
                    obj.put(name, this.getAttr(name));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return obj;
        }

        /** same pairs format name,value,name,value... */
        public Object[] toPairs() {
            int size = this._attrs == null ? 0 : this._attrs.size();
            Object[] ret = new Object[size * 2];

            int i = 0;
            for (String name : this.getPopulatedAttr()) {
                ret[i] = name;
                ret[i + 1] = getAttr(name);
                i += 2;
            }
            return ret;
        }

        /** TODO implement */
        public static JavaAttrAccessImpl fromJsonString(String s) {
            try {
               return fromJson(new JSONObject(s));
            } catch (JSONException e) {
               throw new IllegalArgumentException(e);
            }
        }

        public static JavaAttrAccessImpl fromString(String s) {
           return new JavaAttrAccessImpl (s);
        }

        /** TODO implement */
        public static JavaAttrAccessImpl fromJson(JSONObject o) {
            JavaAttrAccessImpl a = new AttrAccessImpl();
            for (String n : JSONObject.getNames(o))
               try {
                  a.setAttr(n, o.getString(n));
               } catch (JSONException e) {
               throw new IllegalArgumentException(e);
               }
            return a;
        }

        /** TODO implement */
        public final static JavaAttrAccessImpl reflect(Object o) {
            // TODO implement reflection
            throw new UnsupportedOperationException("TODO");
        }

        public boolean hasAttrType(String name) {
            return this._types != null && this._types.get(name) != null;
        }

        public AttrType getAttrType(String name) {
            AttrType t = this._types != null ? this._types.get(name) : null;
            return t == null ? AttrType.STRING : t;
        }

        public void setAttrType(String name, String type) {
            this.setAttrType(name, AttrType.valueOf(type.toUpperCase()));
        }

        public void setAttrType(String name, AttrType type) {
            checkMap();
            // TODO maybe it's too slow this toString?
            this._types.put(name, type);
        }
        
    }

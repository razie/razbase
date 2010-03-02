/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.base;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.razie.pub.base.ScriptFactory;

import razie.base.ActionContext;
import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

/**
 * actions execute in a context of objects available at that time in that environment.
 * 
 * a script context is more complex than just AttributeAccess, it may include a hierarchy of
 * contexts, hardcode mappings etc. This class may go away and be replaced with the jdk1.6
 * scriptables.
 * 
 * it's used to run activities and scripts $
 * 
 * You can define functions, which are evaluated every time
 * 
 * @author razvanc99
 * deprecated move code to ActContext
 */
public interface ScriptContext extends ActionContext {

   /**
    * deine a new function - these are evaluated every time they are invoked. these also overwrite
    * another symbol, so you can redefine a symbol to do something else.
    * 
    * DO NOT forget to seal a context before passing it to untrusted plugins
    */
   public void define(String fun, String expr);

   /** remove a function */
   public void undefine(String macro);

   /** TODO 3 FUNC use guards, document */
   public void guard(String name, String condition, String expr);

   /** TODO 3 FUNC use guards, document */
   public void unguard(String name, String condition, String expr);

   /** TODO remove */
   public void xscrewscala28(String name, Object v);

   /** make execution verbose or not */
   public void verbose(boolean v);

   /** a simple context - supports parents, function overrides and guards */
   public class Impl extends AttrAccess.TreeImpl implements ScriptContext {
      private static ScriptContext    main    = new ScriptContext.Impl();

      protected Map<String, String>   macros  = new HashMap<String, String>();
      protected Map<String, String[]> guards  = new HashMap<String, String[]>();

      protected boolean               verbose = false;

      public static ScriptContext global() {
         return main;
      }

      public Impl() {
         this((ScriptContext) null);
      };

      public Impl(AttrAccessImpl aa) {
         this(null, aa);
      };

      /** supports a map as well */
      public Impl(Object... pairs) {
         this(null, pairs);
      }

      public Impl(ScriptContext parent) {
         super(parent);
      };

      public Impl(ScriptContext parent, AttrAccessImpl aa) {
         super(parent);
         this._attrs = aa._attrs;
      };

      /** supports a map as well */
      public Impl(ScriptContext parent, Object... pairs) {
         super(parent, pairs);
      }

      public Object getAttr(String name) {
         if (macros.containsKey(name)) {
            // TODO 3-1 cache these pre-compiled macros
            return ScriptFactory.make(null, macros.get(name)).eval(this);
         }
         return super.getAttr(name);
      }

      public boolean isPopulated(String name) {
         return macros.containsKey(name) ? true : super.isPopulated(name);
      }

      /**
       * DO NOT forget to seal a context before passing it to untrusted plugins
       */
      public void define(String macro, String expr) {
         macros.put(macro, expr);
      }

      /**
       * reset all overloads of a parm DO NOT forget to seal a context before passing it to
       * untrusted plugins
       */
      public void undefine(String macro) {
         macros.remove(macro);
      }

      /**
       * TODO 3 FUNC use the guards - currently i'm not using them. I think i want them to be what,
       * rules???
       */
      public void guard(String name, String condition, String expr) {
         String[] g = new String[2];
         g[0] = condition;
         g[1] = expr;
         guards.put(name, g);
      }

      public void unguard(String name, String condition, String expr) {
         guards.remove(name);
      }

      /** more verbose or not? */
      public void verbose(boolean v) {
         this.verbose = v;
      }

      /** TODO remove */
      public void xscrewscala28(String name, Object v) {
         super.set(name, v);
      }
   }

   /**
    * seal a context before passing it on - security issue. Others may override the meaning of
    * objects in here. When sealed, a context will not allow overriding symbols or changing their
    * value - may only define new ones
    */
   public class SealedContext implements ScriptContext {
      private ScriptContext wrapped;

      public SealedContext(ScriptContext wraped) {
         this.wrapped = wraped;
      }

      @Override
      public void define(String fun, String expr) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void guard(String name, String condition, String expr) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void xscrewscala28(String name, Object v) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void undefine(String macro) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void unguard(String name, String condition, String expr) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void verbose(boolean v) {
         wrapped.verbose(v);

      }

      @Override
      public Object a(String name) {
         return wrapped.a(name);
      }

      @Override
      public String addToUrl(String url) {
         return wrapped.addToUrl(url);
      }

      @Override
      public Object getOrElse(String name, Object dflt) {
         return wrapped.getOrElse(name, dflt);
      }

      @Override
      public Object getAttr(String name) {
         return wrapped.getAttr(name);
      }

      @Override
      public AttrAccess.AttrType getAttrType(String name) {
         return wrapped.getAttrType(name);
      }

      @Override
      public Iterable<String> getPopulatedAttr() {
         return wrapped.getPopulatedAttr();
      }

      @Override
      public boolean isPopulated(String name) {
         return wrapped.isPopulated(name);
      }

      @Override
      public String sa(String name) {
         return wrapped.sa(name);
      }

      @Override
      public void set(String name, Object value) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void set(String name, Object value, AttrAccess.AttrType t) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void setAttr(String name, Object value) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void setAttr(Object... pairs) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public void setAttrType(String name, AttrAccess.AttrType type) {
         throw new IllegalStateException("This context is sealed - you can't override stuff.");
      }

      @Override
      public int size() {
         return wrapped.size();
      }

      @Override
      public JSONObject toJson(JSONObject obj) {
         return wrapped.toJson(obj);
      }

      @Override
      public Object[] toPairs() {
         return wrapped.toPairs();
      }

      @Override
      public String toXml() {
         return wrapped.toXml();
      }
   }
}

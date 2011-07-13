/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.lightsoa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import razie.base.ActionItem;

import com.razie.pub.comms.PermType;

/**
 * mark the methods callable from by the lightsoa framework, on a service class
 * 
 * service-method ulrs are <code>PREFIX/SERVICE/METHOD?parms=values&</code>
 * 
 * <pre>
 *        class ClassA {
 *            ...
 *            &#064;SoaMethod (descr=&quot;the name of the component&quot;)
 *            public SoaResponse getName() {
 *            }
 *            ...
 *            &#064;SoaMethod (descr=&quot;the name of the component&quot;, args = {&quot;name&quot;})
 *            public SoaResponse setName(String name) {
 *            }
 *            ...
 *            &#064;SoaMethod (descr=&quot;simple string method&quot;)
 *            public String sayHello() {
 *            }
 *        }
 * </pre>
 * 
 * These methods can take arguments - limit yourself to String arguments for now. These however will
 * be escaped properly and un-escaped properly by each binding, that's a requirement for te binding
 * since the method doesn't know which binding will wrap it.
 * 
 * You can return anything. Most protocols will just make that .toString(). If a protocol (UPNP)
 * supports returning multiple arguments, please return a SoaResponse
 * 
 * <b>Annotate with {@link SoaStreamable} if your method can stream a longer reply back. OR if you
 * need to change the reply mime-type and processing - see {@link SampleService}
 * 
 * <p>
 * Annotate with @SoaAllParms if you want all parms in an array
 * 
 * <p>
 * annotate with @SoaMethodSink if this is the sink for all unknown method names
 * 
 * <p>
 * You can access the request's httpattrs inside the call by calling
 * <code>AttrAccess httpattrs = (AttrAccess)ThreadContext.instance().getAttr ("httpattrs")</code>
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface SoaMethod {
   /** the value is a description of the method */
   String descr();

   /**
    * if defined, put here the permission to check - the framework will check the current connection
    * and user for this permission and throw AuthException if not permitted
    */
   PermType perm() default PermType.WRITE;

   /**
    * action type may dictate if it's ACT/GET/POST/PUT/DELETE
    * 
    * TODO did i actually end up using this?
    */
   ActionItem.ActionType actionType() default ActionItem.ActionType.R;

   /** the list of arguments. Each can be "name" or "name:type" */
   String[] args() default {};
  
   /** smarter alternative to args */
   String aargs() default "";
}

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

/**
 * mark the classes implementing callables from by the lightsoa framework
 * 
 * asset ulrs are <code>PREFIX/SERVICE/METHOD?parms=values&</code>
 * 
 * <pre>
 *        &#064;SoaService(name=&quot;network&quot;,descr=&quot;network services&quot;)
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
 *        }
 * </pre>
 * 
 * TODO can be used to figure out name clashes, register services automatically etc
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface SoaService {
    /** the name of the service. I think right now you should make sure it matches the simpleclassname of the service! */
    String name();

    /** the value is a description of the method */
    String descr();

    /**
     * can limit the bindings. if mentioned, the service will be mounted, onStart, to the respective
     * bindings: http, upnp for now
     */
    String[] bindings() default {};
}

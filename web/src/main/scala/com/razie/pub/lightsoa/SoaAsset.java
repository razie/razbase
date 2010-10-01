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
 * identifies an asset class as opposed to a service (see SoaService for details).
 * 
 * asset ulrs are <code>PREFIX/asset/KEYURL/METHOD?parms=values&</code>. Note that "asset" is in the
 * same position as a service would be and it is in fact a reserved service :)
 * 
 * RESTful: by convention, <code>PREFIX/asset/KEYURL</code> represents the asset and the
 * GET/POST/etc are translated directly into inventory commands.
 * 
 * equivalent forms:
 * 
 * <ul>
 * <li><code>PREFIX/asset/KEYURL</code>
 * <li><code>PREFIX/[text|xml|json]/asset/KEYURL</code>
 * <li><code>PREFIX/[text|xml|json]/asset/KEYURL/METHOD?parms=values&</code> - the result of the
 * method is rendered using the indicated format
 *</ul>
 * 
 * the basic idea is that, instead of talking about remote services that manage stupid entities, we
 * like to think in terms of smart objects, implicitly remote (CORBA). To escape the granularity
 * issues that CORBA had, we elevate these to the level of "asset" and make them join the priesthood
 * of the terminologically bastardized concepts.
 * 
 * since the code doesn't have to be remoted as in Java, to invoke the asset remotely, use
 * AssetHandle...sorry, whish i could be nicer but this is a compiled language, not interpreted.
 * 
 * TODO can be used to figure out name clashes, register services automatically etc
 * 
 * TODO use the bindings...currently it's not used
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface SoaAsset {
   /**
    * the type of the asset modelled by this class. You can leave it null ONLY if registering with Assets.manage()
    */
   String meta() default "";

   /** the type of the asset modelled by this class */
   String base() default "";

   /** the value is a description of the asset type */
   String descr();

   /**
    * can limit the bindings. if mentioned, the asset will be mounted, onStart, to the respective
    * bindings: http, upnp for now
    */
   String[] bindings() default {};
}

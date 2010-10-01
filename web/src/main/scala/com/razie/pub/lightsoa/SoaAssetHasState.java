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
 * these assets have state. they must implement two methods:
 * 
 * public void persist();
 * 
 * public static List<XXX> load(); where XXX is the class name
 * 
 * persist() is called whenever the system wants to persist this asset and load() is called to
 * create all assets that were persisted
 * 
 * you can persist the assets in the agentdb, by convention use agent/assets/ASSETTYPE/instance and
 * make sure there's a key attribute
 * 
 * TODO implement/use
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface SoaAssetHasState {
}

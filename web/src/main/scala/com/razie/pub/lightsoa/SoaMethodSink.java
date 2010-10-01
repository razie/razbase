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
 * See SoaMethod - this is a sink for any other url the sink must have all args and the first arg is
 * actual method name
 * 
 * <code>public mysink (AttrAccess args);</code>
 * 
 * NOTE that sink methods must also have the SoaMethod annotation and are subject to all other
 * Soaxxx annotations. You should use SoaAllParms to make sure you really sink anything.
 * 
 * When using SoaAllParms, the original method name is available as SOA_METHODNAME.
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface SoaMethodSink {
    public static final String SOA_METHODNAME = "SOA_METHODNAME";
    
    /**
     * if defined, put here the permission to check - the framework will check the current
     * connection and user for this permission and throw AuthException if not permitted
     */
    PermType perm() default PermType.WRITE;

    /**
     * action type may dictate if it's ACT/GET/POST/PUT/DELETE
     * 
     * TODO did i actually end up using this?
     */
    ActionItem.ActionType actionType() default ActionItem.ActionType.A;
}

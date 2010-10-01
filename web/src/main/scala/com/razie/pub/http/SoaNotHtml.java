/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
//==========================================================================
// FILE INFO
// $Id: ScriptCallback.java,v 1.7 2007-09-26 16:14:58 sorinelc Exp $
//
// REVISION HISTORY
// * Based on CVS log
//==========================================================================
package com.razie.pub.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SoaMethods marked with this will NOT get their String response formatted from text to html
 * (replace \n etc) when invoked via http binding
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface SoaNotHtml {
}

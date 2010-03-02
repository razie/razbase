/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base;

import razie.base.ScriptContext;


/**
 * minimal script interface
 * 
 * TODO use JSR 264 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
public interface RazScript {
    public Object eval(ScriptContext c);

}

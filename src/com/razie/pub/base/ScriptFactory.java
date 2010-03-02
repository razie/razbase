/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base;

/**
 * minimal factory to decouple scripting
 * 
 * TODO use JSR 264 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
public class ScriptFactory {
    public static ScriptFactory singleton = new ScriptFactory();

    public static void init(ScriptFactory theOneToUse) {
        singleton = theOneToUse;
    }

    public static RazScript make(String lang, String script) {
        return singleton.makeImpl(lang, script);
    }

    // TODO make protected
    public RazScript makeImpl(String lang, String script) {
        throw new UnsupportedOperationException ("no default script maker...");
    }
}

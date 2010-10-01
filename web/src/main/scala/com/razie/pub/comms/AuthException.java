/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.comms;

/**
 * just a placeholder for authorization denied exceptions
 * 
 * @author razvanc99
 */
public class AuthException extends RuntimeException {
    
    public AuthException () {}
    
    public AuthException (String m) { super(m);}

}

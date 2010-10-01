/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.lightsoa;

import razie.base.AttrAccessImpl;

/**
 * a service call response is a set of attributes. STRONGLY suggest that the attributes be
 * URL-escaped strings. That means that you will do a fromUrlEncodedString on each...
 * 
 * If your method doesnt return anything, it can return a null response.
 * 
 * If your method returns something, follow the upnp convention: populate a "Result"
 * 
 * NOTE that the framework is required to support void and String returning methods as well, which
 * will be treated correspondingly be each protocol...
 * 
 * @author razvanc99
 * 
 */
public class SoaResponse extends AttrAccessImpl {
    /** supports a map as well */
    public SoaResponse(Object... pairs) {
        super(pairs);
    }
}

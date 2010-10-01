/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base.data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * a bunch of http utils ?
 * 
 * @deprecate just inline...
 * TODO 1-1 just inline
 * @author razvanc99
 */
public class HttpUtils {

    public static String toUrlEncodedString(String ref) {
        try {
            return URLEncoder.encode(ref, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ref;
        }
    }

    public static String fromUrlEncodedString(String ref) {
        try {
            return URLDecoder.decode(ref, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ref;
        }
    }
}

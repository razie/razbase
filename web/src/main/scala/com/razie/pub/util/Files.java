/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * basic file services 
 * 
 * @author razvanc
 * @version $Id$
 */
public class Files {

    public static void copyStream(InputStream is, OutputStream fos) {
        try {
            byte[] buf = new byte[4 * 1024 + 1];
            int n;
            while ((n = is.read(buf, 0, 4096)) > 0) {
                fos.write(buf, 0, n);
            }
            fos.close();
            is.close();
        } catch (IOException e1) {
            throw new RuntimeException("Copystream failed: ", e1);
        }
    }
}

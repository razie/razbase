/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.comms;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import razie.base.AttrAccess;

import com.razie.pub.base.data.ByteArray;
import com.razie.pub.base.log.Log;

/**
 * communications utils - extension to decouple the base
 */
public class Comms2 extends Comms {

  /** copy a stream using a simple SED like filter */
  public static void copyStreamSED(InputStream is, OutputStream fos, List<SedFilter> filters) {
    try {
      String line;
      fos.write(HttpHelper.httpHeader(HttpHelper.OK).getBytes());

      BufferedReader input = new BufferedReader(new InputStreamReader(is));
      while ((line = input.readLine()) != null) {
        for (SedFilter filter : filters) {
          line = filter.filter(line);
        }

        fos.write(line.getBytes());
        fos.write('\n');
      }
      fos.flush();
      fos.close();
      input.close();
      is.close();
    } catch (IOException e1) {
      throw new CommRtException("Copystream failed: ", e1);
    }
  }
}

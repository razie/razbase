/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.http.sample;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.http.LightCmdGET;

/* the default cmdGET does not serve files - this one will serve from classpath 
 * 
 * @author razvanc99
 */
 public  class SimpleClasspathServer extends LightCmdGET {
      String prefix = "/classpath";
      
      public SimpleClasspathServer () { }
      
      public SimpleClasspathServer (String prefix) {
         this.prefix = prefix;
      }
      
      /**
       * serve only from classpath if the path is "/classpath/..."
       */
      @Override
      protected URL findUrlToServe(MyServerSocket socket, String path, Properties parms)
            throws MalformedURLException, AuthException {

         // the main page
         if (path.equals("/") || path.equals("")) {
            path = prefix+"/com/razie/pub/http/test/index.html";
         }

         // hardcoded prefix...yeah sucks
         if (!prefix.equals("/classpath") && path.startsWith("/classpath"))
            path = path.replaceFirst("/classpath", prefix);
         
         // serve files
         if ((prefix.length()<=0 || path.startsWith(prefix)) || path.equals("/favicon.ico")) {
            String filenm = path;
            if (path.equals("/favicon.ico")) {
               filenm = "/public/favicon.ico";
               URL url = this.getClass().getResource(filenm);
               if (url == null) {
               try {
                    socket.close();
               } catch (IOException e) {
                  Log.logThis("IGNORING: ", e);
               }
               return null;
               }
            } else {
               if (prefix != "")
                  filenm = path.replaceFirst(prefix, "");
               else filenm = path;
            }

            URL url = null;

            url = this.getClass().getResource(filenm);

            return url;
         }

         return null;
      }
   }

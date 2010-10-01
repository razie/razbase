package com.razie.pub.comms

import razie.base._
import com.razie.pub.base._
import java.net._
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import razie.assets.AssetLocation;
import razie.base.AttrAccess;
//import com.razie.pub.base.data.ByteArray;
//import com.razie.pub.base.exceptions.CommRtException;
import com.razie.pub.base.log.Log;
import com.razie.pubstage.comms._
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

class UrlComms {

}

class UrlConn (val baseUrl:String) {
   val url = new URL (baseUrl)
   val socket = new Socket (url.getHost, url.getPort)
 
   /** @param path does not include server:port */
   def get (path:String, httpArgs : razie.AA = razie.AA()) : String = {
      HttpHelper.sendGET (socket, "GET "+path+" HTTP/1.1", httpArgs)
            var input = "";

                // don't know why but i can't use this compliant reader...
                // BufferedReader in = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));

                // Get input from the client
                val in = new java.io.DataInputStream(socket.getInputStream());

                // TODO 3 why do i flush empty lines?
                // consume the input until a non-emtpty line
            while (input == null || input.length() <= 0) {
               input = in.readLine();
               if (input == null) {
//                  logger.alarm("ERR_SOCKET_EOF socket had EOF before any byte...dropping connection");
                  return "";
               }
            }

                 var rest = in.readLine();
                val httpattrs = new AttrAccessImpl();
                while (rest != null && rest.length() > 0) {
                   val s = rest.split(": ");
                   httpattrs.set(s(0), s(1));
                    rest = in.readLine();
                }

      val contents = Comms.readStreamNoClose (in)                
                
      if (input.endsWith("200 OK")) {
         var es = 0
//         val contents = new StringBuffer()
//         for (s <- 1 until lines.length)
//            if (lines(s).length <= 0 && es <= 0)
//               es = s
//            else if (es > 0) contents.append(lines(s)).append("\n")
            
         val c = (contents.toString)
         HtmlContents.justBody (c)
      } else
         input
   }
   
   def close = socket.close
}



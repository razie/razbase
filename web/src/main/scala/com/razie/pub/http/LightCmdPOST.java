/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.util.Properties;

import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;

/**
 * POST is the same as GET but there are more attributes, there may be a content with mime type and no reply expected
 * 
 * <code>server.registerCmdListener(new LightCmdGET());</code>
 * 
 * @author razvanc
 */
public class LightCmdPOST extends LightCmdGET {
   LightCmdGET delegateTo=null;
   public LightCmdPOST (LightCmdGET delegateTo) {
      this.delegateTo = delegateTo;
   }
   public Object execServer(String cmdName, String protocol, String args, Properties parms,
         MyServerSocket socket) throws AuthException {
      String input = "";

      try {
         // special hacking for binary transmissions
         if (socket.getHttp() != null && socket.getHttp().isPopulated("Content-Type")
               && socket.getHttp().sa("Content-Type").equals("application/octet-stream")) {
            // only thing i support right now is an object...

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object bee = ois.readObject();
            parms.put("razie.content.mime.octet-stream", bee);
         } else {
            // POST has parms after all the http stuff already read by server. read them like this
            // since there's no CRLF

            // TODO should use the content-length so i don't miss input in large requests, maybe

            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            if (in.available() > 0) {
               // TODO 3-2 i only accept an url of 1k chars - is that fair?
               byte[] cbuf = new byte[1000];
               int c = in.read(cbuf);
               input = new String(cbuf, 0, c);
               Log.traceThis("POST INPUT=" + input);
            }
            // decode the parms
            String[] pairs = input.split("&");
            for (String pair : pairs) {
               String[] split = pair.split("=", 2);
               if (split.length > 1)
                  parms.put(split[0], HttpUtils.fromUrlEncodedString(split[1]));
            }
         }
      } catch (Exception e) {
         Log.logThis("ERROR when decoding a POST request...", e);
      }

      return delegateTo.execServer(cmdName, protocol, args, parms, socket);
   }

   public String[] getSupportedActions() {
      return COMMANDS;
   }

   static final String[] COMMANDS = { "POST" };
   static final Log      logger   = Log.factory.create(LightCmdPOST.class);
}

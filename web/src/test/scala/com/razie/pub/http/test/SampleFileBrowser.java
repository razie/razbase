/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.http.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.draw.Align;
import razie.draw.DrawStream;
import razie.draw.DrawTable;
import razie.draw.widgets.NavLink;

import com.razie.pub.base.NoStatics;
import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.data.HtmlRenderUtils.HtmlTheme;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.comms.SimpleActionToInvoke;
import com.razie.pub.http.LightCmdGET;
import com.razie.pub.http.LightServer;
import com.razie.pub.http.sample.SimpleClasspathServer;
import com.razie.pub.http.sample.SimpleNoThreadsServer;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIconRes;
import com.razie.pub.resources.RazIcons;

/**
 * a simple no threads FILE server - streams to browser as files are found...try it on a large
 * folder and put a sleep here to see the effect on slow operations
 * 
 * NOTE: there is no security whatsoever
 * 
 * @author razvanc99
 */
public class SampleFileBrowser {

   @SoaMethod(descr = "browse folder", args = { "folderpath" })
   @SoaStreamable
   public void browse(DrawStream out, String folderpath) {
      folderpath = HttpUtils.fromUrlEncodedString(folderpath);

      File[] dirs = new File(folderpath).listFiles();
      if (dirs == null) {
         return;
      }

      ActionItem BROWSE = new ActionItem("browse", RazIcons.FOLDER.name());
      ActionItem SERVE = new ActionItem("serve", RazIcons.FILE.name());

      DrawTable list = new DrawTable(0, 2);
      list.packed = true;
      list.horizAlign = Align.LEFT;

      out.open(list);

      for (File f : dirs) {
         if (f.isDirectory()) {
            BROWSE.label = "DIR  " + f.getAbsolutePath();
            // the table will write stuff out only when a row is full - gotta clone the
            // changeables
            ActionToInvoke ai = new ServiceActionToInvoke("lightsoa/service", BROWSE.clone(), "folderpath", f
                  .getAbsolutePath());
            ai.drawTiny = true;

            list.write(ai);
            list.write(new NavLink(ai));
         } else {
            SERVE.label = "FILE " + f.getAbsolutePath();
            // this is a trick so explorer recognizes the filename
            SERVE.name = "serve/" + f.getName();
            // the table will write stuff out only when a row is full - gotta clone the
            // changeables
            ActionToInvoke ai = new SimpleActionToInvoke(SERVE.clone(), "filepath", f.getAbsolutePath());
            ai.drawTiny = true;

            list.write(ai);
            list.write(new NavLink(ai));
         }
      }

      out.close(list);
   }

   @SoaMethod(descr = "shutdown the server")
   public String die() {
      server.shutdown();
      return "dying...";
   }

   public static void main(String[] argv) throws IOException {
      RazIconRes.init();
      new SampleFileBrowser().start(LightServerTest.ME, new AgentCloud(LightServerTest.ME));
   }

   LightServer server = null; // setup in start

   public void start(AgentHandle me, AgentCloud cloud) {
      NoStatics.put(Agents.class, new Agents(cloud, me));

      // stuff to set before you start the server
      HtmlRenderUtils.setTheme(new DarkTheme());

      LightCmdGET cmdGET = new SimpleFileServerCmd();
      server = new SimpleNoThreadsServer(me, cmdGET);

      cmdGET.registerSoa(new HttpSoaBinding(this, "service"));

      Log.logThis("Starting simple file server at: " + me);
      Log.logThis(" - try simple url like" + me.url);

      // start the server thread...
      server.run();
   }

   /**
    * the default cmdGET does not serve files - this one will serve. use the classpathserver to
    * serve stylesheet
    */
   static class SimpleFileServerCmd extends SimpleClasspathServer {
      /**
       * will serve any file on the harddrive
       */
      @Override
      protected URL findUrlToServe(MyServerSocket socket, String path, Properties parms)
            throws MalformedURLException, AuthException {

         // the main page
         if (path.equals("/") || path.equals("")) {
            path = "/classpath/com/razie/pub/http/test/index-files.html";
         }

         // serve files
         if (path.startsWith("/serve")) {
            String filenm = parms.getProperty("filepath");

            URL url = new File(filenm).toURL();

            return url;
         }

         return super.findUrlToServe(socket, path, parms);
      }
   }

   /** a simple, css-based theme to be used by the sample server */
   static class DarkTheme extends HtmlTheme {
      static String[] tags = {
                                 "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"/classpath/com/razie/pub/http/test/style.css\" /></head><body link=\"yellow\" vlink=\"yellow\">",
                                 "</body>", "<html>", "</html>" };

      public String get(int what) {
         return tags[what];
      }
   }
}

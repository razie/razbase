/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http.sample;

import java.io.IOException;

import razie.draw.DrawStream;
import razie.draw.samples.SampleDrawable;

import com.razie.pub.base.NoStatics;
import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.http.LightCmdGET;
import com.razie.pub.http.LightServer;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIconRes;

/**
 * a simple no threads web server with a few echo commands and a nice stylesheet, serving file0s
 * from classpath only (the stylesheet)
 * 
 * NOTE: there is no security whatsoever
 * 
 * TODO 3-2 CODE decouple from lightsoa - the basic server shouldn't know about lightsoa directly
 * 
 * @author razvanc99
 */
public class SampleWebServer {

   @SoaMethod(descr = "echo", args = { "msg" })
   public String echo(String msg) {
      return "echo: " + msg;
   }

   @SoaMethod(descr = "ask the server to die")
   public String die() {
      server.shutdown();
      return "dying...";
   }

   @SoaMethod(descr = "demo for drawing models")
   @SoaStreamable
   public void sampleDrawable(DrawStream out) {
      SampleDrawable d = new SampleDrawable();
      Object ret = d.render(out.getTechnology(), out);
      if (ret != null)
         out.write(ret);
   }

   static Integer PORT = 4445;
   static AgentHandle            ME      = new AgentHandle("localhost", "localhost", "127.0.0.1", PORT
         .toString(), "http://localhost:" + PORT.toString());

   public static void main(String[] argv) throws IOException {
      RazIconRes.init();
      new SampleWebServer().start(ME, new AgentCloud(ME));
   }

      LightServer server = null;// setup in start
      
   public void start(AgentHandle me, AgentCloud cloud) {
      // stuff to set before you start the server
      HtmlRenderUtils.setTheme(new HtmlRenderUtils.DarkTheme());

      NoStatics.put(Agents.class, new Agents(cloud, me));
      
      LightCmdGET cmdGET = new SimpleClasspathServer();
      server = new SimpleNoThreadsServer(me, cmdGET);

      cmdGET.registerSoa(new HttpSoaBinding(this, "service"));

      Log.logThis("Starting simple web server at: " + me);
      Log.logThis(" - try simple url like: " + me.url + "/");

      // start the server thread...
      server.run();
   }
}

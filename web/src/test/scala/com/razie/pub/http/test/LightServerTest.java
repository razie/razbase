/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.http.test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.SimpleActionToInvoke;
import com.razie.pub.http.sample.SampleWebServer;

/**
 * test the light server
 * 
 * @author razvanc99
 */
public class LightServerTest extends LightBaseTest {

   /** test the simple echo - this is like GET but is called 'echo' for testing of flexibility of extending protocol
    *
    * TODO that this fails when laptop not connected to any network (i.e. when commuting)- don't know why...
    */
   public void testSimpleEcho() throws IOException, InterruptedException {
      // start server with echo impl
      SampleEchoCmdHandler echo = new SampleEchoCmdHandler();
      server.registerHandler(echo);

      Thread.sleep(200);

      // send echo command
      Socket remote = new Socket("localhost", PORT);
      PrintStream out = new PrintStream(remote.getOutputStream());
      out.println("echo samurai");
      out.close();

      // wait a bit for receiver thread to consume...
      for (long deadline = System.currentTimeMillis() + 2000; deadline > System.currentTimeMillis();) {
         Thread.sleep(100);
         if (echo.input != null) {
            break;
         }
      }
      server.removeHandler(echo);
      assertTrue("echo.input did not receive string from socket...", echo.input != null && echo.input.contains("samurai"));
   }

   /**
    * test the sample server - just start it as it's defined and check it out - sample class should work always...
    */
   public void testSampleWebServer() throws IOException, InterruptedException {
      // start server with different port so it doesn't clash with the test server
      // also, since it's not threaded, we have to thread it...
      Thread runner = new Thread() {

         public void run() {
            new SampleWebServer().start(MEPLUS1, new AgentCloud(MEPLUS1));
         }
      };
      // test doens't stop until the server actually died - stupid way ot test the server dying
      // runner.setDaemon(true);
      runner.start();

      // give it some time to start up...
      Thread.sleep(250);

      // send echo command
      ActionToInvoke action = new SimpleActionToInvoke(MEPLUS1.url, new ActionItem("service/echo"), "msg", "samurai");
      String result = (String) action.act(null);
      assertTrue(result.contains("samurai"));
      assertTrue(result.replaceFirst("style.*\\.css", "YEAHMAN").contains("YEAHMAN"));

      // send echo command
      action = new SimpleActionToInvoke(MEPLUS1.url, new ActionItem("service/die"));
      result = (String) action.act(null);

      runner.join();
   }
}

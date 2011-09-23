/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http.test;

import junit.framework.TestCase;

import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.LightAuth;
import com.razie.pub.http.LightCmdGET;
import com.razie.pub.http.LightCmdPOST;
import com.razie.pub.http.LightContentServer;
import com.razie.pub.http.LightServer;

/**
 * sample of setting up individual agent tests
 * 
 * @author razvanc99
 */
public class LightBaseTest extends TestCase {
   protected LightServer         server;
   protected Thread              serverThread;

   public static Integer         PORT    = 4445;
   protected static LightCmdGET  cmdGET  = new LightCmdGET();
   protected static LightCmdPOST cmdPOST = new LightCmdPOST(cmdGET);

   static AgentHandle            ME      = new AgentHandle("localhost", "localhost", "127.0.0.1", PORT
                                               .toString(), "http://localhost:" + PORT.toString());
   static AgentHandle            MEPLUS1 = new AgentHandle("localhost", "localhost", "127.0.0.1", String
                                               .valueOf(PORT + 1), "http://localhost:" + (PORT + 1));

   static int                    setupc  = 0;
   static int                    teardc  = 0;

   @Override
   public void setUp() {
      if (server == null) {
         setupc++;

         // take care setting up multi-agent unit tests...
         ExecutionContext.resetJVM();
         LightAuth.init("lightsoa");

         AgentCloud group = new AgentCloud(ME);
         NoStatics.put(Agents.class, new Agents(group, ME));

         server = new LightServer(PORT, 20, null, new LightContentServer(null));
         server.registerHandler(cmdGET);
         server.registerHandler(cmdPOST);

         // you can start the server in its dedicated thread or use a pool
         serverThread = new Thread(server, "AgentServerThread");

         // for testing, we want it to die at the end
         serverThread.setDaemon(false);

         // start the server thread...
         serverThread.start();
      }
   }

   @Override
   public void tearDown() {
      if (server != null) {
         teardc++;
         server.shutdown();
         try {
            serverThread.join();
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         server = null;
      }
   }

   public void testNothing() {}
   
   static final Log logger = Log.factory.create(LightBaseTest.class.getName());
}

/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import razie.base.life.Being

import razie.base.ActionItem;
import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.MyServerSocket;

/**
 * Simple/light socket server: spawns receiver threads to process connections. You can use it not
 * just for http...just derive and overwrite the makeReceiver() to use a fancier receiver
 * 
 * <p>
 * As is, you can hook it up with the {@link com.razie.pub.http.LightCmdGET} and implement simple
 * services or lightsoa bindings...that's usually enough. ROADMAP for usage: define your lightsoa
 * classes, and register them with the server and start the server...that's it.
 * 
 * <p>
 * See self-documented samples in {@link com.razie.pub.http.test.TestLightServer} which does
 * everything you can with the server.
 * 
 * <p>
 * http arguments inthe request will be populated by LightReceiver in the executioncontext: 
 *     ExecutionContext.instance().setLocalAttr("httpattrs", httpattrs);
 *
 * <p>
 * derived from kieser.net sample
 * 
 * construct a server - will open the socket and you'll have to invoke run/process on a thread
 * to start accepting connections
 * 
 * @param port - the port to listen to
 * @param maxConnections - set this to something nonzero to limit the number of connections accepted in parallel 
 * 
 * @author razvanc99
 */
class LightServer(val port: Int, val maxConnections: Int, val mainContext: ExecutionContext, var contents: ContentServer)
  extends Being with Runnable {
  // TODO find an icon for this
  override def whoAreYou = new ActionItem("SocketServer"); //TODO constant
  override def whatAreYouDoing = new ActionItem("Listening..."); //TODO constant

  var currConnections = 0;
  def lessConns() { currConnections -= 1 } // TODO java compat
  def enterCtx() { if (mainContext != null) mainContext.enter } // TODO java compat

  var listener: ServerSocket = listen

  def listen: ServerSocket = {
    // not sure why i try 3 times, but...seems sturdier than not ;)
    var listener: ServerSocket = null
    var i = 0

    while (i < 3) {
      try {
       listener = new ServerSocket(port);
        i = 3 // connected, stop trying
        razie.Audit ("HTTP_INIT LightServer listening on port " + listener.getLocalPort + " at localsoecketaddress " + listener.getLocalSocketAddress)
      } catch {
        case be: java.net.BindException =>
          Log.alarmThisAndThrow("HTTP_ERR_BIND CANNOT bind socket - another program is using it: port=" + port + " ", be);
        case ioe: IOException =>
          Log.alarmThisAndThrow("HTTP_ERR IOException on socket listen: ", ioe);

          // TODO why do i sleep here? forgot to comment...
          Thread.sleep(500);
      }
    }
    listener
  }

  // Listen for incoming connections and handle them
  def run() {

    if (mainContext != null)
      mainContext.enter();

    while ((currConnections < maxConnections) || (maxConnections == 0)) {
      try {
        synchronized {
          currConnections = currConnections + 1
        }

        // i guess i have to die now...
        if (listener.isClosed()) {
          return ;
        }
        val server = listener.accept();
        val conn_c = makeReceiver(new MyServerSocket(server));
        Log.logThis("HTTP_CLIENT_IP: " + server.getInetAddress().getHostAddress());
        runReceiver(conn_c);
      } catch {
        case ioe: IOException => {
          // sockets are closed onShutdown() - don't want irrelevant exceptions in log
          if (ioe.getMessage().equals("Socket closed"))
            Log.logThis("socket closed...stopped listening: " + ioe.getMessage());
          else
            Log.logThis("IOException on socket listen: " + ioe, ioe);
        }
      }
    }
  }

  /** main factory method - overload this to create your own receivers */
  def makeReceiver(socket: MyServerSocket): SocketReceiver = {
    val in = socket.getInputStream();

    // need to detect if the client is in line mode (i.e. http) or not
    Thread.sleep(100)
    razie.Debug("AVAIL " + in.available)

    if (in.available > 3)
      this mkWebServer socket
    else
      this mkTelnetServer socket
  }

  def mkWebServer(socket: MyServerSocket): SocketReceiver =
    new LightReceiver(this, socket, contents)
  def mkTelnetServer(socket: MyServerSocket): SocketReceiver =
    new TelnetReceiver(socket, contents)

  /** if you have a special thread handling, overload this and use your own threads */
  def runReceiver(conn_c: SocketReceiver) {
    if (conn_c.isInstanceOf[Runnable]) {
      val t = new Thread(conn_c.asInstanceOf[Runnable]);
      t.setName("AgentReceiver" + t.getName());
      t.start();
    } else conn_c.run()
  }

  def shutdown() {
    try {
      // it may be null if it didn't initialize properly (socket busy) - no reason to fail shutdown...
      if (this.listener != null) {
        this.listener.close();
        razie.Audit ("HTTP_SHUTDOWN LightServer listener closed on port " + this.listener.getLocalPort + " at localsoecketaddress " + this.listener.getLocalSocketAddress)
      }
    } catch {
      case ioe: IOException => {
        Log.logThis("IOException on socket close: " + ioe);
        ioe.printStackTrace();
      }
    }
  }

  val logger = razie.Log;

  def registerHandler(c: SocketCmdHandler) { contents.asInstanceOf[LightContentServer] registerHandler c }

  def removeHandler(c: SocketCmdHandler) { contents.asInstanceOf[LightContentServer] removeHandler c }

  /**
   * @return the listeners
   */
  def getHandlers(): java.util.List[SocketCmdHandler] = contents.asInstanceOf[LightContentServer].getHandlers
}

trait SocketReceiver {
  def run(): Unit
}

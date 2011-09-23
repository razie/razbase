/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.MyServerSocket;

    /**
     * receiver: spawned by the server to handle an incoming connection
     * 
     * <p>
     * This default receiver will read lines until it finds a non-empty and then will find the
     * command listener that can handle the first word and invoke it. The result will be written
     * back to the socket. This is good for simple command listeners, including ftp, http...
     * 
     * TODO derive from MTWrkRq
     */
/**
 * receiver: spawned by the server to handle an incoming connection
 * 
 * <p>
 * This default receiver will read lines until it finds a non-empty and then will find the
 * command listener that can handle the first word and invoke it. The result will be written
 * back to the socket. This is good for simple command listeners, including ftp, http...
 * 
 * TODO derive from MTWrkRq
 */
class LightReceiver implements SocketReceiver, Runnable {
    protected MyServerSocket socket;
    protected LightServer    server;
    protected ContentServer    cs;

    protected LightReceiver(LightServer server, MyServerSocket socket, ContentServer cs) {
        this.server = server;
        this.socket = socket;
        this.cs = cs;
    }

    @Override
    public void run() {
       receive();
       synchronized (server) {
       server.lessConns();
       }
    }
    
    public void receive() {
        server.enterCtx();

        try {
          // try keepalive
          while (! socket.server.isClosed()) {

            String input = "";

            // don't know why but i can't use this compliant reader...
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));

            // because i'm using the buffered reader here, i cannot use
            // the input socket
            // anymore afterwards...I probably need to modify my socket to
            // include the buffered
            // reader right there
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));

            // Get input from the client
            DataInputStream in = new DataInputStream(socket.getInputStream());
            PrintStream out = new PrintStream(socket.getOutputStream());

            // TODO 3 why do i flush empty lines?
            // consume the input until a non-emtpty line
        while (input == null || input.length() <= 0) {
           input = in.readLine();
           if (input == null) {
              logger.alarm("ERR_SOCKET_EOF socket had EOF before any byte...dropping connection");
              return ;
           }
        }

            logger.trace(3, "INPUT:\n", input);

            // finish reading the input stream until there's nothing else...this will get
            // the entire command

            // reading the http args
            // in http 1.1 i have to read an empty line...
            String rest = in.readLine();
            AttrAccess httpattrs = new AttrAccessImpl();
            while (rest != null && rest.length() > 0) {
               String s[] = rest.split(": ");
               httpattrs.setAttr(s[0], s[1]);
                rest = in.readLine();
            }

           httpattrs.set("RemoteIP", socket.server.getInetAddress().getHostAddress());
           
           ExecutionContext.instance().setLocalAttr("httpattrs", httpattrs);
           logger.trace(3, "   HTTPATTRSINPUT:\n" + httpattrs.toString());

           // don't remember if this code was here to help stupid browsers that way for consumption
           // or not - it messess up handling of POST so i commented it out May'09
//         while (in.available() > 0) {
//           String moreInput = in.readLine();
//           logger.trace(3, "   >>> MOREINPUT:\n", "" + moreInput);
//         }

            handleInputLine(out, input, httpattrs);

            // this is used to test raw performance of socket/http, wihtout handling the input line above
//            DrawStream oo = new HttpDrawStream(socket.from, socket.getOutputStream(), false);
//            oo.write("dudu\n");
//            socket.getOutputStream().flush();
//            socket.getOutputStream().flush();

// TODO try keepalive
                                // if the client is done...
//                 if (socket.server.isInputShutdown())
                      socket.close();

                        } // while

        } catch (Exception ioe) {
            // must catch all exceptions to avoid screwing up something bad...don't remember
            // what
            logger.log("IOException on socket listen: ", ioe);
            ioe.printStackTrace();
        } finally {
        try {
           socket.close();
        } catch (IOException e) {
            Log.logThis("IGNORING: ", e);
        }
            ExecutionContext.exit();
        }
        
        ; // nothing - i closed the socket
    }

        protected void handleInputLine(PrintStream out, String input, AttrAccess httpattrs) {
           Object reply = cs.exec(input, "http", new Properties(), socket, httpattrs);

            if (reply != null) {
                if (!(reply instanceof StreamConsumedReply))
                    out.print(reply);
                // out.println(reply);
                Log.traceThis("HTTP_CLIENT_SERVED");
            } else {
                logger.trace(3, "command listener returned nothing...");
            }
        }

    static final Log logger = Log.factory.create (LightReceiver.class.getSimpleName());
}

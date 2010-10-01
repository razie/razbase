/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.http.sample;

import com.razie.pub.comms.AgentHandle;
import com.razie.pub.http.LightContentServer;
import com.razie.pub.http.LightServer;
import com.razie.pub.http.SocketCmdHandler;
import com.razie.pub.http.SocketReceiver;

/**
 * this is a server that doesn't want to use threads, will handle one request at a time...not sure
 * why you'd do that, but hey...
 *
 * @author razvanc99
 */
public class SimpleNoThreadsServer extends LightServer {

    public SimpleNoThreadsServer(AgentHandle h, SocketCmdHandler... handlers) {
        super(Integer.parseInt(h.port), 10, null, new LightContentServer(null));
        for (SocketCmdHandler cmd : handlers)  registerHandler(cmd);
    }

    /** if you have a special thread handling, overload this and use your own threads */
    @Override
    public void runReceiver(SocketReceiver conn_c) {
        conn_c.run();
    }
}

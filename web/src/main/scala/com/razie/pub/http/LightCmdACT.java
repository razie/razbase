package com.razie.pub.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;

import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;

/**
 * POST is the same as GET but no reply expected...
 * 
 * TODO implement this version of ACT
 * 
 * @author razvanc
 * 
 */
public class LightCmdACT extends LightCmdGET {

    public Object execServer(String cmdName, String protocol, String args, Properties parms,
            MyServerSocket socket) throws AuthException {
        String input = "";
        try {
            // POST has parms after all the http stuff already read by server. read them like this since there's no CRLF
            // TODO should use the content-length so i don't miss input in large requests, maybe
            
            // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            if (in.available() > 0) {
                byte[] cbuf = new byte[1000];
                int c = in.read(cbuf);
                input = new String(cbuf, 0, c);
                Log.traceThis("ACT INPUT=" + input);
            }
        } catch (IOException e) {
            Log.logThis("", e);
        }

        // decode the parms
        String[] pairs = input.split("&");
        for (String pair : pairs) {
            String[] split = pair.split("=", 2);
            if (split.length > 1)
                parms.put(split[0], HttpUtils.fromUrlEncodedString(split[1]));
        }

        return super.execServer(cmdName, protocol, args, parms, socket);
    }

    public String[] getSupportedActions() {
        return COMMANDS;
    }

    static final String[] COMMANDS = { "ACT" };
    static final Log      logger   = Log.factory.create("", LightCmdACT.class.getName());
}

/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.comms;

import java.net.Socket;
import java.net.URL;


/**
 * a communication channel serves two endpoints.
 * 
 * it can serve multiple streams between the two end-points. can have state and not reconnect. it
 * can reconnect automatically etc. The idea is that the client code doesn't care about the actual
 * communication channel technology and optimizations/implementation, i.e. SCTP vs TCP. $
 * 
 * this is also so virtual that it can connect two agents via a proxy. it can also accomodate multiple sessions in parallel.
 * 
 * @author razvanc99
 * 
 */
public class CommChannel {
    // TODO add notional direction, i.e. who is me?
    public ChannelEndPoint from, to;
   
    // the current level of auth of the peer. Normally I am "to" and the peer if "from"
    protected LightAuthType auth = LightAuthType.ANYBODY;

    public CommChannel(LightAuthType ia) {
        auth = ia;
    }

    protected LightAuthType getAuth() { return auth; };
    
    /**
     * this is a client requesting something
     */
    public static class SocketEndPoint extends ChannelEndPoint {
        public java.net.InetAddress address;
        private String ip;
        private String port;

        public SocketEndPoint(Socket sock) {
            super();
            this.address = sock.getInetAddress();
            this.ip = sock.getInetAddress().getHostAddress();
            this.port = String.valueOf(sock.getPort());
        }

        public String getIp(){return ip;}

        @Override
        public String getPort() {
            return port;
        }
        
        @Override
        public String toString() {
            return address.toString() + " IP=" + ip + " PORT="+port;
        }
    }

    /**
     * simple URL based channel end=point. The channel is only interested in the server:port part
     */
    public static class URLChannelEndPoint extends ChannelEndPoint {
        URL    url;
        String surl;

        public URLChannelEndPoint(String surl) {
            super();
            this.surl = surl;
        }

        public URLChannelEndPoint(URL url) {
            super();
            this.url = url;
        }

        @Override
        public String getIp() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPort() {
            throw new UnsupportedOperationException();
        }
    }
}

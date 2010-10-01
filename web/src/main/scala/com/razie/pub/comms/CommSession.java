/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.comms;


/**
 * a communication session serves a logical communication sequence between two endpoints. multiple sessions are multiplexed over a channel. the channel is secured or not and can also be proxied or not.
 * 
 * the session is established for a purpose and supports a dialog - a sequence of requests and replies - after which it is destroyed.
 * 
 * @author razvanc99
 */
public class CommSession {
    public CommChannel myChannel;

    public CommSession (CommChannel chan) {
        myChannel = chan;
    }
}

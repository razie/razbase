/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

import java.io.InputStream;
import java.net.URL;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.CommChannel;
import com.razie.pub.comms.Comms;

/**
 * a two way communication stream. a stream serves one logical purpose, such as making a soa call or
 * grabbing a remote file.
 * 
 * normally this is used for a single RR (request & its response). The request could be the URL to
 * connect to (i.e. servlet) or something sent to an URL (soap).
 * 
 * optimizing the underlying communication is the job of the channel, which can handle multiple
 * streams. For instance, the channel could keep alive a TCP stream if it detects heavy traffic.
 * 
 * if you don't need a full stream created (could be heavy when serving high-volumes) just go strait
 * to the channel and ask it to read/write a message
 * 
 * @author razvanc99
 * 
 */
public class CommStream {
    private CommChannel     myCommChannel;
    protected InputStream is;

    /** empty channel - is is null */
    protected CommStream(CommChannel channel) {
        this.myCommChannel = channel;
    }

    /** creates a comm channel with the remote URL */
    public CommStream(URL url) {
        this((CommChannel)null);
        this.is = Comms.streamUrl(url.toExternalForm());
    }

    /** creates a comm channel with the remote URL */
    public CommStream(String url) {
        this((CommChannel)null);
        this.is = Comms.streamUrl(url);
    }

    /** if you already got the stream and want to wrap it for some reason ;) */
    public CommStream(InputStream is2) {
        this.is = is2;
    }

    /** will read all incoming until the channel is empty */
    public String readAll() {
        return readStreamImpl();
    }

    public InputStream inputStream() {
        return is;
    }

    protected String readStreamImpl() {
        return Comms.readStream(is);
    }

    static Log logger = Log.factory.create(CommStream.class.getName());
}

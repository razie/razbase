/**
 * communications framework
 * 
 * What I'm trying to do here is decouple client/server communication from the actual communication
 * channel implementation. You can then swap actual comm channel technology (i.e. http to tcp, tls,
 * SCTP etc). A communication channel supports many streams. A stream is what's currently understood
 * by an http call - normally a request/response session.
 * 
 * CommChannels are identified by their end-points and preferences. They are automatically picked up
 * and used by the streams. Client code deals only with streams.
 * 
 * There's also a few streams, such as filtered string stream, for your convenience.
 * 
 * CURRENT STATE: quite unclear, if you ask me - just a bunch of utilities for now :)
 * 
 * @version $Id: package-info.java,v 1.1 2007-10-02 11:54:36 razvanc Exp $
 */
package com.razie.pubstage.comms;


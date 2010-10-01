/**
 * Light SOA = reflected wrapping of method calls for remote services and assets.
 * 
 * <p>
 * This package intends to eliminate much of the crap one has to deal with when writing any kind of
 * service accessible remotely. There's a myriad of protocols today and each has its own quirks -
 * here's one more!
 * 
 * <p>
 * Just mark a method to be called with the "@SoaMethod", listing the parameter names. Then wrap the
 * object in a binding wrapper and register that with whatever protocol server you need. So far I
 * have http and upnp bindings. One could easily add SIP...whatnot.
 * 
 * <p>
 * Currently, the parms are just Strings, but since they all get serialized somehow, what's the big
 * deal? Easy way to decouple code - simple event/messaging system.
 * 
 * <h2>Guide</h2>
 * 
 * Create a class and annotate (optional) with {@link SoaService}. Then the different methods you
 * want available remote, annotate with {@link SoaMethod}.
 * 
 * <p>
 * See the other annotations for more options. See the test samples in the test package for
 * examples.
 * 
 * <h2>Use Cases</h2>
 * 
 * <ul>
 * <li>Implement a simple service: use SoaService, SoaMethod and HttpSoaBinding
 * <li>Add a new binding - copy and customize the HttpSoaBinding for a new protocol
 * </ul>
 * 
 * @version $Id: package-info.java,v 1.1 2007-10-02 11:54:36 razvanc Exp $
 */
package com.razie.pub.lightsoa;


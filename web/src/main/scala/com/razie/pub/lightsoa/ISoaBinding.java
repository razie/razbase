/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.lightsoa;

import java.util.Properties;
import java.util.Set;

import com.razie.pub.comms.MyServerSocket;

/**
 * lightsoa services need binding to a certain protocol...bindings are instances of this. You can
 * bind a service to more than one protocols...
 * 
 * each binding must support methods returning: SoaResponse, void and String
 * 
 * Also, each binding will escape and un-escape each input and output argument properly
 * 
 * <p>
 * This base class just implements some common functionality, it has no knowledge of a particular
 * protocol.
 * 
 * @author razvanc99
 * 
 */
public interface ISoaBinding {
  public Set<String> getSoaMethods();

  /**
   * main entry point from the http server
   * 
   * @param actionName the command code == soa method name
   * @param protocol protocol normaly "http"
   * @param cmdargs args in the url following the method name with, think servlet entry point
   *        "mymethod/a/b"
   * @param parms all parms in the url decoded parms follow the url with ? and &
   * @param socket the server socket
   * @return
   */
  public Object execServer(String actionName, String protocol, String cmdargs, Properties parms,
      MyServerSocket socket);

  public String getServiceName();
}

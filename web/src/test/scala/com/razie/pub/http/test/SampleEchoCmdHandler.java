/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.http.test;

import java.util.Properties;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.http.SocketCmdHandler;

/**
 * sample command handler implementing the echo command, as a simple url mapping
 *
 * @author razvanc99
 */
public class SampleEchoCmdHandler extends SocketCmdHandler.Impl {

   public String input = null;

   @Override
   public Object execServer(String cmdName, String protocol, String args, Properties parms,
           MyServerSocket socket) throws AuthException {
      input = cmdName + ": " + args;
      String m = "execute cmdName=" + cmdName + ", protocol=" + protocol + ", args=" + args;
      Log.logThis(m);
      return args;
   }

   @Override
   public String[] getSupportedActions() {
      return COMMANDS;
   }
   
   public static final String[] COMMANDS = {"echo"};
}

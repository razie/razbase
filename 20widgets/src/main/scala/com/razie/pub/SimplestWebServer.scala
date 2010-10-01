/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

import razie.base.ActionItem;
//import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.base.log.Log;

/**
 * simplest web server: open socket, wait for GET, serve and die
 * 
 * <p>
 * derived from kieser.net sample
 * 
 * @author razvanc99
 * @version 1.0
 */
case class SimplestWebServer (val port:Int) (content: => String) {
   val listener = new ServerSocket(port)
   val socket = listener.accept();

   var input = "";
   val in = new DataInputStream(socket.getInputStream());
   val out = new PrintStream(socket.getOutputStream());
   
   // some browsers are stupid: consume the input until a non-emtpty line
   while (input == null || input.length() <= 0) 
      input = in.readLine();

   // finish reading the input stream until there's nothing else...this will get
   // the entire command
   val rest = in.readLine();

   val reply = content
   if (! reply.contains("HTTP/1")) {
      out.println("HTTP/1.1 200 OK");
      out.println("Content-Type: text/text");
      out.println("");
   }
   out.print(reply);
//   out.println("");
   
   socket.close();
}

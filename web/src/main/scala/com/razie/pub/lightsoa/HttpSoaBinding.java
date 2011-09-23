/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.lightsoa;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import razie.base.scripting.ScriptContext;
import razie.base.scripting.ScriptFactory;
import razie.draw.DrawStream;
import razie.draw.HttpDrawStream;
import razie.draw.JsonDrawStream;
import razie.draw.MimeDrawStream;
import razie.draw.SimpleDrawStream;
import razie.draw.Technology;
import razie.g.GRef;

import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.http.SoaNotHtml;
import com.razie.pub.http.StreamConsumedReply;

/**
 * call services or assets via simple http requests
 * 
 * can handle methods that take only String arguments, including SoaStreamables, which return one
 * of: void, String, Drawable, Object - the return value will be drawn on an HttpDrawStream
 * 
 * use SoaNotHtml to prevent the return String from being drawn on html...
 * 
 * as a rule of thumb in implementing these, the URLs are of the form /PREFIX/serviceName/METHODNAME
 * 
 * @author razvanc99
 */
public class HttpSoaBinding extends SoaBinding {

   /**
    * create a simple binding for a service instance - you then have to register it with the server
    * 
    * if the class is annotated, use the other constructor, please...
    * 
    * @param service the object implementing the service methods
    * @param serviceName the name to use - no funky characters, other than ".". Especially no
    *        spaces, eh?
    */
   public HttpSoaBinding(Object service, String serviceName) {
      this(service.getClass(), serviceName);
      this.service = service;
   }

   /**
    * create a simple binding - you then have to register it with the server
    * 
    * if the class is annotated, use the other constructor, please...
    * 
    * @param service the class of the callback implementing the service/asset methods
    * @param serviceName the name to use - no funky characters, other than ".". Especially no
    *        spaces, eh?
    */
   public HttpSoaBinding(Class<?> serviceCls, String serviceName) {
      super(serviceCls, serviceName);

      // check service name matches annotation
      if (serviceCls != null && serviceCls.getAnnotation(SoaService.class) != null) {
         SoaService s = (SoaService) serviceCls.getAnnotation(SoaService.class);
         if (!s.name().equals(serviceName))
            throw new IllegalArgumentException(
                  "can't bind @SoaService/@SoaAsset with wrong name: " 
                        + service.getClass().getName());
      } else if (serviceCls != null && serviceCls.getAnnotation(SoaAsset.class) != null) {
         SoaAsset s = (SoaAsset) serviceCls.getAnnotation(SoaAsset.class);
         if (s.meta().length() > 0 && !s.meta().equals(serviceName))
            throw new IllegalArgumentException(
                  "can't bind @SoaService/@SoaAsset with wrong name: " 
                        + serviceCls.getName());
      } else {
         logger.log("WARN_HTTP_BOUND class which was not annotated: "
               + (serviceCls == null ? "null" : serviceCls.getName()));
      }
   }

   /**
    * create a simple binding for and annotated SoaService - you then have to register it wiht the
    * server
    * 
    * @param service the object implementing the service methods
    * @param serviceName the name to use - no funky characters, other than ".". Especially no
    *        spaces, eh?
    */
   public HttpSoaBinding(Object service) {
      super(service, "");
      if (service.getClass().getAnnotation(SoaService.class) != null) {
         SoaService s = (SoaService) service.getClass().getAnnotation(SoaService.class);
         this.serviceName = s.name();
      } else {
         throw new IllegalArgumentException("can't bind service not annotated with @SoaService: "
               + service.getClass().getName());
      }
   }

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
   @SuppressWarnings("unused")
public Object execServer(String actionName, String protocol, String cmdargs, Properties parms,
         MyServerSocket socket) {

      Object otoi = this.service;
      GRef key = null;
      Method method = null;

      if (otoi == null) {
         // must be an asset instance
         if (actionName.startsWith(razie.G$.MODULE$.RAZIE())) {
            key = razie.g.GRef$.MODULE$.parse(HttpUtils.fromUrlEncodedString(actionName));
//         if (actionName.startsWith(AssetKey$.MODULE$.PREFIX())) {
//            key = AssetKey$.MODULE$.fromString(HttpUtils.fromUrlEncodedString(actionName));
            String[] ss = cmdargs.split("/", 2);
            actionName = ss[0];
            cmdargs = ss.length > 1 ? ss[1] : null;
         } else {
            String[] ss = cmdargs.split("/", 3);
//            key = new AssetKey(actionName, HttpUtils.fromUrlEncodedString(ss[0]),null);
            key = razie.g.GRef$.MODULE$.id(actionName, HttpUtils.fromUrlEncodedString(ss[0]),null);
            // by default actionName would be "details"
            actionName = ss.length > 1 ? ss[1] : "details";
            cmdargs = ss.length > 2 ? ss[2] : null;
         }

         otoi = razie.g.GAMResolver$.MODULE$.jresolve(key);
      }

      if (otoi == null) {
         logger.trace(1, "HTTP_SOA_ASSETNOTFOUND: " + key);
         return "HTTP_SOA_ASSETNOTFOUND: " + key;
      }

      Object response = null;
      DrawStream out = null;

      // this section used to test performance...
//      if (true) response = "dudu";
//      else
      if (methods.size() <= 0) {
         // didn't find it but there's no methods for this anyhow...
         logger.trace(1, "HTTP_SOA_delegateTo_AssetMgr.doAction: " + actionName + ": ");
         ScriptContext ctx = ScriptFactory.mkContext("scala");
         ctx.setAttr(parms);
         response = razie.g.GAMAct$.MODULE$.act(key, actionName, ctx);
      } else {
         if (methods.containsKey(actionName)) {
            method = methods.get(actionName);
         } else if (sink != null) {
            method = sink;
         } else if (methods.size() > 0 && !methods.containsKey(actionName)) {
            logger.log("HTTP_SOA_UNKWNOWNACTION: " + actionName);
            return "HTTP_SOA_UNKNOWNACTION: " + actionName;
         }

         if (method != null) {
            logger.trace(1, "HTTP_SOA_" + actionName + ": ");

            AttrAccess args = new AttrAccessImpl(parms);

            // setup the parms
            SoaMethod mdesc = method.getAnnotation(SoaMethod.class);

            socket.auth(mdesc.perm());

            if (method.getAnnotation(SoaStreamable.class) != null) {
               SoaStreamable nh = method.getAnnotation(SoaStreamable.class);
               if (nh.mime().length() > 0) {
                  out = makeMimeDrawStream(socket, nh.mime());
               } else
                  out = makeDrawStream(socket, protocol);
               response = invokeStreamable(otoi, actionName, out, args);
            } else {
               response = invoke(otoi, actionName, args);
            }

            if (method.getAnnotation(SoaNotHtml.class) != null) {
               if (method.getAnnotation(SoaStreamable.class) != null) {
                  throw new IllegalArgumentException("Cannot have a streamable nothtml");
               }
               // no special formatting, probably defaults to toString()
               return response;
            }
         }
      }

      if (response != null) {
         // maybe stream already created for a streamable that returned a Drawable?
         // unbelievable...
         out = out != null ? out : makeDrawStream(socket, protocol);
         out.write(response);
         out.close();
         return new StreamConsumedReply();
      } else if (response == null) {
         if (out != null)
            out.close();
         return new StreamConsumedReply();
      }
      return response;
   }

   protected DrawStream makeDrawStream(MyServerSocket socket, String protocol) {
      DrawStream out;
      try {
         if ("http".equals(protocol))
            out = new HttpDrawStream(socket.from, socket.getOutputStream());
         else if ("json".equals(protocol))
            out = new JsonDrawStream(socket);
         else
            out = new SimpleDrawStream(Technology.TEXT, socket.getOutputStream());
      } catch (IOException e2) {
         throw new RuntimeException(e2);
      }
      return out;
   }

   protected DrawStream makeMimeDrawStream(MyServerSocket socket, String mime) {
      DrawStream out;
      try {
         if (HttpDrawStream.MIME_TEXT_HTML.equals(mime))
            out = new HttpDrawStream(socket.from, socket.getOutputStream());
         else if (JsonDrawStream.MIME_APPLICATION_JSON.equals(mime))
            out = new JsonDrawStream(socket);
         else
            out = new MimeDrawStream(socket.getOutputStream(), mime);
      } catch (IOException e2) {
         throw new RuntimeException(e2);
      }
      return out;
   }

   public String toString() {
      return this.serviceName + " : "
            + (this.service == null ? "NULL SERVICE - probably the asset service?" : this.service.getClass().getName());
   }

   private static final Log logger = Log.factory.create("http", HttpSoaBinding.class.getName());
}

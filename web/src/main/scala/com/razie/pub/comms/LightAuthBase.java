/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.comms;

import java.net.Socket;

import razie.base.AttrAccess;

import com.razie.pub.base.NoStaticSafe;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.log.Log;

/**
 * auth providers have, for me, several responsibilities - see the static methods below
 * 
 * this default will do nothing
 * 
 * NOTE that authentication providers are supposed to understand different remote targets and
 * properly massage the urls accordingly. for instance, one remote could be a lightsoa server while
 * another could be a bank that needs a session token appended etc. That mechanism is implemented in
 * the razagents framework, not here. Feel free to write your own...
 * 
 * <p>
 * This simple implementation uses a prefix which precedes all URLs. This is used so that, if you
 * open a port in the firewall, I hope it would confuse any would-be attacker which just scanned
 * your port, since the mutant wouldn't say a word without the prefix.
 * 
 * TODO 1-2 AA should be custom per endpoint (same agent can use other agents with different AA
 * capabilities). Also, the AA can be negociated with the endpoints. Not sure how this will hookup
 * with the comms channel framework.
 * 
 * @author razvanc99
 */
@NoStaticSafe
public class LightAuthBase {
   protected String       prefix = "";
   private boolean locked = false;

   public LightAuthBase(String prefix) {
      // be nice
      if (prefix.startsWith("/"))
         this.prefix = prefix.substring(1);
      else
         this.prefix = prefix;
   }

   /** @deprecated use instance() */
   protected static LightAuthBase singleton() {
      return instance();
   }

   public static LightAuthBase instance() {
      if (NoStatics.get(LightAuthBase.class) == null) {
         // try to reuse the one set in the main thread
         if (NoStatics.root().getLocal(LightAuthBase.class) == null)
            NoStatics.put(LightAuthBase.class, new LightAuthBase(""));
         else
            NoStatics.put(LightAuthBase.class, NoStatics.root().getLocal(LightAuthBase.class));
      }
      return (LightAuthBase) NoStatics.get(LightAuthBase.class);
   }

   /** initialize the (for now static) AA used in this server/client */
   public static void init(LightAuthBase impl) {
      LightAuthBase i = (LightAuthBase) NoStatics.get(LightAuthBase.class);
      if (i == null || !i.locked)
         NoStatics.put(LightAuthBase.class, impl);
   }

   /** lock it - can't reset after this */
   public static void lock() {
      instance().locked = true;
   }

   /**
    * used by client to prepare the URL just before using it.
    * 
    * in basic HTTP communication, the only way to AA is by messing with the URL - add/remove, add
    * tokens parameters etc.
    * 
    * TODO We could mess with the header parms...but...ok...in the future :)
    * 
    * @parm url the url to prepare, full form possibly including server:port
    * @return the URL to use, with AA info inserted
    */
   public static String wrapUrl(String url) {
      return instance().wrapUrlImpl(url);
   }

   public AttrAccess httpSecParms(java.net.URL url) {
      return null;
   }

   /**
    * used by server to the URL just before using it.
    * <p>
    * in basic HTTP communication, the only way to AA is by messing with the URL - add/remove, add
    * tokens parameters etc
    * <p>
    * THIS is tricky. If there is any AA information to save in a "session" on the server to be used
    * later when processing the URL, you do that any way you see fit. The only thing I will help
    * with right now is to make sure that the request is processed on the same thread that made this
    * call. Go think!
    * <p>
    * NOTE you don't have to authorize right now, since auth is supposed to be done per
    * service/asset/resource type. Just save whatever you need (token) in a session for this
    * request.
    * <p>
    * If you simply don't like this URL at all, then just throw AuthException right here
    * 
    * <p>NOTE also that this SHOULD get rid of the leading "/"
    * 
    * @parm url the url to prepare, starting with "/" and NOT including server:port
    * @return the URL to process, with AA info removed and wihtout leading "/"
    */
   public static String unwrapUrl(String url) throws AuthException {
      return instance().unwrapUrlImpl(url);
   }

   /** default impl will just prefix the url */
   protected String wrapUrlImpl(String url) {
      return prefixUrl(url, prefix);
   }

   /** default impl will just prefix the url */
   protected String prefixUrl(String url, String prefix) {
      String[] ss = url.split("://", 2);

      // be nice if the path is local
      if (ss.length == 1) {
         // if the paht was alreadey prepared, don't do it again...
         if (url != null && (url.startsWith(prefix) || url.startsWith("/" + prefix))) {
            return url;
         }

         return "/" + (prefix.length() > 0 ? (prefix) : "") + (url.startsWith("/") ? url : ("/" + url));
      }

      String[] ss2 = ss[1].split("/", 2);

      // if the paht was alreadey prepared, don't do it again...
      if (ss2[1] != null && ss2[1].startsWith(prefix)) {
         return url;
      }

      return ss[0] + "://" + ss2[0] + "/" + (prefix.length() > 0 ? (prefix + "/") : "") + ss2[1];
   }

   /**
    * default impl will just prefix the url
    * <p>
    * NOTE - derived classes should clear any session context info from previous calls here
    * <p>
    * If you simply don't like this URL at all, then just throw AuthException right here
    * <p>
    * NOTE one thing to remember is the path "/favicon.ico" - this is requested by the browsers
    * directly without asking for auth preparation or whatever, so...you must make sure you serve
    * that if you want to
    * <p>NOTE also that this SHOULD get rid of the leading "/"
    */
   protected String unwrapUrlImpl(String url) throws AuthException {
      if (url.equals("/favicon.ico") || url.startsWith("/public/")) // don't remove / from public
        return url;
      else {
         if (url.startsWith("/"+prefix)) {
           if (prefix.length() == 0) 
             url = url.replaceFirst("/", "");
           else {
             if (url.equals("/"+prefix)) 
               url = url.replaceFirst("/"+prefix, "");
             else
               url = url.replaceFirst("/"+prefix+"/", "");
           }
         }
         return url;
      }
   }

   /**
    * figure out authorization credentials in one request. NOTE that this basic implementation
    * doesn't know SHAREDSECRET and FRIEND - take the valueadd option
    * 
    * @param socket - the socket involved in the request
    * @param url - the url of the request
    * @param httpArgs - args of the http request
    * @return the auth level of the other end
    */
   public LightAuthType iauthorize(Socket socket, String url, AttrAccess httpArgs) {
      String clientip = socket.getInetAddress().getHostAddress();

      // if Agents doesn't know myself, this should succeed, it's not a proper server but maybe
      // some sort of a test???

      // TODO this auth is really weak anyways...
      Object debug1 = Agents.getMyHostName();

      Log.traceThis("AUTH_RECON: LightAuthBase- " + clientip + " / me=" + Agents.me() + " / " + debug1);

      if (Comms.isLocalhost(clientip)) {
         return LightAuthType.INHOUSE;
         // TODO is this correct in linux?
      } else if (clientip.startsWith(Agents.getHomeNetPrefix())
            || Agents.agent(Agents.getMyHostName()) == null /* TODO what is this condition? */
            || clientip.equals(Agents.me().ip)) {
         return LightAuthType.INHOUSE;
      } else {
         if (Agents.agentByIp(clientip) != null && Agents.agentByIp(clientip).isUp())
            return LightAuthType.INCLOUD;
         return LightAuthType.ANYBODY;
      }
   }

   public static LightAuthType mapAuth(PermType perm) {
      switch (perm) {
      case ADMIN:
//         return LightAuthType.INHOUSE;
         return LightAuthType.SHAREDSECRET;
      case CONTROL:
      case WRITE:
         return LightAuthType.INCLOUD;
      case VIEW:
         return LightAuthType.FRIEND;
      case PUBLIC:
         return LightAuthType.ANYBODY;
      }
      return LightAuthType.SHAREDSECRET;
   }

   public String toString() {
      return "simple LightAuthBase - no real security";
   }

   // TODO security - make these final somehow - plugns can attack by wrapping the lightauth...
   public String resetSecurity(String pwd) {
      return "NOT IMPLEMENTED - you need the advanced security from valueadd package";
   }

   public String accept(String pwd, AgentHandle who, String pk) {
      return "NOT IMPLEMENTED - you need the advanced security from valueadd package";
   }

   public String pubkey(AgentHandle who) {
      return "NOT IMPLEMENTED - you need the advanced security from valueadd package";
   }
}

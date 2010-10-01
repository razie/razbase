/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.comms;

import java.net.MalformedURLException;
import java.net.URL;

import com.razie.pub.comms.Agents;

import razie.base.ActionToInvoke;
import razie.base.ActionItem;
import razie.base.AttrAccess;
import razie.base.ActionContext;
import razie.base.BaseActionToInvoke;
import razie.draw.Drawable;

/**
 * same semantics as ActionToInvike, except it is done via POST, not GET
 * 
 * this cannot be printed to a web page...since you can't
 * 
 * @author razvanc99
 */
public class POSTActionToInvoke extends BaseActionToInvoke implements Cloneable, AttrAccess, Drawable {
   /**
    * constructor
    * 
    * @param target the prefix used depending on the drawing technology - for http, it's the URL to
    *        append to
    * @param item this is the action, contains the actual command name and label to display
    * @param pairs
    */
   public POSTActionToInvoke(String target, ActionItem item, AttrAccess postArgs, Object... pairs) {
      super(target, item, pairs);
      this.postArgs = postArgs;
   }

   /**
    * constructor
    * 
    * @param target the prefix used depending on the drawing technology - for http, it's the URL to
    *        append to
    * @param item this is the action, contains the actual command name and label to display
    * @param pairs
    */
   public POSTActionToInvoke(ActionItem item, AttrAccess postArgs, Object... pairs) {
      super(Agents.me().url, item, pairs);
      this.postArgs = postArgs;
   }

   // TODO 3-1 CODE clone args and postargs
   public POSTActionToInvoke clone() {
      return new POSTActionToInvoke(this.target, this.actionItem.clone(), this.postArgs, this.toPairs());
   }

   /**
    * should not tie this to actual technology, but URLs are the most common form of invoking
    * actions
    */
   public String makeActionUrl() {
      String url = target.endsWith("/") ? target : target + "/";
      url += actionItem.name;
      url = addToUrl(url);
      return LightAuthBase.wrapUrl(url);
   }

   /**
    * this will take an URL and parse it into the same action object it created it... could be
    * useful, so we'll implement then - for now it documents the idea
    */
   public static ActionToInvoke fromActionUrl(String url) {
      // TODO FUTURE-implement
      return null;
   }

   /**
    * execute this action in a given context. The context must include me as well?
    * 
    * default implementation assumes i need to call an url and get the first line of response
    */
   public Object act(ActionContext ctx) {
      try {
         URL url = new URL(this.makeActionUrl());
         // TODO implement the POST
         return Comms.readUrl(url.toExternalForm());
      } catch (MalformedURLException e) {
         throw new RuntimeException("while getting the command url: " + this.makeActionUrl(), e);
      }
   }

   @Override
   public ActionToInvoke args(Object... args) {
      return new POSTActionToInvoke(this.target, this.actionItem.clone(), this.postArgs, args);
   }

   public AttrAccess postArgs;
}

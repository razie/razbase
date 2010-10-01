/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.comms;

import razie.base.ActionItem;

/**
 * this action is on a service - relevant when making up the URL: PREFIX/service/action/...
 * 
 * $
 * @author razvanc99
 * 
 */
public class ServiceActionToInvoke extends SimpleActionToInvoke {
    String service;

    /**
     * constructor
     * 
     * @param target the prefix used depending on the drawing technology - for http, it's the URL to
     *        append to
     * @param service the name of the service, it's part of the URL
     * @param item this is the action, contains the actual command name and label to display
     * @param pairs
     */
    public ServiceActionToInvoke(String target, String service, ActionItem item, Object... pairs) {
        super(target, item, pairs);
        this.service = service;
    }

    /**
     * constructor
     * 
     * @param service the name of the service, it's part of the URL
     * @param item this is the action, contains the actual command name and label to display
     * @param pairs
     */
    public ServiceActionToInvoke(String service, ActionItem item, Object... pairs) {
        super(item, pairs);
        this.service = service;
    }

    public ServiceActionToInvoke clone() {
        return new ServiceActionToInvoke(this.target, this.service, this.actionItem.clone(), this.toPairs());
    }

    public ServiceActionToInvoke args(Object...pairs) {
       return new ServiceActionToInvoke(this.target, this.service, this.actionItem.clone(), pairs);
    }

    public String makeActionUrl() {
        String url = target.endsWith("/") ? target : target + "/";
        url += service + "/";
        url += actionItem.name;
        url = addToUrl(url);
        return LightAuthBase.wrapUrl(url);
    }

    // TODO implement
    public static ServiceActionToInvoke fromActionUrl(String url) {
        return null;
    }
}

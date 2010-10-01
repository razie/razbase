/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

import java.util.ArrayList;
import java.util.List;

import com.razie.pub.comms.Comms;

/**
 * a two way STRING communication channel. Adds filtering functionality
 * 
 * @author razvanc99
 * 
 */
public class StrCommProxy extends StrCommStream {
    List<IStrFilter> filters;
    private StrCommStream proxy;

    /** empty channel - is is null*/
    protected StrCommProxy(StrCommStream proxy) {
        this.proxy=proxy;
    }

    /** will read all incoming until the channel is empty */
    public String readAll() {
        String s = Comms.readStream(is);
        if (filters != null)
            for (IStrFilter f : filters)
                s = f.filter(s);
        return s;
    }

    /**
     * will read incoming until the end of line OR the channel is empty
     * 
     * @return the next line in hte stream or NULL if empty
     */
    public String readLine() {
        return proxy.readLine();
    }

    public void setFilters(IStrFilter... f) {
        if (filters == null)
            filters = new ArrayList<IStrFilter>();
        for (IStrFilter fi : f)
            filters.add(fi);
    }
}

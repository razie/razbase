/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.razie.pub.comms.CommChannel;

/**
 * a two way STRING communication channel. Adds filtering functionality
 * 
 * NOTE that you either read it line by line or all at once, can't combine them right now
 * 
 * @author razvanc99
 * 
 */
public class StrCommStream extends CommStream {
    List<IStrFilter> filters;
    BufferedReader   buffer;

    /** empty channel - is is null */
    protected StrCommStream() {
        super((CommChannel)null);
    }

    /** creates a comm channel with the remote URL */
    public StrCommStream(InputStream is, IStrFilter... f) {
        super(is);
        setFilters(f);
    }

    /** creates a comm channel with the remote URL */
    public StrCommStream(URL url, IStrFilter... f) {
        super(url);
        setFilters(f);
    }

    /** creates a comm channel with the remote URL */
    public StrCommStream(String url, IStrFilter... f) {
        super(url);
        setFilters(f);
    }

    private void setup() {
        if (buffer == null)
            buffer = new BufferedReader(new InputStreamReader(is));
    }

    /** will discard the rest of the stream */
    public void discard() {
        setup();
        String rest;
        try {
            rest = buffer.readLine();
            while (rest != null && rest.length() > 0)
                rest = buffer.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** will read all incoming until the channel is empty */
    public String readAll() {
        String s = readStreamImpl();
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
        String s = readStreamLineImpl();
        if (filters != null)
            for (IStrFilter f : filters)
                s = f.filter(s);
        return s;
    }

    protected String readStreamLineImpl() {
        setup();
        try {
            return buffer.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFilters(IStrFilter... f) {
        if (filters == null)
            filters = new ArrayList<IStrFilter>();
        for (IStrFilter fi : f)
            filters.add(fi);
    }
}

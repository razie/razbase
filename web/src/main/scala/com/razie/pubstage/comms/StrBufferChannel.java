/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pubstage.comms;

/**
 * simple string buffer
 * 
 * @author razvanc99
 */
public class StrBufferChannel extends StrCommStream {

    private String buffer;

    /** creates a comm channel with the remote URL */
    public StrBufferChannel(String buffer, IStrFilter... f) {
        super();
        this.buffer = buffer;
        setFilters(f);
    }

    /** strip header/footer */
    @Override
    protected String readStreamImpl() {
        return buffer;
    }

}

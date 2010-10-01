/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

/**
 * a filter for strings, think regexp replacement
 * 
 * TODO i need 2 kinds of filters: stream filters and line filters. stream filters are applied to
 * the entire contents while line filters are applied per line
 * 
 * @author razvanc99
 * 
 */
public interface IStrFilter {
    /** not sure yet if i should have filterLine() and filterAll() */
    public String filter(String i);

}

/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

/**
 * a filter for strings, think regexp replacement
 * 
 * TODO heavy optimization needed here - i hate poltergeists
 * 
 * @author razvanc99
 */
public abstract class StrFilter implements IStrFilter {
    /** not sure yet if i should have filterLine() and filterAll() */
    public abstract String filter(String i);

    /** makes a java regexp replacement filter */
    public static StrFilter regexp(String... pairs) {
        return new JavaRegExpFilter(pairs);
    }

    public static class JavaRegExpFilter extends StrFilter {
        private String[] pairs;

        /** (pattern,replacement) pairs */
        public JavaRegExpFilter(String... pairs) {
            this.pairs = pairs;
        }

        public String filter(String in) {
            for (int i = 0; i <pairs.length-1; i += 2)
                in = in.replaceAll(pairs[i], pairs[i + 1]);
            return in;
        }
    }
}

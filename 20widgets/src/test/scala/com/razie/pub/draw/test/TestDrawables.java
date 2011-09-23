/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.draw.test;

import junit.framework.TestCase;
import razie.draw.Technology;
import razie.draw.widgets.DrawError;
import razie.draw.widgets.DrawToString;

import com.razie.pub.base.log.Log;

public class TestDrawables extends TestCase {

    public void setUp() {
    }

    public void testToString() {
        // nasty way to get "13"... :))
        String s = (String) new DrawToString(new Integer(13)).render(Technology.ANY, null);
        assertTrue("13".equals(s));
    }

    public void testDrawError() {
        try {
            String s = new String("gg");
            if ("gg".equals(s))
                throw new IllegalArgumentException("you'd wish...");
        } catch (Exception e) {
            String s = (String) new DrawError(e).render(Technology.ANY, null);
            assertTrue(s.contains("IllegalArgumentException"));
            return;
        }

        assertTrue(false);
    }

    static final Log logger = Log.factory.create(TestDrawables.class.getName());
}

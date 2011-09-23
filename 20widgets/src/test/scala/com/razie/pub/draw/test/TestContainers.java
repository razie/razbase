/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.draw.test;

import java.io.IOException;

import junit.framework.TestCase;
import razie.draw.DrawList;
import razie.draw.DrawStream;
import razie.draw.DrawTable;
import razie.draw.SimpleDrawStream;
import razie.draw.Technology;

import com.razie.pub.base.log.Log;

public class TestContainers extends TestCase {

    public void setUp() {
    }

    public void testWriteList() throws IOException {
        DrawStream stream = new SimpleDrawStream(Technology.HTML);
        DrawList list = new DrawList();
        list.write("11");
        list.write("22");
        list.write("33");
        stream.write(list);
        String s = stream.toString();
        assertTrue(s.contains("33"));
    }

    public void testWriteTable() throws IOException {
        DrawStream stream = new SimpleDrawStream(Technology.HTML);
        DrawTable list = new DrawTable(0, 2);
        list.write("11");
        list.write("22");
        list.write("33");
        stream.write(list);
        String s = stream.toString();
        assertTrue(s.contains("33"));
    }

    public void testStreamList() throws IOException {
        DrawStream stream = new SimpleDrawStream(Technology.HTML);
        DrawList list = new DrawList();
        stream.open(list);
        String s = stream.toString();
        list.write("11");
        list.write("22");
        list.write("33");
        s = stream.toString();
        assertTrue(s.contains("33"));
        list.close();
        s = stream.toString();
        assertTrue(s.contains("33"));
    }

    public void testStreamTable() throws IOException {
        DrawStream stream = new SimpleDrawStream(Technology.HTML);
        DrawTable list = new DrawTable(0, 2);
        stream.open(list);
        String s = stream.toString();
        list.write("11");
        list.write("22");
        list.write("33");
        s = stream.toString();
        assertFalse(s.contains("33"));
        list.close();
        s = stream.toString();
        assertTrue(s.contains("33"));
    }

    public void testStreamTableWithExactCols() throws IOException {
        DrawStream stream = new SimpleDrawStream(Technology.HTML);
        DrawTable list = new DrawTable(0, 2);
        stream.open(list);
        String s = stream.toString();
        list.write("11");
        list.write("22");
        list.write("33");
        list.write("44");
        s = stream.toString();
        assertFalse(s.contains("33"));
        list.close();
        s = stream.toString();
        assertTrue(s.contains("33"));
    }

    static final Log logger = Log.factory.create(TestContainers.class.getName());
}

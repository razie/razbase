/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

import java.util.ArrayList;
import java.util.List;

/**
 * this is an interesting concept...active/live streams of objects, which can be processed in
 * parallel or sequence etc. At the minimum, consuder this a producer/consumer pipe.
 * 
 * <p>
 * most operations produce streams of objects. the idea of the stream is to stream it as fast as
 * possible to the user. organizing the code in this manner enforces that performance to the
 * end-user is the most important criteria.
 * 
 * @author razvanc99
 */
public interface ObjectStream {
    public void write(Object o);

    public Object read();

    public void doneWriting();

    public boolean hasMore();

    public static class Impl implements ObjectStream {
        @SuppressWarnings("unchecked")
        List    objects = new ArrayList();
        boolean done    = false;

        public Impl(Object... objects) {
            for (Object o : objects) {
                this.write(o);
            }
        }

        public synchronized Object read() {
            return this.objects.remove(0);
        }

        public synchronized void write(Object o) {
            if (done) {
                throw new IllegalStateException("ObjectStream closed for write()");
            }
            this.objects.add(o);
        }

        public void doneWriting() {
            done = true;
        }

        public boolean hasMore() {
            // TODO Auto-generated method stub
            return false;
        }
    }
}

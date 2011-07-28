/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pubstage.life;

import java.util.ArrayList;
import java.util.List;

/**
 * simple MT barrier waiting for a few threads to finish
 * 
 * TODO 3-3 implement
 * 
 * @author razvanc99
 */
public class Barrier {
    List<Worker> workers;

    public Barrier(Worker... workers) {
        this.workers = new ArrayList<Worker>();
        for (Worker w : workers)
            this.workers.add(w);
    }

    public boolean check() {
        boolean done = true;
        for (Worker w : workers)
            if (!w.isDone()) {
                done = false;
                break;
            }
        return done;
    }
}
/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.comms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * a group of agents representing a logical structure, i.e. all the agents in my home (that is
 * actually a special group, the "home cloud". The different groups you could have/belong to are
 * equivalent, except the home cloud. The home cloud is by default the current group that is the
 * target of distributed operations.
 * 
 * agent clouds are the logical unit for distributed services, including negociation, database sync
 * etc.
 * 
 * agent clouds can be configured statically (agent.xml) or dynamically, based on
 * AgentCloudNegociation
 * 
 * TODO genericize this - clouds of any kind of agent/device, together with its protocol
 * 
 * @author razvanc
 */
public class AgentCloud {
    /** map<name,handle> - i'm so lazy I used a synchronized map :) */
    private Map<String, AgentHandle> agents    = Collections
                                                       .synchronizedMap(new HashMap<String, AgentHandle>());

    /** simple constructor with optional pre-population */
    public AgentCloud (AgentHandle...handles) {
       for (AgentHandle h : handles) this.put(h);
    }
    
    /** simple constructor with optional pre-population */
    public AgentCloud (Iterable<AgentHandle> handles) {
       for (AgentHandle h : handles) this.put(h);
    }
    
    /**
     * only access is to clone the sync'd collection. The individual agents may still be modified by
     * async status updates, but as assignments are atomic should be ok
     */
    public Map<String, AgentHandle> agents() {
        synchronized (agents) {
            Map<String, AgentHandle> copy = new HashMap<String, AgentHandle>();
            copy.putAll(agents);
            return copy;
        }
    }

    /**
     * agents are indexed by name which can't change. You can change IPs etc without any obvious
     * side effects (at least at this level :)
     */
    public AgentHandle put(AgentHandle h) {
        agents.put(h.name, h);
        return h;
    }

    public AgentHandle get(String name) {
        return agents.get(name);
    }
}

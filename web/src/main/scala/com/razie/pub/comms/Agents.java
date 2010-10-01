/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.comms;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.razie.pub.base.NoStaticSafe;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.log.Log;

/**
 * just a static helper that concentrates current agent-location information. this is used in soooo
 * many classes... It is updated by the agent at runtime. Notifications about these updates are sent
 * by the agent to all services as an AGENTS_UPDATE
 * 
 * this is used with NoStatic so you use it on your thread with Agents.instance()
 * 
 * TODO 1-3 monitor network and ip and update this and send event
 * 
 * @author razvanc99
 */
@NoStaticSafe
public class Agents {
    /** sent by agent to all AgentServices to notify of changes of network/ip friends etc */
    public static final String evAGENTS_UPDATE = "AGENTS_UPDATE";

    protected AgentCloud       myCloud         = null;
    // TODO implement this
    public AgentCloud         homeCloud = null;

    public boolean             testing         = false;
    public static final String TESTHOST        = "TEST-host";
    
    // this is generally the local area network prefix?
    // TODO possible security breach?
    public static String       homeNetPrefix   = "192.168.1.";
    private AgentHandle        me              = null;

    public Agents(AgentCloud homeGroup, AgentHandle me) {
        myCloud = homeGroup;
        this.me = me;
    }

    /** THIS must be initialized in the nostatic context before this... */
    public static Agents instance() {
        Agents singleton = (Agents) NoStatics.get(Agents.class);

        if (singleton == null)
            throw new IllegalStateException("Agents not initialied - You need to initialize it in NoStatics");

        return singleton;
    }

    public static AgentHandle me() {
        return instance().me;
    }

    /** @return my host name */
    public static String getMyHostName() throws RuntimeException {
        return instance().getMyHostNameImpl();
    }

    /** @return the handle for the mentioned remote agent
     * 
     * @param remote the name of the remote agent or "localhost" meaning me()
     * @return
     */
    public static AgentHandle agent(String remote) {
        AgentHandle ah = "localhost".equals(remote) ? me() : instance().agentImpl(remote);
        return ah;
    }

    /** @return the handle for the mentioned remote agent
     * 
     * @param remote the name of the remote agent or "localhost" meaning me()
     * @return
     */
    public static AgentHandle handleOf(String remote) {
        AgentHandle ah = "localhost".equals(remote) ? me() : instance().agentImpl(remote);
        return ah;
    }

    /** TODO fix this - should not get by IP since there can be many at the same ip */
    public static AgentHandle agentByIp(String remote) {
        return instance().agentByIpImpl(remote);
    }

    public String getMyHostNameImpl() throws RuntimeException {
//        return findMyHostName(testing);
        return me.name;
    }

    public static String findMyHostName(boolean testing) throws RuntimeException {
        if (testing) {
            return TESTHOST;
        }
        try {
            String n = InetAddress.getLocalHost().getHostName();
            return n;
        } catch (UnknownHostException e1) {
            throw new RuntimeException("ERR: " + e1.toString(), e1);
        }
    }

    public AgentHandle agentImpl(String host) {
        return instance().myCloud.get(host);
    }

    /** TODO sync this */
    public AgentHandle agentByIpImpl(String ip) {
        for (AgentHandle a : instance().myCloud.agents().values()) {
            if (a.ip.equals(ip) && ! a.status.equals(AgentHandle.DeviceStatus.EXCLUDED)) {
                return a;
            }
        }

        Log.logThis("ERR_AGENTBYIP_NOTFOUND - you may get a null pointer about now. ip=" + ip
                + instance().myCloud.agents().values());
        return null;
    }

    public static String getHomeNetPrefix() {
        return homeNetPrefix;
    }

    public static AgentCloud homeCloud() {
        return instance().myCloud;
    }

    public static void setMe(AgentHandle myHandle) {
        Agents i = instance();
        if (i.me != null && !i.me.name.equals(myHandle.name))
            throw new IllegalStateException("Can't change who I am...");
        else
            i.me = myHandle;
    }
}

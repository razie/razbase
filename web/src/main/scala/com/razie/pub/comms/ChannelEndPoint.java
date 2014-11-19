package com.razie.pub.comms;

/**
  * need to figure out the commonalities of channel end-points. at the very least, these will be
  * abstract logical constructs and have nothing to do with phisical
  * node/hardware/software/services/network deployment.
  */
 public abstract class ChannelEndPoint implements razie.comms.CommsEndPoint {
     /**
      * today, even local host has an IP address - not enought ouniquely identify it, but it's
      * gotta have one...
      */
     public abstract String getIp();
     public abstract String getPort();
 }
package com.razie.pub.comms;

/** auth types */
public enum LightAuthType {
   /** only in-house...i.e. from the same subnetwork */
   INHOUSE,
   /**
    * shared same secret anywhere. this is basically you (or your delegate) from anywhere in the
    * world
    */
   SHAREDSECRET,
   /** in-cloud - members of the same cloud */
   INCLOUD,
   /** friends - their public ID is listed in your friends list */
   FRIEND,
   /** anybody */
   ANYBODY,
}
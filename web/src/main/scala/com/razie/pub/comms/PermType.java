package com.razie.pub.comms;

/**
 * permissions that can be assigned to objects, actions and methods. These are decoupled from the
 * authentications below.
 */
public enum PermType {
   /** highest permission: includes upgrades and code changes, define new assets */
   ADMIN,
   /** allows control of play/preferences, turn on lights etc */
   CONTROL,
   /** since we have VIEW/READ, let's have write as well, eh? */
   WRITE,
   /** just query and view */
   VIEW,
   /** what you want anybody to be able to do */
   PUBLIC
}
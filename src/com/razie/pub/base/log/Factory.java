package com.razie.pub.base.log;

/**
 * This is how you can use any underlying logging package: simply overwrite this with your own
 * factory before the thing starts (first thing in main())
 * 
 * OR 
 * 
 * Just overwrite this in the classpath - easier, eh?
 */
public class Factory {

   public Log create(String component, String categoryName) {
      return new Log(component, categoryName);
   }

   public Log create(String categoryName) {
      return new Log("?", categoryName);
   }
}
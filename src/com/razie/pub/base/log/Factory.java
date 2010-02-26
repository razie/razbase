package com.razie.pub.base.log;

/**
 * This is how you can use any underlying logging package: simply overwrite this with your own
 * factory before the thing starts (first thing in main())
 * 
 * TODO implement proper factory pattern
 */
public class Factory {

   public static Log create(String component, String categoryName) {
      return new Log(component, categoryName);
   }

   public static Log create(String categoryName) {
      return new Log("?", categoryName);
   }
}
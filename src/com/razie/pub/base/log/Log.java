/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.base.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * simple log proxy - log4j is dominant but then there's the JDK's log... this gives you the freedom
 * to use one or the other...or simply recode to use your own - if you hapen to use another
 * one...doesn't it suck when you use a library which writes to stdout?
 * 
 * create logs per class with the Factory. then invoke log() trace() or alarm().
 * 
 * if you're lazy, use the static Log.logThis()
 * 
 * @author razvanc99
 */
public class Log {

   private String         category;
   private String         component;
   public static String   program    = "dflt";
//   public static int      MAXLOGS    = 1000;
//   public static String[] lastLogs   = new String[MAXLOGS];
//   public static int      curLogLine = 0;
   public static boolean  DEBUGGING  = true;

   public Log(String componentNm, String categoryNm) {
      this.category = categoryNm;
      this.component = componentNm;
   }

   /** from http://www.javapractices.com/topic/TopicAction.do?Id=78 */
   public static String getStackTraceAsString(Throwable aThrowable) {
       final Writer result = new StringWriter();
       final PrintWriter printWriter = new PrintWriter(result);
       aThrowable.printStackTrace(printWriter);
       return result.toString();
     }

   public void log(String m, Throwable t) {
      log(m + " Exception: " + getStackTraceAsString(t));
   }

   public void log(Object... o) {
      String m = "";
      for (int i = 0; i < o.length; i++) {
         m += o[i].toString();
      }

      String msg = "LOG-" + program + "-" + component + "-" + category + ": " + m;
      System.out.println(msg);
   }

   public void alarm(String m, Throwable... e) {
      log(m + (e.length <= 0 ? "" : getStackTraceAsString(e[0])));
   }

   /**
    * trace by concatenating the sequence of objects to String - this is the most efficient trace
    * since the strings will only be evaluated and concatenated if the trace is actually turned on
    */
   public void trace(int l, Object... o) {
      if (isTraceLevel(l)) {
         String m = "";
         for (int i = 0; i < o.length; i++) {
            m += (o[i] == null ? "null" : o[i].toString());
         }
         log(m);
      }
   }

   public boolean isTraceLevel(int l) {
      return DEBUGGING;
   }

   public static boolean isTraceOn() {
      return DEBUGGING;
   }

   public static void logThis(String m) {
      logger.log(m);
   }

   // TODO 2-1 implement the separate audit facility
   public static void audit(String m) { logger.log("AUDIT: "+m); }
   public static void audit(String m, Throwable t) { logger.log("AUDIT: "+m, t); }

   public static void traceThis(String m) { logger.trace(1, m); }
   public static void traceThis(String m, Throwable t) { logger.trace(1, m, t); }

   public static void logThis(String m, Throwable t) {
//      Factory.logger.log(m + " Exception: " + Exceptions.getStackTraceAsString(t));
      logger.log(m + " Exception: " + t);
   }

//   /** alarm this only once in this run...*/
//   public static void alarmOnce(String errorcode, String m, Throwable... e) {
//      if (! alarmedOnce.containsKey(errorcode)) {
//         Factory.logger.alarm(m, e);
//         alarmedOnce.put(errorcode, errorcode);
//      }
//   }

   /** alarm this */
   public static void alarmThis(String m, Throwable... e) {
      logger.alarm(m, e);
   }

   /** alarm this and throw a new RT exception with the message and the cause */
   public static void alarmThisAndThrow(String m, Throwable... e) {
      // TODO i don't think this should log again...since it throws it, eh?
      logger.alarm(m, e);
      if (e.length > 0 && e[0] != null)
         throw new RuntimeException(m, e[0]);
      else
         throw new RuntimeException(m);
   }

//   /**
//    * helper to turn lists/arrays/maps into strings for nice logging
//    * 
//    * @param ret object to toString
//    * @return either the new String or the original object if not recognized
//    */
//   @SuppressWarnings("unchecked")
//   public static Object tryToString(String indent, Object ret) {
//      return Log4j.tryToString(indent, ret);
//   }

   public static Log logger = Factory.create("?", "DFLTLOG");
   
   public static Log create(String component, String categoryName) {
      return factory.create(component, categoryName);
   }

   public static Log create(String categoryName) {
      return factory.create( categoryName);
   }
   
   public static Factory factory = new Factory();
}

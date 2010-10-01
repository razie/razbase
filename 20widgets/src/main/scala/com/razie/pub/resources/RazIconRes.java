package com.razie.pub.resources;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.razie.pub.base.log.Log;

/**
 * just to proxy/wrap icons functionality, in case i introduce "themes" later - a theme would be a different
 * icons.properties file for now
 * 
 * if you want to use this, have an icons.properties at the root in the classpath
 * 
 * TODO implement registry of cascaded property files etc
 * 
 * @author razvanc99
 * 
 */
public class RazIconRes {
   public static String curTheme = "icons.properties";
   public static String getPictureService = "/classpath/public/pics/";
   public static String PIC_CLASSPATH = "/public/pics/";
   public static String UNK_CLASSPATH = "/public/pics/help_index.png";
   static Properties props = new Properties();

   public static void init() throws IOException {
      if (RazIconRes.class.getClassLoader().getResource(curTheme) == null)
         throw new IllegalStateException("ERR_CONFIG missing resource (should be in classpath): " + curTheme);
      props.load(RazIconRes.class.getClassLoader().getResource(curTheme).openStream());
   }

   public static String getIconFile(RazIcons icon) {
      return getIconFile(icon.name());
   }

   /** the actual url to pic (on server at runtime) or empty */
   public static String getIconFile(String icon) {
      if (icon == null || icon.length() <= 0)
         icon = razie.Icons.UNKNOWN.toString();
      String f = props.getProperty(icon.toLowerCase());
      return f == null ? icon : getPictureService + f;
   }

   /** use this version for Swing local applications - will return classic icon URL in classpath */
   public static URL getIconRes(String icon) {
      if (icon == null || icon.length() <= 0)
         icon = razie.Icons.UNKNOWN.toString();
      String f = props.getProperty(icon.toLowerCase());
      if (f == null) {
         Log.logThis("ERR_PROG: cant find icon resource for icon code: " + icon);
      }

      URL ret = RazIconRes.class.getResource(f == null ? UNK_CLASSPATH : PIC_CLASSPATH + f);
      if (ret == null) {
         Log.logThis("ERR_PROG: cant find icon resource for icon code: " + f);
      }

      return ret != null ? ret : RazIconRes.class.getResource(UNK_CLASSPATH);
   }

   Properties p;
}

package com.razie.pub.comms;

/** what it says - a part of a sed filter */
public interface SedFilter {
   /** filter the line - i.e. replace your pattern... 
    * 
    * @param line a line in a file/stream
    * @return
    */
   String filter (String line);
}


package razie.draw.test

import java.io._
import razie.draw._
import razie.draw.samples._

object TestWeb {
  
   def main (argv:Array[String]) {
      com.razie.pub.SimplestWebServer (4445) (mk)
   }

   def mk :String = {

      val os = new ByteArrayOutputStream()
      val stream = new HttpDrawStream (null, os)
   
      stream write SimpleModel.model
   
      stream.close();
      val s = os.toString
   
      println (s)
   
      s
      }
   
//   def draw = new SampleDrawable()
}

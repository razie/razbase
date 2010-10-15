
package razie.draw.test

import java.io._
import razie.draw._
import razie.draw.samples._

object TextTest {
  
   def main (argv:Array[String]) {
   
   val os = new ByteArrayOutputStream()
   val stream = new SimpleDrawStream (Technology.TEXT, os)
   
//   val d = new SampleDrawable()
   
   stream write SimpleModel.model
   
   stream.close();
   val s = os.toString
   
   println (s)
   
//         val f = new File("sampledrawable.html");
//         val fos = new FileOutputStream(f);
//         fos.write(s.getBytes());
//         fos.write('\n');
//         fos.flush();
//         fos.close();
   }
}

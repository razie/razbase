
package razie.test

import java.io._
import razie.draw._
import razie.draw.samples._
import com.razie.pub.http._
import com.razie.pub.base._
import com.razie.pub.comms._
import razie.base._

object TestWeb2 {
  
   def main (argv:Array[String]) {
      new LightServer (4445, 20, ExecutionContext.instance(), new CSDemo()) 
   }

   def mk (url:String) : String = {

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

/** a demo content assist implementation */
class CSDemo extends LightContentServer with razie.Logging {
   
   override def options (s:String, sessionId:String) : Seq[ActionItem] = {
      val ret = 
         if (s endsWith "a") razie.AI("b") :: razie.AI("c") :: Nil
         else if (s endsWith "b") razie.AI("c") :: Nil
         else Nil
   this trace "options for: \'"+s+"\' are: " +ret
   ret
   }
}


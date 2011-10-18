package razie.base.data
import java.io.FileWriter
import java.io.File
import java.io.FileReader
import scala.collection.mutable.ListBuffer
import razie.Logging

/** safe log file - use it to safely store some records.
 *
 *  will spawn a background thread that will read records from the file, to make sure they're there... unless the OS is
 *  messing with us
 *
 *  Note that it will open/create automatically, when object is created
 */
class SafeFile[T](val path: String) extends Logging {
  debug ("creating new file: " + path)
  val f = new File(path)
  f.createNewFile()

  val fout = new FileWriter(f)
  val fin = new FileReader(f)
  var closing = false // close() was called - waiting for reader to die

  val waiting = new ListBuffer[(T, () => Unit)]()

  def writeSync(buf: T) = error ("TODO - not fluent with continuations annotations")

  /** write a record - the continuation is called when the record's presence has been verified... */
  def writeAsync(buf: T)(continuation: => Unit) {
    synchronized {
      val wtf = (buf, () => continuation)
      waiting += wtf
      fout.write(buf.toString + "\n") // yes, inside the synchronized... test from 1000 threads to see why :)
    }
  }

  /** background thread reads from file to verify records were written and calls continuations */
  val reader = razie.Threads.fork {
    var isClosing = closing
    val acc = new StringBuilder()

    while (!isClosing) {

      try {
        val c = fin.read()
        if (c != -1) {
          if (c.toChar == '\n') synchronized {
            if (waiting.head._1.toString == acc.toString) {
              waiting.remove(0)._2()
            } else {
              println ("WTF? Unmatched record: " + acc.toString)
            }
            acc.clear()
          }
          else acc += c.toChar
        } else
          Thread.sleep(100)
      } finally {
        synchronized {
          isClosing = closing && waiting.isEmpty
        }
      }
    }
    fin.close()
  }

  /** blocking call - close this for good */
  def close() {
    synchronized {
      fout.close()
      closing = true
      if (waiting.isEmpty)
        reader.interrupt() // TODO is this correct? don't remember
    }
    reader.join()
    debug ("reader dead - file closed?: " + path)
  }
}

object SafeFileApp extends App {
  val sf = new SafeFile[String]("safe-" + System.currentTimeMillis().toString + ".txt")
  val PREFIX="1234567890123456789012345678901234567890"
  var inc = 0
  val LOOPS = 1000
  val THREADS = 1000

  val start=System.currentTimeMillis()
  
  razie.Threads.repeatAndWait(THREADS) { thread =>
    for (i <- 1 to LOOPS)
      sf.writeAsync (PREFIX+i.toString) { synchronized { inc += 1 }; println(thread, inc) }
    null
  }

  println (inc, inc < THREADS * LOOPS) // should be incomplete

  sf.close() // flush and verify

  println (inc, inc == THREADS * LOOPS)
  
  val end=System.currentTimeMillis()
  
  println ("took %d msec for a performance of %f records/sec".format(end-start, (THREADS*LOOPS*1000.0)/(end-start)))
}

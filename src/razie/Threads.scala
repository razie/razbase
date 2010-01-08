/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie

/** multithreading helpers, to tie me over until I learn to effectively exploit actors or other 
 * inferior beings
 * @author razvanc
 */
object Threads {
   
   /** run a func in its own, separate thread - don't wait for result */
   def fork (f: =>Unit) : java.lang.Thread = {
      val thread = new java.lang.Thread(new java.lang.Runnable() {
         override def run() = {
            f
         }
      })
      thread.start()
      thread
   }

   /** fork one thread per element, each running the func */
   def forkForeach[A] (as:Iterable[A]) (f:A =>Unit) : Iterable[java.lang.Thread] = {
	   val threads = (for (a <- as) yield
	      new java.lang.Thread(new java.lang.Runnable() {
	         override def run() = {
	            f(a)
	         }
	      })).toList // use toList since the iterable may not be strict
	   threads.foreach (_.start())
	   threads
	   }

   /** join a bunch of thread - this is more to have a symmetry for fork */
   def join (threads:Iterable[java.lang.Thread]) = {
      threads.foreach (_.join)
   }

   /** fork a bunch of threads, then join them and return the results */
   def forkjoin[A,B>:Null<:AnyRef] (as:Iterable[A]) (f:A =>B) : Iterable[B] = {
      val threads = (for (a <- as) yield new FuncValThread (a, f)).toList
      threads.foreach (_.start())
      threads.foreach (_.join)
      threads.map (_.res).toList // use toList since the iterable may not be strict
      }

   /** just repeat the func on as many threads */
   def repeat (i:Int) (f: =>Unit) {
      for (t <- 0 until i)
         new java.lang.Thread(new java.lang.Runnable() {
            override def run() = {
               f
            }
         }).start();
      }
  
   /** repeat the func on as many threads, but each has a result which is joined and then collected */
   def repeatAndWait[A>:Null<:AnyRef] (i:Int) (f: => A)(implicit m:scala.reflect.Manifest[A]) : Iterable[A] = {
      val threads = Array.tabulate (i)(_ => new FuncThread (f))       
      threads.foreach (_.start)
      threads.foreach (_.join)
      threads.map (_.res).toList  // use toList since the iterable may not be strict
      }

   class FuncValThread[A, B>:Null<:AnyRef] (val a:A, val f:A=>B) extends java.lang.Thread {
      var res:B = null
      
      override def run() = res = f(a)
   }

   class FuncThread[A>:Null<:AnyRef] (f: =>A) extends java.lang.Thread {
      var res: A = null

      override def run() = res = f 
   }
}

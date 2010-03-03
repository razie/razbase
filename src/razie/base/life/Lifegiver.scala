package razie.base.life;

import com.razie.pub.base.ExecutionContext;
import razie.base.ActionItem

/**
 * stupid thread pool model
 * 
 * TODO make nice, dynamic priorities and all that
 * 
 * TODO take into account the being's breathing schedule
 * 
 * @author razvanc99
 */
object Lifegiver {
    val beings = razie.Listi[Breather]()
    var myThread : Thread = null
    var ec : ExecutionContext = null;                             // passed at

    // self init
    init (ExecutionContext.DFLT_CTX)
    
    private def init (tc:ExecutionContext ) {
        if (myThread == null) {
            ec = tc;
            myThread = new Thread(new Runner());
            myThread.setName("Lifegiver" + myThread.getName());
            myThread.setDaemon(true);
            myThread.start();
        }
    }

    def die () {
        // TODO be more gracious: give beings a notification and maybe timeout/clean resources etc
        myThread.stop();
    }
    
    def needstoBreathe(b:Breather) {
        if (myThread == null)
            throw new IllegalStateException("Lifegiver needs init() beforehand!");

        beings.append(b);
    }

    private class Runner extends Runnable {
        def run() {
           if (ec != null) ec.enter();

            while (true) {
                    sleep(6 * 1000);

                var b : List[Breather]=null

                beings.synchronized {
                    b = beings.toList
                }

                b.foreach { being=>
                  being.breathe();
                  sleep(1 * 1000);
                }
            }
        }
    }
    
    def sleep (ms:Int) {
                try {
                    Thread.sleep(ms);
                } catch {
                   case e =>
                    // ignore
                    e.printStackTrace();
                }
    }
}

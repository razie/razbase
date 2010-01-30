package razie

object Timer {
   def apply[A] (f : => A) : (Long, A) = {
           val start = System.currentTimeMillis()
           val x = f
           val stop = System.currentTimeMillis()
           val dur= stop - start
       (dur, x)
   }
}

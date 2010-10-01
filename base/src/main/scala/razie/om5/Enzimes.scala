package razie.om5

object Enzo extends Application {
   val po = Samples.prodOrder
   println ("po: " + po)
   val so = OM.executepo(po)
   println ("so: " + so)
}


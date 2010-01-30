package razie

/** unix like commons - easy to invoke */
object U {
   def pwd = new java.io.File(".").getCanonicalFile
}

object UMain extends Application {
   println ("pwd == " + U.pwd)
}

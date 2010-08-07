package razie

object G {
   import razie.g._
   
   val LOCAL = new GLoc(null, null)

   val PREFIX = "razie.uri"
   val RAZIE  = "razie."
   val GIDREF = "razie.gidref"
   val GAQREF = "razie.gaqref"
   val GRQREF = "razie.grqref"
   val GXQREF = "razie.gxqref"
      
   def from       (s:String) : GRef = GRef parse s
   def fromString (s:String) : GRef = GRef parse s
   
   def apply(m:String, id:String, loc:GLoc=G.LOCAL) = GIDRef (m, id, loc)
   def id   (m:String, id:String, loc:GLoc=G.LOCAL) = GIDRef (m, id, loc)
   def aq   (m:String, attrs:razie.AA, loc:GLoc=G.LOCAL) = GAQRef (m, attrs, loc)
   def rq   (m:String, ql:String, loc:GLoc=G.LOCAL) = GRQRef (m, ql, loc)
   def xq   (x:String, loc:GLoc=G.LOCAL) = GXQRef (x, loc)

}



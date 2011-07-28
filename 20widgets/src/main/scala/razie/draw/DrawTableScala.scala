/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package razie.draw

import razie.draw.DrawTable.MyRenderer

/** enhanced DrawTable
 * 
 */
class DrawTableScala extends DrawTable {
	// TODO set prefCol == headers.size when setting headers
   var headers : Iterable[String] = List()
   
   override def getRenderer(t:Technology) : Renderer[AnyRef] = new DTSRenderer()

   def cols (i:Int)    : this.type = { this.prefCols=i; this }
   def rows (i:Int)    : this.type = { this.prefRows=i; this }
   def align (x:Align) : this.type = { this.horizAlign=x; this }
}

class DTSRenderer extends DrawTable.MyRenderer {

   override def canRender(o:AnyRef, t:Technology) =
      o.isInstanceOf[DrawTable];

   override def renderHeader(o:AnyRef, technology:Technology, stream:DrawStream):AnyRef = {
      val table = o.asInstanceOf[DrawTableScala]
                                   
      if (Technology.HTML.equals(technology)) {
         // TODO stream or use StringBuilder
         var x = "<table valign=center" + table.htmlWidth + ">\n";
            
         if (! table.headers.isEmpty) {
            x += DrawTable.MyRenderer.makeTR (table)
            table.headers foreach (x += "<td>" + _ + "</td>")
            x += "</tr>\n"
         }
            
         x
      }
      else
         "?"
   }
}

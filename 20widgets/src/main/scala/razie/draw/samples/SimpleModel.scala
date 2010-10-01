package razie.draw.samples

import razie.Draw

object SimpleModel {
 def model = {
    Draw.seq (
       Draw.label("label"),
       Draw.label("label"),
       Draw.hBar ("seq"),
       Draw.seq(
           Draw.label("label"),
             Draw.button(razie.AI("haha")) { println ("haha") },
             Draw.button(razie.AI("hoho")) { println ("hoho") }
             ),
       Draw.hBar ("hlist"),
       Draw.hlist(
           Draw.label("label"),
             Draw.button(razie.AI("haha")) { println ("haha") },
             Draw.button(razie.AI("hoho")) { println ("hoho") }
             ),
       Draw.hBar ("vlist"),
       Draw.vlist(
           "label",
             Draw.button(razie.AI("haha")) { println ("haha") },
             Draw.button(razie.AI("hoho")) { println ("hoho") }
             ),
       Draw.hBar ("table"),
       Draw.table (2)(
           "11", "12", "21", "22",
             Draw.button(razie.AI("haha")) { println ("haha") },
             Draw.button(razie.AI("hoho")) { println ("hoho") }
             ),
       Draw.hBar ("form"),
       Draw.form(razie.AI("a form"), "name:String=John,creditcard:String=12345") { aa => 
          println (aa.toString) 
          },
       Draw.hBar
       )
  }
}

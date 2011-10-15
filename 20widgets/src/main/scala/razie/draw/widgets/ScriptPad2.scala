/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.widgets

import razie.Draw
import com.razie.pub.comms._
import razie.base._
import razie.draw._
//import java.awt._
import java.awt.event.KeyEvent
import java.awt.event.InputEvent
import razie.draw.Drawable.Widget

/** statics */
object ScriptPad2 {
  val cmdSCRIPT = new ActionItem("ScriptPad")
  lazy val INITIAL = io.Source.fromURL(this.getClass.getClassLoader.getResource("InitialScriptPad.txt")).mkString
}

/** simple scripting pad
 *
 *  @param run - factory, creates the ATI to invoke to run the script. Will receive two args: "language" and "script"
 */
class ScriptPad2(
  lang: String,
  val run: () => ActionToInvoke,
  val options: (String, Int) => Seq[String],
  val reset: () => ActionToInvoke,
  val simple: Boolean = false,
  val css: String = "dark", // it's dark/light for now
  val applet: Boolean = false,
  val initial: String = ScriptPad2.INITIAL,
  var moreButtons : List[NavLink] = Nil,
  val makeButtons : () => Seq[NavLink] = ()=>Nil) extends Drawable {
  val ilang: String = Option(lang).getOrElse("scala")
  var rows = 15
  var cols = 80

  override def render(t: Technology, out: DrawStream): AnyRef = {
    val atis = makeButtons()
    val ati = run()

    if (Technology.HTMLNOJS == t || simple) {
      new DrawForm(
        ScriptPad2.cmdSCRIPT,
        ati,
        new AttrAccessImpl("language:String=scala,script:script=" + initial + ",sessionId=" + ati.getAttr("sessionId")))
    } else if (Technology.HTML == t) {
      if (out.isInstanceOf[HttpDrawStream]) {
        val s = out.asInstanceOf[HttpDrawStream]
        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/ace.js\" charset=\"UTF-8\"></script>")
//        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/ace-uncompressed.js\" charset=\"UTF-8\"></script>")
        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/theme-twilight.js\" charset=\"UTF-8\"></script>")
        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/theme-crimson_editor.js\" charset=\"UTF-8\"></script>")
        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/theme-dawn.js\" charset=\"UTF-8\"></script>")
        s.addMeta("<script type=\"text/javascript\" src=\"/public/build/src/mode-scala.js\" charset=\"UTF-8\"></script>")
        if (css == "dark")
          s.addMeta("<link rel=\"stylesheet\" type=\"text/css\" href=\"/public/scriptpad2-dark.css\">")
        else
          s.addMeta("<link rel=\"stylesheet\" type=\"text/css\" href=\"/public/scriptpad2-light.css\">")
      } else {
        razie.Log("WARN-KEYB rendering on unkonwn stream: " + out.getClass.getName)
      }

      Draw.seq(
        Draw html "<script type=\"text/javascript\" charset=\"UTF-8\" >var razSetup = function () {configIt('" + css + "', '" + ilang + "');}; </script>",
        Draw html "<div class=\"ScriptPad\" id=\"code\">",
        Draw html initial,
        Draw html "</div>",
        Draw html "<div id=\"m1\" ></div>",
        Draw html "<script type=\"text/javascript\" charset=\"UTF-8\" >var razSpSession = '" + ati.getAttr("sessionId") + "';</script>",
        Draw html "<script type=\"text/javascript\" src=\"/public/scriptpad2.js\" charset=\"UTF-8\"></script>",
        Draw html "<script type=\"text/javascript\" charset=\"UTF-8\" >setup2();</script>",
        if (atis.isEmpty)
        {
          val l = Draw list (
            Draw button (razie.AI("F9 - Run Line"), "javascript:runLine('/scripster/run?')"),
            Draw text "|",
            Draw button (razie.AI("Ctrl+F9 - Run Selection"), "javascript:runSelection('/scripster/run?')"),
            Draw text "|",
            Draw button (razie.AI("Reset"), "javascript:razInvoke('/scripster/reset?sessionId=" + ati.getAttr("sessionId") + "')"),
            Draw text "|",
            Draw button (razie.AI("Simple style"), "/scripster/simpleSession"))
          
           moreButtons.map (b => l.w(Draw text "|").w(b))
          Draw seq (
              l,
             Draw html "<textarea id=\"result\" cols=\""+cols+"\" rows=\"5\"></textarea>",
             Draw html "<br><div id=\"status\" style=\"color:orange\">Status is updated here...</div>"
             )
        } else {
          val l = Draw list ()
           atis.map (b => l.w(Draw text "|").w(b))
          l
        })
    } else {
      "?"
    }
  }
}

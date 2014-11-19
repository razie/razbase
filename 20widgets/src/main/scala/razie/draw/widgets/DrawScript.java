/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package razie.draw.widgets;

import razie.base.scripting.*;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.Renderer;
import razie.draw.Technology;
import razie.draw.Drawable.Widget;

/**
 * dynamic painting - will paint the TEXT results of running the script
 * 
 * TODO figure out how can the script change the format
 * 
 * @author razvanc99
 * 
 */
public class DrawScript extends Widget {
   String        lang;
   String        script;
   ScriptContext ctx;

   public DrawScript(String lang, String script) {
      this.lang = lang;
      this.script = script;
      this.ctx = ScriptContextImpl.global();
   }

   public DrawScript(String lang, String script, ScriptContext ctx) {
      this.lang = lang;
      this.script = script;
      this.ctx = ctx;
   }

   public Object render(Technology technology, DrawStream stream) {
      RazScript js = ScriptFactory.make(lang, script);
      Object res = js.eval(ctx);
      if (res instanceof Drawable) {
         return Renderer.Helper.draw(res, technology, stream);
      } else {
         return res.toString();
      }
   }

   public String toString() {
      return script == null ? "" : script;
   }
}

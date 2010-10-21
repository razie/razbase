/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.swing;

import razie.base._
import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._

import scala.swing._
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import jsyntaxpane._
import jsyntaxpane.actions._
import jsyntaxpane.actions.gui._
import jsyntaxpane.util._

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.actions.gui.ComboCompletionDialog;
import jsyntaxpane.util.StringUtils;

/** simple renderer */
class ScriptPadRenderer extends Renderer[ScriptPad] {
   
  override def render(f:ScriptPad , t:Technology , out:DrawStream ) :AnyRef = {
     SPRaz.setup (new ScriptPadRendererState (f)).render(t, out)
  }
}

/** need this state for some callbacks */
class ScriptPadRendererState (val f:ScriptPad) {
    val ed1 = new ScriptPadPanel(f.initial, f.rows, f.cols)
    val ed2 = new TextArea("result here", 5, f.cols)
    val b = razie.Draw.button(razie.AI(name="Ctrl+F9 Run Selection", tooltip="Run the selection or the entire pad if no selection")) { 
      runsel
      }
            
    val b2 = razie.Draw.button(razie.AI(name="F9 Run Line", tooltip="Run only the current line")) { 
      runline
      }
    
    val b3 = razie.Draw.button(razie.AI(name="Reset", tooltip="Reset when in trouble")) { 
      val r = f.reset().act(null)
      if (r==null) "<null>" else r.toString
      }
    
    def runline { ed2.text = runit (ed1.line, f) }
    def runsel  { ed2.text = runit (ed1.text, f) }
            
  def render(t:Technology , out:DrawStream ) :AnyRef = {
     ed1.init
    new BoxPanel (Orientation.Vertical) {
      contents += scala.swing.Component.wrap (ed1)
      contents += Init.swingify (razie.Draw.list(b, b2, b3).vertical(false), t,out)
      contents += ed2
    }
  }
  
  def runit (s:String, f:ScriptPad) : String = {
      val ati = f.run()
      ati.set ("language", "scala")
      ati.set ("script", s)
      val r = ati.act(null)
      if (r==null) "<null>" else r.toString
  }
}


// statics
object SPRaz {
   var sp : ScriptPadRendererState = null // TODO not MT safe...
   
   def setup (spp:ScriptPadRendererState) = {
      sp=spp
     DefaultSyntaxKit.getConfig (classOf[DefaultSyntaxKit]).put ("Action.RAZIE_F9ACTION","razie.draw.swing.F9RunCompletionAction,F9");
     DefaultSyntaxKit.getConfig (classOf[DefaultSyntaxKit]).put ("Action.RAZIE_CF9ACTIOn","razie.draw.swing.CF9RunSelCompletionAction,control F9");
     DefaultSyntaxKit.getConfig (classOf[DefaultSyntaxKit]).put ("Action.RAZIE_COMPLETION","razie.draw.swing.RazieCompletionAction,control SPACE");
     sp
   }
}

/** script completion in interactive mode */
class RazieCompletionAction extends DefaultSyntaxAction ("RAZIE_COMPLETION") {
   import scala.collection.JavaConversions._
   
    private var dlg:ComboCompletionDialog = null
    
  override def actionPerformed(target:JTextComponent , sDoc:SyntaxDocument , dot:Int , e:ActionEvent ) {
    val line = sDoc.getLineAt(dot);

    // vvv copy from CompleteWordAction
            val current = sDoc.getWordAt(dot, wordsPattern);
        if (current == null) {
            return;
        }
        val cw = current.getString(sDoc);
        target.select(current.start, current.end());

        sDoc.readLock();
//^^ copy
        val matches = SPRaz.sp.f.options(line)
        val buf = new scala.collection.mutable.ListBuffer[String] ()
         matches foreach (buf append _)

//vv copy
        sDoc.readUnlock();
        if (matches.size == 0) {
        } else if (matches.size == 1) {
            target.replaceSelection(matches.head);
        } else {

        if (dlg == null) {
            dlg = new ComboCompletionDialog(target);
        }
       
        dlg.displayFor(cw, buf)
        }
    }
   
    val DEFAULT_WORDS_REGEXP = Pattern.compile("\\w+");
    val wordsPattern = DEFAULT_WORDS_REGEXP;
}

/** run current line */
class F9RunCompletionAction extends DefaultSyntaxAction ("RAZIE_F9ACTION") {
  override def actionPerformed(target:JTextComponent , sDoc:SyntaxDocument , dot:Int , e:ActionEvent ) {
    SPRaz.sp.runline
    }
}

/** run selection or entire pad */
class CF9RunSelCompletionAction extends DefaultSyntaxAction ("RAZIE_CF9ACTION") {
  override def actionPerformed(target:JTextComponent , sDoc:SyntaxDocument , dot:Int , e:ActionEvent ) {
    SPRaz.sp.runsel
    }
}

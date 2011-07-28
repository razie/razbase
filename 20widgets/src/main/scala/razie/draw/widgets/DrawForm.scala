/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.widgets;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import razie.base.AttrType;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.Technology;

/**
 * a simple form, with an action to invoke and a set of parms to capture
 * 
 * will display the form and will then invoke the action, with all the parms
 * 
 * it only supports the GET method for now
 */
class DrawForm (val name:ActionItem , val ai:ActionToInvoke , val parms:AttrAccess ) extends Drawable.Widget {

    private var idebug=false;
    private var iliner=false;
    protected var enumeratedAttrs:AttrAccess = null;
    var overrideLabels : Seq[ActionItem] = Nil

    /** set the value of the named attribute + the name can be of the form name:type */
    def set(name:String , value:Object ) { this.parms.set(name, value); }

    def liner () : DrawForm = { this.iliner=true; this;}
    def debug () : DrawForm = { this.idebug=true; this;}
    def labels (l:Seq[ActionItem]) : DrawForm = { this.overrideLabels =l; this;}
   
    /** TODO 2-2 very stupid way to support selection of enumerated values */
    def enumerated (name:String , values:String ) : DrawForm = {
       if (enumeratedAttrs == null)
          enumeratedAttrs = new AttrAccessImpl();
       
       enumeratedAttrs.set (name, values);
       this;
    }
    
    /** shortcut to render self - don't like controllers that much */
    override def render(technology:Technology , stream:DrawStream ) = {
        var s = "\n<form name=\"" + name.label + "\" action=\"" + ai.makeActionUrl() + "\" method=\"get\">";

        // add the existing args as hidden controls
       
        ai.foreach ((a,value)=> s += DrawForm.HIDDEN.replaceAll("RAZ.NAME", a).replaceAll("RAZ.VALUE", value.toString) )
        
        if (idebug)
           s += "FORM: " + name.getLabel() + " ("+ai.makeActionUrl()+")<br>";
        
//           s += "<fieldset>";
        s += "<table>";
//        s += "<ol>";
           if (iliner) s += "\n<tr>";
        
        for (a <- parms.sgetPopulatedAttr) {
           if (!iliner) s += "\n<tr>";
           s += "<td style=\"background:darkgray\">";
//           s += "\n<li>";
           
            s += "<label for=\""+a+"\">"+ overrideLabels.find(_ == a).map(_.label).getOrElse(a) +"</label>"
            
            if (!iliner) s += "</td><td>";
            
           if (enumeratedAttrs == null || ! enumeratedAttrs.isPopulated(a)) {
            val ttype = 
               if (parms.getAttrType(a) != null)
                 DrawForm.types.getOrElse(parms.getAttrType(a), DrawForm.typeForString);
               else
                 DrawForm.typeForString
            
            val value = parms.getOrElse (a, "").toString

            s += ttype.replaceAll("RAZ.NAME", a).replaceAll("RAZ.VALUE", value);
           }
           else {
//            s += "</td><td>";
              s += "<select id=\""+a+"\" name=\""+a+"\">";
              val v = enumeratedAttrs.sa(a).split(",");
              for (va <- v) 
                 s += "<option value=\"" + va + "\">" + va + "</option>";
              s += "</select>";
           }
          s += "</td>";
          if (!iliner) s += "</tr>";
        }

//        s += "</ol>";
//           s += "</fieldset>";
        s += "<td>";
        s += "\n<input type=\"submit\" value=\""+overrideLabels.find(_ == "submit").map(_.label).getOrElse("Submit")+"\"/> ";
        s += "</td>";
          if (iliner) s += "</tr>";
        s += "</table>";
        s += "\n</form>\n";

        s;
    }
}

object DrawForm {
  /** html templates for the different types of fields */
//  private Map<AttrAccess.AttrType, String> types = new HashMap<AttrAccess.AttrType, String>();
  private val types = Map (
    AttrType.STRING -> "<input type=\"text\" id=\"RAZ.NAME\" name=\"RAZ.NAME\" value=\"RAZ.VALUE\" />",
    AttrType.MEMO -> "<TEXTAREA rows=20 cols=80 id=\"RAZ.NAME\" name=\"RAZ.NAME\" >RAZ.VALUE</textarea>",
    AttrType.SCRIPT -> "<TEXTAREA rows=20 cols=80 id=\"RAZ.NAME\" name=\"RAZ.NAME\" >RAZ.VALUE</textarea>",
    AttrType.INT -> "<input type=\"text\" id=\"RAZ.NAME\" name=\"RAZ.NAME\" value=\"RAZ.VALUE\" />",
    AttrType.FLOAT -> "<input type=\"text\" id=\"RAZ.NAME\" name=\"RAZ.NAME\" value=\"RAZ.VALUE\" />",
    AttrType.DATE -> "<input type=\"text\" id=\"RAZ.NAME\" name=\"RAZ.NAME\" value=\"RAZ.VALUE\" />"
  )
  
  val HIDDEN = "<input type=\"hidden\" id=\"RAZ.NAME\" name=\"RAZ.NAME\" value=\"RAZ.VALUE\" />"
  
 lazy val typeForString = types.get(AttrType.STRING).orNull
 
}
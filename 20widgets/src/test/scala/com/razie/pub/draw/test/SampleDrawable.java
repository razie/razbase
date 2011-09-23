/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.draw.test;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.base.AttrAccessImpl;
import razie.draw.DrawSequence;
import razie.draw.DrawStream;
import razie.draw.Technology;
import razie.draw.widgets.DrawError;
import razie.draw.widgets.DrawForm;
import razie.draw.widgets.DrawSelection;
import razie.draw.widgets.DrawToString;
import razie.draw.widgets.NavButton;
import razie.draw.widgets.NavLink;
import razie.draw.widgets.NavLink.Size;
import razie.draw.widgets.NavLink.Style;
import razie.draw.widgets.SimpleButton;

import com.razie.pub.comms.SimpleActionToInvoke;
import com.razie.pub.resources.RazIcons;

/**
 * this is a sample model drawable - it has one label and two buttons
 * 
 * a model drawable, ideally will ignore the technology and the stream, just use widgets and ask
 * them to render
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class SampleDrawable extends razie.draw.Drawable.Widget {

    /** shortcut to render self - don't like controllers that much */
    public Object render(Technology technology, DrawStream stream) {
        DrawSequence seq = new razie.draw.DrawSequence();

        ActionItem AI = new ActionItem("lightsoa/service/echo", RazIcons.FILE.name());
        AI.label = "echo something...";
        ActionToInvoke ATI = new SimpleActionToInvoke(AI, "msg", "echo...");

        seq.write("\nno action label vvv\n");
        seq.write(new NavLink(AI, (String) null).style(NavLink.Style.JUST_LABEL));
        seq.write("\nno action label ^^^\n");

        seq.write("\n action label vvv\n");
        seq.write(new NavLink(ATI).style(Style.JUST_LABEL));
        seq.write("\n action label ^^^\n");

        seq.write("\n just icon vvv\n");
        seq.write(new NavLink(ATI).style(NavLink.Style.JUST_ICON, Size.TINY));
        seq.write(new NavLink(ATI).style(NavLink.Style.JUST_ICON, Size.SMALL));
        seq.write(new NavLink(ATI).style(NavLink.Style.JUST_ICON, Size.NORMAL));
        seq.write(new NavLink(ATI).style(NavLink.Style.JUST_ICON, Size.LARGE));
        seq.write("\n just icon ^^^\n");

        seq.write("\n nice link vvv\n");
        seq.write(new NavLink(ATI));
        seq.write("\n nice link ^^^\n");

        seq.write("\nno action button vvv\n");
        seq.write(new SimpleButton(AI, (String) null));
        seq.write("\nno action button ^^^\n");

        seq.write("\n action button vvv\n");
        seq.write(new SimpleButton(ATI));
        seq.write("\n action button ^^^\n");

        seq.write("\n nav button vvv\n");
        seq.write(new NavButton(ATI));
        seq.write("\n nav button ^^^\n");

        seq.write("\n tiny nav button vvv\n");
        NavButton b = new NavButton(ATI);
        b.setTiny(true);
        seq.write(b);
        seq.write("\n tiny nav button ^^^\n");

        seq.write("\n selection vvv\n");
        DrawSelection sel = new DrawSelection("samplesel", ATI, ATI, ATI);
        seq.write(sel);
        seq.write("\n selection ^^^\n");

        seq.write("\nToString vvv\n");
        seq.write(new DrawToString("some text"));
        seq.write("\nToString ^^^\n");

        seq.write("\nDrawError vvv\n");
        try {
            justThrow();
        } catch (Throwable t) {
            seq.write(new DrawError(t));

        }
        seq.write("\nDrawError ^^^\n");

        seq.write("\nDrawForm vvv\n");
        DrawForm df = new DrawForm(AI, ATI,
                new AttrAccessImpl("url", "http://www.google.com", "filter:script", "<write script here>"));
        seq.write(df);
        seq.write("\nDrawForm ^^^\n");
        
        seq.write("\nDrawLater vvv\n");
// TODO 0 enable this       seq.write(new DrawLater(ATI));
        seq.write("\nDrawLater ^^^\n");

        // for actual rendering, ask the widget
        return seq.render(technology, stream);
    }

    private void justThrow() {
        throw new RuntimeException("exc");
    }

}

/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw.widgets;

import java.util.ArrayList;
import java.util.List;

import razie.base.ActionToInvoke;
import razie.draw.DrawList;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.Technology;

/**
 * a simple selection from a list of options
 * 
 * when selecting, the action is invoked. if only one of
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class DrawSelection extends Drawable.Widget {

    private List<ActionToInvoke> actions;
    private boolean[]            selected;
    protected String               name;

    public DrawSelection(String name, List<ActionToInvoke> actions) {
        this(name, actions.toArray(new ActionToInvoke[0]));
    }

    public DrawSelection(String name, ActionToInvoke... actions) {
        this.name = name;
        this.actions = new ArrayList<ActionToInvoke>();
        
        for (ActionToInvoke ati : actions)
            this.actions.add(ati);

        this.selected = new boolean[actions.length];

        for (int i = 0; i < actions.length; i++)
            selected[i] = false;
    }

    public void setSelected(ActionToInvoke ai, boolean select) {
        selected[actions.indexOf(ai)] = select;
    }

    public boolean isSelected(ActionToInvoke ai) {
        return selected[actions.indexOf(ai)];
    }

    /** shortcut to render self - don't like controllers that much */
    public Object render(Technology technology, DrawStream stream) {
        DrawList seq = new razie.draw.DrawList();

        // TODO mark the selected somehow
        for (ActionToInvoke ati : actions)
            seq.write(ati);

        return seq.render(technology, stream);
    }
}

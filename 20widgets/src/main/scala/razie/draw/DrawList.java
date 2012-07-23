/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import razie.draw.Renderer.ContainerRenderer;

/**
 * a drawable list of objects. objects must implement Drawable3 or else we'll use toString()
 * 
 * @author razvanc99
 * 
 */
@SuppressWarnings("unchecked")
public class DrawList extends StreamableContainer.Impl implements Drawable3, StreamableContainer {

    @SuppressWarnings("rawtypes")
	private List   list       = new ArrayList();
    public boolean isVertical = false;
    public String  valign     = null;
    public String  rowColor   = null;

    /** set the attributes before rendering, eh? */
    public DrawList() {}
    
    /** pre-populate: set the attributes before rendering, eh? */
    public DrawList(Object[] arr) {
       for (Object o : arr)
          write (o);
    }

    public DrawList vertical (boolean yes) {
       this.isVertical = yes;
       return this;
    }
    
    /** pre-populate: set the attributes before rendering, eh? */
    public DrawList(Collection arr) {
       for (Object o : arr)
          write (o);
    }
    
    /** shortcut to render self - don't like controllers that much */
    public Object render(Technology t, DrawStream stream) {
        return Renderer.Helper.draw(this, t, stream);
    }

    @SuppressWarnings("rawtypes")
    public Renderer getRenderer(Technology technology) {
        return new MyRenderer();
    }

    /**
     * @param list the list to set
     */
    @SuppressWarnings("rawtypes")
    public void setList(List list) {
        this.list = list;
    }

    /**
     * @return the list
     */
    @SuppressWarnings("rawtypes")
    public List getList() {
        return list;
    }

    public void write(Object o) {
        this.getList().add(o);
        if (this.state.equals(DrawStream.ElementState.OPEN)) {
            this.ownerStream.renderElement(this, o);
        }
    }
   
    // TODO 3-2 replace the return type of write in the entire class hierarchy to a DrawAccumulator, I think..
    public DrawList w (Object o) {
       write (o);
       return this;
    }

    public static class MyRenderer implements ContainerRenderer {

        public boolean canRender(Object o, Technology technology) {
            return o instanceof DrawList;
        }

        public Object render(Object o, Technology technology, DrawStream stream) {
            DrawList list = (DrawList) o;
            String res = "LIST???";

            if (Technology.HTML.equals(technology) || Technology.TEXT.equals(technology)) {
                res = (String) renderHeader(o, technology, stream);
                for (Object e : list.list) {
                    res += (String) renderElement(o, e, technology, stream);
                }
                res += (String) renderFooter(o, technology, stream);
            } else if (Technology.JSON.equals(technology)) {
                JSONObject jso = new JSONObject(list);
                res = jso.toString();
            }
            list.wroteHeader = list.wroteFooter = list.wroteElements = true;
            return res;
        }

        private String makeTR(DrawList list) {
            return (list.rowColor == null ? "<tr " : "<tr bgcolor=\"" + list.rowColor + "\" ")
                    + (list.valign == null ? "" : "valign=\"" + list.valign + "\"") + ">";
        }

        public Object renderElement(Object container, Object element, Technology technology, DrawStream stream) {
            DrawList list = (DrawList) container;
            String s = "";
            if (Technology.HTML.equals(technology)) {
                s += list.isVertical ? makeTR(list) + "<td>" : "<td>";
                s += Renderer.Helper.draw(element, technology, stream);
                s += list.isVertical ? "</td></tr>" : "</td>";
            } else if (Technology.JSON.equals(technology)) {
                JSONObject o = new JSONObject(element);
                s = o.toString();
            } else if (Technology.TEXT.equals(technology)) {
                s += Renderer.Helper.draw(element, technology, stream);
                s += list.isVertical ? "\n" : ",";
            }
            return s;
        }

        public Object renderFooter(Object o, Technology technology, DrawStream stream) {
            DrawList list = (DrawList) o;
            String res = "";

            if (Technology.HTML.equals(technology)) {
                res = list.isVertical ? "" : "</tr>";
                res += "</table>";
            } else if (Technology.JSON.equals(technology)) {
                res = "]";
            }
            list.wroteFooter = true;
            return res;
        }

        public Object renderHeader(Object o, Technology technology, DrawStream stream) {
            DrawList list = (DrawList) o;
            String res = "";

            if (Technology.HTML.equals(technology)) {
                res = "<table>" + (list.isVertical ? "" : makeTR(list));
            } else if (Technology.JSON.equals(technology)) {
                res = "[";
            }

            list.wroteHeader = true;
            return res;
        }
    }
}

package razie.draw.widgets;
/** i want to abstract a window
 * 
 * TODO i don't know how to abstract nicely the layouts...
 * 
 */
public class CoolWindow {
    public enum Style {
        POPUP, BROWSED
    };

    Style style;

    public CoolWindow style(Style s) {
        this.style = s;
        return this;
    }
}

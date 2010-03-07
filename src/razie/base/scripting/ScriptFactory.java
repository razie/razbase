/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package razie.base.scripting;

/**
 * minimal factory to decouple scripting
 * 
 * TODO use JSR 264 or whatever the thing is and ditch custom code...
 * 
 * @author razvanc
 */
public class ScriptFactory {
    public static ScriptFactory singleton = new ScriptFactory();

    public static void init(ScriptFactory theOneToUse) {
        singleton = theOneToUse;
    }

    public static RazScript make(String lang, String script) {
        return singleton.makeImpl(lang, script);
    }

    // TODO make protected
    public RazScript makeImpl(String lang, String script) {
        throw new UnsupportedOperationException ("no default script maker...");
    }
}

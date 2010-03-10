/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */

object Bibi extends Application {

  println ((<a attr="val"/> \ "@attr") + "==val is " + ((<a attr="val"/> \ "@attr") == "val"))
  println ("because \"val\" isInstanceOf " + (<a attr="val"/> \ "@attr").getClass.getName)

}



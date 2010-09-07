package razie;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

public class JAA {
  public static AttrAccess of (Object... args) {
     return new AttrAccessImpl (args);
  }
}
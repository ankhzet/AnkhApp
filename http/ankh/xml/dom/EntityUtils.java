package ankh.xml.dom;

import ankh.utils.PatternRunner;
import ankh.utils.Strings;
import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public final class EntityUtils extends PatternRunner {

  static final HashMap<String, String> entityMapping = new HashMap<String, String>() {
    {
      put("trade", "\u2122");
      put("copy", "\u00A9");
      put("larr", "\u2190");
      put("uarr", "\u2191");
      put("rarr", "\u2192");
      put("darr", "\u2193");
      put("nbsp", "\u00A0");
      put("", "&amp;amp;");
      put("amp", "&amp;");
    }
  };
  static final String mappingPattern = "&((" + (new Strings(entityMapping.keySet()).join("|")) + ")?);";

  public static String convertEntities(String html) {
    return runPattern(html, mappingPattern, (m) -> {
      String entity = m.group(1);
      String mapped = entityMapping.get(entity);
//      if (!entity.equals("nbsp"))
//        System.out.printf("%s -> %s\n", entity, mapped);
      return mapped;
    }).replace("<", "&lt;").replace(">", "&gt;");
  }

}

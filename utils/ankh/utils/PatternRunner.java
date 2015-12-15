package ankh.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PatternRunner {

  static final HashMap<String, Pattern> patterns = new HashMap<>();

  protected static String runPattern(String string, final String pattern, Replacer replacer) {
    Pattern p = patterns.get(pattern);
    if (p == null)
      patterns.put(pattern, p = Pattern.compile(pattern));

    Matcher m = p.matcher(string);
    if (m.find()) {
      StringBuffer sb = new StringBuffer(string.length() * 2);

      do {
        m.appendReplacement(sb, replacer.replace(m).replace("$", "\\$"));
      } while (m.find());
      
      m.appendTail(sb);
      return sb.toString();
    }
    return string;
  }

  protected interface Replacer {

    String replace(Matcher m);

  }

}

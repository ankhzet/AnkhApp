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
    StringBuffer sb = new StringBuffer(string.length() * 2);
    while (m.find())
      m.appendReplacement(sb, replacer.replace(m).replace("$", "\\$"));

    m.appendTail(sb);

    return sb.toString();
  }

  protected interface Replacer {

    String replace(Matcher m);

  }

}

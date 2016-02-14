package ankh.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Strings extends ArrayList<String> {

  public Strings() {
    super();
  }

  public Strings(String... strings) {
    super(Arrays.asList(strings));
  }

  public Strings(Collection<String> strings) {
    super(strings);
  }

  public static Strings explode(String s, String glue) {
    return split(s, Pattern.quote(glue));
  }

  public static Strings split(String s, String regex) {
    return new Strings(s.split(regex));
  }

  public static String toTitleCase(String input) {
    StringBuilder titleCase = new StringBuilder();
    boolean nextTitleCase = true;

    for (char c : input.toCharArray()) {
      if (Character.isSpaceChar(c))
        nextTitleCase = true;
      else
        if (nextTitleCase) {
          c = Character.toTitleCase(c);
          nextTitleCase = false;
        }

      titleCase.append(c);
    }

    return titleCase.toString();
  }

  public static String toCamelCase(String input) {
    StringBuilder titleCase = new StringBuilder();
    boolean nextTitleCase = false;

    for (char c : input.toCharArray()) {
      if (Character.isSpaceChar(c))
        nextTitleCase = true;
      else
        if (nextTitleCase) {
          c = Character.toTitleCase(c);
          nextTitleCase = false;
        }

      titleCase.append(c);
    }

    return titleCase.toString();
  }

  public static String triml(String input, String chars) {
    Pattern p = Pattern.compile(String.format("^[%s]*", Pattern.quote(chars)));
    Matcher m = p.matcher(input);
    if (m.find())
      input = input.substring(m.end());

    return input;
  }

  public static String trimr(String input, String chars) {
    if (input == null || input.isEmpty())
      return input;

    Pattern p = Pattern.compile(String.format("[%s]*$", Pattern.quote(chars)));
    Matcher m = p.matcher(input);
    if (m.find())
      input = input.substring(0, m.start());

    return input;
  }

  public static String trim(String input, String chars) {
    return triml(trimr(input, chars), chars);
  }

  public static String md5(String str) {
    String hash16 = "";
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(str.getBytes());
      for (int i = 0; i < digest.length; i++)
        hash16 += Integer.toString(digest[i] & 0xff, 16);
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
    return hash16;
  }

  public String join(String glue) {
    return implode(glue, toArray(new String[]{}));
  }

  public static String implode(String glue, String... pieces) {
    String result = "";
    boolean e = true;
    for (String s : pieces)
      if ((s != null) && !s.isEmpty()) {
        result += e ? s : glue + s;
        e = false;
      }

    return result;
  }

  public String pop() {
    int elements = size();
    return elements > 0 ? remove(elements - 1) : null;
  }

  public String shift() {
    return size() > 0 ? remove(0) : null;
  }

}

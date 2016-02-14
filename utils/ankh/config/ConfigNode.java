package ankh.config;

import ankh.utils.Strings;
import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigNode extends HashMap<String, Object> {

  protected boolean save = true;

  ConfigNode parent;

  public ConfigNode() {
  }

  public ConfigNode(ConfigNode parent) {
    this.parent = parent;
  }

  public boolean has(String key) {
    return get(key) != null;
  }

  public <T> T get(String key, T def) {
    Object value = get(key);
    if (value == null)
      return def;
    if (value instanceof String)
      if (((String) value).isEmpty())
        return def;
    return (T) value;
  }

  public Object get(String key) {
    Strings path = Strings.explode(key, ".");
    String childKey = path.shift();

    Object child = super.get(childKey);

    if ((child == null) || (path.size() == 0))
      return child;

    if (child instanceof ConfigNode)
      return ((ConfigNode) child).get(path.join("."));

    throw new RuntimeException(String.format("%s entry should be instance of %s, but %s fould", key, ConfigNode.class.getName(), child.getClass().getName()));
  }

  public void set(String key, Object value) {
    Strings path = Strings.explode(key, ".");
    String childKey = path.shift();

    if (path.size() == 0)
      put(childKey, value);
    else {
      ConfigNode child = (ConfigNode) super.get(childKey);

      if (child == null)
        put(childKey, child = new ConfigNode(this));
      else
        if (!(child instanceof ConfigNode))
          throw new RuntimeException(String.format("%s entry should be instance of %s, but %s fould", key, ConfigNode.class.getName(), child.getClass().getName()));

      child.set(path.join("."), value);
    }
  }

  @Override
  public final Object put(String key, Object value) {
    Object was = super.put(key, value);

    if (value != was && (was == null || !was.equals(value)))
      save();

    return was;
  }

  protected void readFromParser(Parser p) {
    save = false;
    try {
      p.checkAndNext("{");

      do {
        String node = p.Token;
        if (node.isEmpty() || node.equalsIgnoreCase("}"))
          break;

        p.next();
        if (p.isToken("=")) {
          put(node, decode(p.Token));
          p.nextAndCheck(";");
          p.next();
        } else {
          ConfigNode child = new ConfigNode(this);
          child.readFromParser(p);
          put(node, child);
        }

      } while (true);

      p.checkAndNext("}");
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to parse config: " + ex.getLocalizedMessage(), ex);
    } finally {
      save = true;
    }
  }

  public void save() {
    if (save)
      parent.save();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{\r\n");

    for (String key : keySet()) {
      Object value = super.get(key);
      if (value == null)
        continue;
      
      if (value instanceof ConfigNode && ((ConfigNode) value).isEmpty())
        continue;

      sb.append("\t")
        .append(key);

      if (value instanceof ConfigNode)
        sb.append(" ")
          .append(tab(value.toString()));
      else
        sb.append(" = ")
          .append(encode(value))
          .append(";");

      sb.append("\r\n");
    }

    sb.append("}");

    return sb.toString();
  }

  static String tab(String code) {
    Strings s = Strings.split(code, "\r?\n");
    return s.join("\r\n\t");
  }

  String encode(Object value) {
    if (value == null)
      return null;

    if (value instanceof String)
      return '"' + (String) value + '"';

    return value.toString();
  }

  Object decode(String value) {
    if (value.isEmpty())
      return value;

    if (value.matches("\\s"))
      return value;

    if (value.matches("^\\d*[\\.,]\\d*$"))
      return Double.valueOf(value);

    if (value.matches("^\\d*$"))
      return Long.valueOf(value);

    if (value.matches("^(0[xX]|#)\\p{XDigit}+$"))
      return Long.valueOf(value, 16);

    if (value.matches("^(?i)(true|false)$"))
      return Boolean.valueOf(value);

    return value;
  }

}

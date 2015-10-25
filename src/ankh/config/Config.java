package ankh.config;

import ankh.annotations.DependenciesInjected;
import ankh.annotations.DependencyInjection;
import ankh.AbstractApp;
import ankh.utils.Strings;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Config extends ConfigNode {

  @DependencyInjection()
  protected AbstractApp app;

  protected String path;
  protected String from;

  private boolean save = true;

  public Config(String src, String def) {
    path = src;
    from = def;
  }

  public Config(Parser p) {
    readFromParser(p);
  }

  public String resolveAppDir(String param, String def) {
    return app.resolveDir(get(param, def));
  }

  public String resolveAppDir(String param) {
    return resolveAppDir(param, app.appName());
  }

  public Config resolve() throws Exception {
    String realPath = app.cfgFilePath(path);
    File cfg = new File(realPath);
    if (!cfg.exists()) {
      cfg.getParentFile().mkdirs();

      InputStream template;
      if ((from == null) || null == (template = app.resourceStream(new File(app.cfgFilePath(from)).getName())))
        Files.write(cfg.toPath(), "{}".getBytes(), StandardOpenOption.CREATE);
      else
        try {
          Files.copy(template, cfg.toPath());
        } catch (IOException e) {
          throw new Exception(String.format("Failed do copy template config file to \"%s\"", path), e);
        }
    }

    path = realPath;
    return this;
  }

  @Override
  public void save() {
    if (!save)
      return;

    assert path != null;

    try (Writer w = new FileWriter(path)) {
      w.write(toString());
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to save config: " + ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  protected void readFromParser(Parser p) {
    try {
      save = false;
      super.readFromParser(p);
    } finally {
      save = true;
    }
  }

  @DependenciesInjected()
  private void diInjected() throws Exception {
    if (path != null) {
      path = app.cfgFilePath(path);
      if (!new File(path).exists())
        resolve();

      readFromParser(new Parser(path));
    }
  }

}

class ConfigNode extends HashMap<String, Object> {

  ConfigNode parent;

  public ConfigNode(ConfigNode parent) {
    this.parent = parent;
  }

  public ConfigNode() {
  }

  public boolean has(String key) {
    return get(key) != null;
  }

  public <T> T get(String key, T def) {
    Object value = get(key);
    return (value != null) ? (T) value : def;
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
  public Object put(String key, Object value) {
    Object was = super.put(key, value);

    save();

    return was;
  }

  protected void readFromParser(Parser p) {
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
        } else
          put(node, new Config(p));

      } while (true);

      p.checkAndNext("}");
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to parse config: " + ex.getLocalizedMessage(), ex);
    }
  }

  public void save() {
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

      sb.append("\t")
      .append(key);

      if (value instanceof ConfigNode)
        sb.append(tab(value.toString()));
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

    return " = " + value.toString() + ";";
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

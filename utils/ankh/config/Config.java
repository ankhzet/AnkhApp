package ankh.config;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Config extends ConfigNode {

  public Config() {
  }

  public Config(Parser p) {
    super(p);
  }

  public String getString(String key) {
    return get(key, "");
  }

}

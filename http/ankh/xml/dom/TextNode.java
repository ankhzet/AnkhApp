package ankh.xml.dom;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class TextNode extends Node {

  public TextNode(String source, int o, int l) {
    super(false, source, null, null);
    offset = o;
    length = l;
  }

  @Override
  public String toString() {
    return contents();
  }

}

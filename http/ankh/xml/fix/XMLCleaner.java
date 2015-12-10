package ankh.xml.fix;

import ankh.xml.dom.Node;
import ankh.xml.dom.XMLParser;
import ankh.xml.fix.fixtures.xml.XMLFixer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XMLCleaner {

  private static final Fixer fixer = new XMLFixer();

  public static String cleanup(String html) {
    XMLParser p = new XMLParser(html);
    Node root = p.parse();

    root = fixer.fix(root);

    return root.toString();
  }

  protected Fixer fixer() {
    return fixer;
  }

}

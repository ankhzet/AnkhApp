package ankh.xml.fix.fixtures.xml;

import ankh.xml.dom.Node;
import ankh.xml.dom.XMLParser;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UnaryFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, (child) -> {
      if (XMLParser.isUnary(child.tag))
        child.close();

      return child;
    });
  }

}

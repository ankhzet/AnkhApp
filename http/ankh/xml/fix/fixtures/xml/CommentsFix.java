package ankh.xml.fix.fixtures.xml;

import ankh.xml.dom.CommentNode;
import ankh.xml.dom.Node;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CommentsFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c instanceof CommentNode)
        c.attributes = null;

      return c;
    });
  }

}

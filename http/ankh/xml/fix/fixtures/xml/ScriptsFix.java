package ankh.xml.fix.fixtures.xml;

import ankh.xml.dom.Node;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ScriptsFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.is("script")) {
        String contents = c.contents();
        if (contents != null)
          c.setContents(
            contents.replace("&", "&amp;")
            .replace("<", "&lt;")
          );
      }

      return c;
    });
  }

}

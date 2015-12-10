package ankh.xml.fix.fixtures.xml;

import ankh.xml.dom.EntityUtils;
import ankh.xml.dom.Node;
import ankh.xml.dom.TextNode;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class EntityFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, (child) -> {
      if (child instanceof TextNode) {
        String contents = child.contents();
        if (contents != null && !contents.trim().isEmpty())
          child.setContents(
            EntityUtils.convertEntities(runPattern(contents, "(&(#\\d++|[a-zA-Z\\d\\-]*)(;?))", m -> {
              String wrap = m.group(3);
              if (!wrap.isEmpty())
                return m.group(1);

              String htm = String.format("&%s;", m.group(2));
              return htm;
            }))
          );
      }

      return child;
    });
  }

}

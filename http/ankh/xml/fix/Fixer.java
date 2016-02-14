package ankh.xml.fix;

import ankh.xml.dom.Node;
import ankh.xml.fix.fixtures.Fixture;
import java.util.ArrayList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Fixer extends ArrayList<Fixture> {

  public Node fix(Node root) {
    for (Fixture fix : this)
      root = fix.applyFixture(root);

    return root;
  }

}

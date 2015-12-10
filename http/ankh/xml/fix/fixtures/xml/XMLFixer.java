package ankh.xml.fix.fixtures.xml;

import ankh.xml.fix.Fixer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XMLFixer extends Fixer {

  public XMLFixer() {
    add(new CommentsFix());
    add(new EntityFix());
    add(new UnaryFix());
    add(new AttributesFix());
    add(new ScriptsFix());
  }

}

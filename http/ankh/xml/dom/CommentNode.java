package ankh.xml.dom;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CommentNode extends Node {

  public CommentNode(String comment) {
    super(true, null, null, comment);
  }

  @Override
  public String toString() {
    return (attributes != null)
           ? String.format("<!-- %s -->", attributes)
           : S_EMPTY;
  }

}

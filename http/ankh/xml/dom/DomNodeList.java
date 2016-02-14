package ankh.xml.dom;

import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DomNodeList extends ArrayList<Node> implements NodeList {

  public DomNodeList() {
  }

  public DomNodeList(Collection<? extends Node> c) {
    super(c);
  }

  public DomNodeList(NodeList c) {
    for (int i = 0; i < c.getLength(); i++)
      add(c.item(i));
  }


  @Override
  public Node item(int index) {
    return get(index);
  }

  @Override
  public int getLength() {
    return size();
  }

  public Node first() {
    return size() > 0 ? get(0) : null;
  }

  public Node last() {
    return size() > 0 ? get(size() - 1) : null;
  }

}

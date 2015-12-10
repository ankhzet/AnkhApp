package ankh.xml.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Node implements Iterable<Node> {

  static final String S_EMPTY = "";

  private String contents;

  Node parent;
  List<Node> childs;

  int offset, length = 0;

  boolean unary;

  private boolean closed = false;
  private boolean close = true;

  String source;
  public String tag;
  public String attributes;

  public Node(boolean unary, String source, String tag, String attributes) {
    this.unary = unary;
    this.source = source;
    this.tag = tag;
    this.attributes = attributes;
  }

  public boolean is(String tag) {
    if (this.tag == null)
      return (tag == null);

    return (tag != null) && this.tag.equalsIgnoreCase(tag);
  }

  public void addChild(Node node) {
    if (childs == null)
      childs = new ArrayList<>();

    childs.add(node);
    node.parent = this;
  }

  public boolean hasChilds() {
    return (childs != null) && !childs.isEmpty();
  }

  public boolean isClosed() {
    return closed;
  }

  public void close() {
    closed = true;
//    unary = false;
  }

  public boolean isUnary() {
    return unary;
  }

  public void setUnary(boolean unary) {
    this.unary = unary;
  }

  public Node hasParentOrSelf(String tag) {
    Node node = this;
    while (node != null)
      if (node.is(tag))
        break;
      else
        node = node.parent;

    return node;
  }

  public boolean hasParentNode(Node node) {
    Node n = this;
    while (n != null)
      if (n == node)
        return true;
      else
        n = n.parent;

    return false;
  }

  @Override
  public String toString() {
    if (tag == null)
      return contents();

    String t = tag;
//    if (!(closed || unary))
//      t = "!" + t + "!";

    return String.format(
      "<%s%s%s>%s",
      t,
      (attributes != null) ? attributes : S_EMPTY,
      unaryTag(),
      rest()
    );
  }

  String rest() {
    if (unary)
      return S_EMPTY;

    String closedTag = closedTag();
    String contents = contents();
    if (contents.isEmpty())
      return closedTag;

    return String.format("%s%s", contents, closedTag);
  }

  public String contents() {
    if (!unary) {
      if (contents != null)
        return contents;

      if (childs != null) {
        StringBuilder b = new StringBuilder();
        for (Node child : childs)
          b.append(child.toString());
        return b.toString();
      }

      if (source != null && length > 0)
        return (offset == 0 && length == source.length())
               ? source
               : source.substring(offset, offset + length);

    }
    return S_EMPTY;
  }

  public void setContents(String contents) {
    this.contents = contents;
    if (childs != null)
      childs.clear();
  }

  String unaryTag() {
    if (unary && tag.startsWith("!"))
      return S_EMPTY;

    return unary ? "/" : S_EMPTY;
  }

  String closedTag() {
    String t = close ? "</" + tag + ">" : S_EMPTY;

//    if (!(closed || unary))
//      t = "!" + t + "!";
    return t;
  }

  @Override
  public Iterator<Node> iterator() {
    return (childs != null) ? childs.iterator() : (new ArrayList<Node>()).iterator();
  }

}

package ankh.xml.fix.fixtures;

import ankh.utils.PatternRunner;
import ankh.xml.dom.Node;
import ankh.xml.dom.TextNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractFixture extends PatternRunner implements Fixture {

  Set<Node> rec = new HashSet<>();

  protected static Node ifTextNode(Node node, NodeTransformer<TextNode> t) {
    if (node instanceof TextNode)
      node = t.transform((TextNode) node);
    return node;
  }

  protected Node recursive(Node node, NodeTransformer<Node> consumer) {
    if (rec.contains(node))
      if (!node.isUnary())
        throw new RuntimeException(String.format("Node %s already fixed", node.tag));
      else
        return node;

//    System.err.printf("%s -> recursive (%d) -> %h, %s %s\n", getClass().getSimpleName(), rec.size(), node, node.getClass().getSimpleName(), node.tag);
    rec.add(node);

    node = consumer.transform(node);
    for (Node child : node)
      recursive(child, consumer);

    return node;
  }

  protected <T extends Node> T match(boolean condition, T node, Consumer<T> consumer) {
    if (condition)
      consumer.accept(node);
    return node;
  }

  protected static List<String> fetch(String html, final String open, final String close) {
    ArrayList<String> r = new ArrayList<>();

    String m = html.toLowerCase();
    int end = 0;
    while (true) {
      int start = m.indexOf(open, end);
      if (start < 0)
        break;

      end = m.indexOf(close, start + open.length());
      if (end < 0)
        break;

      r.add(html.substring(start, end + close.length()));
    }

    return r;
  }

  protected interface NodeTransformer<T extends Node> {

    T transform(T node);

  }

}

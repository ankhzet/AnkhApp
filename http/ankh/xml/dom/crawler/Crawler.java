package ankh.xml.dom.crawler;

import ankh.xml.dom.serializer.NodeSerializer;
import ankh.xml.dom.DomNodeList;
import java.util.Collection;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Crawler extends DomNodeList {

  public Crawler() {
  }

  public Crawler(Node root) {
    add(root);
  }

  public Crawler(Collection<? extends Node> c) {
    super(c);
  }

  public Crawler filter(String xpath) {
    Crawler result = new Crawler();
    for (Node node : this)
      result.addAll(xPath(node, xpath));
    return result;
  }

  public Crawler remove(String xpath) {
    Crawler result = filter(xpath);
    for (Node node : this)
      node.getParentNode().removeChild(node);
    return result;
  }

  public static Crawler filter(Collection<? extends Node> c, String xpath) {
    Crawler result = new Crawler();
    for (Node node : c)
      result.addAll(xPath(node, xpath));
    return result;
  }

  public static Crawler filter(Node node, String xpath) {
    Crawler result = new Crawler();
    result.addAll(xPath(node, xpath));
    return result;
  }

  public static Crawler filter(Node node, String xpath, Object... args) {
    return filter(node, String.format(xpath, args));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(size() * 1024);
    NodeSerializer serializer = new NodeSerializer();
    for (Node node : this)
      serializer.serialize(sb, node);

    return sb.toString();
  }

  protected static final XPath xPath = XPathFactory.newInstance().newXPath();

  public static DomNodeList xPath(Document doc, String xpath) {
    return xPath(doc.getDocumentElement(), xpath);
  }

  public static DomNodeList xPath(Node node, String xpath) {
    try {
      NodeList nodes = (NodeList) xPath.evaluate(xpath, node, XPathConstants.NODESET);
      return new DomNodeList(nodes).keepChilds(node);
    } catch (XPathExpressionException ex) {
      throw new RuntimeException(ex);
    }
  }

}

package ankh.xml.dom.serializer;

import ankh.xml.dom.EntityUtils;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class NodeSerializer {

  static final String key = NodeSerializer.class.getSimpleName();

  static final Set<String> noContentTags = new HashSet<String>() {
    {
      add("br");
      add("img");
    }
  };

  static final Set<String> inlineTags = new HashSet<String>() {
    {
      add("i");
      add("b");
      add("s");
      add("sup");
      add("sub");
      add("a");
      add("img");
    }
  };

  static final Set<String> dontSkipAttribute = new HashSet<String>() {
    {
      add("style");
      add("href");
      add("src");
      add("lang");
    }
  };

  public String serialize(Node node) {
    StringBuilder sb = new StringBuilder();
    serialize(sb, node);
    return sb.toString();
  }

  public void serialize(StringBuilder sb, Node node) {
    switch (node.getNodeType()) {
    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
    case Node.ENTITY_NODE:
      String contents = node.getTextContent();
      contents = remapEntities(contents);
      sb.append(contents);
      break;
    default:
      boolean noContent = noContent(node);

      String tag = tag(node);
      if (tag != null) {
        sb.append("<").append(tag);
        flatenAttributes(sb, node.getAttributes());
        if (noContent)
          sb.append(" /");
        sb.append(">");
      }

      if (!noContent) {
        serializeChilds(sb, node);

        if (tag != null)
          sb.append("</").append(tag).append(">");
      }
    }
  }

  void serializeChilds(StringBuilder sb, Node node) {
    NodeList childs = node.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++)
      serialize(sb, childs.item(i));
  }

  protected String tag(Node node) {
    return node.getNodeName();
  }

  protected boolean noContent(Node node) {
    return noContentTags.contains(tag(node));
  }

  protected boolean noAttribute(Node node, String name) {
    return !dontSkipAttribute.contains(name) || name.startsWith("data-");
  }

  void flatenAttributes(StringBuilder sb, NamedNodeMap attr) {
    for (int i = 0; i < attr.getLength(); i++) {
      Node a = attr.item(i);
      String name = a.getNodeName();
      if (noAttribute(a, name))
        continue;

      sb.append(" ").append(name)
        .append("=\"").append(a.getNodeValue()).append("\"");
    }
  }

  boolean isInlineTag(Node node) {
    return inlineTags.contains(tag(node));
  }

  boolean isTextNode(Node node) {
    switch (node.getNodeType()) {
    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
    case Node.ENTITY_NODE:
      return true;
    default:

    }
    return false;
  }

  boolean isEmptyTextNode(Node node) {
    if (isTextNode(node))
      return (node.getTextContent()).trim().isEmpty();
    return false;
  }

  protected String remapEntities(String html) {
    return EntityUtils.convertEntities(html);
  }

}

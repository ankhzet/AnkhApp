package ankh.xml.fix.fixtures.xml;

import ankh.xml.dom.Node;
import java.util.HashMap;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AttributesFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.attributes != null && !c.attributes.isEmpty()) {
        String attributes = c.attributes;
        HashMap<Integer, String> hash = new HashMap<>();
        attributes = runPattern(attributes, "(?s)([\"'])(.*?)\\1", m -> {
          String value = m.group(2);
          int i = hash.size() + 1;
          hash.put(i, value);
          return String.format("{!{%d}}", i);
        });

        attributes = runPattern(attributes, "(\\s+(([^$=\\s]+)\\s*=\\s*([^\"'\\s$]+)))", (m) -> {
          String attr = m.group(3);
          String val = m.group(4);
          String htm = String.format(" %s=\"%s\"", attr, val);
          return htm;
        });
        attributes = runPattern(attributes, "(\\s+([^$=\\s]++))\\s*([^=]+|$)", (m) -> {
          String attr = m.group(2);
          String rest = m.group(3);
          String htm = String.format(" %s=\"%s\"%s", attr, attr, rest);
          return htm;
        });
        attributes = runPattern(attributes, "\\{!\\{(\\d+)\\}\\}", (m) -> {
          String i = m.group(1);
          String htm = hash.get(Integer.valueOf(i));
          return htm;
        });
        c.attributes = attributes.replace("&", "&amp;");
      }

      return c;
    });
  }

}

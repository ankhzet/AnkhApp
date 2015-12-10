package ankh.xml.dom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XMLParser {

  String html;

  public XMLParser(String html) {
    this.html = html.trim();
  }

  public Node parse() {
    Node root = new RootNode();

    try (StringReader r = new StringReader(html)) {
      Node current = root;

      int contentStart = 0;

      int i = 0;
      for (;;) {
        int c = r.read();
        if (c < 0)
          break;

        switch (c) {
        case '<':
          int start = i;
          i++;
          if (start - contentStart > 0)
            current.addChild(new TextNode(html, contentStart, start - contentStart));

          int j = consume(r, "/");
          if (j < 0)
            continue;

          boolean close = j > 0;
          i += j;

          int tagEnd = expectTag(r);
          if (tagEnd <= 0)
            continue;

          String tag = html.substring(i, i += tagEnd).toLowerCase();
          String att = html.substring(i, i += expectArguments(r));

          Node n = null;

          boolean unary;
          if (unary = tag.startsWith("!"))
            n = new CommentNode(att);
          else {
            boolean slash = att.endsWith(" /") || att.endsWith("\"/") || att.equals("/");
            if (slash)
              att = att.substring(0, att.length() - 1);

            unary = slash || isUnary(tag);
          }

          j = consume(r, ">");
          if (j <= 0)
            break;
          i += j;

          contentStart = i;

          if (close) {
            Node picked = pickToClose(current, tag);
            if (picked != null) {
              picked.close();
              if (!unary)
                picked.length = start - current.offset;
              current = picked.parent;
            } else
              current.offset = i;
          } else {
            if (n == null)
              n = new Node(unary, html, tag, att);

            if (idIn(tag, ".dd.p.")) {
              Node tagSeek = current.hasParentOrSelf(tag);

              if (tagSeek != null)
                while (current != null) {
                  current.close();
                  current.length = start - current.offset;

                  Node was = current;
                  current = current.parent;
                  if (was == tagSeek)
                    break;
                }
            }

            current.addChild(n);
            if (!unary) {
              current = n;
              current.offset = i;
            }
          }
          continue;

        default:
          i++;
          continue;
        }

        break;
      }

      if (i - contentStart > 0)
        current.addChild(new TextNode(html, contentStart, i - contentStart));

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    return root;
  }

  Node pickToClose(Node current, String tag) {
    if (tag.equalsIgnoreCase(current.tag))
      return current;

    if (current.unary)
      return null;

    ArrayList<Node> l = new ArrayList<>();
    for (Node child : current)
      l.add(0, child);

    Node picked = null;
    for (Node child : l)
      if (child.tag != null && child.tag.equalsIgnoreCase(tag) && !child.isClosed()) {
        picked = child;
        break;
      }

    if (picked != null)
      return picked;

    Node n = current;
    while ((n != null) && ((n.tag == null) || !n.tag.equalsIgnoreCase(tag)))
      n = n.parent;

    if (n == null) {
      if (current.hasChilds()) {
        n = new Node(false, null, tag, null);
        for (Node child : current)
          n.addChild(child);

        current.setContents(null);
        current.addChild(n);
      }
    } else
      current.close();

    return n;
  }

  int expectTag(Reader r) throws IOException {
    int i = 0;
    for (;;) {
      r.mark(1);
      int c = r.read() & 0xffff;
      switch (c) {
      case -1:
      case '<':
      case '>':
      case '/':
      case ' ':
        r.reset();
        return i;
      }
      i++;
    }
  }

  int expectArguments(Reader r) throws IOException {
    int i = 0;
    for (;;) {
      r.mark(1);
      int c = r.read() & 0xffff;
      switch (c) {
      case -1:
      case '>':
        r.reset();
        return i;
      case '"':
        int j = seek(r, "\"");
        if (j <= 0)
//          r.reset();
          return i;
        i += j;
      }
      i++;
    }
  }

  int expect(Reader r, String match) throws IOException {
    int l = match.length();
    r.mark(l);
    try {
      return seek(r, match);
    } finally {
      r.reset();
    }
  }

  int seek(Reader r, String match) throws IOException {
    int i = 0;
    for (;;) {
      int j = consume(r, match);
      if (j != 0)
        return i + j;

      i++;
      r.skip(1);
    }
  }

  int consume(Reader r, String match) throws IOException {
    int l = match.length();
    r.mark(l);

    char[] buf = new char[l];

    if (r.read(buf) < l)
      return 0;

    if (match.equals(new String(buf)))
      return l;

    r.reset();
    return 0;
  }

  static final HashSet<String> unaries = new HashSet<String>() {
    {
      add("meta");
      add("link");
      add("base");
      add("br");
      add("img");
      add("hr");
    }
  };

  public static boolean isUnary(String tag) {
    return tag != null && unaries.contains(tag);
  }

  static boolean idIn(String id, String in) {
    return in.contains("." + id + ".");
  }

}

package ankh.http;

import ankh.utils.Strings;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class RequestParameters extends HashMap<String, Object> {

  public RequestParameters() {

  }

  public RequestParameters(HashMap<String, Object> params) {
    putAll(params);
  }

  public RequestParameters(String params) {
    parse(params);
  }

  public String format(String encoding) throws UnsupportedEncodingException {
    if (encoding == null)
      encoding = "UTF-8";

    Strings request = new Strings();
    for (Entry<String, Object> param : entrySet())
      request.add(encode(param.getKey(), param.getValue(), encoding));

    return request.join("&");
  }

  public String format(String encoding, String uri) throws UnsupportedEncodingException {
    String params = this.format(encoding);
    return params.isEmpty() ? uri : uri + "?" + params;
  }

  public String md5(String salt) {
    String r = salt;
    try {
      r = format(null, salt);
    } catch (UnsupportedEncodingException e) {

    }

    return Strings.md5(r);
  }

  public final void parse(String uri, String encoding, boolean clear) throws UnsupportedEncodingException {
    if (clear)
      clear();

    Strings params = Strings.explode(uri, "&");
    for (String param : params) {
      Strings parts = Strings.explode(param, "=");
      put(parts.get(0), URLDecoder.decode(parts.get(1), encoding));
    }

  }

  public final void parse(String uri, String encoding) throws UnsupportedEncodingException {
    parse(uri, encoding, true);
  }

  public final void parse(String uri) {
    try {
      parse(uri, "UTF-8");
    } catch (UnsupportedEncodingException e) {
    }
  }

  String encode(String property, Object o, String encoding) throws UnsupportedEncodingException {
    if (o instanceof List) {
      Strings s = new Strings();
      for (Object item : (List) o)
        s.add(String.format("%s[]=%s", property, encode(null, item, encoding)));
      return s.join("&");
    } else
      if (o instanceof HashMap) {
        HashMap<String, Object> map = (HashMap<String, Object>) o;
        Strings s = new Strings();
        for (Entry<String, Object> entry : map.entrySet())
          s.add(String.format("%s[%s]=%s", property, entry.getKey(), encode(null, entry.getValue(), encoding)));
        return s.join("&");
      } else {
        String encoded = URLEncoder.encode(o.toString(), encoding);
        return (property != null) ? property + "=" + encoded : encoded;
      }
  }

}

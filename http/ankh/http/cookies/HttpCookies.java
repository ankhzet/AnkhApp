package ankh.http.cookies;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class HttpCookies extends CookiesBag {

  static final String COOKIES_HEADER = "Set-Cookie";
  static final String COOKIE_HEADER = "Cookie";

  protected void putCookies(HttpURLConnection connection) {
    forEach((cookie) -> {
      connection.addRequestProperty(COOKIE_HEADER, cookie.toString());
    });
  }

  protected void grabCookies(HttpURLConnection connection) {
    Map<String, List<String>> headerFields = connection.getHeaderFields();
    List<String> cookiesHeader = headerFields != null ? headerFields.get(COOKIES_HEADER) : null;

    if (cookiesHeader != null)
      cookiesHeader.forEach((cookie) -> {
        if (!cookie.isEmpty())
          add(HttpCookie.parse(cookie.replace("; httponly", "")).get(0));
      });
  }

}

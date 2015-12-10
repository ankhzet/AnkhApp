package ankh.http.cookies;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.ArrayList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CookiesBag extends ArrayList<HttpCookie> {

  public CookiesBag() {
    CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
  }

  public String get(String name) {
    if (size() > 0)
      for (HttpCookie cookie : this)
        if (cookie.getName().equalsIgnoreCase(name))
          return cookie.getValue();

    return "";
  }

  public void dropCookies() {
    clear();
  }

}

package ankh.app;

import ankh.ioc.annotations.DependencyInjection;
import ankh.http.Proxy;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AppProxy extends Proxy {

  @DependencyInjection()
  protected AppConfig config;

  @Override
  protected String proxyString() {
    String proxyString = config.getString("api.proxy");
    return !proxyString.isEmpty()
           ? proxyString
           : super.proxyString();
  }

}

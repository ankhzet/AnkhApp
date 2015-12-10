package ankh.app;

import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.http.cached.CacheableClient;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AppCacheableHttpClient extends CacheableClient {

  public static final String CACHE_DIR = "cache";
  public static final long CACHE_TTL = 3600000 * 24 * 7;

  @DependencyInjection()
  protected AppConfig appConfig;

  @DependenciesInjected()
  private void diInjected() {
    setTtl(appConfig.get("api.cache.ttl", CACHE_TTL));
    setDir(appConfig.resolveAppDir("api.cache.path", CACHE_DIR));
  }

}

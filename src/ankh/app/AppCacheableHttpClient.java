package ankh.app;

import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.http.cached.CacheableClient;
import ankh.http.cached.ResponseCache;
import ankh.ioc.exceptions.FactoryException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AppCacheableHttpClient extends CacheableClient {

  public static final long CACHE_TTL = 60 * 24 * 7;

  @DependencyInjection()
  protected AppConfig appConfig;

  @DependencyInjection()
  protected ResponseCache responseCache;

  @DependenciesInjected()
  private void diInjected() throws FactoryException {
    setTtl(appConfig.apiCacheTtlProperty().asLong(CACHE_TTL) * 60000); // in minutes
    setCache(responseCache);
  }

}

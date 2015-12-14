package ankh.http.cached;

import ankh.cache.AbstractCache;
import ankh.cache.Cache;
import ankh.http.Response;
import ankh.utils.Strings;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ResponseCache extends AbstractCache<Response, Boolean> {

  Cache<InputStream, Long> underlyingCache;
  ResponseFactory responseFactory;

  public ResponseCache(Cache<InputStream, Long> underlyingCache, ResponseFactory responseFactory) {
    this.underlyingCache = underlyingCache;
    this.responseFactory = responseFactory;
  }

  public void setResponseFactory(ResponseFactory responseFactory) {
    this.responseFactory = responseFactory;
  }

  @Override
  public String key(String id) {
    return Strings.trim(id.replaceAll("(?i)^https?://", ""), "/\\");
  }

  @Override
  public Boolean has(String key) {
    return length(key) > 0;
  }

  public long length(String key) {
    return underlyingCache.has(key(key));
  }

  @Override
  public Response get(String key) throws IOException {
    InputStream cache = underlyingCache.get(key(key));
    if (cache == null)
      return null;

    return responseFactory.create(this, key(key), cache);
  }

  @Override
  public Response put(String key, Response response, long ttl) throws IOException {
    InputStream stream = response.getStream();
    if (stream != null) {
      stream = underlyingCache.put(key(key), stream, ttl);
      if (stream != null)
        response = response.setStream(stream);
    }

    return response;
  }

  @Override
  public long cleanup() {
    return underlyingCache.cleanup();
  }

  @Override
  public void forget(String key) {
    underlyingCache.forget(key(key));
  }

}

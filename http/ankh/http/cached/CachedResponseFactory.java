package ankh.http.cached;

import ankh.http.Response;
import java.io.InputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CachedResponseFactory implements ResponseFactory<InputStream> {

  @Override
  public Response create(ResponseCache cache, String key, InputStream storedAs) {
    return new Response(storedAs, cache.length(key));
  }

}

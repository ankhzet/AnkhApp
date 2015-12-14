package ankh.http.cached;

import ankh.cache.Cache;
import ankh.fs.cache.FileCache;
import ankh.fs.cache.FileStreamCache;
import ankh.http.Client;
import ankh.http.Request;
import ankh.http.Response;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CacheableClient extends Client {

  ResponseCache cache;

  String dir;
  long ttl;

  public void setTtl(long ttl) {
    this.ttl = ttl;
  }

  public void setDir(String dir) {
    this.dir = dir;
    this.cache = new ResponseCache(new FileStreamCache(new FileCache(dir)), new CachedResponseFactory());
  }

  @Override
  public Response execute(Request request) throws IOException {
    return execute(request, ttl);
  }

  public Response execute(Request request, long ttl) throws IOException {
    String key = clean(request.getFullUrl());

    return cache != null
           ? cache.remember(key, () -> super.execute(request), ttl)
           : super.execute(request);
  }

  private String clean(URL url) {
    String key = url.toString();
    String ref = url.getRef();
    if (ref != null && !ref.isEmpty())
      key = key.replaceAll(Pattern.quote("#" + ref), "");
    return key;
  }

  public void forget(URL url) {
    if (cache != null)
      cache.forget(clean(url));
  }

}

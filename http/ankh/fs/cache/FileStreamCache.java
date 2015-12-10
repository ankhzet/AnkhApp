package ankh.fs.cache;

import ankh.cache.AbstractCache;
import ankh.cache.Cache;
import ankh.fs.StreamBufferer;
import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FileStreamCache extends AbstractCache<InputStream, Long> {

  Cache<File, File> underlyingCache;

  public FileStreamCache(Cache<File, File> underlyingCache) {
    this.underlyingCache = underlyingCache;
  }

  @Override
  public String key(String id) {
    return underlyingCache.key(id);
  }

  @Override
  public Long has(String key) {
    File file = underlyingCache.has(key);
    return file != null ? file.length() : 0;
  }

  @Override
  public InputStream get(String key) throws IOException {
    File file = underlyingCache.get(key);
    if (file == null)
      return null;

    return new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public InputStream put(String filename, InputStream object, long ttl) throws IOException {
    try (FileOutputStream stream = new FileOutputStream(key(filename), false)) {
      ByteBuffer buffer = StreamBufferer.buffer(object);
      stream.write(buffer.array(), 0, buffer.limit());
      return new FileInputStream(key(filename));
    } finally {
      underlyingCache.put(filename, null, ttl);
    }
  }

  @Override
  public long cleanup() {
    return underlyingCache.cleanup();
  }

  @Override
  public void forget(String key) {
    underlyingCache.forget(key);
  }

}

package ankh.fs.cache;

import ankh.cache.AbstractCache;
import ankh.utils.Strings;
import java.io.File;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FileCache extends AbstractCache<File, File> {

  public static long DEFAULT_TTL = 1000 * 60 * 60 * 24 * 30;

  public String directory;

  public FileCache(String directory) {
    this.directory = Strings.trimr(directory, "/\\") + "/";

    File cache = new File(directory);
    if (!cache.isDirectory())
      cache.mkdir();
  }

  @Override
  public String key(String key) {
    return directory + Strings.md5(key);
  }

  @Override
  public File has(String filename) {
    File file = new File(key(filename));
    if (file.exists() && !outdated(file))
      return file;

    return null;
  }

  @Override
  public File get(String filename) {
    return has(filename);
  }

  @Override
  public File put(String filename, File object, long ttl) {
    File file = new File(key(filename));

    if (file.exists()) {
      if (ttl == 0)
        ttl = DEFAULT_TTL;

      file.setLastModified(System.currentTimeMillis() + ttl);
    }

    return file;
  }

  @Override
  public void forget(String filename) {
    File file = new File(key(filename));
    if (file.exists())
      file.delete();
  }

  @Override
  public long cleanup() {
    File d = new File(directory);

    long i = 0;
    for (File file : d.listFiles((File f) -> f.isFile()))
      if (outdated(file)) {
        file.delete();
        i++;
      }

    return i;
  }

  boolean outdated(File file) {
    long now = System.currentTimeMillis();
    long expire = file.lastModified();

    return now > expire;
  }

}

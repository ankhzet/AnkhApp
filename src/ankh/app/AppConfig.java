package ankh.app;

import ankh.AbstractApp;
import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.config.Config;
import ankh.config.Parser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AppConfig extends Config {

  @DependencyInjection()
  protected AbstractApp app;

  protected String path;
  protected String from;

  public AppConfig(String src, String def) {
    path = src;
    from = def;
  }

  public AppConfig(String src) {
    this(src, "config");
  }

  public String resolveAppDir(String param, String def) {
    return app.resolveDir(get(param, def));
  }

  public String resolveAppDir(String param) {
    return resolveAppDir(param, app.appName());
  }

  public Config resolve() throws Exception {
    String realPath = app.cfgFilePath(path);
    File cfg = new File(realPath);
    if (!cfg.exists()) {
      cfg.getParentFile().mkdirs();

      InputStream template;
      if ((from == null) || null == (template = AbstractApp.resourceStream(new File(app.cfgFilePath(from)).getName())))
        Files.write(cfg.toPath(), "{}".getBytes(), StandardOpenOption.CREATE);
      else
        try {
          Files.copy(template, cfg.toPath());
        } catch (IOException e) {
          throw new Exception(String.format("Failed do copy template config file to \"%s\"", path), e);
        }
    }

    path = realPath;
    return this;
  }

  public void save() {
    if (!save || path == null)
      return;

    try (Writer w = new FileWriter(path)) {
      w.write(toString());
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to save config: " + ex.getLocalizedMessage(), ex);
    }
  }

  @DependenciesInjected()
  private void diInjected() throws Exception {
    if (path != null) {
      path = app.cfgFilePath(path);
      if (!new File(path).exists())
        resolve();

      readFromParser(new Parser(path));
    }
  }
  
  public String getApiCacheDir(String... def) {
    return get(C_API_CACHE_DIR, def.length > 0 ? def[0] : null);
  }

  private static final String C_API_PROXY = "api.proxy";
  private static final String C_API_SERVER = "api.server.url";
  private static final String C_API_CACHE_DIR = "api.cache.dir";
  private static final String C_API_CACHE_TTL = "api.cache.ttl";

  public ConvertableProperty apiProxyProperty() {
    return property(C_API_PROXY);
  }

  public ConvertableProperty apiServerProperty() {
    return property(C_API_SERVER);
  }

  public ConvertableProperty apiCacheDirProperty(String... def) {
    return property(C_API_CACHE_DIR, def);
  }

  public ConvertableProperty apiCacheTtlProperty(String... def) {
    return property(C_API_CACHE_TTL, def);
  }

}

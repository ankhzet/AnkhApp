package ankh;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.security.CodeSource;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
abstract public class AbstractApp {

  static final String APP_ICON = "icon{:size}x{:size}.png";
  static final String RES_PATH = "/resources/";

  abstract public String appName();

  abstract public String appTitle();

  public static String resource(String name) {
    URL resource = AbstractApp.class.getResource(RES_PATH + name);
    return (resource != null) ? resource.toString() : null;
  }

  public static InputStream resourceStream(String name) {
    return AbstractApp.class.getResourceAsStream(RES_PATH + name);
  }

  public static String icon(int size) {
    return resource(APP_ICON.replaceAll("\\{:size\\}", Integer.toString(size)));
  }

  public String resolveDir(String dir) {
    if ((new File(dir)).getName().equals(dir))
      return appDir(dir);

    if (dir.contains("%app")) {
      dir = dir.replace("%app", appContainingFolder());
      File f = new File(dir);
      dir = f.toPath().toString();
    }
    return dir;
  }

  public String appDir() {
    return String.format("%s/.%s", System.getProperty("user.home"), appName());
  }

  public String appDir(String relative) {
    File f = new File(appDir());
    Path r = f.toPath().resolve(relative);
    return r.toString();
  }

  public String cfgFilePath(String cfgName) {
    if (!new File(cfgName).getName().equals(cfgName))
      return cfgName;
    
    return appDir("config/" + cfgName + ".cfg");
  }

  public static String appContainingFolder() {
    return appContainingJar().getParentFile().getAbsolutePath();
  }

  public static File appContainingJar() {
    try {
      CodeSource codeSource = AbstractApp.class.getProtectionDomain().getCodeSource();

      File jarFile;

      if (codeSource.getLocation() != null)
        jarFile = new File(codeSource.getLocation().toURI());
      else {
        String path = AbstractApp.class.getResource(AbstractApp.class.getSimpleName() + ".class").getPath();
        String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
        jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
        jarFile = new File(jarFilePath);
      }
      return jarFile;
    } catch (URISyntaxException | UnsupportedEncodingException ex) {
      throw new RuntimeException("Failed to examine app path", ex);
    }
  }

}

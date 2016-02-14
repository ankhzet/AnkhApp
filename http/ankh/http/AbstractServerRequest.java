package ankh.http;

import ankh.http.Request.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractServerRequest extends Request {

  URL apiAddress;

  public AbstractServerRequest() {
    super(Method.GET);
  }

  public AbstractServerRequest(Method method) {
    super(method);
  }

  public AbstractServerRequest(Method method, URL apiAddress) {
    super(method);
    this.apiAddress = apiAddress;
  }

  public AbstractServerRequest(URL apiAddress) {
    super(Method.GET);
    this.apiAddress = apiAddress;
    setUrl(apiAddress);
  }

  protected URL apiURL(String append, Object... args) {
    try {
      return new URL(apiAddress, String.format(append, args));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public URL getApiAddress() {
    return apiAddress;
  }

  public void setApiAddress(URL apiAddress) {
    this.apiAddress = apiAddress;
  }

  public <T extends AbstractServerRequest> T resolve(Method method, String link, Object... args) {
    T request = instantiate((Class<T>) getClass(), method);
    request.setUrl(request.apiURL(link.replace("%", "%%"), args));
    return request;
  }

  public <T extends AbstractServerRequest> T resolve(String link, Object... args) {
    return resolve(Method.GET, link, args);
  }

  protected abstract <T extends AbstractServerRequest> T instantiate(Class<T> clazz, Method method);

}

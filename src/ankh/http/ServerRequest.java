package ankh.http;

import ankh.ioc.IoC;
import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.app.AppConfig;
import ankh.ioc.exceptions.FactoryException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ServerRequest extends AbstractServerRequest {

  @DependencyInjection()
  protected AppConfig config;

  @DependencyInjection()
  protected Client httpClient;

  public ServerRequest() {
  }

  public ServerRequest(Method method) {
    super(method);
  }

  public ServerRequest(URL apiAddress) {
    super(apiAddress);
  }

  public ServerRequest(Method method, URL apiAddress) {
    super(method, apiAddress);
  }

  @Override
  public Response execute() throws IOException {
    try {
      return httpClient.execute(this);
    } finally {
      httpClient.done(this);
    }
  }

  @Override
  public void cancel() {
    httpClient.cancel(this);
  }

  @Override
  protected void finalize() throws Throwable {
    httpClient.done(this);
    super.finalize();
  }

  @Override
  protected <T extends AbstractServerRequest> T instantiate(Class<T> clazz, Method method) {
    try {
      return IoC.resolve(clazz, method, getApiAddress());
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  @DependenciesInjected()
  private void diInjected() throws MalformedURLException {
    if (getApiAddress() == null) {
      String apiUrl = config.getString("api.server.url");
      if (!apiUrl.isEmpty())
        setApiAddress(new URL(apiUrl));
    }
  }

}

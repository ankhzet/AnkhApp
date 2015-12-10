package ankh.http;

import ankh.utils.Strings;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class Request {

  public enum Method {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,

  }

  protected URL url;
  protected Method method = Method.GET;
  protected RequestParameters parameters;

  protected Exception failure;

  public Request(Method method) {
    this.method = method;
    this.parameters = new RequestParameters();
  }

  public Request(Method method, URL url, RequestParameters parameters) {
    this.url = url;
    this.method = method;
    this.parameters = parameters;
  }

  public URL getUrl() {
    return url;
  }

  public Request setUrl(URL url) {
    this.url = url;
    return this;
  }

  public Request parameters(RequestParameters parameters) {
    this.parameters = parameters;
    return this;
  }

  public URL getFullUrl() {
    try {
      String params = parameters.format(null);
      String query = Strings.implode("&", this.url.getQuery(), params);

      if (!query.isEmpty()) {
        String spec = this.url.getPath() + "?" + query;

        return new URL(this.url, spec);
      }
    } catch (UnsupportedEncodingException | MalformedURLException ex) {
      ex.printStackTrace();
      return null;
    }
    return this.url;
  }

  abstract public Response execute() throws IOException;

  abstract public void cancel();

  public void setFailure(Exception failure) {
    this.failure = failure;
  }

  public Exception getFailure() {
    return failure;
  }

  @Override
  public String toString() {
    return String.format("%s %s", method.toString(), getFullUrl());
  }

}

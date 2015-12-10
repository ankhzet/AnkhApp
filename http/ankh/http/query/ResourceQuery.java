package ankh.http.query;

import ankh.http.Request;
import ankh.http.Response;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 * @param <Resource>
 */
public class ResourceQuery<Source, Resource> extends RequestQuery {

  public interface ResourceSupplier<Source, Resource> {

    Resource get(Source from) throws Exception;

  }

  public interface SourceSupplier<Source> {

    Source get(ResourceQuery query, Response response);

  }

  private ResourceSupplier<Source, Resource> resourceSuplier;
  private SourceSupplier<Source> sourceSupplier;
  private ResourceSupplier<Response, Resource> redirectedResourceSuplier;

  public ResourceQuery(Request request) {
    super(request);
    setFollowRedirects(true);
  }

  public ResourceQuery(Request request, ResourceSupplier<Source, Resource> resourceSuplier, SourceSupplier<Source> sourceSupplier) {
    this(request);
    this.resourceSuplier = resourceSuplier;
    this.sourceSupplier = sourceSupplier;
  }

  public ResourceQuery(Request request, SourceSupplier<Source> sourceSupplier) {
    this(request, source -> (Resource) source, sourceSupplier);
  }

  public void setSourceSupplier(SourceSupplier<Source> sourceSupplier) {
    this.sourceSupplier = sourceSupplier;
  }

  public void setResourceSuplier(ResourceSupplier<Source, Resource> resourceSuplier) {
    this.resourceSuplier = resourceSuplier;
  }

  public Resource executeQuery() throws IOException, Exception {
    Response response = execute();

    if (response != null && response.isRedirected())
      return redirected(response);

    return processResponse(response);
  }

  private Resource processResponse(Response response) throws Exception {
    Source source = sourceSupplier.get(this, response);

    return resourceSuplier.get(source);
  }

  protected Resource redirected(Response response) throws Exception {
    if (redirectedResourceSuplier != null)
      return redirectedResourceSuplier.get(response);

    throw new RuntimeException("Got redirect but redirection strategy not defined");
  }

  public void setRedirectedResourceSuplier(ResourceSupplier<Response, Resource> redirectedResourceSuplier) {
    this.redirectedResourceSuplier = redirectedResourceSuplier;
  }

  public final void setFollowRedirects(boolean follow) {
    setRedirectedResourceSuplier(!follow ? null : response -> {
      String location = response.getLocation();
      getRequest().setUrl(new URL(location));
      return executeQuery();
    });
  }

}

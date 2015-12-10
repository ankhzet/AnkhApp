package ankh.http.query;

import ankh.http.Request;
import java.io.InputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Resource>
 */
public class InputStreamResourceQuery<Resource> extends ResourceQuery<InputStream, Resource> {

  public InputStreamResourceQuery(Request request, ResourceSupplier<InputStream, Resource> resourceSuplier, SourceSupplier<InputStream> sourceSupplier) {
    super(request, resourceSuplier, sourceSupplier);
  }

}

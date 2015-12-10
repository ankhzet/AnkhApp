package ankh.http.query;

import ankh.http.Request;
import org.w3c.dom.Document;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Resource>
 */
public class DocumentResourceQuery<Resource> extends ResourceQuery<Document, Resource> {

  public DocumentResourceQuery(Request request) {
    super(request);
  }

  public DocumentResourceQuery(Request request, ResourceSupplier<Document, Resource> resourceSuplier, SourceSupplier<Document> sourceSupplier) {
    super(request, resourceSuplier, sourceSupplier);
    setFollowRedirects(true);
  }

}

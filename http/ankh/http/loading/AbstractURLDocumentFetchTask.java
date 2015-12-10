package ankh.http.loading;

import org.w3c.dom.Document;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 * @param <Resource>
 */
public abstract class AbstractURLDocumentFetchTask<Source, Resource> extends AbstractURLFetchTask<Source, Document, Resource> {

  public AbstractURLDocumentFetchTask() {
  }

  public AbstractURLDocumentFetchTask(Source source) {
    super(source);
  }

}

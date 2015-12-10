package ankh.http.loading;

import ankh.http.query.ResourceQuery;
import javafx.concurrent.Task;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 * @param <Resource>
 */
public abstract class AbstractRequestTask<Source, Resource> extends Task<Resource> {

  private boolean loaded = false;

  @Override
  protected Resource call() throws Exception {
    ResourceQuery<Source, Resource> resourceQuery = query();

    resourceQuery.progressProperty().addListener((h, o, n) -> {
      double progress = n.doubleValue();

      updateProgress(progress, 1.0);
      if (!loaded && progress >= 1.0) {
        loaded = true;
        loaded();
      }
    });

    Resource result = resourceQuery.executeQuery();
    if (result == null) {
      resourceQuery.rethrow();
      
      throw new Exception("Request failed");
    }

    return result;
  }

  protected abstract ResourceQuery<Source, Resource> query();

  protected void loaded() {
  }

}

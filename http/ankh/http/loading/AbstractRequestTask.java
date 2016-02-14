package ankh.http.loading;

import ankh.http.query.ResourceQuery;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    setQuery(resourceQuery);

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

      throw new Exception("Request failed: Resource not found at specified location");
    }

    return result;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    ResourceQuery<Source, Resource> query = getQuery();
    if (query != null)
      query.cancel();

    return super.cancel(mayInterruptIfRunning);
  }

  protected abstract ResourceQuery<Source, Resource> query();

  protected void loaded() {
  }

  private ObjectProperty<ResourceQuery<Source, Resource>> queryProperty;

  public ObjectProperty<ResourceQuery<Source, Resource>> queryProperty() {
    if (queryProperty == null)
      queryProperty = new SimpleObjectProperty<>(this, "query", null);

    return queryProperty;
  }

  public ResourceQuery<Source, Resource> getQuery() {
    return (queryProperty == null) ? null : queryProperty.get();
  }

  private void setQuery(ResourceQuery<Source, Resource> query) {
    queryProperty().set(query);
  }

}

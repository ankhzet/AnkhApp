package ankh.http.query;

import ankh.http.Request;
import ankh.http.Response;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class RequestQuery extends ReentrantLock {

  Request request;
  Thread asynkTask;

  public RequestQuery(Request request) {
    this.request = request;
  }

  public Response execute() throws IOException {
    lock();
    try {
      Response r;
      setResponse(r = request.execute());
      if (r != null)
        if (!r.failed())
          return r;
        else
          request.setFailure(new IOException(r.responseMessage()));
    } catch (IOException ex) {
      request.setFailure(ex);
      throw ex;
    } finally {
      unlock();
    }

    return null;
  }

  synchronized public RequestQuery asynk(Consumer<Response> task) {
    asynkTask = new Thread(() -> {
      try {
        Response result = execute();
        task.accept(result);
      } catch (Exception e) {
        request.setFailure(e);
        if (e instanceof RuntimeException)
          throw (RuntimeException) e;
        throw new RuntimeException(e);
      }
    });

    asynkTask.start();

    return this;
  }

  synchronized public void cancel() {
    request.cancel();
  }

  public void rethrow() throws Exception {
    rethrow(null);
  }

  public void rethrow(String wrap) throws Exception {
    if (request == null)
      return;

    Exception e = request.getFailure();
    if (e != null)
      throw (wrap == null) ? e : new RuntimeException(wrap, e);
  }

  public Request getRequest() {
    return request;
  }

  private SimpleObjectProperty<Response> response;

  public Response getResponse() {
    return (response == null) ? null : response.get();
  }

  void setResponse(Response r) {
    responseProperty();
    response.set(r);
  }

  public ObservableValue<Response> responseProperty() {
    if (response == null)
      response = new SimpleObjectProperty<>(this, "response", null);
    return response;
  }

  private SimpleDoubleProperty progress;

  public double getProgress() {
    return (progress == null) ? -1. : progress.get();
  }

  public DoubleProperty progressProperty() {
    if (progress == null) {
      progress = new SimpleDoubleProperty(this, null, -1.);

      Consumer<Response> rConsumer = r -> {
        r.setListener((readed, total) -> {
          if (total == 0)
            total = -1;

          if (readed > total)
            readed = total;

          progress.set((double) readed / (double) total);
          
//          System.out.printf("Progress: %d%%\n", (int) (((double) readed / (double) total) * 100));
        });
      };

      Response r = getResponse();
      if (r == null)
        responseProperty().addListener((h, o, n) -> {
          if (n != null)
            rConsumer.accept(n);
        });
      else
        rConsumer.accept(r);

    }

    return progress;
  }

}

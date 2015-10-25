package ankh.tasks;

import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class CustomTask<V> extends Task<V> {

  public void onStateChange(Consumer<State> listener) {
    stateProperty().addListener((l, o, n) -> listener.accept(n));
  }

  public Throwable unwrapException() {
    Throwable r;
    Throwable e = r = getException();

    while ((e != null) && (e.getMessage() == null))
      e = e.getCause();

    return (e != null) ? e : r;
  }

}

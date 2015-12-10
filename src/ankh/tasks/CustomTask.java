package ankh.tasks;

import javafx.concurrent.Task;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class CustomTask<V> extends Task<V> {

  public static Throwable unwrapException(Task task) {
    Throwable r;
    Throwable e = r = task.getException();

    while ((e != null) && (e.getMessage() == null))
      e = e.getCause();

    return (e != null) ? e : r;
  }

}

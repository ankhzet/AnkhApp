package ankh.tasks;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <V>
 */
public class RunTask<V> extends CustomTask<V> {

  public interface Runnable<V> {
    V run() throws Exception;
  }

  String failed;
  String complete;
  Runnable<V> runnable;
  
  public RunTask(String title, Runnable<V> runnable) {
    updateTitle(title);
    updateMessage(title);
    this.runnable = runnable;
    setComplete("Done...");
    setFailed("Failed{1}");
  }

  public RunTask(Runnable runnable) {
    this("Working...", runnable);
  }

  public final RunTask setFailed(String failed) {
    this.failed = failed;
    return this;
  }

  public final RunTask setComplete(String complete) {
    this.complete = complete;
    return this;
  }

  @Override
  protected V call() throws Exception {
    V result = null;
    try {
      result = runnable.run();
      updateMessage(complete);
    } catch (Exception e) {
      updateMessage(failed.replace("{1}", ": " + e.getLocalizedMessage()));

      throw e;
    }
    return result;
  }

}

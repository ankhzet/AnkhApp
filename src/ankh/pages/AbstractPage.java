package ankh.pages;

import ankh.tasks.TaskManager;
import ankh.utils.Utils;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractPage implements Page, TaskManager {

  private PageNavigator navigator;
  private TaskManager taskManager;
  private Object[] navData;

  private Node node;

  private StringProperty title;

  public String getTitle() {
    return titleProperty().get();
  }

  public void setTitle(String title) {
    titleProperty().set(title);
  }

  public StringProperty titleProperty() {
    if (title == null)
      title = new SimpleStringProperty(this, "title", pathFragment());
    return title;
  }

  @Override
  public Node getNode() {
    if (node == null)
      node = buildNode();
    return node;
  }

  protected abstract Node buildNode();

  @Override
  public void setNavigator(PageNavigator navigator) {
    this.navigator = navigator;
  }

  public PageNavigator getNavigator() {
    return navigator;
  }

  @Override
  public void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  @Override
  public boolean navigateIn(Page from, Object... args) {
    navData = args;
    buildNode();
    ready();
    return true;
  }

  @Override
  public boolean navigateOut(Page to) {
    done();
    node = null;
    navData = null;
    return true;
  }

  public Object[] getNavData() {
    return navData;
  }

  public <Type> Type navDataAtIndex(int index, Supplier<Type> def) {
    return Utils.isAnyAt(index, navData, def);
  }

  public <Type> Type navDataAtIndex(int index) {
    return Utils.isAnyAt(index, navData, null);
  }

  public boolean proceed(Class<? extends Page> id, Object... args) {
    return getNavigator().navigateTo(id, args);
  }

  @Override
  public boolean perform(Task<?> task) {
    return (taskManager != null) && taskManager.perform(task);
  }

  @Override
  public void notify(String message, Action... actions) {
    if (taskManager != null)
      taskManager.notify(message, actions);
  }

  @Override
  public void dissmissNotifier() {
    if (taskManager != null)
      taskManager.dissmissNotifier();
  }

  @Override
  public void error(String message, Throwable e) {
    if (taskManager != null)
      taskManager.error(message, e);
  }

  protected void ready() {
  }

  protected void done() {
  }

  protected interface FollowupSupplier<V> {

    TaskedFollowup<V> get(TaskSupplier<V> supplier);

  }

  protected interface TaskedResultSupplier<V> {

    boolean get(FollowupSupplier<V> supplier);

  }

  protected <V> boolean followup(TaskedResultSupplier<V> consumer) {
    return consumer.get(supplier -> new TaskedFollowup<>(supplier));
  }

  protected interface TaskSupplier<V> {

    Task<V> get() throws Exception;

  }

  protected class TaskedFollowup<V> {

    TaskSupplier<V> taskSupplier;

    String error;

    public TaskedFollowup(TaskSupplier<V> taskSupplier) {
      this.taskSupplier = taskSupplier;
    }

    public Task<V> supplyTask() throws Exception {
      Task<V> t = taskSupplier.get();
      setTask(t);
      return t;
    }

    public String getError() {
      return error;
    }

    public TaskedFollowup<V> setError(String error) {
      this.error = error;
      return this;
    }

    public TaskedFollowup<V> setOnSucceeded(EventHandler<WorkerStateEvent> handler) {
      taskProperty().addListener((l, o, task) -> {
        if (task != null)
          task.setOnSucceeded(handler);
      });
      return this;
    }

    public TaskedFollowup<V> setOnFailed(EventHandler<WorkerStateEvent> handler) {
      taskProperty().addListener((l, o, task) -> {
        if (task != null)
          task.setOnFailed(handler);
      });
      return this;
    }

    public TaskedFollowup<V> setOnCancelled(EventHandler<WorkerStateEvent> handler) {
      taskProperty().addListener((l, o, task) -> {
        if (task != null)
          task.setOnCancelled(handler);
      });
      return this;
    }

    public boolean schedule(Consumer<V> onSucceed) {
      if (onSucceed != null)
        setOnSucceeded(h -> onSucceed.accept(getTask().getValue()));

      return Utils.safely(() -> {
        Task<V> task = supplyTask();

        if (perform(task))
          dissmissNotifier();
        else
          throw new Exception("Failed to run task");
      }, (e) -> {
        error(error != null ? error : e.getLocalizedMessage(), e);
      });
    }

    private SimpleObjectProperty<Task<V>> taskProperty;

    public Task<V> getTask() {
      return taskProperty.get();
    }

    public void setTask(Task<V> task) {
      taskProperty().set(task);
    }

    public SimpleObjectProperty<Task<V>> taskProperty() {
      if (taskProperty == null)
        taskProperty = new SimpleObjectProperty<>(this, "task", null);

      return taskProperty;
    }

  }

}

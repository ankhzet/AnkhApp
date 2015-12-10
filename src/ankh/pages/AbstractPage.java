package ankh.pages;

import javafx.concurrent.Task;
import ankh.tasks.TaskManager;
import ankh.utils.Utils;
import java.util.function.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
      title = new SimpleStringProperty(this, "title", null);
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
    ready();
    return true;
  }

  @Override
  public boolean navigateOut(Page to) {
    node = null;
    dissmissNotifier();
    done();
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

}

package ankh.pages;

import ankh.tasks.CustomTask;
import ankh.tasks.TaskManager;
import ankh.utils.Utils;
import java.util.function.Supplier;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractPage implements Page, TaskManager {

  private PageNavigator navigator;
  private TaskManager taskManager;
  private Object[] navData;

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
    return true;
  }

  @Override
  public boolean navigateOut(Page to) {
    return true;
  }

  public Object[] getNavData() {
    return navData;
  }

  public <Type> Type navDataAtIndex(int index, Supplier<Type> def) {
    return Utils.isAnyAt(index, navData, def);
  }

  @Override
  public boolean perform(CustomTask task) {
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

}

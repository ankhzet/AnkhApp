package ankh.pages;

import ankh.ioc.IoC;
import ankh.tasks.AbstractTaskManager;
import ankh.tasks.NotificationPane;
import ankh.tasks.TaskManager;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractPageManager extends AbstractTaskManager implements PageNavigator, TaskManager {

  private Page current;

  public AbstractPageManager(NotificationPane notificationPane) {
    super(notificationPane);
  }

  @Override
  public boolean navigateTo(Class<? extends Page> id, Object... args) {
    Page to = IoC.get(id);
    if (to == null)
      throw new RuntimeException(String.format("Page class [%s] not registered", id.getName()));

    to.setNavigator(this);
    to.setTaskManager(this);
    if (current != null)
      if (!current.navigateOut(to))
        return false;

    if (to.navigateIn(current, args)) {
      current = to;
      return true;
    }

    return false;
  }

  public Page getCurrent() {
    return current;
  }

}

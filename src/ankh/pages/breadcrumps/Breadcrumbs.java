package ankh.pages.breadcrumps;

import ankh.pages.Page;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.BreadCrumbBar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Breadcrumbs extends BreadCrumbBar<Breadcrumb> {

  public Breadcrumbs() {
    super();
    setOnCrumbAction((EventHandler<BreadCrumbBar.BreadCrumbActionEvent<Breadcrumb>>) (event) -> {
      Breadcrumb bc = event.getSelectedCrumb().getValue();
      if (bc.action != null)
        bc.action.run();
    });
  }

  public static Breadcrumb crumb(String label, Runnable action) {
    return new Breadcrumb(label, action);
  }

  public TreeItem<Breadcrumb> add(Breadcrumb crumb) {
    return setChild(crumb, getSelectedCrumb());
  }

  public TreeItem<Breadcrumb> setChild(Breadcrumb crumb, TreeItem<Breadcrumb> parent) {
    TreeItem<Breadcrumb> leaf = new TreeItem<>(crumb);

    if (parent != null)
      parent.getChildren().add(leaf);

    setSelectedCrumb(leaf);

    return leaf;
  }

  public void rebuild(Consumer<NavPathPoint<Page, Object[]>> handler) {
    Platform.runLater(() -> {
      setSelectedCrumb(null);
      TreeItem<Breadcrumb> last = null;
      for (Map.Entry<Page, NavPathPoint<Page, Object[]>> entry : navPath.entrySet())
        last = add(Breadcrumbs.crumb(entry.getKey().pathFragment(), () -> {
          handler.accept(entry.getValue());
        }));

      setSelectedCrumb(last);
      if (last != null)
        last.getValue().action = null;
    });
  }

  public void push(Page page, Object[] args) {
    navPath.push(page, args);
  }

  public NavPathPoint<Page, Object[]> pop() {
    return navPath.pop();
  }

  public int size() {
    return navPath.size();
  }

  private PageNav navPath = new PageNav();

  private class PageNav extends LinkedHashMap<Page, NavPathPoint<Page, Object[]>> {

    public PageNav() {
      super();
    }

    public PageNav(LinkedHashMap<? extends Page, ? extends NavPathPoint<Page, Object[]>> m) {
      super(m);
    }

    void push(Page page, Object[] args) {
      if (containsKey(page)) {
        PageNav copy = new PageNav(this);
        clear();

        for (Map.Entry<Page, NavPathPoint<Page, Object[]>> entry : copy.entrySet())
          if (entry.getKey() == page)
            break;
          else
            put(entry.getKey(), entry.getValue());
      }

      put(page, new NavPathPoint<>(page, args));
    }

    NavPathPoint<Page, Object[]> pop() {
      ArrayList<Page> keys = new ArrayList<>(keySet());
      if (keys.isEmpty())
        return null;
      else {
        Page last = keys.get(keys.size() - 1);

        PageNav copy = new PageNav(this);
        clear();

        NavPathPoint<Page, Object[]> pred = null;
        for (Map.Entry<Page, NavPathPoint<Page, Object[]>> entry : copy.entrySet())
          if (entry.getKey() == last)
            break;
          else
            put(entry.getKey(), pred = entry.getValue());

        return pred;
      }
    }

  }

}

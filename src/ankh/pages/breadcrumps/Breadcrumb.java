package ankh.pages.breadcrumps;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Breadcrumb {

  String label;
  Runnable action;

  public Breadcrumb(String label, Runnable action) {
    this.label = label;
    this.action = action;
  }

  @Override
  public String toString() {
    return label;
  }
  
}

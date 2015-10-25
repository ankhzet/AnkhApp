package ankh.pages.breadcrumps;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T>
 * @param <A>
 */
public class NavPathPoint<T, A> {

  private final T page;
  private final A arguments;

  public NavPathPoint(T page, A arguments) {
    this.page = page;
    this.arguments = arguments;
  }

  public T page() {
    return page;
  }
  
  public A arguments() {
    return arguments;
  }
  
}

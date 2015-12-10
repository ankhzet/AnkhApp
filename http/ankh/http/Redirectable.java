package ankh.http;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Redirectable {

  private String location;

  public boolean isRedirected() {
    return location != null;
  }

  public void setLocation(String location) {
    if (location != null && location.isEmpty())
      location = null;

    this.location = location;
  }

  public String getLocation() {
    return location;
  }

}

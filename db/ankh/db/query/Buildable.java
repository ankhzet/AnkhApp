package ankh.db.query;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Buildable {

  public static String ID_COLUMN = "id";

  public String idColumn() {
    return ID_COLUMN;
  }

  public String tableName() {
    return getClass().getSimpleName().replaceFirst("Table$", "").toLowerCase();
  }

}

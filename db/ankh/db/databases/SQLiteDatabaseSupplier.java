package ankh.db.databases;

import ankh.db.DatabaseSupplier;
import ankh.utils.Strings;
import java.io.File;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SQLiteDatabaseSupplier extends DatabaseSupplier {

  public static final String dbFileExt = "sqlite";

  @Override
  protected String database(String dbName) {
    File f = new File(dbName);

    String fileName = f.getName();
    if (Strings.explode(fileName, ".").size() <= 1)
      dbName = dbName + "." + dbFileExt;

    return dbName;
  }

}

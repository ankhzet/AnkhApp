package ankh;

import ankh.ioc.annotations.DependencyInjection;
import ankh.app.AppConfig;
import ankh.db.databases.SQLiteDatabaseSupplier;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigDatabaseSupplier extends SQLiteDatabaseSupplier {

  static final String dbFileKey = "db.file";

  @DependencyInjection()
  protected AppConfig config;

  @Override
  protected String database(String dbName) {
    return super.database(
      dbName != null
      ? dbName
      : config.resolveAppDir(dbFileKey)
    );
  }

}

package ankh.db.databases;

import ankh.db.ConnectionBuilder;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SQLiteConnectionBuilder extends ConnectionBuilder {

  public SQLiteConnectionBuilder() {
    super();

    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException ex) {
      LOG.log(Level.SEVERE, "Failed to init JDBC driver", ex);
      throw new RuntimeException("Failed to init JDBC driver", ex);
    }
  }

  @Override
  public Connection connect(String database, String connectionString) throws SQLException {
    File journal = new File(database + "-journal");
    if (journal.exists())
      journal.delete();

    return super.connect(database, connectionString);
  }

  @Override
  protected String driverConnection() {
    return "jdbc:sqlite:%s";
  }

}

package ankh.db;

import ankh.ioc.annotations.DependencyInjection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ankh.ioc.builder.ClassBuilder;
import ankh.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class ConnectionBuilder extends ClassBuilder<Connection> {

  @DependencyInjection()
  protected DatabaseSupplier databaseSupplier;

  public ConnectionBuilder() {
    super(Connection.class);
  }

  protected abstract String driverConnection();

  @Override
  public synchronized Connection build(Class<? extends Connection> c, Object... args) throws Exception {
    String database = databaseSupplier.database(Utils.isAny(args));

    return connect(database, connectionString(database));
  }

  public String connectionString(String database) {
    return String.format(driverConnection(), database);
  }

  public Connection connect(String database, String connectionString) throws SQLException {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(connectionString);
      LOG.log(Level.INFO, "Connecting to [{0}]: OK", connectionString);
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, String.format("Connecting to [{0}]: FAIL", connectionString), e);
      throw e;
    }
    return connection;
  }

  protected static final Logger LOG = Logger.getLogger(ConnectionBuilder.class.getName());

}

package ankh.db;

import ankh.db.query.Buildable;
import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.db.query.Builder;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <KeyType>
 */
public abstract class Table<KeyType> extends Buildable {

  @DependencyInjection(instantiate = false)
  protected Builder<KeyType> builder;

  @DependenciesInjected(suppressInherited = false, beforeInherited = false)
  private void diInjected() throws Exception {
    try {
      createIfNotExists();
      LOG.log(Level.FINE, "Table [{0}] is OK.\n", tableName());
    } catch (Throwable e) {
      throw new Exception(String.format("Can't create table [%s]!", tableName()), e);
    }
  }

  public void createIfNotExists() throws SQLException {
    String schema = schema().trim();

    String key = idColumn();
    Matcher m = Pattern.compile("((^|,)\\s*" + key + ")\\s", Pattern.CASE_INSENSITIVE).matcher(schema);
    if (!m.find())
      schema = key + " INTEGER PRIMARY KEY AUTOINCREMENT, " + schema;

    tableBuilder().create(schema);
  }

  public void truncate() throws SQLException {
    tableBuilder().truncate();
  }

  public void drop() throws SQLException {
    tableBuilder().drop();
  }

  public Builder<KeyType> tableBuilder() {
    return builder.table(this);
  }

  protected abstract String schema();

  private static final Logger LOG = Logger.getLogger(Table.class.getName());

}

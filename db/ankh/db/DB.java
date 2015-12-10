package ankh.db;

import ankh.db.query.Buildable;
import ankh.db.query.Builder;
import ankh.utils.Strings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DB extends Builder<Object> {

  public DB() {
  }

  public DB(Buildable from) {
    super(from);
  }

  public boolean isLogging() {
    return false;
  }

  @Override
  public String beforeSQL(String sql) {
    if (isLogging())
      logSQL(sql);

    return sql;
  }

  void logSQL(String sql) {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();

    Strings filtered = new Strings();

    for (StackTraceElement element : stack) {
      if (element.isNativeMethod() || element.getLineNumber() < 0)
        continue;

      String c = element.getClassName();
      if (!c.contains(".ankh.") || c.contains(".db."))
        continue;

      filtered.add(element.toString());
    }

    LOG.log(Level.FINER, String.format("SQL => %s \n\t[ %s\n\t]", subtitutedSQL(sql), filtered.join("\n\t| ")));
  }

  private static final Logger LOG = Logger.getLogger(DB.class.getName());

}

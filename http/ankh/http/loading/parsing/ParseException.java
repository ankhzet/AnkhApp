package ankh.http.loading.parsing;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ParseException extends Exception {

  protected int lineNumber, columnNumber;

  public ParseException(Throwable cause, int lineNumber, int columnNumber) {
    super(cause);
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getColumnNumber() {
    return columnNumber;
  }

  @Override
  public String getLocalizedMessage() {
    return getCause().getLocalizedMessage();
  }

  @Override
  public String getMessage() {
    return getCause().getMessage();
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return getCause().getStackTrace();
  }

}

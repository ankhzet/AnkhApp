package ankh.http.loading;

import ankh.http.loading.parsing.ExceptionReporter;
import static ankh.http.loading.parsing.ExceptionReporter.report;
import ankh.http.loading.parsing.ParseException;
import java.io.PrintStream;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SAXParseExceptionReporter extends ExceptionReporter {

  public SAXParseExceptionReporter(PrintStream stream, SAXParseException ex, String source) {
    super(stream, wrap(ex), source);
  }

  public static void report(SAXParseException ex, String html) {
    report(wrap(ex), html);
  }

  static ParseException wrap(SAXParseException ex) {
    return new ParseException(ex, ex.getLineNumber(), ex.getColumnNumber());
  }

}

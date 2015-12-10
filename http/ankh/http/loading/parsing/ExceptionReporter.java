package ankh.http.loading.parsing;

import ankh.utils.Strings;
import java.io.PrintStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ExceptionReporter {

  int context = 10;
  int tabWidth = 8;
  
  PrintStream stream;

  ParseException exception;
  String source;

  public ExceptionReporter(PrintStream stream, ParseException ex, String source) {
    this.stream = stream;
    this.exception = ex;
    this.source = source;
  }

  public ExceptionReporter(ParseException ex, String source) {
    this(System.err, ex, source);
  }

  public void print() {
    stream.println(toString());
  }

  @Override
  public String toString() {
    int errLine = exception.getLineNumber();
    int errColm = exception.getColumnNumber();
    Strings lines = Strings.explode(source, "\n");

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("\n  ---->%s\n\n", lines.get(errLine - 1)));

    int s = Math.max(errLine - context, 0);
    int e = Math.min(errLine + context, lines.size() - 1);
    int i = s;
    for (String line : lines.subList(s, e)) {
      boolean hit = errLine == ++i;

      sb.append(hit ? "> " : "  ");

      sb.append(String.format("[%3d] %s\n", i, line));

      if (hit) {
        for (int index = 0; index < Math.min(line.length(), errColm); index++)
          if (line.charAt(index) == '\t')
            errColm += tabWidth - 1;
          
        sb.append("        ")
          .append(String.format("%" + errColm + "s", "^"))
          .append("\n");
      }
    }

    return sb.toString();
  }

  public static void report(ParseException ex, String source) {
    new ExceptionReporter(ex, source).print();
  }

}

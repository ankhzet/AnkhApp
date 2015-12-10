package ankh.http.loading;

import ankh.fs.StreamBufferer;
import ankh.http.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ResponseReader {

  private static final Pattern charsetPattern = Pattern.compile("charset\\s*=[\\s\"]*([^;\"\\s]+)", Pattern.CASE_INSENSITIVE);

  public String read(Response response) throws IOException {
    String charset = null;
    HttpURLConnection c = response.getConnection();
    if (c != null)
      charset = parseCharset(c.getContentType());
    if (charset == null)
      charset = "windows-1251";

    charset = charset.toLowerCase();

    ByteBuffer bytes = StreamBufferer.buffer(response.getStream());
    String content = new String(bytes.array(), charset);

    String actualCharset = parseCharset(content);
    if (!charset.equalsIgnoreCase(actualCharset))
      try {
        content = new String(bytes.array(), actualCharset);
      } catch (UnsupportedEncodingException ex) {
        System.err.println("Unsupported encoding: " + actualCharset);
      }

    return content;
  }

  private String parseCharset(String from) {
    Matcher m = charsetPattern.matcher(from);

    if (m.find())
      return m.group(1).trim().toLowerCase();

    return from;
  }

}

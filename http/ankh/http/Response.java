package ankh.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Response extends Redirectable {

  public interface ProgressListener {

    void progress(long done, long max);

  }

  public static final int SUCCESS_STATUS = HttpURLConnection.HTTP_OK;

  public int status = SUCCESS_STATUS;
  private InputStream stream;
  private long dataLength = -1;

  HttpURLConnection connection;

  private ProgressListener listener;

  public Response(HttpURLConnection connection) {
    this.connection = connection;
    try {
      status = connection.getResponseCode();
      switch (status) {
      case SUCCESS_STATUS:
        dataLength = connection.getContentLengthLong();
        setStream(connection.getInputStream());

        String encoding = connection.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("gzip"))
          stream = new GZIPInputStream(stream);

      default:
        if ((status >= 300) && (status < 400))
          setLocation(connection.getHeaderField("location"));
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Response(InputStream stream, long dataLength) {
    this.dataLength = dataLength;
    setStream(stream);
  }

  public boolean failed() {
    return status != SUCCESS_STATUS && !isRedirected();
  }

  public final Response setStream(InputStream stream) {
    this.stream = new ProgressedStream(stream);
    return this;
  }

  public InputStream getStream() {
    return stream;
  }

  public HttpURLConnection getConnection() {
    return connection;
  }

  public void setListener(ProgressListener listener) {
    this.listener = listener;
  }

  public String responseMessage() {
    try {
      return String.format("Request failed with %d %s",
                           status,
                           (connection == null)
                           ? "HTTP code"
                           : connection.getResponseMessage()
      );
    } catch (IOException ex) {
      return ex.getLocalizedMessage();
    }
  }

  private void progress(long readed) {
    if (listener != null)
      listener.progress(readed, getTotalSize());
  }

  public long getTotalSize() {
    return dataLength;
  }

  @Override
  public String toString() {
    return String.format("[%d] %s, %s", status, connection, stream);
  }

  class ProgressedBufferedStream extends BufferedInputStream {

    public ProgressedBufferedStream(InputStream in) {
      super(new ProgressedStream(in));
    }

  }

  class ProgressedStream extends BufferedInputStream {

    long readed = 0;

    public ProgressedStream(InputStream in) {
      super(in);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int r = super.read(b, off, len);

      readed += r;

      if (r >= 0)
        progress(readed);

      return r;
    }

  }

}

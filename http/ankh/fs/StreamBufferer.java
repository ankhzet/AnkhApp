package ankh.fs;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class StreamBufferer {

  static final int BUFFER_SIZE = 1024 * 32;

  public static ByteBuffer buffer(InputStream stream) throws IOException {
    try (BufferedInputStream reader = new BufferedInputStream(stream)) {
      byte[] buffer = new byte[BUFFER_SIZE];
      ByteBuffer result = ByteBuffer.allocate(BUFFER_SIZE);

      int totalReaded = 0, readed;
      while ((readed = reader.read(buffer)) >= 0) {
        if (readed > result.remaining()) {
          Buffer t = result;
          result = (ByteBuffer) ByteBuffer.allocate(totalReaded + readed);
          ((ByteBuffer) result).put(((ByteBuffer) t).array(), 0, totalReaded);
        }

        totalReaded += readed;
        result.put(buffer, 0, readed);
      }

      return ByteBuffer.wrap(result.array(), 0, totalReaded);
    }
  }

}

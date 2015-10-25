package ankh.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Parser {

  private BufferedReader reader;
  public String Token = "";

  boolean flush;
  char character;

  public Parser(String src) throws IOException {
    src = src.replace("file:", "");
    init(new FileReader(src));
  }

  public Parser(Reader r) throws IOException {
    init(r);
  }

  private void init(Reader r) throws IOException {
    reader = new BufferedReader(r);
    flush = true;
    seekNext();
  }

  private char c() {
    if (flush)
      try {
        if (!reader.ready())
          return 0;

        char[] buffer = new char[1];
        int readed = reader.read(buffer);
        character = (readed > 0) ? buffer[0] : 0;
      } catch (IOException ex) {
        ex.printStackTrace();
        return 0;
      }

    flush = true;
    return character;
  }

  private String seekNext() {
    return next();
  }

  public int nextInt() {
    return Integer.parseInt(next());
  }

  public String next() {
    Token = "";
    char chr, tc;
    while (true) {
      chr = c();
      switch (chr) {
      case 0:
        break;
      case '/':
        chr = c();
        if (chr != '/') {
          Token += '/';
          flush = false;
          break;
        }

        while ((chr = c()) != 0)
          if ((chr == 13) || (chr == 10))
            break;

        flush = false;
        continue;
      case '\r':
      case '\n':
      case '\t':
      case ' ':
        while ("\r\n\t ".indexOf(chr) >= 0)
          chr = c();

        flush = false;
        continue;
      case '\'':
      case '"':
        tc = chr;
        while ((chr = c()) != 0) {
          if (chr == tc) {
            chr = c();
            if (chr != tc) {
              flush = false;
              break;
            }
          }

          Token += chr;
        }
        break;
      default:
        flush = false;
        if ((chr >= '0') && (chr <= '9'))
          while ((((chr = c()) >= '0') && (chr <= '9')) || (chr == '.'))
            Token += chr;
        else
          if (((chr >= 'a') && (chr <= 'z')) || ((chr >= 'A') && (chr <= 'Z')) || (chr == '_'))
            while ((((chr = c()) >= '0') && (chr <= '9')) || ((chr >= 'a') && (chr <= 'z')) || ((chr >= 'A') && (chr <= 'Z')) || (chr == '_') || (chr == '-'))
              Token += chr;
          else {
            flush = true;
            if (chr > 32) {
              Token += chr;
              break;
            }

            continue;
          }

        flush = false;
      }
      break;
    }

    return Token;
  }

  public boolean aboveEnd() {
    try {
      return !reader.ready();
    } catch (IOException ex) {
      ex.printStackTrace();
      return true;
    }
  }

  /**
   * Return integer between left and right tokens (current token must be
   * {@code left}).
   *
   * @param left
   * @param right
   * @return int
   * @throws java.lang.Throwable
   */
  public int getInt(String left, String right) throws Throwable {
    return Integer.parseInt(getValue(left, right));
  }

  /**
   * If token != parser current token - throws exception.
   *
   * @param token
   * @throws java.lang.Throwable
   */
  public void check(String token) throws Throwable {
    if (!token.equalsIgnoreCase(Token))
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
  }

  /**
   * If token != parser current token - throws exception, else returns next
   * token.
   *
   * @param token
   * @return {@code String} next token.
   * @throws java.lang.Throwable
   */
  public String checkAndNext(String token) throws Throwable {
    if (!token.equalsIgnoreCase(Token))
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
    return next();
  }

  /**
   * If token = parser current token - returns true and goes to next token.
   *
   * @param token
   * @return {@code true} if current token == {@code token};
   *
   * {@code false} current token != {@code token}
   * @throws java.lang.Throwable
   */
  public boolean isToken(String token) throws Throwable {
    if (token.equalsIgnoreCase(Token)) {
      next();
      return true;
    }
    return false;
  }

  /**
   * Returns value between {@code left} and {@code right} tokens. Current token
   * must be equal to {@code left}. After that current token points on
   * {@code right} word.
   *
   * @param left
   * @param right
   * @return {@code String} token.
   * @throws java.lang.Throwable
   */
  public String getValue(String left, String right) throws Throwable {
    String r = checkAndNext(left);
    nextAndCheck(right);
    return r;
  }

  /**
   * if next token != {@code token} - throws exception.
   *
   * @param token
   * @throws java.lang.Throwable
   */
  public void nextAndCheck(String token) throws Throwable {
    if (!token.equalsIgnoreCase(next()))
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
  }

}

package ankh.cache;

import java.io.IOException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 * @param <Has>
 */
public interface Cache<Type, Has> {

  String key(String id);

  Has has(String id);

  Type get(String id) throws IOException;

  Type put(String id, Type object, long ttl) throws IOException;

  Type add(String id, Type object, long ttl) throws IOException;

  Type remember(String id, Remember<Type> supplier, long ttl) throws IOException;

  void forget(String id);

  long cleanup();

  interface Remember<Type> {

    Type get() throws IOException;

  }

}

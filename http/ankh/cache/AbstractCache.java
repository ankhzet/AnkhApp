package ankh.cache;

import java.io.IOException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 * @param <Has>
 */
public abstract class AbstractCache<Type, Has> implements Cache<Type, Has> {

  @Override
  public Type add(String key, Type object, long ttl) throws IOException {
    Type has = get(key);
    if (has != null)
      return has;

    return put(key, object, ttl);
  }

  @Override
  public Type remember(String key, Remember<Type> supplier, long ttl) throws IOException {
    Type object = get(key);
    if (object != null)
      return object;

    object = supplier.get();
    return (object != null)
           ? put(key, object, ttl)
           : null;
  }

}

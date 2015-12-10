package ankh.ioc.registrar;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <P> Type, produced by factory
 */
public class AbstractFactoryRegistrar<P> implements FactoryRegistrar<P> {

  static final HashMap<Object, Boolean> counters = new HashMap<Object, Boolean>();

  public AbstractFactoryRegistrar(Object identifier) {
    registerIfNeeded(identifier);
  }

  final void registerIfNeeded(Object identifier) {
    Object factoryIdentifier = getFactoryIdentifier(identifier);
    synchronized (counters) {
      Boolean registered = counters.get(factoryIdentifier);
      if (!Objects.equals(registered, Boolean.TRUE)) {
        counters.put(factoryIdentifier, Boolean.TRUE);
        register(factoryIdentifier, getInstance(factoryIdentifier));
      }
    }

  }

  @Override
  public void register(Object factoryIdentifier, P factory) {
    throw new RuntimeException(String.format("Don't know how to register %s", factoryIdentifier));
  }

  @Override
  public Object getFactoryIdentifier(Object identifier) {
    return identifier;
  }

  @Override
  @SuppressWarnings("unchecked")
  public P getInstance(Object identifier) {
    return (P) identifier;
  }

}

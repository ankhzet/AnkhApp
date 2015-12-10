package ankh.ioc;

import ankh.ioc.exceptions.FactoryException;
import ankh.ioc.exceptions.UnknownFactoryProductException;
import ankh.ioc.factory.Factory;
import ankh.ioc.resolver.ClassDependencyResolver;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoC extends ClassDependencyResolver {

  static IoC ioc = new IoC();

  public static synchronized IoC instance() {
    return ioc;
  }

  public static void drop() {
    ioc = null;
  }

  public static <T> T get(Class<? extends T> identifier) {
    Factory<Class<? extends T>, T> factory;
    try {
      factory = getFactory(identifier);
      return factory.get(identifier);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <P> P make(Class<? extends P> identifier, Object... args) throws FactoryException {
    Factory<Class<? extends P>, P> factory = getFactory(identifier);
    return factory.make(identifier, args);
  }

  @SuppressWarnings("unchecked")
  public static <I, P> Factory<I, P> registerFactory(Factory<I, P> factory) {
    for (I identifier : factory.canProduce())
      ioc.put(identifier, factory);

    return factory;
  }

  @SuppressWarnings("unchecked")
  static final <P> Factory<Class<? extends P>, P> getFactory(Class<? extends P> identifier) throws UnknownFactoryProductException {
    return ioc.factory(identifier);
  }

  @SuppressWarnings("unchecked")
  public static <P> P resolve(Class<? extends P> identifier, Object... args) throws FactoryException {
    Factory<Class<? extends P>, P> factory = getFactory(identifier);

    P instance = factory.make(identifier, args);
    if (instance != null)
      ioc.injectDependencies(instance);

    return instance;
  }

  @SuppressWarnings("unchecked")
  public static <P> P resolve(Class<? extends P> identifier) throws FactoryException {
    return resolve(identifier, new Object[]{});
  }

  @SuppressWarnings("unchecked")
  public static <Type> Type resolve(Type instance) throws FactoryException {
    return (Type) ioc.injectDependencies(instance);
  }

}

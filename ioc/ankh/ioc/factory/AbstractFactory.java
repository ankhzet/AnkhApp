package ankh.ioc.factory;

import java.util.HashMap;
import java.util.Set;
import ankh.ioc.builder.Builder;
import ankh.ioc.exceptions.FactoryException;
import ankh.ioc.exceptions.FailedFactoryProductException;
import ankh.ioc.exceptions.UnknownFactoryProductException;
import ankh.ioc.resolver.DependencyResolver;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> Type of producers identifiers
 * @param <P> Type of objects, produced by factory
 */
public abstract class AbstractFactory<I, P> implements Factory<I, P> {

  HashMap<I, P> container = new HashMap<>();
  HashMap<I, Builder<I, P>> builders = new HashMap<>();

  protected DependencyResolver<I, P> resolver;

  public AbstractFactory(DependencyResolver<I, P> resolver) {
    this.resolver = resolver;
  }

  @Override
  public Set<I> canProduce() {
    return builders.keySet();
  }

  @Override
  synchronized public P get(I identifier) throws FactoryException {
    P instance = null;
    try {
      instance = pick(container, identifier);
    } catch (UnknownFactoryProductException e) {
    }

    if (instance == null) {
      container.put(identifier, instance = make(identifier));
      resolver.injectDependencies(instance);
    }

    return instance;
  }

  @Override
  public P make(I identifier, Object... args) throws FactoryException {
    Builder<I, P> builder;
    synchronized (this) {
      builder = pick(builders, identifier);
    }

    try {
      return builder.build(identifier, args);
    } catch (Exception e) {
      throw FactoryException.unwrap(e, () -> {
        return new FailedFactoryProductException(identifier, e);
      });
    }
  }

  @Override
  synchronized public Builder<I, P> register(I identifier, Builder<I, P> builder) {
    Builder<I, P> old = builders.get(identifier);
    builders.put(identifier, builder);
    return old;
  }

  protected abstract <R> R pick(HashMap<I, R> map, I id) throws UnknownFactoryProductException;

}

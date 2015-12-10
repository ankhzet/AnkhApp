package ankh.ioc.registrar;

import ankh.ioc.builder.Builder;
import ankh.ioc.factory.SingleClassFactory;
import ankh.ioc.resolver.DependencyResolver;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Produces>
 */
public class SingleClassFactoryRegistrar<Produces> extends ClassFactoryRegistrar<Produces> {

  public SingleClassFactoryRegistrar(DependencyResolver<Class<? extends Produces>, Produces> resolver, Class<Produces> c, Builder<Class<? extends Produces>, Produces> builder) {
    super(new SingleClassFactory<Produces>(resolver, c, builder));
  }

  public SingleClassFactoryRegistrar(DependencyResolver<Class<? extends Produces>, Produces> resolver, Class<Produces> c) {
    super(new SingleClassFactory<Produces>(resolver, c));
  }

}

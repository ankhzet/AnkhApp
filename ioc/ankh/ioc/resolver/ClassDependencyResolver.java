package ankh.ioc.resolver;

import ankh.ioc.exceptions.UnknownFactoryProductException;
import ankh.ioc.factory.ClassFactory;
import ankh.ioc.factory.Factory;
import java.lang.reflect.Method;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <P>
 */
public abstract class ClassDependencyResolver<P> extends AbstractDependencyResolver<Class<? extends P>, P, ClassFactory<P>> {

  @SuppressWarnings("unchecked")
  @Override
  public <Identifier extends Class<? extends P>, Produces extends P> Factory<Identifier, Produces> factory(Identifier identifier) throws UnknownFactoryProductException {
    ClassFactory<P> f = get(identifier);

    if (f == null)
      for (Class<? extends P> c : keySet())
        if (identifier.isAssignableFrom(c)) {
          f = get(c);
          break;
        }

    if (f != null)
      return (Factory<Identifier, Produces>) f;

    throw new UnknownFactoryProductException(identifier);
  }

  @Override
  protected DependencyNode makeDependencyNode(Class clazz, FieldsList fields, Method finisher) {
    return new IoCDependencyNode(fields, finisher, clazz);
  }

}

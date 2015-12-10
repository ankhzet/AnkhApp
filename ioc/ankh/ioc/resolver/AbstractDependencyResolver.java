package ankh.ioc.resolver;

import ankh.ioc.annotations.DependenciesInjected;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import ankh.ioc.exceptions.UnknownFactoryProductException;
import ankh.ioc.factory.Factory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I>
 * @param <P>
 * @param <F>
 */
public abstract class AbstractDependencyResolver<I, P, F extends Factory<I, P>> extends HashMap<I, F> implements DependencyResolver<I, P> {

  public abstract <Identifier extends I, Produces extends P> Factory<Identifier, Produces> factory(Identifier identifier) throws UnknownFactoryProductException;

  @Override
  public P resolve(I identifier, Object... args) throws FactoryException {
    Factory<I, P> factory = factory(identifier);

    P instance = factory.make(identifier, args);
    if (instance != null)
      injectDependencies(instance);

    return instance;
  }

  @Override
  public P injectDependencies(P instance) throws FactoryException {
    Class<?> c = instance.getClass();
    DependencyNode dependencies = collectDependencies(instance, c);

    if (dependencies != null)
      for (DependencyNode dependency : dependencies)
        dependency.resolve(instance);

    return instance;
  }

  protected DependencyNode collectDependencies(Object instance, Class<?> c) {
    Method finisher = null;
    for (Method declaredMethod : c.getDeclaredMethods())
      if (declaredMethod.isAnnotationPresent(DependenciesInjected.class)) {
        finisher = declaredMethod;
        break;
      }

    FieldsList fields = new FieldsList();
    for (Field declaredField : c.getDeclaredFields())
      if (declaredField.isAnnotationPresent(DependencyInjection.class))
        fields.add(declaredField);

    DependencyNode current = makeDependencyNode(c, fields, finisher);

    boolean suppress = false;
    boolean reversed = false;

    if (finisher != null) {
      DependenciesInjected annotation = finisher.getAnnotation(DependenciesInjected.class);
      suppress = annotation.suppressInherited();
      reversed = annotation.beforeInherited();
    }

    DependencyNode superclass = null;
    if (!suppress) {
      c = c.getSuperclass();
      if (c != null && c != Object.class)
        superclass = collectDependencies(instance, c);
    }

    if (superclass != null)
      if (!reversed) {
        superclass.append(current);
        current = superclass;
      } else
        current.append(superclass);

    return current.fold();
  }

  protected abstract DependencyNode makeDependencyNode(Class clazz, FieldsList fields, Method finisher);

}

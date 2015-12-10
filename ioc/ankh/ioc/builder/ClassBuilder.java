package ankh.ioc.builder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import ankh.ioc.exceptions.FailedFactoryProductException;
import java.util.Arrays;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type> Class, builded by builder
 */
public class ClassBuilder<Type> implements Builder<Class<? extends Type>, Type> {

  ReentrantLock lock = new ReentrantLock();

  Class<? extends Type> classRef;

  public ClassBuilder() {
  }

  public ClassBuilder(Class<? extends Type> classRef) {
    this.classRef = classRef;
  }

  /**
   *
   * @param c Class to be instantiated
   * @param args Arguments to pass to constructor
   * @return instantiated object
   * @throws Exception if has no accesible constructors, has more than 1
   * constructor or can't resolve it's dependencies.
   */
  @Override
  synchronized public Type build(Class<? extends Type> c, Object... args) throws Exception {
    if (!lock.tryLock())
      return null;

    try {

      if (classRef != null)
        c = classRef;

      Constructor<? extends Type> constructor = pickConstructor(c, types(args));

      Type instance = constructor.newInstance(args);

      return instance;
    } finally {
      lock.unlock();
    }
  }

  Class<?>[] types(Object[] args) {
    ArrayList<Class<?>> list = new ArrayList<>();
    for (Object arg : args)
      if (arg != null)
        list.add(arg.getClass());
      else
        list.add(null);
    return list.toArray(new Class<?>[]{});
  }

  @SuppressWarnings("unchecked")
  Constructor<? extends Type> pickConstructor(Class<? extends Type> c, Class<?>[] args) throws FailedFactoryProductException {
    Constructor<? extends Type>[] constructors = (Constructor<? extends Type>[]) c.getConstructors();

    ArrayList<Constructor<? extends Type>> list = new ArrayList<>();
    for (Constructor<? extends Type> constructor : constructors)
      if (constructor.getParameterCount() == args.length || constructor.isVarArgs())
        list.add(constructor);

    ArrayList<Constructor<? extends Type>> p = new ArrayList<>();

    for (Constructor<? extends Type> constructor : list) {
      int param = 0;
      Class<?>[] t = constructor.getParameterTypes();
      boolean ok = true;
      for (Class<?> arg : args) {
        Class<?> paramType = t[param++];
        if (!(ok = arg == null || paramType.isAssignableFrom(arg)))
          break;
      }

      if (ok)
        p.add(constructor);
    }

    if (p.isEmpty())
      throw new FailedFactoryProductException(String.format("%s, no constructor with parameters %s", c.getSimpleName(), Arrays.asList(args).toString()));

    // todo: pick more corresponding constructor here
    return p.get(0);
  }

}

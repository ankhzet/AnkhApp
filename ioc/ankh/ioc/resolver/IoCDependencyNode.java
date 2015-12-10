package ankh.ioc.resolver;

import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.lang.reflect.Method;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCDependencyNode extends AbstractDependencyNode {

  public IoCDependencyNode(FieldsList fields, Method finisher, Class<?> forClass) {
    super(fields, finisher, forClass);
  }

  @Override
  protected Object makeDependency(Class<?> c, boolean instantiate) throws FactoryException {
    return instantiate ? IoC.make(c) : IoC.get(c);
  }

}

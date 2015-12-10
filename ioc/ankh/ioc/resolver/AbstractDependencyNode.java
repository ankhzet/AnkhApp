package ankh.ioc.resolver;

import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import ankh.ioc.exceptions.FailedFactoryProductException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractDependencyNode implements DependencyNode {

  protected FieldsList fields;
  protected Method finisher;
  protected Class<?> forClass;

  protected DependencyNode next;

  public AbstractDependencyNode(FieldsList fields, Method finisher, Class<?> forClass) {
    this.fields = fields;
    this.finisher = finisher;
    this.forClass = forClass;
  }

  protected abstract Object makeDependency(Class<?> c, boolean instantiate) throws FactoryException;
  
  @Override
  public void append(DependencyNode node) {
    if (this.next == null)
      this.next = node;
    else
      this.next.append(node);
  }

  @Override
  public DependencyNode next() {
    return next;
  }
  
  @Override
  public DependencyNode fold() {
    return empty() ? next : this;
  }

  @Override
  public boolean empty() {
    return (finisher == null) && (fields.size() == 0);
  }

  @Override
  public void resolve(Object instance) throws FactoryException {
    try {

      if (!fields.isEmpty())
        makeDependencies(instance);

      if (finisher != null)
        invokeFinisher(instance);

    } catch (Exception e) {
      throw FactoryException.unwrap(e, () -> {
        return new FailedFactoryProductException(forClass, e);
      });
    }
  }

  void makeDependencies(Object instance) throws Exception {
    for (Field field : fields) {
      DependencyInjection di = field.getAnnotation(DependencyInjection.class);

      Object param = makeDependency(field.getType(), di.instantiate());

      field.setAccessible(true);
      field.set(instance, param);
    }
  }

  void invokeFinisher(Object instance) throws Exception {
    finisher.setAccessible(true);
    finisher.invoke(instance);
  }

  @Override
  public Iterator<DependencyNode> iterator() {
    return new DependencyIterator();
  }

  private class DependencyIterator implements Iterator<DependencyNode> {

    DependencyNode node = AbstractDependencyNode.this;

    @Override
    public boolean hasNext() {
      return node != null;
    }

    @Override
    public DependencyNode next() {
      if (node == null)
        throw new NoSuchElementException();

      DependencyNode toReturn = node;
      node = node.next();
      return toReturn;
    }

  }

}


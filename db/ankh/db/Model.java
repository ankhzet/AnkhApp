package ankh.db;

import ankh.ioc.annotations.DependencyInjection;
import ankh.db.query.ObjectsMap;
import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <KeyType>
 */
public abstract class Model<KeyType> {

  @DependencyInjection()
  protected ModelFields modelFields;

  public abstract KeyType id();

  public abstract Table<KeyType> table();

  protected boolean filter() {
    return true;
  }

  public KeyType save() {
    try {
      List<Field> fields = modelFields.of(getClass(), null);

      HashMap<String, Object> map = new HashMap<>();
      for (Field field : fields)
        map.put(modelFields.fieldName(field), field.get(this));

      return table().tableBuilder()
        .insertOrUpdate(table().idColumn(), new ObjectsMap(map));

    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean delete(KeyType id) {
    return table().tableBuilder().delete(table().idColumn(), id) > 0;
  }

  private static final HashMap<Class, ModelBuilder<?, ?>> mb = new HashMap<>();

  protected static synchronized <T, M extends Model<T>> ModelBuilder<M, T> modelBuilder(Class<M> clazz) {
    ModelBuilder<M, T> b = (ModelBuilder<M, T>) mb.get(clazz);
    if (b == null)
      try {
        mb.put(clazz, b = IoC.resolve(ModelBuilder.class, clazz));
      } catch (FactoryException ex) {
        throw new RuntimeException(ex);
      }

    return b;
  }

  public static <T, M extends Model<T>> ModelBuilder<M, T> find(Class<M> clazz, T id) {
    return modelBuilder(clazz).find(id);
  }

  protected static <T, M extends Model<T>> ModelBuilder<M, T> all(Class<M> clazz) {
    return modelBuilder(clazz).all();
  }

  protected static <T, M extends Model<T>> boolean all(Class<M> clazz, int chunk, ChunkedConsumer<M> consumer) {
    ModelBuilder<M, T> builder = all(clazz);

    List<M> list = null;
    for (M model : builder) {
      if (list == null)
        list = new ArrayList<>(chunk);
      
      list.add(model);
      
      if (list.size() >= chunk) {
        if (!consumer.accept(list))
          return false;
       
        list = null;
      }
    }
    
    if (list != null)
      return consumer.accept(list);
    
    return true;
  }

  public interface ChunkedConsumer<M> {

    boolean accept(List<M> chunk);

  }

}

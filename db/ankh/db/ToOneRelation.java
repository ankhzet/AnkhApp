package ankh.db;

import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.lang.reflect.Field;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 * @param <Key>
 */
public class ToOneRelation<Type extends Model<Key>, Key> {

  private interface IdGetter<Key> {

    Key get(Object value);

  }

  Model model;
  Type reference;
  Class<? extends Type> cast;
  Field field;
  IdGetter<Key> idGetter;

  public ToOneRelation(Model model, Class<? extends Type> cast, String field) {
    this.model = model;
    this.cast = cast;
    try {
      this.field = model.getClass().getField(field);
      Class idType = this.field.getType();
      if (Number.class.isAssignableFrom(idType) || long.class.isAssignableFrom(idType) || int.class.isAssignableFrom(idType))
        idGetter = (identifier) -> (Key) (Long) ((Number) identifier).longValue();
      else if (String.class.isAssignableFrom(idType))
        idGetter = (identifier) -> (Key) Long.valueOf((String) identifier);
      else
        throw new RuntimeException(String.format("Unsupported id field type %s", idType.getSimpleName()));
    } catch (NoSuchFieldException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Type get() {
    if (reference == null)
      try {
        Object identifier = field.get(model);
        if (identifier instanceof Model)
          reference = (Type) identifier;
        else {
          Key id = idGetter.get(identifier);

          reference = (Type) IoC.resolve(ModelBuilder.class, cast).find(id).get();
        }
      } catch (FactoryException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }

    return reference;
  }

  public void set(Type target) {
    try {
      field.set(model, target.id());
      reference = null;
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Key id() {
    try {

      Object identifier = field.get(model);
      if (identifier instanceof Model)
        return ((Type) identifier).id();
      else
        return idGetter.get(identifier);

    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }

  }

}

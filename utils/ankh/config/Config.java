package ankh.config;

import java.util.HashMap;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Config extends ConfigNode {

  public Config() {
  }

  public String getString(String key) {
    Object value = get(key, null);

    if (value == null)
      return "";

    return value instanceof String ? (String) value : value.toString();
  }

  @Override
  public void set(String key, Object value) {
    super.set(key, value);
    FireableProperty p = (FireableProperty) properties.get(key);
    if (p != null)
      p.fireValueChangedEvent();
  }

  HashMap<String, FireableProperty> properties = new HashMap<>();

  public ConvertableProperty property(String key, String... def) {
    FireableProperty p = properties.get(key);
    if (p == null)
      properties.put(key, p = new FireableProperty(key, def));

    return p;
  }

  protected interface Converter<Type> {

    Type convert(String value);

  }

  public class ConvertableProperty extends SimpleStringProperty {

    String initial;

    public ConvertableProperty(String name, String... initialValue) {
      super(Config.this, name, initialValue.length > 0 ? initialValue[0] : null);
      initial = initialValue.length > 0 ? initialValue[0] : null;
    }

    protected <T> T as(Converter<T> converter, T... def) {
      String coreValue = get();
      T value;
      if (coreValue == null || coreValue.isEmpty()) {
        value = def.length > 0 ? def[0] : null;

        if (value != null)
          set(String.valueOf(value));

        return value;
      } else
        return converter.convert(coreValue);
    }

    public Long asLong(Long... def) {
      return as(value -> value.isEmpty() ? 0 : Long.valueOf(value), def);
    }

    public Boolean asBoolean(Boolean... def) {
      return as(value -> value.isEmpty() ? false : Boolean.valueOf(value), def);
    }

    @Override
    public String get() {
      return Config.this.getString(getName());
    }

    @Override
    public void set(String newValue) {
      Config.this.set(getName(), newValue);
    }

  }

  class FireableProperty extends ConvertableProperty {

    public FireableProperty(String name, String... initialValue) {
      super(name, initialValue);
    }

    public void fireValueChangedEvent() {
      super.fireValueChangedEvent();
    }

  }

}

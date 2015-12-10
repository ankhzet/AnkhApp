package ankh.db;

import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ModelFields extends HashMap<Class<?>, List<Field>> {

  synchronized List<Field> of(Class<? extends Model> clazz) {
    return of(clazz, null);
  }

  synchronized List<Field> of(Class<? extends Model> clazz, ResultSet rs) {
    List<Field> fields = get(clazz);

    if (fields == null) {
      if (rs == null) {
        Model model;
        try {
          model = (Model) IoC.resolve(clazz);
        } catch (FactoryException ex) {
          ex.printStackTrace();
          return null;
        }

        Table table = model.table();
        rs = table.tableBuilder()
          .where(table.idColumn(), 0)
          .limit(1)
          .get();
      }

      fields = fields(rs, clazz);

      put(clazz, fields);
    }

    return fields;
  }

  public List<Field> fields(ResultSet rs, Class<?> clazz) {
    ArrayList<Field> fields = new ArrayList<>();

    try {
      Field[] fielda = clazz.getFields();

      ResultSetMetaData m = rs.getMetaData();
      for (int i = 1; i <= m.getColumnCount(); i++) {
        String column = m.getColumnName(i);

        boolean found = false;
        for (Field field : fielda)
          if (found = isField(field, column)) {
            fields.add(field);
            break;
          }

        if (!found)
          throw new RuntimeException(String.format("Field %s not fount in model %s", column, clazz.getName()));
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return fields;
  }

  public boolean isField(Field field, String name) {
    return fieldName(field).equals(name);
  }

  public String fieldName(Field field) {
    return field.getName();
  }

  public void apply(Object o, List<Field> fields, ResultSet rs) throws SQLException, IllegalAccessException {
    for (Field field : fields)
      fieldSet(o, field, rs);
  }

  public void apply(Model o, ResultSet rs) throws SQLException, IllegalAccessException {
    for (Field field : of(o.getClass(), rs))
      fieldSet(o, field, rs);
  }

  public Object fieldSet(Object o, Field field, ResultSet rs) throws SQLException, IllegalAccessException {
    String column = fieldName(field);
    Class<?> clazz = field.getType();
    Object value;
    if (clazz.isAssignableFrom(Date.class))
      value = rs.getDate(column);
    else
      if (clazz.isAssignableFrom(boolean.class))
        value = rs.getBoolean(column);
      else
        value = rs.getObject(fieldName(field));

    field.set(o, value);
    return value;
  }

}

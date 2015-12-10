package ankh.db;

import ankh.db.query.Builder;
import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <M>
 * @param <T>
 */
public class ModelBuilder<M extends Model<T>, T> implements Iterable<M> {

  @DependencyInjection()
  protected ModelFields modelFields;

  Class<M> clazz;
  Builder<T> query;
  Table<T> table;
  String[] columns;

  public ModelBuilder(Class<M> clazz) {
    this.clazz = clazz;
    table = resolveModel().table();
  }

  private M resolveModel() {
    try {
      return IoC.resolve(clazz);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  private Builder<T> newQuery() {
    columns = null;
    return query = table.tableBuilder();
  }

  private Builder<T> query() {
    return (query == null) ? newQuery() : query;
  }

  public ModelBuilder<M, T> columns(String... columns) {
    this.columns = columns;
    return this;
  }

  public List<M> get(String... columns) {
    ArrayList<M> list = new ArrayList<>();
    for (M model : columns(columns))
      list.add(model);
    return list;
  }

  public M first(String... columns) {
    for (M model : columns(columns))
      return model;
    return null;
  }

  private ResultSet getResultSet() {
    ResultSet set = query().get(columns);
    query = null;
    return set;
  }

  public ModelBuilder<M, T> find(T id) {
    query().where(table.idColumn(), id).limit(1);
    return this;
  }

  public ModelBuilder<M, T> all() {
    newQuery();
    return this;
  }

  public ModelBuilder<M, T> orderBy(String column, boolean desc) {
    query().orderBy(column, desc);
    return this;
  }

  public ModelBuilder<M, T> orderBy(String column) {
    query().orderBy(column);
    return this;
  }

  public ModelBuilder<M, T> where(String column, Object value) {
    query().where(column, value);
    return this;
  }

  public ModelBuilder<M, T> where(String column, String operator, Object value) {
    query().where(column, operator, value);
    return this;
  }

  public ModelBuilder<M, T> whereIn(String column, Object[] values) {
    query().whereIn(column, values);
    return this;
  }

  synchronized List<Field> of(Class<M> clazz) {
    return modelFields.of(clazz);
  }

  private M aweken;

  private M wakeup(ResultSet set, List<Field> fields) throws SQLException, IllegalAccessException {
    if (aweken == null)
      aweken = resolveModel();

    M model = aweken;

    modelFields.apply(model, fields, set);

    if (!model.filter())
      return null;

    aweken = null;
    return model;
  }

  @Override
  public Iterator<M> iterator() {
    return new ModelIterator();
  }

  class ModelIterator implements Iterator<M> {

    List<M> collection;
    int cursor;

    public ModelIterator() {
      ResultSet rs = getResultSet();

      if (rs != null)
        try (ResultSet set = rs) {
          List<Field> fields = modelFields.of(clazz, set);

          while (set.next()) {
            M model = wakeup(set, fields);

            if (model == null)
              continue;

            if (collection == null) {
              collection = new ArrayList<>();
              cursor = 0;
            }

            collection.add(model);
          }

        } catch (SQLException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasNext() {
      return (collection != null) && cursor < collection.size();
    }

    @Override
    public M next() {
      if (!hasNext())
        throw new NoSuchElementException();

      return collection.get(cursor++);
    }

  }

}

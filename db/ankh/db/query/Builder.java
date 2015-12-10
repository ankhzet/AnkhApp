package ankh.db.query;

import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import ankh.utils.Strings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <KeyType>
 */
public class Builder<KeyType> {

  @DependencyInjection
  Connection connection;

  @DependencyInjection
  SQLGrammar grammar;

  ArrayList<Where> wheres = new ArrayList<>();
  ArrayList<Order> orders = new ArrayList<>();
  Strings groups = new Strings();

  Strings columns;

  Boolean distinct = false;
  Buildable from;
  Integer limit = 0;
  Integer offset = 0;

  ArrayList<Object> bindings = new ArrayList<>();

  public Builder() {
  }

  public Builder(Buildable from) {
    this.from = from;
  }

  public Builder<KeyType> table(Buildable table) {
    try {
      return IoC.resolve(getClass(), table);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  <T> T wrapCall(BuilderRunner<T> c) {
    try {
      return c.execute(this);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public <T> T value(BuilderRunner<T> b, T def) {
    T value = wrapCall(b);
    return (value != null) ? value : def;
  }

  public void from(Buildable table) {
    from = table;
  }

  public void create(String schema) throws SQLException {
    try (PreparedStatement s = prepareSql(grammar.compileCreate(this, schema))) {
      s.executeUpdate();
    }
  }

  public void truncate() throws SQLException {
    try (PreparedStatement s = prepareSql(grammar.compileTruncate(this))) {
      s.executeUpdate();
    }
  }

  public void drop() throws SQLException {
    try (PreparedStatement s = prepareSql(grammar.compileDrop(this))) {
      s.executeUpdate();
    }
  }

  public int insert(ObjectsMap record) {
    bindings.addAll(record.values());
    return wrapCall((b) -> {
      try (PreparedStatement s = prepareSql(grammar.compileInsert(this, record))) {
        return s.executeUpdate();
      }
    });
  }

  public KeyType insertID(Buildable table) {
    return table(table).value(b -> {
      String column = table.idColumn();
      Object value = b.orderBy(column, true).value(column);
      if (value instanceof Integer)
        value = new Long((Integer) value);

      return (KeyType) value;
    }, (KeyType) null);
  }

  public int update(ObjectsMap values) {
    bindings.addAll(0, values.values());
    return wrapCall((b) -> {
      try (PreparedStatement s = prepareSql(grammar.compileUpdate(this, values))) {
        return s.executeUpdate();
      }
    });
  }

  public KeyType insertOrUpdate(String column, ObjectsMap values) {
    Object inDB = where(column, values.get(column))
      .value(column);

    KeyType id = (KeyType) values.get(from.idColumn());
    if (inDB != null) {
      values = new ObjectsMap(values);
      values.remove(column);
      if (update(values) > 0)
        return id;
    } else
      if (table(from).insert(values) > 0)
        return (id != null) ? id : insertID(from);

    return null;
  }

  public ResultSet get(String... columns) {
    if (columns.length > 0)
      addSelect(columns);
    else
      addSelect("*");

    return wrapCall((b) -> {
      return prepareSql(grammar.compileSelect(this)).executeQuery();
    });
  }

  public int delete() {
    return wrapCall((b) -> {
      try (PreparedStatement s = prepareSql(grammar.compileDelete(this))) {
        return s.executeUpdate();
      }
    });
  }

  public int delete(KeyType id) {
    return delete(from.idColumn(), id);
  }

  public int delete(String idColumn, KeyType id) {
    return where(idColumn, id).delete();
  }

  public Object value(String column) {
    return wrapCall((b) -> {
      ResultSet r = first(column);
      if (r != null) {
        Object o = r.getObject(column);
        r.close();
        return o;
      }
      return null;
    });
  }

  public ResultSet first(String... columns) {
    return wrapCall((b) -> {
      ResultSet r = limit(1).get(columns);
      return (r != null && r.next()) ? r : null;
    });
  }

  public Builder<KeyType> select(String... withColumns) {
    columns = new Strings(withColumns);
    return this;
  }

  public Builder<KeyType> addSelect(String... withColumns) {
    if (columns == null)
      columns = new Strings(withColumns);
    else {
      columns.remove("*");
      columns.addAll(new Strings(withColumns));
    }
    return this;
  }

  public Builder<KeyType> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public Builder<KeyType> offset(int offset) {
    this.offset = offset;
    return this;
  }

  public Builder<KeyType> distinct() {
    this.distinct = true;
    return this;
  }

  public Builder<KeyType> groupBy(String column) {
    groups.add(column);
    return this;
  }

  public Builder<KeyType> orderBy(String column) {
    return orderBy(column, false);
  }

  public Builder<KeyType> orderBy(String column, boolean desc) {
    orders.add(new Order(column, desc));
    return this;
  }

  public Builder<KeyType> where(String column, String operator, Object value) {
    return where(Where.JOIN_AND, column, operator, value);
  }

  public Builder<KeyType> whereIn(String column, Object[] values) {
    Strings strings;

    if (values instanceof String[])
      strings = new Strings((String[]) values);
    else {
      strings = new Strings();
      for (Object value : values) {
        String str = (value instanceof String) ? (String) value : value.toString();
        strings.add("\"" + str.replace("\"", "\\\"") +  "\"");
      }
    }

    Where w = new Where(Where.JOIN_AND, String.format("\"%s\" in (%s)", column, strings.join(", ")));
    wheres.add(w);

    return this;
  }

  public Builder<KeyType> orWhere(String column, String operator, Object value) {
    return where(Where.JOIN_OR, column, operator, value);
  }

  public Builder<KeyType> where(String column, Object value) {
    return where(Where.JOIN_AND, column, value);
  }

  public Builder<KeyType> orWhere(String column, Object value) {
    return where(Where.JOIN_OR, column, value);
  }

  public Builder<KeyType> where(int join, String column, Object value) {
    return where(join, column, "=", value);
  }

  public Builder<KeyType> where(int join, String column, String operator, Object value) {
    String placeholder = "?";
    if (value == null)
      if (Strings.explode("=,!=,<>,==", ",").contains(operator)) {
        operator = "is";
        placeholder = (Strings.explode("!=,<>", ",").contains(operator) ? "not " : "") + "null";
      }

    Where w = new Where(join, String.format("\"%s\" %s %s", column, operator, placeholder));

    wheres.add(w);

    if (placeholder.equals("?"))
      bindings.add(value);

    return this;
  }

  PreparedStatement applyBindings(PreparedStatement statement) throws SQLException {
    int i = 1;
    for (Object o : bindings)
      if (o == null)
        statement.setNull(i++, Types.JAVA_OBJECT);
      else
        if (o instanceof String)
          statement.setString(i++, (String) o);
        else
          if (o instanceof Integer)
            statement.setInt(i++, (Integer) o);
          else
            if (o instanceof Long)
              statement.setLong(i++, (Long) o);
            else
              if (o instanceof Double)
                statement.setDouble(i++, (Double) o);
              else
                if (o instanceof Float)
                  statement.setFloat(i++, (Float) o);
                else
                  if (o instanceof Boolean)
                    statement.setBoolean(i++, (Boolean) o);
                  else
                    statement.setObject(i++, o);

    return statement;
  }

  PreparedStatement prepareSql(String sql) throws SQLException {
    return applyBindings(statement(beforeSQL(sql)));
  }

  public PreparedStatement statement(String sql) throws SQLException {
    return connection.prepareStatement(sql);
  }

  public String beforeSQL(String sql) {
    return sql;
  }

  public String subtitutedSQL(String sql) {
    int idx = 0;

    String result = sql;
    while (result.contains("?")) {
      Object o = bindings.get(idx++);
      if (o == null)
        o = "null";
      else
        if (o instanceof String)
          o = "\"" + o + "\"";
        else
          o = o.toString();

      result = result.replaceFirst("\\?", (String) o);
    }

    return result;
  }

}

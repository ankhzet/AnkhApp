package ankh.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Utils {

  public static void open(String command, Object... args) throws Exception {
    Runtime.getRuntime().exec(String.format(command, args));
  }

  public interface PassThrough<Type> {

    Type pass(Type what);

  }

  public static <Type> Type pass(Type what, PassThrough<Type> callable) {
    return callable.pass(what);
  }

  public static int pass(int what, PassThrough<Integer> callable) {
    return callable.pass(what);
  }

  public static boolean pass(boolean what, PassThrough<Boolean> callable) {
    return callable.pass(what);
  }

  public interface ThrowsRunner<V> {

    V run() throws Throwable;

  }

  public interface VoidThrowsRunner {

    void run() throws Throwable;

  }

  public interface ThrowableConsumer {

    public void accept(Throwable t);

  }

  public static boolean safely(VoidThrowsRunner r) {
    return safely(r, null);
  }

  public static boolean safely(VoidThrowsRunner r, ThrowableConsumer onError) {
    Boolean result = safely(() -> {
      r.run();
      return true;
    }, onError);
    return result != null ? result : false;
  }

  public static <V> V safely(ThrowsRunner<V> r) {
    return safely(r, null);
  }

  public static <V> V safely(ThrowsRunner<V> r, ThrowableConsumer onError) {
    try {
      return r.run();
    } catch (Throwable e) {
      e.printStackTrace();

      if (onError != null)
        onError.accept(e);

      return null;
    }
  }

  public static <Type> Type anyOf(Object[] array, Class<? extends Type> clazz) {
    for (Object arg : array)
      if (arg != null && clazz.isAssignableFrom(arg.getClass()))
        return (Type) arg;

    return null;
  }

  public static <Type> Type isAny(Object[] array) {
    return isAny(array, null);
  }

  public static <Type> Type isAny(Object[] array, Supplier<Type> def) {
    return isAnyAt(0, array, def);
  }

  public static <Type> Type isAnyAt(int at, Object[] array, Supplier<Type> def) {
    return (array.length > at) ? (Type) array[at] : (def != null ? def.get() : null);
  }

  public static int constraint(int value, int min, int max) {
    if (value > max)
      value = max;
    if (value < min)
      value = min;

    return value;
  }

  public static double constraint(double value, double min, double max) {
    if (value > max)
      value = max;
    if (value < min)
      value = min;

    return value;
  }

  public static <T> T[] shift(T[] array) {
    if (array.length == 0)
      return null;

    List<T> list = new ArrayList<>(Arrays.asList(array));
    T o = list.remove(0);
    try {
      return list.toArray((T[]) array.getClass().newInstance());
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> T[] unshift(T o, T[] array) {
    return add(o, array, 0);
  }

  public static <T> T[] add(T o, T[] array, int idx) {
    Objects.requireNonNull(array);
    return add(o, array, idx, (Class<T[]>) array.getClass());
  }

  public static <T> T[] add(T o, T[] array) {
    return add(o, array, -1);
  }

  public static <T> T[] add(T o, T[] array, int idx, Class<T[]> clazz) {
    ArrayList<T> list = (array == null) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(array));
    if (idx >= 0)
      list.add(idx, o);
    else
      list.add(o);

    return Arrays.copyOf(list.toArray(), list.size(), clazz);

  }

}

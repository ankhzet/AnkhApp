package ankh.ioc.resolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FieldsList extends ArrayList<Field> {

  public FieldsList(Collection<? extends Field> c) {
    super(c);
  }

  public FieldsList() {
  }

}

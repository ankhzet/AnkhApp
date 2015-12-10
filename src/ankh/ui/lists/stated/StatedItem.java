package ankh.ui.lists.stated;

import java.util.HashMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Item>
 */
public class StatedItem<Item> {

  Item item;

  public StatedItem() {
  }

  public StatedItem(Item item) {
    this.item = item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }

  private HashMap<String, ObjectProperty<Boolean>> stateProperties;

  public ObjectProperty<Boolean> stateProperty(String state) {
    if (stateProperties == null)
      stateProperties = new HashMap<>();

    ObjectProperty<Boolean> property = stateProperties.get(state);
    if (property == null)
      stateProperties.put(state, property = new SimpleObjectProperty<>(item, state, Boolean.FALSE));

    return property;
  }

  public boolean hasState(String state) {
    if (stateProperties == null)
      return false;

    ObjectProperty<Boolean> property = stateProperties.get(state);
    return (property == null) ? false : property.get();
  }

  public void setState(String state, boolean set) {
    stateProperty(state).set(set);
  }

}

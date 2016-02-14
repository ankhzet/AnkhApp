package ankh.ui.lists.stated;

import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Item>
 * @param <Stated>
 */
public abstract class StatedItemsList<Item, Stated extends StatedItem<? extends Item>> extends ObservableListBase<Stated> {

  ObservableList<Stated> backingArray;

  public StatedItemsList() {
    backingArray = FXCollections.observableArrayList();
  }

  public StatedItemsList(ObservableList<? extends Item> c) {
    this();
    addAllItems(c);
    c.addListener((ListChangeListener<Item>) change -> synk(change.getList()));
  }

  private void synk(ObservableList<? extends Item> l) {
    ArrayList<Item> n = new ArrayList<>(l);
    ArrayList<Stated> d = new ArrayList<>();
    for (Stated stated : backingArray) {
      Item i = stated.getItem();
      if (!l.contains(i))
        d.add(stated);
      else
        n.remove(i);
    }

    beginChange();
    try {
      if (d.size() > 0)
        removeAll(d);

      if (n.size() > 0)
        for (Item item : n)
          add(l.indexOf(item), newStated(item));
    } finally {
      endChange();
    }
  }

  public final void addAllItems(Collection<? extends Item> c) {
    beginChange();
    try {
      for (Item item : c)
        add(newStated(item));
    } finally {
      endChange();
    }
  }

  @Override
  public Stated get(int index) {
    return backingArray.get(index);
  }

  @Override
  public int size() {
    return backingArray.size();
  }

  @Override
  public void add(int index, Stated element) {
    beginChange();
    try {
      backingArray.add(index, element);
      nextAdd(index, index + 1);
    } finally {
      endChange();
    }
  }

  @Override
  public Stated remove(int index) {
    beginChange();
    try {
      Stated s = backingArray.remove(index);
      nextRemove(index, s);
      return s;
    } finally {
      endChange();
    }
  }

  @Override
  public Stated set(int index, Stated element) {
    beginChange();
    try {
      Stated s = backingArray.set(index, element);
      nextSet(index, s);
      return s;
    } finally {
      endChange();
    }
  }
  
  

  public Stated stated(Item item) {
    for (Stated stated : this)
      if (stated.getItem() == item)
        return stated;
    return null;
  }

  protected abstract Stated newStated(Item item);

}

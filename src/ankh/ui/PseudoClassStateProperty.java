package ankh.ui;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.scene.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PseudoClassStateProperty extends ReadOnlyBooleanWrapper {

  private final Node node;
  private final PseudoClass pseudoclass;

  public PseudoClassStateProperty(Node node, PseudoClass pseudoclass) {
    this.node = node;
    this.pseudoclass = pseudoclass;
  }

  @Override
  protected void invalidated() {
    node.pseudoClassStateChanged(pseudoclass, get());
  }

  @Override
  public String getName() {
    return pseudoclass.getPseudoClassName();
  }

}

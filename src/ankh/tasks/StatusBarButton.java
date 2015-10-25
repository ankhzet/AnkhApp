package ankh.tasks;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class StatusBarButton extends Button {

  public StatusBarButton() {
    super();
  }

  public StatusBarButton(String text) {
    super(text);
  }

  public StatusBarButton(String text, Node graphic) {
    super(text, graphic);
  }

  public void setColor(Color color) {
    setBackground(
      new Background(
        new BackgroundFill(color, new CornerRadii(2), new Insets(4))
      )
    );
  }

}

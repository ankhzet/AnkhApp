package ankh.tasks;

import java.util.function.Consumer;
import javafx.application.Platform;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class NotificationPane extends org.controlsfx.control.NotificationPane {

  public void notify(String message, Action... actions) {
    Platform.runLater(() -> {
      getActions().setAll(actions);
      if (isShowing())
        setText(message);
      else
        show(message);
    });
  }

  public void dissmissNotifier() {
    Platform.runLater(() -> {
      hide();
    });
  }

  public void prepared(String message, Consumer<Boolean> ready) {
    Platform.runLater(() -> {
      boolean notif = isShowing();
      if (notif)
        hide();

      ready.accept(notif);
//      setText(message);
    });
  }

}

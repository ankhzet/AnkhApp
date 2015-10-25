package ankh.utils;

import ankh.Launcher;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class D {

  public static Optional<ButtonType> alert(String header, String msg) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Oops...");
    alert.setHeaderText(header);
    alert.setContentText(msg);
    return alert.showAndWait();
  }

  public static Optional<ButtonType> confirm(String header, String msg) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Oh, um...");
    alert.setHeaderText(header);
    alert.setContentText(msg);
    return alert.showAndWait();
  }

  public static void info(String header, String msg) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(header);
    alert.setContentText(msg);
    alert.showAndWait();
  }

  public static void error(String title, Throwable e) {
    Dialogs.create()
    .owner(Launcher.mainStage().stage)
    .styleClass(Dialog.STYLE_CLASS_CROSS_PLATFORM)
    .title(title)
    .showException(e);
  }

}

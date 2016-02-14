package ankh.tasks;

import ankh.AbstractApp;
import ankh.utils.D;
import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AbstractTaskManager extends StatusBar implements TaskManager {

  static final Image alert = new Image(AbstractApp.resource("alert.png"));

  private final NotificationPane notificationPane;

  private final ObservableList<Task<?>> tasks = FXCollections.observableArrayList();

  public AbstractTaskManager(NotificationPane notificationPane) {
    super();
    setText("");
    this.notificationPane = notificationPane;
  }

  @Override
  public boolean perform(Task<?> task) {
    textProperty().bind(task.messageProperty());
    progressProperty().bind(task.progressProperty());

    addButtonToStatusBar("X", (button) -> {
      button.setColor(Color.ORANGE);
      button.setOnAction((l) -> task.cancel());

      task.stateProperty().addListener((l, o, n) -> {
        String msg = task.getMessage();
        switch (n) {
        case READY:
        case RUNNING:
        case SCHEDULED:
          return;

        case SUCCEEDED:
        case CANCELLED:
          msg = "";
          break;

        case FAILED:
          error(msg, CustomTask.unwrapException(task));
        }
        unbindStatusBar(msg, button);
      });

      task.runningProperty().addListener((l, o, isRunning) -> {
        if (isRunning)
          tasks.add(task);
        else
          tasks.remove(task);
      });
    });

    new Thread(task).start();
    return true;
  }

  public boolean cancelAllTasks() {
    boolean ok = true;
     // wrapped in ArrayList to avoid "Concurrent modification" exception
    for (Task<?> task : new ArrayList<>(tasks))
      ok &= task.cancel();
    return ok;
  }

  void unbindStatusBar(String msg, Node remove) {
    textProperty().unbind();
    progressProperty().unbind();
    setText(msg);
    setProgress(0);
    removeNodeFromStatusBar(remove);
  }

  StatusBarButton addButtonToStatusBar(String text, Color bg, EventHandler<ActionEvent> handler) {
    return addButtonToStatusBar(text, (button) -> {
      button.setColor(bg);
      button.setOnAction(handler);
    });
  }

  StatusBarButton addButtonToStatusBar(String text, Consumer<StatusBarButton> setup) {
    StatusBarButton button = new StatusBarButton(text);
    if (setup != null)
      setup.accept(button);
    getLeftItems().add(button);
    return button;
  }

  StatusBarButton addButtonToStatusBar(Node node, Consumer<StatusBarButton> setup) {
    StatusBarButton button = new StatusBarButton(null, node);
    if (setup != null)
      setup.accept(button);
    getLeftItems().add(button);
    return button;
  }

  void removeNodeFromStatusBar(Node n) {
    getLeftItems().remove(n);
  }

  @Override
  public void notify(String message, Action... actions) {
    notificationPane.notify(message, actions);
  }

  @Override
  public void dissmissNotifier() {
    notificationPane.dissmissNotifier();
  }

  @Override
  public void error(String message, Throwable e) {
    notificationPane.prepared(message, (repop) -> {
      String old = getText();

      textProperty().unbind();
      progressProperty().unbind();
      setText(message);
      setProgress(0);

      addButtonToStatusBar(new ImageView(alert), (button) -> {
        button.setColor(Color.TRANSPARENT);
        button.setOnAction((h) -> {
          textProperty().unbind();
          setText(old);

          removeNodeFromStatusBar(button);
          D.error(message, e);

          if (repop)
            notificationPane.show();
        });
      });
    });
    e.printStackTrace();
  }

}

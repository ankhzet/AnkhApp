package ankh;

import ankh.config.Config;
import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.Page;
import ankh.pages.AbstractPageManager;
import ankh.tasks.NotificationPane;
import com.sun.javafx.tk.Toolkit;
import java.util.function.Supplier;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AbstractMainStage extends AbstractPageManager {

  @DependencyInjection()
  protected Config config;

  @DependencyInjection()
  protected AbstractApp application;

  public Stage stage;

  protected BorderPane clientArea;
  private NotificationPane notifPane;

  public AbstractMainStage() {
    this(((Supplier<NotificationPane>) () -> {
      NotificationPane pane = new NotificationPane();
      pane.setShowFromTop(false);
      pane.getStyleClass().remove(NotificationPane.STYLE_CLASS_DARK);

      return pane;
    }).get());
  }

  public AbstractMainStage(NotificationPane notificationPane) {
    super(notificationPane);
    notifPane = notificationPane;
  }

  public Scene constructScene() {
    BorderPane statusAndClient = new BorderPane();
    statusAndClient.setBottom(this);

    clientArea = new BorderPane();

    clientArea.setPadding(new Insets(8));
    VBox.setVgrow(clientArea, Priority.ALWAYS);
    statusAndClient.setCenter(clientArea);

    notifPane.setContent(statusAndClient);

    return new Scene(notifPane);
  }

  public Stage constructStage() {
    stage = new Stage(StageStyle.DECORATED);
    stage.setTitle(application.appTitle());
    stage.getIcons().addAll(new Image(AbstractApp.icon(32)), new Image(AbstractApp.icon(16)));
    stage.setScene(constructScene());
    return stage;
  }

  public void setRoot(Node node) {
    Toolkit.getToolkit().defer(() -> {
      clientArea.setCenter(node);
    });
  }

  public void setTop(Node node) {
    Toolkit.getToolkit().defer(() -> {
      clientArea.setTop(node);
    });
  }

  @Override
  public boolean navigateTo(Class<? extends Page> id, Object... args) {
    boolean navigated = super.navigateTo(id, args);
    if (navigated)
      setRoot(getCurrent().getNode());

    return navigated;
  }

  public void navigateOut() {
    IoC.drop();
  }

}

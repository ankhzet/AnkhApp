package ankh;

import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.Page;
import ankh.pages.AbstractPageManager;
import ankh.tasks.NotificationPane;
import ankh.config.Config;
import com.sun.javafx.tk.Toolkit;
import java.net.URL;
import java.util.function.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

  AppUILayout layout;
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
    clientArea = new BorderPane();

    clientArea.setPadding(new Insets(8));
    VBox.setVgrow(clientArea, Priority.ALWAYS);

    layout = new AppUILayout() {
      Labeled titleNode;
      Node topNode;

      @Override
      public Node topNode() {
        if (topNode == null) {
          titleNode = new Label();
          titleNode.textProperty().bind(titleProperty());

          Button close = new Button("X");
          close.setOnAction(h -> close());

          StackPane t = new StackPane(titleNode);
          topNode = new HBox(t, new HBox(close));

          HBox.setHgrow(t, Priority.ALWAYS);

          topNode.setId("header");
          titleNode.setId("title");
        }
        return topNode;
      }

      @Override
      public Node centerNode() {
        return clientArea;
      }

      @Override
      public Node bottomNode() {
        return AbstractMainStage.this;
      }

    };

    notifPane.setContent(layout);

    Scene scene = new Scene(new Underlay(notifPane));
    return scene;
  }

  public Stage constructStage() {
    stage = new Stage(StageStyle.TRANSPARENT); //UNDECORATED
    stage.titleProperty().bind(titleProperty());
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
    if (navigated) {
      Page current = getCurrent();
      setRoot(current.getNode());
      titleProperty().bind(current.titleProperty());
    }

    return navigated;
  }

  public void navigateOut() {
    IoC.drop();
  }

  private StringProperty title;

  public String getTitle() {
    return titleProperty().get();
  }

  public void setTitle(String title) {
    titleProperty().set(title);
  }

  public StringProperty titleProperty() {
    if (title == null)
      title = new SimpleStringProperty(this, "title", application.appTitle());
    return title;
  }

}

class AppUILayout extends BorderPane {

  public AppUILayout() {
    setTop(topNode());
    setCenter(centerNode());
    setBottom(bottomNode());
  }

  public Node topNode() {
    return null;
  }

  public Node centerNode() {
    return null;
  }

  public Node bottomNode() {
    return null;
  }

  @Override
  public String getUserAgentStylesheet() {
    String styles;
    URL stylesURL = getClass().getResource("style.css");
    if (stylesURL == null)
      styles = AbstractApp.resource("style.css");
    else
      styles = stylesURL.toString();

    return styles;
  }

}

class Underlay extends StackPane {

  public Underlay(Node innermost) {
    Node container = new StackPane(innermost);
    getChildren().add(container);
  }

  @Override
  public String getUserAgentStylesheet() {
    String styles;
    URL stylesURL = getClass().getResource("style.css");
    if (stylesURL == null)
      styles = AbstractApp.resource("style.css");
    else
      styles = stylesURL.toString();

    return styles;
  }

}

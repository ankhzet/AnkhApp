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
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

  Drag drag;

  public AbstractMainStage() {
    this(((Supplier<NotificationPane>) () -> {
      NotificationPane pane = new NotificationPane();
      pane.setShowFromTop(false);
      pane.setCloseButtonVisible(false);
      pane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);

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

          StackPane t = new StackPane(titleNode);
          HBox actions = new HBox(actionPane());
          actions.setAlignment(Pos.CENTER_RIGHT);
          topNode = new StackPane(t, actions);

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
    scene.setFill(Color.TRANSPARENT);
    return scene;
  }

  public void close() {
    if (stage != null)
      stage.close();
  }

  public Pane actionPane() {
    Button close = new Button("X");
    close.setOnAction(h -> close());
    close.getStyleClass().add("close");

    HBox pane = new HBox(close);
    pane.setId("actions");
    return pane;
  }

  public Stage constructStage() {
    stage = new Stage(StageStyle.TRANSPARENT); //UNDECORATED
    stage.titleProperty().bind(titleProperty());
    stage.getIcons().addAll(new Image(AbstractApp.icon(32)), new Image(AbstractApp.icon(16)));
    stage.setScene(constructScene());

    drag = new Drag(stage);
    drag.monitor(stageDragNode());
    return stage;
  }

  public Node stageDragNode() {
    return layout.topNode();
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

  private class Drag {

    Point2D start;
    Point2D origin;

    Stage stage;

    public Drag(Stage stage) {
      this.stage = stage;
    }

    private void pick(double x, double y) {
      origin = new Point2D(stage.getX(), stage.getY());
      start = new Point2D(x, y);
    }

    private void move(double x, double y) {
      stage.setX(origin.getX() + (x - start.getX()));
      stage.setY(origin.getY() + (y - start.getY()));
    }

    public void monitor(Node node) {
      node.setOnMousePressed(h -> pick(h.getScreenX(), h.getScreenY()));
      node.setOnMouseDragged(h -> move(h.getScreenX(), h.getScreenY()));
    }

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

  Node innermost;

  public Underlay(Node innermost) {
    this.innermost = innermost;

    Node container = new StackPane(innermost);

    setId("underlay");
    container.setId("container");

    getChildren().add(container);

    innermost.layoutBoundsProperty()
      .addListener((l, o, bounds) -> updateClipRect(bounds));
  }

  public double borderCorner() {
    return 16.;
  }

  private void updateClipRect(Bounds bounds) {
    Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());

    double corner = borderCorner();
    clip.setArcHeight(corner);
    clip.setArcWidth(corner);

    innermost.setClip(clip);
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

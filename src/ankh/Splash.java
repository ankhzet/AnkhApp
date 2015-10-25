package ankh;



import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Splash extends VBox {
  
  static final int DEF_WIDTH = 200;

  protected ProgressBar loadProgress;
  protected Label progressText;

  public Node splashView, outer;
  public Image iconImage;

  @Override
  public String getUserAgentStylesheet() {
    return AbstractApp.resource("splash-style.css");
  }

  public void setSplashView(Node splashView) {
    this.splashView = splashView;
  }

  public void setIconImage(Image iconImage) {
    this.iconImage = iconImage;
  }

  public void init() {
    getStyleClass().add("splash");

    Pane content = new VBox();
    content.getStyleClass().add("container");

    loadProgress = new ProgressBar();
    loadProgress.getStyleClass().add("progress");

    if (splashView != null) {
      loadProgress.setPrefWidth(splashView.getLayoutBounds().getWidth());

      Pane gap = new Pane();
      gap.setPrefHeight(10);
      content.getChildren().addAll(splashView, gap);
    } else {
      loadProgress.setPrefWidth(DEF_WIDTH - 20);
      content.setPrefWidth(DEF_WIDTH);
    }

    progressText = new Label();
    progressText.getStyleClass().add("text");

    content.getChildren().addAll(loadProgress, progressText);
    outer = new VBox(content);
    outer.getStyleClass().add("outer");

    getChildren().add(outer);
    setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 14., .1, 2., 2.));
  }

  public void show(final Stage initStage, Task<?> task, Runnable initCompletionHandler) {
    progressText.textProperty().bind(task.messageProperty());
    loadProgress.progressProperty().bind(task.progressProperty());
    task.stateProperty().addListener((observableValue, oldState, newState) -> {
      switch (newState) {
      case SUCCEEDED: {
        loadProgress.progressProperty().unbind();
        loadProgress.setProgress(1);
        initStage.toFront();
        setEffect(null);
        FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), this);
        fadeSplash.setFromValue(1.0);
        fadeSplash.setToValue(0.0);
        fadeSplash.setOnFinished(actionEvent -> initStage.hide());
        fadeSplash.play();

        initCompletionHandler.run();
        break;
      }
      case FAILED:
        Throwable e = task.getException();
        if (e == null)
          e = new Exception(String.format("Failed to initialize"));

        e.printStackTrace();
//        Dialogs.create()
//                .owner(initStage)
//                .styleClass(Dialog.STYLE_CLASS_CROSS_PLATFORM)
//                .title(e.getLocalizedMessage())
//                .showException(task.getException());

        System.exit(-1);
      }
    });

    Scene splashScene = new Scene(this);
    splashScene.setFill(Color.TRANSPARENT);

    outer.autosize();
    final Bounds sceneBounds = outer.getLayoutBounds();
    final Rectangle2D bounds = Screen.getPrimary().getBounds();
    double x = bounds.getMinX() + (bounds.getWidth() - sceneBounds.getWidth()) / 2;
    double y = bounds.getMinY() + (bounds.getHeight() - sceneBounds.getHeight()) / 2;

    initStage.initStyle(StageStyle.TRANSPARENT);
    initStage.setScene(splashScene);
    initStage.setX(x);
    initStage.setY(y);

    if (iconImage != null)
      initStage.getIcons().add(iconImage);

    initStage.show();
  }

}

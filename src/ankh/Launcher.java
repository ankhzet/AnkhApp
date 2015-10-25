package ankh;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Launcher Zet (ankhzet@gmail.com)
 */
public abstract class Launcher extends Application {

  public static final String SPLASH_IMAGE = AbstractApp.resource("splash.png");

  static AbstractMainStage mainStage;

  private Splash splash;

  public static void main(String[] args) throws Exception {
    launch(args);
  }

  @Override
  public void init() {
    splash = initSplash();
    splash.setSplashView(new ImageView(splashImage()));
    splash.setIconImage(new Image(AbstractApp.icon(32)));
    splash.init();
  }

  @Override
  public void start(final Stage initStage) throws Exception {
    final AbstractAppLoadTask loadTask = loadTask();

    splash.show(initStage, loadTask, () -> {
      mainStage = loadTask.getValue();
      mainStage.constructStage().show();
    });

    loadTask.detach();
  }

  @Override
  public void stop() throws Exception {
    mainStage.navigateOut();
    mainStage = null;
  }

  public static AbstractMainStage mainStage() {
    return mainStage;
  }

  public Splash initSplash() {
    return new Splash();
  }

  public Image splashImage() {
    return new Image(SPLASH_IMAGE);
  }

  public abstract AbstractAppLoadTask loadTask();

}

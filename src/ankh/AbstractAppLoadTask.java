package ankh;

import ankh.ioc.IoC;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Semaphore;
import javafx.beans.value.ObservableValueBase;
import javafx.concurrent.Task;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AbstractAppLoadTask extends Task<AbstractMainStage> {

  ObservableStage stage = new ObservableStage();

  public Thread detach() {
    Thread thread = new Thread(this);
    thread.start();
    return thread;
  }

  @Override
  protected AbstractMainStage call() throws InterruptedException {
    updateMessage(String.format("App at %s", AbstractApp.appContainingFolder()));

    HashMap<String, Runnable> tasks = tasks();

    for (int i = 0; i < tasks.size(); i++) {
      String key = (String) tasks.keySet().toArray()[i];
      updateMessage(key);
      tasks.get(key).run();
      updateProgress(i + 1, tasks.size());
    }

    updateMessage("Loaded.");

    return stage.getValue();
  }

  public LinkedHashMap<String, Runnable> tasks() {
    LinkedHashMap<String, Runnable> map = new LinkedHashMap<>();
    Runnable prepare = prepare();
    
    if (prepare != null)
      map.put("Preparing...", prepare);
    
    map.put("Loading main stage...", (Runnable) () -> {
      stage.setStage(IoC.get(AbstractMainStage.class));
    });
    
    return map;
  }

  public Runnable prepare() {
    return null;
//    () -> {
//      IoCFactoriesRegistrar.register();
//    };
  }

  class StageWaiter extends Semaphore {

    AbstractMainStage stage;

    public StageWaiter() {
      super(0);
    }

    public AbstractMainStage getValue() {
      return stage;
    }

    public void setStage(AbstractMainStage stage) {
      this.stage = stage;
      release();
    }

  }

  class ObservableStage extends ObservableValueBase<AbstractMainStage> {

    AbstractMainStage stage;

    void setStage(AbstractMainStage stage) {
      this.stage = stage;
    }

    @Override
    public AbstractMainStage getValue() {
      return stage;
    }

  };

}

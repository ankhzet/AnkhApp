/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.tasks;

import javafx.concurrent.Task;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface TaskManager {

  boolean perform(Task<?> task);

  void notify(String message, Action... actions);

  void dissmissNotifier();

  void error(String message, Throwable e);

}

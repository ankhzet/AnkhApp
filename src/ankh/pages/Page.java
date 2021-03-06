/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.pages;

import ankh.tasks.TaskManager;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface Page {

  String pathFragment();
  
  Node getNode();

  StringProperty titleProperty();

  void setNavigator(PageNavigator navigator);

  boolean navigateIn(Page from, Object... args);

  boolean navigateOut(Page to);

  void setTaskManager(TaskManager manager);

  boolean perform(Task<?> task);

}

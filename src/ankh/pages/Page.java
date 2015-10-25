/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.pages;

import ankh.tasks.CustomTask;
import ankh.tasks.TaskManager;
import javafx.scene.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface Page {

  Node getNode();

  String title();

  void setNavigator(PageNavigator navigator);

  boolean navigateIn(Page from, Object... args);

  boolean navigateOut(Page to);

  void setTaskManager(TaskManager manager);

  boolean perform(CustomTask task);

}

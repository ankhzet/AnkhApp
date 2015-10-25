/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.pages;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface PageNavigator {

  boolean navigateTo(Class<? extends Page> id, Object... args);

}

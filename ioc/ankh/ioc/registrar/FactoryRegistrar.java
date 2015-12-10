/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.ioc.registrar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface FactoryRegistrar<P> {

  Object getFactoryIdentifier(Object identifier);

  P getInstance(Object identifier);

  void register(Object factoryIdentifier, P factory);
  
}

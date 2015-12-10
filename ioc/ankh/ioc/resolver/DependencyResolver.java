/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ankh.ioc.resolver;

import ankh.ioc.exceptions.FactoryException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> identifier's type
 * @param <P> type of resolvable entities
 */
public interface DependencyResolver<I, P> {

  P resolve(I identifier, Object... args) throws FactoryException;

  P injectDependencies(P instance) throws FactoryException;

}

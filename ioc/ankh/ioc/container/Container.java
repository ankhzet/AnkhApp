package ankh.ioc.container;

import ankh.ioc.exceptions.FactoryException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> identifier's type
 * @param <P> type of contained entities
 */
public interface Container<I, P> {
  
  /**
   * Get entity of inferred type from container.
   * 
   * @param identifier identifier, to select preffered entity by
   * @return entity of type {@code P}
   * @throws ankh.ioc.exceptions.FactoryException if can't pick any suitable entity
   */
  P get(I identifier) throws FactoryException;

}

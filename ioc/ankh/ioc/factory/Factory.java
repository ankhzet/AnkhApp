package ankh.ioc.factory;

import ankh.ioc.builder.Builder;
import ankh.ioc.container.Container;
import ankh.ioc.exceptions.FactoryException;
import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> Identifier to select builder with
 * @param <P> Type of created objects
 */
public interface Factory<I, P> extends Container<I, P> {

  /**
   * Returns collection of all produceable object types.
   * 
   * @return collection of all produceable object types
   */
  Collection<I> canProduce();

  /**
   * Instantiates and returns object, which builder is specified by 
   * {@code identifier}, with specified optional {@code args} passed to 
   * builder.
   * 
   * Optional <tt>args</tt> parameters can be used directly by builder for it's 
   * own purpose, or/and then filtered and passed to object's constructor.
   *
   * @param identifier selector to pick builder by
   * @param args optional args to pass to builder (and which it can then pass 
   * further to constructor of object)
   * @return newly instantiated object
   * @throws FactoryException if specified object can't be instantiated, like, 
   * when <tt>identifier</tt> has no corresponding builders, or object's 
   * instantiation failed by itself.
   */
  P make(I identifier, Object... args) throws FactoryException;

  /**
   * Registers specified {@code builder} as {@identifier}-build capable.
   *
   * @param identifier identifies object types, producable by specified builder
   * @param builder builder, capable of building objects, specified by 
   *        <tt>identifier</tt>
   * @return builder, previously related to specified identifier, if any
   */
  Builder<I, P> register(I identifier, Builder<I, P> builder);

  /**
   * Registers {@identifier} as possible production of this factory.
   *
   * @param identifier identifies object types, producable by this factory
   * @return
   */
  Builder<I, P> register(I identifier);

}

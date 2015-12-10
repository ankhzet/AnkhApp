package ankh.ioc.builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> Class selector identifier
 * @param <P> Class of produced objects
 */
public interface Builder<I, P> {

  P build(I identifier, Object... args) throws Exception;

}

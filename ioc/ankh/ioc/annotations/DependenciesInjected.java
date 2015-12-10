package ankh.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(DependenciesInjecteds.class)
public @interface DependenciesInjected {

  boolean suppressInherited() default false;

  boolean beforeInherited() default false;

}

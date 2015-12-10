package ankh.ioc.exceptions;

import java.util.function.Supplier;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FactoryException extends Exception {

  Object specifier;

  public FactoryException(Object specifier) {
    this.specifier = specifier;
  }

  public FactoryException(Object specifier, Throwable cause) {
    super(cause);
    this.specifier = specifier;
  }

  @Override
  public String getMessage() {
    return messageFromSpecifier(specifier);
  }

  protected String messageFormat() {
    return "%s";
  }

  protected String messageFromSpecifier(Object specifier) {
    return String.format(messageFormat(), specifierName(specifier));
  }

  protected String specifierName(Object specifier) {
    String identifierString;
    if (specifier instanceof Class)
      identifierString = ((Class) specifier).getName();
    else
      identifierString = specifier.toString();

    return identifierString;
  }

  public static FactoryException unwrap(Exception exception, Supplier<FactoryException> wrap) {
    if (!(exception instanceof FactoryException)) {
      if (exception instanceof RuntimeException) {
        Throwable c = exception.getCause();
        if ((c != null) && (c instanceof FactoryException))
          return (FactoryException) c;
      }
      
      return wrap.get();
    }

    return (FactoryException) exception;
  }

}

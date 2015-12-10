package ankh.http.loading;

import ankh.ioc.annotations.DependencyInjection;
import ankh.http.query.DocumentResourceQuery;
import ankh.http.Request;
import ankh.http.query.ResourceQuery;
import ankh.http.Response;
import ankh.xml.fix.XMLCleaner;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class HTMLLoader {

  public interface RequestConsumer<Type> {

    void accept(Request request, Type object);

  }

  @DependencyInjection()
  protected DocumentBuilder builder;

  ResponseReader reader = new ResponseReader();

  public Document fetch(Request request, Response response) throws IOException {
    String content = reader.read(response);

    content = XMLCleaner.cleanup(content);
    content = content.trim();

    try {
      return builder.parse(new ByteArrayInputStream(content.getBytes()));
    } catch (SAXParseException ex) {
      SAXParseExceptionReporter.report(ex, content);
      request.setFailure(ex);
    } catch (SAXException ex) {
      request.setFailure(ex);
    }

    return null;
  }

  public <Resource> DocumentResourceQuery<Resource> query(Request request, ResourceQuery.ResourceSupplier<Document, Resource> supplier) {
    DocumentResourceQuery<Resource> query = query(request);
    query.setResourceSuplier(supplier);
    query.setSourceSupplier((q, response) -> {
      Document document = null;

      if (response != null)
        try {
          document = fetch(request, response);
        } catch (Exception ex) {
          request.setFailure(ex);
        }

      return document;
    });
    return query;
  }

  public <Resource> DocumentResourceQuery<Resource> query(Request request) {
    return new DocumentResourceQuery<>(request);
  }

}

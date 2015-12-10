package ankh.http.loading;

import ankh.http.query.ResourceQuery;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 * @param <Queried>
 * @param <Result>
 */
public abstract class AbstractURLFetchTask<Source, Queried, Result> extends AbstractRequestTask<Queried, Result> {

  private Source source;

  public AbstractURLFetchTask() {
  }

  public AbstractURLFetchTask(Source source) {
    this.source = source;
  }

  public Source getSource() {
    return source;
  }

  @Override
  protected ResourceQuery<Queried, Result> query() {
    return query(source);
  }

  protected String identifier(Source source) {
    return source.toString();
  }

  protected abstract ResourceQuery<Queried, Result> query(Source source);

  @Override
  protected void running() {
    String i = identifier(source);
    if (i != null)
      updateMessage(String.format(runningMessage(), i));
  }

  @Override
  protected void loaded() {
    String i = identifier(source);
    if (i != null)
      updateMessage(String.format(loadedMessage(), i));
  }

  protected String runningMessage() {
    return "Loading \"%s\"...";
  }

  protected String loadedMessage() {
    return "Parsing \"%s\"...";
  }

}

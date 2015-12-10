package ankh.http.cached;

import ankh.http.Response;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <StorageType>
 */
public interface ResponseFactory<StorageType> {

  Response create(ResponseCache cache, String key, StorageType storedAs);

}

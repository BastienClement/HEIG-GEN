package ch.heigvd.gen.interfaces;

/**
 * Interface defining the structure of our HTTP Callbacks
 *
 * @param <T> type parameter of the callback
 */
public interface ICallback<T> {

    /**
     * In case of success
     *
     * @param result callback's result
     */
    void success(T result);

    /**
     * In case of failure
     *
     * @param ex raised Exception
     */
    void failure(Exception ex);
}

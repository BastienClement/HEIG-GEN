package ch.heigvd.gen.interfaces;

/**
 * TODO
 *
 * @param <T>
 */
public interface ICallback<T> {

    /**
     * TODO
     *
     * @param result
     */
    void success(T result);

    /**
     * TODO
     *
     * @param ex
     */
    void failure(Exception ex);
}

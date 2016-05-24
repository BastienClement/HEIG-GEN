package ch.heigvd.gen.interfaces;

public interface ICallback<T> {

    void success(T result);

    void failure(Exception ex);
}

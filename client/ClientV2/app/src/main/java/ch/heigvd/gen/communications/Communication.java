package ch.heigvd.gen.communications;

import android.os.AsyncTask;

import ch.heigvd.gen.interfaces.ICallback;

/**
 * Abstract generic class to implement the bases of every type of HTTP requests used by the messaging
 * application to communicate with the server and the corresponding callbacks
 *
 * @param <T> a type parameter for the request
 */
public abstract class Communication<T> extends AsyncTask<Void, Void, T> {
    private ICallback<T> mCallback;
    private Exception mException;

    /**
     * To execute in the background thread
     *
     * @param params required ellipse
     * @return the result
     */
    @Override
    protected T doInBackground(Void... params) {
        mException = null;
        return communication();
    }

    /**
     * After executing the request
     *
     * @param ret the request
     */
    @Override
    protected void onPostExecute(T ret) {
        if (mException == null) {
            mCallback.success(ret);
        } else {
            mCallback.failure(mException);
        }
    }

    /**
     * Signature of the actual execution method wich will be implemented by each type of reqeust
     *
     * @return the request
     */
    protected abstract T communication();

    /**
     * Set the request's Callback
     *
     * @param callback the setted Callback
     */
    protected void setCallback(ICallback<T> callback) {
        mCallback = callback;
    }

    /**
     * When an exception has occured
     *
     * @param exception the Exception
     */
    protected void setException(Exception exception) {
        mException = exception;
    }
}
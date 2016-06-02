package ch.heigvd.gen.communications;

import android.os.AsyncTask;

import ch.heigvd.gen.interfaces.ICallback;

/**
 * TODO
 *
 * @param <T>
 */
public abstract class Communication<T> extends AsyncTask<Void, Void, T> {
    private ICallback<T> mCallback;
    private Exception mException;

    /**
     * TODO
     *
     * @param params
     * @return
     */
    @Override
    protected T doInBackground(Void... params) {
        mException = null;
        return communication();
    }

    /**
     * TODO
     *
     * @param ret
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
     * TODO
     *
     * @return
     */
    protected abstract T communication();

    /**
     * TODO
     *
     * @param callback
     */
    protected void setCallback(ICallback<T> callback) {
        mCallback = callback;
    }

    /**
     * TODO
     *
     * @param exception
     */
    protected void setException(Exception exception) {
        mException = exception;
    }
}
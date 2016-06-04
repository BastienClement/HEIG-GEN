package ch.heigvd.gen.communications;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.heigvd.gen.interfaces.ICallback;

/**
 * TODO
 */
public class RequestPUT extends Communication<String> {

    private String token;
    private String url;

    private final static String TAG = RequestPUT.class.getSimpleName();

    /**
     * TODO
     *
     * @param callback
     * @param token
     * @param url
     */
    public RequestPUT(ICallback<String> callback, String token, String url) {
        setCallback(callback);
        this.token = token;
        this.url = url;
        Log.i(TAG, "New request PUT on " + url + "\n Token : " + token);
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    protected String communication() {
        String body = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("X-Auth-Token", token);
            connection.setRequestProperty("connection", "close");
            int status = connection.getResponseCode();
            InputStream is;
            Log.i(TAG, "HTTP status : " + String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            body = "";
            while ((line = br.readLine()) != null) {
                body += line + "\n";
            }
            br.close();
            connection.disconnect();
            if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_NO_CONTENT) {
                setException(new Exception(body));
            }
        } catch (IOException ex) {
            setException(ex);
        }
        return body;
    }
}
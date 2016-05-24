package ch.heigvd.gen.communications;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.heigvd.gen.interfaces.ICallback;

public class RequestPOST extends Communication<String> {

    private String token;
    private String url;
    private String content;

    private final static String TAG = RequestPOST.class.getSimpleName();

    public RequestPOST(ICallback<String> callback, String token, String url, String content) {
        setCallback(callback);
        this.token = token;
        this.url = url;
        this.content = content;
    }

    @Override
    protected String communication() {
        String body = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Auth-Token", token);
            connection.setUseCaches(false);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(content);
            bw.flush();
            bw.close();

            int status = connection.getResponseCode();
            InputStream is;
            Log.i(TAG, "HTTP status : " + String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT || status == HttpURLConnection.HTTP_CREATED ) {
                is = connection.getInputStream();
            } else {
                is = connection.getInputStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String line;
            body = "";
            while ((line = br.readLine()) != null) {
                body += line + "\n";
            }
            br.close();
            if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_NO_CONTENT && status != HttpURLConnection.HTTP_CREATED) {
                setException(new Exception(body));
            }
        } catch (IOException e) {
            setException(e);
        }
        return body;
    }
}
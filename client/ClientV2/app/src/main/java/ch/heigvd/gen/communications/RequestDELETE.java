package ch.heigvd.gen.communications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.heigvd.gen.interfaces.ICallback;

public class RequestDELETE extends Communication<String> {

    private String token;
    private String url;
    private String content;

    public RequestDELETE(ICallback<String> callback, String token, String url) {
        setCallback(callback);
        this.token = token;
        this.url = url;
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
            connection.setRequestMethod("DELETE");
            // TODO: Add your header name for the token !
            connection.setRequestProperty("X-Auth-Token", token);
            connection.setRequestProperty("connection", "close");
            connection.setUseCaches(false);

            int status = connection.getResponseCode();
            InputStream is;
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String line;
            body = "";
            while ((line = br.readLine()) != null) {
                body += line + "\n";
            }
            br.close();
            if (status != HttpURLConnection.HTTP_OK) {
                setException(new Exception(body));
            }
        } catch (IOException e) {
            setException(e);
        }
        return body;
    }
}
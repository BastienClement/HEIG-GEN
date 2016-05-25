package ch.heigvd.gen.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.heigvd.gen.interfaces.ICallback;

public class RequestGET extends Communication<String> {

    private String token;
    private String url;

    public RequestGET(ICallback<String> callback, String token, String url) {
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
            connection.setDoInput(true);
            // TODO: Add your header name for the token !
            connection.setRequestProperty("X-Auth-Token", token);
            connection.setRequestProperty("connection", "close");
            int status = connection.getResponseCode();
            InputStream is;
            if (status == HttpURLConnection.HTTP_OK) {
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
            if (status != HttpURLConnection.HTTP_OK) {
                setException(new Exception(body));
            }
        } catch (IOException ex) {
            setException(ex);
        }
        return body;
    }
}
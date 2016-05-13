package com.heigvd.gen.chat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by guillaume on 13.05.16.
 */
public class JsonSender {

    protected void sendJson(final String username,final String password ){
        Thread t = new Thread() {

            public void run() {

                //Send Json Message to server
                try {
                    String url = "http://loki.cpfk.net:9999/api/register";
                    URL object = new URL(url);
                    HttpURLConnection con =  (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestMethod("POST");

                    //Create the JSon object
                    JSONObject message = new JSONObject();
                    message.put("user", username);
                    message.put("pass", password);

                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(message.toString());
                    writer.flush();

                    //Recieve Response
                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        Log.d(JsonSender.class.getSimpleName(), sb.toString());
            /* restore state */
                    } else {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        Log.d(JsonSender.class.getSimpleName(), sb.toString());
                        Log.d(JsonSender.class.getSimpleName(), con.getResponseMessage());
                    }

                }catch (JSONException e){
                    Log.d(JsonSender.class.getSimpleName(), e.getMessage());
                }catch(Exception e){ //Catch all
                    Log.d(JsonSender.class.getSimpleName(), e.getMessage());
                }

            }
        };

        t.start();

    }
}

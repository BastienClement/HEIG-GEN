package com.heigvd.gen.chat.Network;

import android.util.Log;

import com.google.gson.Gson;
import com.heigvd.gen.chat.Network.Query.Query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by guillaume on 13.05.16.
 */
public class JsonSender {

    public static void send(final Query query){
        Thread t = new Thread() {

            public void run() {

                //Send Json Message to server
                try {
                    String url = Configuration.url + query.getName();
                    URL object = new URL(url);
                    HttpURLConnection con =  (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestMethod("POST");

                    Gson gson = new Gson();
                    String json = gson.toJson(query);
                    System.out.println(json);

                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(json);
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
                }catch(Exception e){ //Catch all
                    Log.d(JsonSender.class.getSimpleName(), e.getMessage());
                }
            }
        };

        t.start();
    }
}

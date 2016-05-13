package com.example.guillaume.client_android;

import android.os.AsyncTask;

/**
 * Created by guillaume on 08.05.16.
 */
public class ConnectTask extends AsyncTask {

    private TCPClient c1;

    @Override
    protected Object doInBackground(Object[] params) {
        c1 = new TCPClient();
        c1.connect("localhost", 2525, "Guillaume");

        return null;
    }
}

package com.example.stephanie.flashback_music;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by looki on 3/9/2018.
 */

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {
    protected String doInBackground(String... urls){
        String content = "", line;

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            Log.w("BackgroundTask", "url: "+ url +" content: "+ content);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        FlashbackActivity.getResult = content;


        return content;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // update your UI here
        Toast.makeText(FlashbackActivity.mainContext, result, Toast.LENGTH_LONG).show();
    }
}

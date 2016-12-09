package com.example.urja.urjakhurana_pset6;

import android.util.Log;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HTTPRequestHelper {

    protected static synchronized String downloadFromServer(String... params) {
        // initialize string for result
        String result = "";
        // one part of the url
        String url1 = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=thqTCO7jvSwCAsQ9Z4w6M4Ga3OMISAA7&keyword=";
        // second part of url
        String url2 = "&city=amsterdam";
        // get movie name
        String title = params[0];
        // combine url to get the right search url
        String completeUrl = url1 + title + url2;
        URL url = null;

        try {
            url = new URL(completeUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection;
        if (url != null) {
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // right or wrong answer
                Integer responseCode = connection.getResponseCode();
                if (200 <= responseCode && responseCode <= 299) {
                    // read result of search query
                    BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = bf.readLine()) != null) {
                        result = result + line;
                    }
                } else {
                    Log.d("hehehe", "hohohoo");
                    BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
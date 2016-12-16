package com.example.urja.urjakhurana_pset6;

/* The HTTPRequestHelper makes a call to the API to get the request and get corresponding
 * information, according to the given artist name by the user in the main activity. */

import android.util.Log;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HTTPRequestHelper {

    // gets result of request to api
    protected static synchronized String downloadFromServer(String... params) {
        // initialize string for result
        String result = "";
        // get the url to send a request to the api
        String completeUrl = setUrl(params[0]);

        URL url = null;
        // change string of url into a real URL for the request
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

    // given the value by the user, get the proper url for the api
    private static String setUrl(String query) {
        String[] splitQuery = query.split(",");
        String artist = splitQuery[0];
        String city = splitQuery[1];

        // one part of the url
        String url1 = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=thqTCO7jvSw" +
                "CAsQ9Z4w6M4Ga3OMISAA7&keyword=";
        // second part of url
        String url2 = "&city=";
        // combine url to get the right search url
        return url1 + artist + url2 + city;
    }

}

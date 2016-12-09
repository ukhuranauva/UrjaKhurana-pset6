package com.example.urja.urjakhurana_pset6;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;


public class EventAsyncTask extends AsyncTask<String, Integer, String> {

    MainActivity activity;
    Context context;

    // constructor
    public EventAsyncTask(MainActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    protected void onPreExecute() {
        Toast.makeText(context, "Searching for movie", Toast.LENGTH_SHORT).show();
    }

    protected String doInBackground(String... params) {
        // get results of search
        return HTTPRequestHelper.downloadFromServer(params);
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Bitmap bmp;
        ArrayList<Concert> concerts = new ArrayList<>();
        Concert concert;
        // if nothing is found
        if (result.equals("{\"Response\":\"False\",\"Error\":\"Movie not found!\"}")) {
            Toast.makeText(context, "No data was found", Toast.LENGTH_SHORT).show();
            // close screen since no results
            this.activity.finish();
        } else {
            try {
                // get all the information of the movie from the json file
                JSONObject readObj = new JSONObject(result);
                JSONObject embObj = readObj.getJSONObject("_embedded");
                JSONArray eventArray = embObj.getJSONArray("events");
                for(int i = 0; i < eventArray.length(); i++) {
                    JSONObject eventObj = eventArray.getJSONObject(i);
                    String title = eventObj.getString("name");
                    String id = eventObj.getString("id");
                    String url = eventObj.getString("url");
                    JSONArray images = eventObj.getJSONArray("images");
                    JSONObject imageEvent = images.getJSONObject(0);
                    String imageUrl = imageEvent.getString("url");
                    ImageAsyncTask image = new ImageAsyncTask();
                    bmp = image.execute(imageUrl).get();
                    JSONObject datesObj = eventObj.getJSONObject("dates");
                    JSONObject dates = datesObj.getJSONObject("start");
                    String date = dates.getString("localDate");
                    String time;
                    if(dates.has("localTime")) {
                        time = dates.getString("localTime");
                    } else {
                        time = "undefined";
                    }
                    JSONArray genres = eventObj.getJSONArray("classifications");
                    JSONObject genresObj = genres.getJSONObject(0);
                    JSONObject segmentObj = genresObj.getJSONObject("segment");
                    String segment = segmentObj.getString("name");
                    JSONObject genreObj = genresObj.getJSONObject("genre");
                    String genre = genreObj.getString("name");
                    JSONObject detailsObj = eventObj.getJSONObject("_embedded");
                    JSONArray venues = detailsObj.getJSONArray("venues");
                    JSONObject venueObj = venues.getJSONObject(0);
                    String venue;
                    if(venueObj.has("name")) {
                        venue = venueObj.getString("name");
                    } else {
                        venue = "undefined";
                    }
                    JSONObject cityObj = venueObj.getJSONObject("city");
                    String city = cityObj.getString("name");
                    JSONObject countryObj = venueObj.getJSONObject("country");
                    String country = countryObj.getString("countryCode");
                    JSONArray attractions = detailsObj.getJSONArray("attractions");
                    JSONObject attractionObj = attractions.getJSONObject(0);
                    String artist = attractionObj.getString("name");
                    concert = new Concert(id, url, artist, title, city, country, segment, genre,
                            date, time, venue, bmp);
                    concerts.add(concert);
                }
            } catch (JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            this.activity.setData(concerts);
        }
    }
}

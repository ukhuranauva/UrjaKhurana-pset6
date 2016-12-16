package com.example.urja.urjakhurana_pset6;

import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Urja Khurana, 10739947
 * In this class, with the help of an AsyncTask, the wanted data from the API is retrieved and
 * turned into a list of Concert objects to showcase to the user.
 */

public class ConcertAsyncTask extends AsyncTask<String, Integer, String> {

    // initialize variables
    private MainActivity activity;
    private Context context;

    /** Constructor for the ConcertAsyncTask */
    public ConcertAsyncTask(MainActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    /** Before executing, show a toast */
    protected void onPreExecute() {
        Toast.makeText(context, "Searching for concerts", Toast.LENGTH_SHORT).show();
    }

    /** Get results of request to api */
    protected String doInBackground(String... params) {
        return HtttRequestHelper.downloadFromServer(params);
    }

    /** Parse results from API and turn them into concert object to showcase to the user */
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ArrayList<Concert> concerts = new ArrayList<>();
        Concert concert;
        JSONObject readObj;
        try {
            readObj = new JSONObject(result);
            // if there are results (the tag _embedded has all of the results in the json file)
            if (readObj.has("_embedded")) {
                // get all of the concerts (the API is pretty nesteld so there's lots of code needed)
                JSONArray concertArray = readObj.getJSONObject("_embedded").getJSONArray("events");
                // for each concert, get all the information and add it to the list of concerts
                for(int i = 0; i < concertArray.length(); i++) {
                    // get a concert and all of its basic needed information
                    JSONObject concertObj = concertArray.getJSONObject(i);
                    String title = concertObj.getString("name");
                    String id = concertObj.getString("id");
                    String url = concertObj.getString("url");

                    // get image url of artist
                    JSONArray images = concertObj.getJSONArray("images");
                    String imageUrl = images.getJSONObject(0).getString("url");

                    // get all the data regarding date and time
                    JSONObject dates = concertObj.getJSONObject("dates").getJSONObject("start");
                    String date = dates.getString("localDate");
                    String time;
                    // check if concert has a time (because some don't)
                    if(dates.has("localTime")) {
                        time = dates.getString("localTime");
                    } else {
                        time = "undefined";
                    }

                    // get genre and segment details of concert
                    JSONObject genres = concertObj.getJSONArray("classifications").getJSONObject(0);
                    String segment = genres.getJSONObject("segment").getString("name");
                    String genre = genres.getJSONObject("genre").getString("name");

                    // get extra details of the concert
                    JSONObject detailsObj = concertObj.getJSONObject("_embedded");
                    JSONObject venueObj = detailsObj.getJSONArray("venues").getJSONObject(0);
                    String venue;
                    // if venue has a name, get it or else set it equal to undefined
                    if(venueObj.has("name")) {
                        venue = venueObj.getString("name");
                    } else {
                        venue = "undefined";
                    }

                    String city = venueObj.getJSONObject("city").getString("name");
                    String country = venueObj.getJSONObject("country").getString("countryCode");
                    String artist = detailsObj.getJSONArray("attractions").getJSONObject(0)
                            .getString("name");

                    // create new Concert object and add it to the list of concerts
                    concert = new Concert(id, url, artist, title, city, country, segment, genre,
                            date, time, venue, imageUrl);
                    concerts.add(concert);
                }
                // Puts all of the concerts retrieved in a proper way for the user
                this.activity.setData(concerts);
            } else {
                // if no results given back by API, inform the user about it
                Toast.makeText(context, "No concerts were found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

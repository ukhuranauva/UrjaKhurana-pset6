package com.example.urja.urjakhurana_pset6;

import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/* In this class, with the help of an AsyncTask, the wanted data from the API is retrieved and
 * turned into a list of Concert objects to showcase to the user. */
public class EventAsyncTask extends AsyncTask<String, Integer, String> {

    private MainActivity activity;
    private Context context;

    // constructor for the EventAsyncTask
    public EventAsyncTask(MainActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    // before executing, show a toast
    protected void onPreExecute() {
        Toast.makeText(context, "Searching for concerts", Toast.LENGTH_SHORT).show();
    }

    // get results of request to api
    protected String doInBackground(String... params) {
        return HTTPRequestHelper.downloadFromServer(params);
    }

    // after retrieving results from api, turn them into concert object to showcase to the user
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
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
                // for each concert, get all the information and add it to the list of concerts
                for(int i = 0; i < eventArray.length(); i++) {
                    JSONObject eventObj = eventArray.getJSONObject(i);
                    String title = eventObj.getString("name");
                    String id = eventObj.getString("id");
                    String url = eventObj.getString("url");
                    JSONArray images = eventObj.getJSONArray("images");
                    JSONObject imageEvent = images.getJSONObject(0);
                    String imageUrl = imageEvent.getString("url");
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
                    // if venue has a name, get it or else set it equal to undefined
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
                    // create new Concert object and add it to the list of concerts
                    concert = new Concert(id, url, artist, title, city, country, segment, genre,
                            date, time, venue, imageUrl);
                    concerts.add(concert);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Puts all of the concerts retrieved in a proper way for the user
            this.activity.setData(concerts);
        }
    }
}

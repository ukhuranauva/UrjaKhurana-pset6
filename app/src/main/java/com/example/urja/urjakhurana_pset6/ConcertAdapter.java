package com.example.urja.urjakhurana_pset6;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* With this ConcertAdapter, all of the information of a concert is properly showcased in a listview
 * where the image, artist name, venue and time and date for example are shown to the user.
 */

public class ConcertAdapter extends ArrayAdapter<Concert> {

    // initialize variables
    private ArrayList<Concert> concertList;

    // Constructor for the adapter, where the layout and arraylist are given
    public ConcertAdapter(Context context, int layout, ArrayList<Concert> concertList) {
        super(context, layout, concertList);
        this.concertList = concertList;
    }

    /* Made my ConcertAdapter with the help of the following website:
     * https://devtut.wordpress.com/2011/06/09/custom-arrayadapter-for-a-listview-android/
     */
    public View getView(int position, View convertView, ViewGroup parent){


        View v = convertView;
        // if the view is null, create the view
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_layout, null);
        }

        // get the proper concert object at the specific position
        Concert concert = concertList.get(position);

        // if the concert object is not empty
        if (concert != null) {
            // get all the placeholders for the information and set it with the corresponding values
            TextView concertName = (TextView) v.findViewById(R.id.concertName);
            concertName.setText(concert.getConcertName());
            TextView artist = (TextView) v.findViewById(R.id.artist);
            artist.setText(concert.getArtist());
            TextView time = (TextView) v.findViewById(R.id.time);
            time.setText(concert.getTime());
            TextView date = (TextView) v.findViewById(R.id.date);
            date.setText(concert.getDate());
            TextView venue = (TextView) v.findViewById(R.id.venue);
            venue.setText(concert.getVenue());
            TextView city = (TextView) v.findViewById(R.id.city);
            city.setText(concert.getCity());
            TextView country = (TextView) v.findViewById(R.id.country);
            country.setText(concert.getCountry());
            TextView segment = (TextView) v.findViewById(R.id.segment);
            segment.setText(concert.getSegment());
            TextView genre = (TextView) v.findViewById(R.id.genre);
            genre.setText(concert.getGenre());
            ImageView image = (ImageView) v. findViewById(R.id.image);
            String imageUrl = concert.getImage();
            ImageAsyncTask imageConcert = new ImageAsyncTask();
            Bitmap bmp = null;
            // get image of concert by the url and display in listview
            try {
                bmp = imageConcert.execute(imageUrl).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bmp);
        }
        return v;
    }
}

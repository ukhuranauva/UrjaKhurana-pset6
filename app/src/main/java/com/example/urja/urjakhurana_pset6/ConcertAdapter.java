package com.example.urja.urjakhurana_pset6;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ConcertAdapter extends ArrayAdapter<Concert> {

    ArrayList<Concert> concertList;

    public ConcertAdapter(Context context, int layout, ArrayList<Concert> concertList) {
        super(context, layout, concertList);
        this.concertList = concertList;
    }

    //https://devtut.wordpress.com/2011/06/09/custom-arrayadapter-for-a-listview-android/
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_layout, null);
        }

        Concert concert = concertList.get(position);

        if (concert != null) {
            TextView concertName = (TextView) v.findViewById(R.id.concertName);
            concertName.setText(concert.concertName);
            TextView artist = (TextView) v.findViewById(R.id.artist);
            artist.setText(concert.artist);
            TextView time = (TextView) v.findViewById(R.id.time);
            time.setText(concert.time);
            TextView date = (TextView) v.findViewById(R.id.date);
            date.setText(concert.date);
            TextView venue = (TextView) v.findViewById(R.id.venue);
            venue.setText(concert.venue);
            TextView city = (TextView) v.findViewById(R.id.city);
            city.setText(concert.city);
            TextView country = (TextView) v.findViewById(R.id.country);
            country.setText(concert.country);
            TextView segment = (TextView) v.findViewById(R.id.segment);
            segment.setText(concert.segment);
            TextView genre = (TextView) v.findViewById(R.id.genre);
            genre.setText(concert.genre);
            ImageView image = (ImageView) v. findViewById(R.id.image);
            image.setImageBitmap(concert.image);
        }

        // the view must be returned to our activity
        return v;
    }
}

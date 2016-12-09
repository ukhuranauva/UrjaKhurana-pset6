package com.example.urja.urjakhurana_pset6;

import android.graphics.Bitmap;

public class Concert {

    String id;
    String url;
    String artist;
    String concertName;
    String city;
    String country;
    String segment;
    String genre;
    String date;
    String time;
    String venue;
    Bitmap image;

    public Concert(String id, String url, String artist, String concertName, String city, String
                   country, String segment, String genre, String date, String time, String venue, Bitmap image) {
        this.id = id;
        this.url = url;
        this.artist = artist;
        this.concertName = concertName;
        this.city = city;
        this.country = country;
        this.segment = segment;
        this.genre = genre;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.image = image;
    }
}

package com.example.urja.urjakhurana_pset6;

/* The Concert object consist of all the information that is relevant for the user regarding a
 * concert. So that is the id, url, artist, location, type of concert and date for example.
 */

public class Concert {

    // initialize all variables
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
    // url of image
    String image;

    // constructor with no arguments for when the data is fetched from Firebase
    public Concert() {
    }

    // constructor for when a new object has to be made with all of the information
    public Concert(String id, String url, String artist, String concertName, String city, String
                   country, String segment, String genre, String date, String time, String venue, String image) {
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

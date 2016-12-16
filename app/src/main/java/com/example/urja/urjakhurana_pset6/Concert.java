package com.example.urja.urjakhurana_pset6;

/*
 * Urja Khurana, 10739947
 * The Concert object consist of all the information that is relevant for the user regarding a
 * concert. So that is the id, url, artist, location, type of concert and date for example.
 */

public class Concert {

    // initialize all variables
    private String id;
    private String url;
    private String artist;
    private String concertName;
    private String city;
    private String country;
    private String segment;
    private String genre;
    private String date;
    private String time;
    private String venue;
    // url of image
    private String image;

    /** Constructor with no arguments for when the data is fetched from Firebase */
    public Concert() {
    }

    /** Constructor for when a new object has to be made with all of the information */
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

    /** All of the getters for the variables of the object */

    public String getConcertId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getArtist() {
        return artist;
    }

    public String getConcertName() {
        return concertName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public String getSegment() {
        return segment;
    }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }
}

package com.example.stephanie.flashback_music;

import android.location.Location;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Song {
    //////////// Variables ////////////
    private String songTitle;
    private String songsAlbumTitle;
    private String songsArtistName;
    private String whoPlayedSongLast;
    private String url;
    private Uri uri;

    private boolean neutral;
    private boolean favorite;
    private boolean dislike;

    public boolean completed;
    public boolean fromFlashback;

    private int points;
    String timeAndDate;
    Calendar cal;


    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<String> whoHasPlayed = new ArrayList<>();
    //////////// Variables ////////////


    //////////// Functions ////////////
    public Song (String in_name, String in_album, String in_artist, String url) {
        if(in_name == null) { this.songTitle = "Unknown Title"; }
        else { this.songTitle = in_name; }

        if(in_album == null) { this.songsAlbumTitle = "Unknown Album"; }
        else { this.songsAlbumTitle = in_album; }

        if(in_artist == null) { this.songsArtistName = "Unknown Artist"; }
        else { this.songsArtistName = in_artist; }

        this.url = url;
        this.uri = null;

        timeAndDate = null;
        whoPlayedSongLast = null;

        completed = false;
        fromFlashback = false;

        this.setNeutralTrue();
    }

    public String getSongTitle () {
        return songTitle;
    }

    public String getAlbumTitle () {
        return songsAlbumTitle;
    }

    public String getArtistName () {
        return songsArtistName;
    }

    public String getWhoPlayedSongLast() {
        return whoPlayedSongLast;
    }

    public ArrayList<String> getWhoHasPlayedSong() { return whoHasPlayed; }

    public String getURL () {
        return url;
    }

    public void setUri(Uri uri) { this.uri = uri; }

    public Uri getURI () {
        return uri;
    }

    public ArrayList<Location> getLocations () {
        return locations;
    }

    public String getMostRecentLocation(){
        Location lastPlayedLocation = locations.get(locations.size() - 1);
        return lastPlayedLocation.getProvider();
    }

    public String getTimeAndDate () {
        return timeAndDate;
    }

    public void setTimeAndDate (Calendar calender) {
        Date date = calender.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat ft2 = new SimpleDateFormat("HH:mm");
        timeAndDate = " on " + ft.format(date) + " at " + ft2.format(date);
    }

    public Calendar getCalendar () {
        return cal;
    }


    /* GETTER/SETTER for "points" */
    public int getPoints () {
        return points;
    }

    public void setPoints (int in_points) {
        this.points = in_points;
    }


    /* GETTER/SETTER for "neutral" */
    public boolean getNeutralStatus () {
        return neutral;
    }

    public void setNeutralTrue () {
        this.neutral = true;
        this.favorite = false;
        this.dislike = false;
    }


    /* GETTER/SETTER for "favorite" */
    public boolean getFavoriteStatus () {
        return favorite;
    }

    public void setFavoriteTrue () {
        this.favorite = true;
        this.dislike = false;
        this.neutral = false;
    }


    /* GETTER/SETTER for "dislike" */
    public boolean getDislikeStatus () {
        return dislike;
    }

    public void setDislikeTrue () {
        this.dislike = true;
        this.neutral = false;
        this.favorite = false;
    }


    public void update (Calendar calendar, Location location, String whoPlayedSong) {
        locations.add(location);
        whoHasPlayed.add(whoPlayedSong);

        completed = true;
        cal = calendar;
        setTimeAndDate(calendar);

        this.whoPlayedSongLast = " by " + whoPlayedSong;
    }
    //////////// Functions ////////////
}


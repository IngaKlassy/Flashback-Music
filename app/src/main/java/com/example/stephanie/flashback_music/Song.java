package com.example.stephanie.flashback_music;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Stephanie on 2/6/2018.
 */

public class Song {
    //////////// Variables ////////////
    private String songTitle;
    private String songsAlbumTitle;
    private String songsArtistName;
    private int resourceId;

    private boolean neutral;
    private boolean favorite;
    private boolean dislike;

    public boolean completed;
    public boolean fromFlashback;

    private int points;
    String timeAndDate;
    Calendar cal;


    private ArrayList<Location> locations = new ArrayList<>();
    //////////// Variables ////////////


    //////////// Functions ////////////
    public Song (String in_name, String in_album, String in_artist, int rId) {
        this.songTitle = in_name;
        this.songsAlbumTitle = in_album;
        this.songsArtistName = in_artist;
        this.resourceId = rId;
        timeAndDate = null;
        completed = false;
        fromFlashback = false;

        this.setNeutralTrue();
    }

    public String getSongTitle () {
        return songTitle;
    }

    public String getArtistName () {
        return songsArtistName;
    }

    public String getAlbumTitle () {
        return songsAlbumTitle;
    }

    public int getResId () {
        return resourceId;
    }

    public ArrayList<Location> getLocations () {
        return locations;
    }

    public String getName() {
        return songTitle;
    }



    public String getTimeAndDate () { return timeAndDate; }

    public void setTimeAndDate () {
        Date date = cal.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat ft2 = new SimpleDateFormat("HH:mm");
        timeAndDate = ft.format(date) + " at " + ft2.format(date);
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


    public void update (Calendar calendar, Location location) {
        locations.add(location);
        completed = true;
        cal = calendar;
        setTimeAndDate();
    }
    //////////// Functions ////////////
}


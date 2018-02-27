package com.example.stephanie.flashback_music;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.stephanie.flashback_music.Player.WEEK;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Song implements Serializable{
    //////////// Variables ////////////
    private String songTitle;
    private String songsAlbumTitle;
    private String songsArtistName;
    private int resourceId;

    private boolean neutral;
    private boolean favorite;
    private boolean dislike;

    public boolean completed = false;
    public boolean fromFlashback = false;

    private int timestampOfLastPlay = 0;   // HHMM
    private int datestampOfLastPlay = 0;   // MMDDYYYY
    private int points;

    private ArrayList<Location> locations = new ArrayList<>();
    private int[] weekDays;   //Seven: Sunday, Monday, etc...
    private int[] timesOfDay;  //Three: Morning, Afternoon, Night
    //////////// Variables ////////////


    //////////// Functions ////////////
    public Song (String in_name, String in_album, String in_artist, int rId) {
        this.songTitle = in_name;
        this.songsAlbumTitle = in_album;
        this.songsArtistName = in_artist;
        this.resourceId = rId;

        this.setNeutralTrue();

        //sets base values for weekdays, timesOfDay
        weekDays = new int [7];
        for(int i = 0; i < weekDays.length; i++)
        { weekDays[i] = 0; }
        timesOfDay = new int [3];
        for(int i = 0; i < timesOfDay.length; i++)
        { timesOfDay[i] = 0; }
    }

    public String getSongTitle () { return songTitle; }

    public String getArtistName () { return songsArtistName; }

    public String getAlbumTitle () { return songsAlbumTitle; }

    public int getResId () { return resourceId; }

    public int getTime () { return timestampOfLastPlay; }

    public boolean setTime (int in_time) {
        this.timestampOfLastPlay = in_time;
        return true;
    }

    public int getDate () { return datestampOfLastPlay; }

    public boolean setDate (int in_date) {
        // make sure input was 8 digits of less
        int check = in_date;
        int i;

        for(i = 0 ; i < 8; i++) {
            check = check/10;
            if (check == 0)
                break;
        }
        // invalid input
        if (i != 7)
            return false;
        else {
            this.datestampOfLastPlay = in_date;
            return true;
        }
    }

    public String getName() { return songTitle;}

    /* GETTER/SETTER for "points" */
    public int getPoints () {
        return points;
    }
    public void setPoints (int in_points) {
        this.points = in_points;
    }


    /* GETTER/SETTER for "neutral" */
    public boolean getNeutralStatus () { return neutral; }

    public void setNeutralTrue () {
        this.neutral = true;
        this.favorite = false;
        this.dislike = false;
    }


    /* GETTER/SETTER for "favorite" */
    public boolean getFavoriteStatus () { return favorite; }

    public void setFavoriteTrue () {
        this.favorite = true;
        this.dislike = false;
        this.neutral = false;
    }


    /* GETTER/SETTER for "dislike" */
    public boolean getDislikeStatus () { return dislike; }

    public void setDislikeTrue () {
        this.dislike = true;
        this.neutral = false;
        this.favorite = false;
    }


    public void update (Calendar c, Location location) {
        locations.add(location);

        //Pull weekday from Calender and change
        //appropriate slot in weekdays array to 1

        //Set datestampOfLastPlay and
        //timestampOfLastPlay from Calender
        //Also update timesOfDay array
    }
    //////////// Functions ////////////
}
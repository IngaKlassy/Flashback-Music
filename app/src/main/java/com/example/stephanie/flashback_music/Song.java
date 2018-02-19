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

    private int timestampOfLastPlay;   // HHMM
    private int datestampOfLastPlay;   // MMDDYYYY
    private int points;

    private ArrayList<Location> locations;
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

        timestampOfLastPlay = 0;
        datestampOfLastPlay = 0;

        locations = new ArrayList<>();
        weekDays = new int [7];
        for(int i = 0; i < weekDays.length; i++)
        { weekDays[i] = 0; }
        timesOfDay = new int [3];
        for(int i = 0; i < timesOfDay.length; i++)
        { timesOfDay[i] = 0; }
    }

    String getSongTitle () { return songTitle; }

    String getArtistName () { return songsArtistName; }

    String getAlbumTitle () { return songsAlbumTitle; }

    int getResId () { return resourceId; }



    int getTime () { return timestampOfLastPlay; }

    boolean setTime (int in_time) {
        this.timestampOfLastPlay = in_time;
        return true;
    }


    int getDate () { return datestampOfLastPlay; }

    boolean setDate (int in_date) {
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

    String getName() { return songTitle;}

    /* GETTER/SETTER for "points" */
    int getPoints () {
        return points;
    }
    void setPoints (int in_points) {
        this.points = in_points;
    }


    /* GETTER/SETTER for "neutral" */
    boolean getNeutralStatus () { return neutral; }

    void setNeutralTrue () {
        this.neutral = true;
        this.favorite = false;
        this.dislike = false;
    }


    /* GETTER/SETTER for "favorite" */
    boolean getFavoriteStatus () { return favorite; }

    void setFavoriteTrue () {
        this.favorite = true;
        this.dislike = false;
        this.neutral = false;
    }


    /* GETTER/SETTER for "dislike" */
    boolean getDislikeStatus () { return dislike; }

    void setDislikeTrue () {
        this.dislike = true;
        this.neutral = false;
        this.favorite = false;
    }


    void update (Calendar c, Location location) {
        locations.add(location);

        //Pull weekday from Calender and change
        //appropriate slot in weekdays array to 1

        //Set datestampOfLastPlay and
        //timestampOfLastPlay from Calender
        //Also update timesOfDay array
    }
    //////////// Functions ////////////
}




/* ALL original code from Stephanie and Mathias

/* GETTER/SETTER for "place"
int getPlace () {
    return place;
}
    void setPlace (int in_place) {
        //TODO some sort of check for whatever place will look like to ensure valid input
        this.place = in_place;
    }



    /* GETTER/SETTER for "day"
int getDay () {
        for(int i = 0; i < WEEK; i++) {
            if (song.day[i] == 1)
                return i;
        }
        return -1;
    return this.day;
}


    void setDay (int in_day) {
        for(int i = 0; i < WEEK; i++) {
            if (i == in_day)
                this.day[in_day] = 1;
            else
                this.day[i] = 0;
        }
        this.day = in_day;
    }
*/

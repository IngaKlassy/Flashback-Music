package com.example.stephanie.flashback_music;

import android.location.Location;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.stephanie.flashback_music.Player.WEEK;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Song {
    //////////// Variables ////////////
    private int time;   // HHMM
    //private int place;
    private int date;   // MMDDYYYY
    //private int day;
    private int resId;
    private String name;
    private int points;

    private boolean neutral;
    private boolean favorite;
    private boolean dislike;

    private String album;
    private String artistName;

    private ArrayList<Location> locations;
    private ArrayList<Integer> weekDays;
    private ArrayList<String> timeOfDay;
    //////////// Variables ////////////


    //////////// Functions ////////////
    public Song (String in_name, String in_album, String in_artist, int rId) {
        this.setName(in_name);
        this.setAlbum(in_album);
        this.setArtistName(in_artist);
        this.setNeutralTrue();
        this.resId = rId;
    }

    int getResId () {
        return resId;
    }

    /* GETTER/SETTER for "time" */
    int getTime () {
        return time;
    }
    boolean setTime (int in_time) {

        if (in_time >=0 && in_time < 2400) {

            this.time = in_time;
            return true;
        }

        // invalid input
        return false;
    }


    /* GETTER/SETTER for "date" */
    int getDate () {
        return date;
    }
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
            this.date = in_date;
            return true;
        }
    }


    /* GETTER/SETTER for "points" */
    int getPoints () {
        return points;
    }
    void setPoints (int in_points) {
        this.points = in_points;
    }

    /* GETTER/SETTER for "neutral" */
    boolean getNeutralStatus () {
        return neutral;
    }
    void setNeutralTrue () {
        this.neutral = true;
        this.favorite = false;
        this.dislike = false;

    }

    /* GETTER/SETTER for "favorite" */
    boolean getFavoriteStatus () {
        return favorite;
    }
    void setFavoriteTrue () {
        this.favorite = true;
        this.dislike = false;
        this.neutral = false;
    }


    /* GETTER/SETTER for "dislike" */
    boolean getDislikeStatus () {
        return dislike;
    }
    void setDislikeTrue () {
        this.dislike = true;
        this.neutral = false;
        this.favorite = false;
    }

    /* GETTER/SETTER for "name" */
    String getName () {
        return name;
    }

    void setArtistName (String in_name) {
        this.artistName = in_name;
    }


    /* GETTER/SETTER for "artist" */
    String getartistName () {
        return artistName;
    }

    void setName (String in_name) {
        this.name = in_name;
    }


    /* GETTER/SETTER for "album" */
    String getAlbum () {
        return album;
    }
    void setAlbum (String in_album) {
        this.album = in_album;
    }

    void update (Calendar c, Location l) {


    }
    //////////// Functions ////////////
}




/* ALL original code from Stephanie and Mathias

ORGINAL CONSTRUCTOR
public Song (String in_name, String in_album, String in_artist, int in_time, int in_place, int in_date, int in_day) {
        this.setName(in_name);
        this.setAlbum(in_album);
        this.setArtistName(in_artist);
        this.setTime(in_time);
        this.setPlace(in_place);
        this.setDate(in_date);
        this.setNeutralTrue();

        this.day = in_day;
        for(int i = 0; 0 < WEEK; i++) {
            this.day[i] = in_day[i];
        }



Original Setters/Getters for changed variables



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

package com.example.stephanie.flashbackmusic;

import java.util.*;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Player {
    //////////// Constants ////////////

    // DAY OF THE WEEK CORRESPONDS TO INDEX VALUE (THE INT VALUE)
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    public static final int WEEK = 7;

    //////////// Constants ////////////


    //////////// Variables ////////////
    String currentSong;
    String currentSpotInSong;
    String currentTime;    // formatted: HHMM (HourHourMinuteMinute)
    String currentLocation;
    String currentDate;    // formatted: MMDDYYY
    int [] currentDay = new int[7];  // will be size 7, all 0's except the index of the correct day is non-zero

    String previousSong;
    String previousTime;
    String previousLocation;
    String previousDate;
    int [] previousDay;

    PriorityQueue<Song> songPriorities;

    List<Album> albums;

    ArrayList<Song> [][] songDatabase;

    List<Song> flashbackQueue;

    SortedSet<String> playedSongs = new TreeSet<>();
    //////////// Variables ////////////


    //////////// Functions ////////////

    // override using comparator class for priority queue

    // updates point values for song objects and uses a priority queue to prioritize songs
    static void prioritizeSongs (int location, ) {
        // set point values for song
        // add song to priority queue
        // repeat until done with all songs from songDatabase

        // use "shuffle" to determine if song at top of queue will be played
        // if yes, add to flashbackQueue and remove
        // if no, remove to add back in after you get a yes and move on to next song
        // repeat until empty priority queue
    }

    boolean shuffle (int points) {
        // determine, based on points, the probability that this song will be played
        // true if it will play
        // false if it will not play
        return true;
    }

    //TODO this should happen upon completion of a song
    void savePrevious () {
        // copy current values into previous values
    }

    //TODO this should happen after savePrevious
    void playSong() {
        // update the current values
        // play the song
    }

    //TODO this should happen after savePrevious
    //TODO this should be able to happen at the same time as the create method
    void playFlashbackSong() {
        // update the current values
        // check if the song is in the binary tree (try to insert)
        // if insertion succeeded
            // play the song
        // if insertion failed
            // remove the song from the list
            // repeat from start of function
    }

    //TODO this should happen at the same time as playFlashbackSong/playSong
    //TODO aka it will happen upon completion of a song and the start of the next song
    void create () {
        // create a new song object with the previous values
        // call insertToDatabase
    }

    boolean insertToDatabase (String songName) {
        // add song alphabetically to songDatabase
        // "duplicate" insertions should go on the same row as one another
        return true;
    }

    //////////// Functions ////////////
}

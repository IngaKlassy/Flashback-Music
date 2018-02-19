package com.example.stephanie.flashback_music;

import android.app.Activity;
import android.location.Location;
import android.media.MediaPlayer;
import android.widget.Toast;

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
    MediaPlayer mp;

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

    static boolean inFlashback;

    PriorityQueue<Song> songPriorities;

    ArrayList<Album> albums;

    Map<Integer, Song> idsToSongs;

    ArrayList<Song> songDatabase; //UNINITIALIZED

    List<Song> flashbackQueue;  //UNINITIALIZED

    SortedSet<String> playedSongs = new TreeSet<>();

    //////////// Variables ////////////


    //////////// Functions ////////////

    public Player() {
        Comparator<Song> comp = new SongPointsComparator();
        songPriorities = new PriorityQueue<>(comp);
        albums = new ArrayList<Album>();
        idsToSongs = new LinkedHashMap<>();
        //songDatabase = new ArrayList<ArrayList<Song>>();
        songDatabase = new ArrayList<Song>();
        inFlashback = false;
    }

    public ArrayList<Album> returnAlbums() {
        return albums;
    }

    public MediaPlayer getMp(){
        return this.mp;
    }

    public PriorityQueue<Song> getSongPriorities(){
        return songPriorities;
    }

    public void add(String songTitle, String albumName, String artist, int resId)
    {
        Song currSong = new Song(songTitle, albumName, artist, resId);
        idsToSongs.put(resId, currSong);
        if(albums.size() == 0)
        {
            albums.add(new Album(albumName, artist));
            albums.get(0).addSong(currSong);
        }

        for(int i = 0; i < albums.size(); i++) {
            if(albums.get(i).getAlbumTitle().equals(albumName))
            {
                albums.get(i).addSong(currSong);
                return;
            }
        }

        albums.add(new Album(albumName, artist));
        albums.get(albums.size() - 1).addSong(currSong);


    }


    void playSong(final Activity a, final int resID) {
        final Calendar calendar = Calendar.getInstance();
        final Location location = new Location("La Jolla");

        if(mp != null)
        {
            mp.release();
        }

        mp = MediaPlayer.create(a, resID);
        mp.start();


        mp.setLooping(false);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Song song = idsToSongs.get(resID);

                if (song.fromFlashback) {
                    song.completed = true;
                }

                //songDatabase.get(0).add(song);
                songDatabase.add(song);
                idsToSongs.get(resID).update(calendar, location);
                Toast.makeText(a.getBaseContext(), "UPDATED!!", Toast.LENGTH_LONG).show();
                mediaPlayer.reset();
            }
        });
    }

    void playAlbum(Activity a, Album album)
    {
        if(mp != null)
        {
            mp.release();
        }
        ArrayList<Integer> songs = album.getSongIds();

        for (int i = 0; i < songs.size(); i++) {
            playSong(a, songs.get(i));
            //while(mp.isPlaying())
            //{}
        }



        /*
                mp = MediaPlayer.create(a, resID);
        mp.setLooping(false);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(MainActivity.this, tracks.getNextTrack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }
        }
    });
         */
    }


    public class SongPointsComparator implements Comparator<Song>{
        @Override
        public int compare(Song x, Song y){
            if (x.getPoints() < y.getPoints()){
                return -1;
            }
            if (x.getPoints() > y.getPoints()){
                return 1;
            }
            return 0;
        }
    }


    public void prioritizeSongsPlayed () {
        for(int i = 0; i < songDatabase.size(); i++){
        /*    for(int j = 0; j < songDatabase.get(i).size(); j++){
                String currName = songDatabase.get(i).get(j).getName();
                if(playedSongs.contains(currName)){
                    songDatabase.get(i).get(j).setPoints(songDatabase.get(i).get(j).getPoints() + 1);
                }*/
                //songPriorities.add(songDatabase.get(i).get(j));
            songDatabase.get(i).completed = false;
            songDatabase.get(i).fromFlashback = true;
            songPriorities.add(songDatabase.get(i));
            //}
        }
    }


    // override using comparator class for priority queue

    // updates point values for song objects and uses a priority queue to prioritize songs
    static void prioritizeSongs (String location) {
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
        previousSong = currentSong;
        previousTime = currentTime;
        previousDate = currentDate;
        previousDay = currentDay;
        previousLocation = currentLocation;
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
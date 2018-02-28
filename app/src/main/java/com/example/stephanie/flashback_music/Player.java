package com.example.stephanie.flashback_music;

import android.app.Activity;
import android.location.Location;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Player implements Serializable{

    //////////// Variables ////////////
    static boolean inRegularMode;
    static boolean inFlashbackMode;


    MediaPlayer mediaPlayer;

    String currentSong;
    String currentSpotInSong;

    String currentTime;    // formatted: HHMM (HourHourMinuteMinute)
    String currentLocation;
    String currentDate;    // formatted: MMDDYYY

    PriorityQueue<Song> songPriorities;

    ArrayList<Album> albumObjects;
    ArrayList<Song> songObjects;

    Map<Integer, Song> idsToSongs;

    ArrayList<Song> songDatabase; //UNINITIALIZED

    List<Song> flashbackQueue;  //UNINITIALIZED

    SortedSet<String> playedSongs;

    LinkedList<Integer> regularModePlaylist;

    //////////// Variables ////////////


    //////////// Functions ////////////

    public Player() {
        albumObjects = new ArrayList<>();
        songObjects = new ArrayList<>();

        Comparator<Song> comp = new SongPointsComparator();
        songPriorities = new PriorityQueue<>(comp);
        idsToSongs = new LinkedHashMap<>();
        songDatabase = new ArrayList<>();
        inFlashbackMode = false;

        playedSongs = new TreeSet<>();
        regularModePlaylist =  new LinkedList<>();
    }

    public ArrayList<Album> getListOfAlbumObs() {
        return albumObjects;
    }

    public MediaPlayer getMediaPlayer(){
        return this.mediaPlayer;
    }

    public PriorityQueue<Song> getSongPriorities(){
        return songPriorities;
    }

    public void add(String songTitle, String albumName, String artist, int resId)
    {
        Song currSong = new Song(songTitle, albumName, artist, resId);

        songObjects.add(currSong);
        idsToSongs.put(resId, currSong);

        if(albumObjects.size() == 0)
        {
            albumObjects.add(new Album(albumName, artist));
            albumObjects.get(0).addSong(currSong);
            return;
        }

        for(int i = 0; i < albumObjects.size(); i++) {
            if(albumObjects.get(i).getAlbumTitle().equals(albumName))
            {
                albumObjects.get(i).addSong(currSong);
                return;
            }
        }

        albumObjects.add(new Album(albumName, artist));
        albumObjects.get(albumObjects.size() - 1).addSong(currSong);
    }


    void playSong(final Activity activity, final int resID) {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        regularModePlaylist.add(new Integer(resID));

        play(activity);
    }


    void playAlbum(Activity activity, Album album)
    {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        ArrayList<Integer> albumTrackList = album.getSongIds();
        for(int i = 0; i < albumTrackList.size(); i++)
        {
            regularModePlaylist.add(new Integer(albumTrackList.get(i)));
        }

        play(activity);
    }


    void play(final Activity activity)
    {
        if(mediaPlayer != null)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        final int currentResourceId = regularModePlaylist.poll();
        mediaPlayer = MediaPlayer.create(activity, currentResourceId);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                /*if (song.fromFlashback) {
                    song.completed = true;
                }*/

                Song finishedSong = idsToSongs.get(new Integer(currentResourceId));
                songDatabase.add(finishedSong);

                idsToSongs.get(currentResourceId).update(Calendar.getInstance(), new Location("La Jolla"));
                Toast.makeText(activity.getBaseContext(), "UPDATED!!", Toast.LENGTH_LONG).show();

                if(!regularModePlaylist.isEmpty())
                {
                    play(activity);
                }

                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }


    public class SongPointsComparator implements Comparator<Song>, Serializable {
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
package com.example.stephanie.flashback_music;

import android.app.Activity;
import android.location.Location;
import android.media.MediaPlayer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Player {

    //////////// Variables ////////////
    static boolean inRegularMode;
    static boolean inFlashbackMode;


    private MediaPlayer mediaPlayer;

    public ArrayList<Album> albumObjects;
    private ArrayList<Song> songObjects;

    private Map<Integer, Song> idsToSongs;

    //SortedSet<String> playedSongs;
    //playedSongs = new TreeSet<>();

    private LinkedList<Integer> regularModePlaylist;
    protected PriorityQueue<Song> vibeModePlaylist;



    //////////// Functions ////////////
    public Player() {
        albumObjects = new ArrayList<>();
        songObjects = new ArrayList<>();

        Comparator<Song> comp = new SongPointsComparator();

        idsToSongs = new LinkedHashMap<>();

        inRegularMode = true;
        inFlashbackMode = false;

        regularModePlaylist =  new LinkedList<>();
        vibeModePlaylist = new PriorityQueue<>(comp);
    }

    public ArrayList<Album> getListOfAlbumObs() {
        return albumObjects;
    }


    protected void switchMode()
    {
        if(inRegularMode) {
            inRegularMode = false;
            inFlashbackMode = true;
            return;
        }

        inRegularMode = true;
        inFlashbackMode = false;
    }


    protected void add(String songTitle, String albumName, String artist, int resId)
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


    protected void playSong(Activity activity, final int resID, TextView textView) {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        regularModePlaylist.add(resID);

        regularModePlay(activity, textView);
    }


    protected void playAlbum(Activity activity, Album album, TextView textView)
    {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        regularModePlaylist.addAll(album.getSongIds());

        regularModePlay(activity, textView);
    }


    private void regularModePlay(final Activity activity, final TextView textView)
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
        Song currentPlayingSong = idsToSongs.get(currentResourceId);

        mediaPlayer = MediaPlayer.create(activity, currentResourceId);
        updateRegModeSongDataTextview(textView, currentPlayingSong);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Song finishedSong = idsToSongs.get(currentResourceId);

                finishedSong.update(Calendar.getInstance(), new Location("La Jolla"), "You");
                Toast.makeText(activity.getBaseContext(), "UPDATED!!", Toast.LENGTH_LONG).show();

                if(!regularModePlaylist.isEmpty())
                {
                    regularModePlay(activity, textView);
                }

                updateRegModeNoSongDataTextview(textView);
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }


    protected void vibeModePlay(final Activity activity, final ArrayList<TextView> textViews)
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

        if(vibeModePlaylist.isEmpty()) {
            return;
        }

        Song currentSongInPlaylist = vibeModePlaylist.poll();
        final int currentResourceId = currentSongInPlaylist.getResId();

        mediaPlayer = MediaPlayer.create(activity, currentResourceId);
        updateVibeModeSongDataTextview(textViews, currentSongInPlaylist);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Song finishedSong = idsToSongs.get(currentResourceId);
                songObjects.add(finishedSong);

                if(!vibeModePlaylist.isEmpty())
                {
                    vibeModePlay(activity, textViews);
                }

                prioritizeSongsPlayed();
                vibeModePlay(activity, textViews);
            }
        });
    }


    public void play()
    {
        if(mediaPlayer != null) {
            if(!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    public void pause()
    {
        if(mediaPlayer != null) {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public void next(Activity activity, ArrayList<TextView> textViews)
    {
        if(inRegularMode)
        {
            if(!regularModePlaylist.isEmpty())
            {
                regularModePlay(activity, textViews.get(0));
                return;
            }
            else
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

                updateRegModeNoSongDataTextview(textViews.get(0));
                return;
            }
        }
        else
        {
            if(!vibeModePlaylist.isEmpty()) {
                vibeModePlay(activity, textViews);
            }
            else
            {
                prioritizeSongsPlayed();
                vibeModePlay(activity, textViews);
            }
        }
    }


    public void stop()
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
    }


    public void updateRegModeSongDataTextview(TextView textView, Song song)
    {
        String songTitle = song.getSongTitle();
        if(songTitle == null){
            songTitle = "Unknown";
        }
        String songTitleWhole = "Song Title: " + songTitle;

        String albumTitle = song.getAlbumTitle();
        if(albumTitle == null){
            albumTitle = "Unknown";
        }
        String albumTitleWhole = "Album: " + albumTitle;

        String artistName = song.getArtistName();
        if(artistName == null){
            artistName = "Unknown";
        }
        String artistNameWhole = "Artist: " + artistName;

        String timeAndDate = song.getTimeAndDate();
        if(timeAndDate == null){
            timeAndDate = "";
        }

        String playedByWho = song.getWhoPlayedSong();
        if(playedByWho == null){
            playedByWho = "";
        }

        String lastPlayed = "Last played " + playedByWho + timeAndDate;

        if(timeAndDate == "" && playedByWho == "")
        {
            lastPlayed = "This song is Playing for the first time";
        }



        textView.setText(songTitleWhole + "\n" + albumTitleWhole + "\n" + artistNameWhole
        + "\n" + lastPlayed);
    }


    public void updateRegModeNoSongDataTextview(TextView textView)
    {
        textView.setText("");
    }


    public void updateVibeModeSongDataTextview(ArrayList<TextView> textViews, Song song)
    {
        String songTitle = song.getSongTitle();
        if(songTitle == null){
            songTitle = "Unknown";
        }
        String songTitleWhole = "Current Song: " + "\n" + songTitle;
        textViews.get(0).setText(songTitleWhole);


        String albumTitle = song.getAlbumTitle();
        if(albumTitle == null){
            albumTitle = "Unknown";
        }
        String albumTitleWhole = "Album: " + "\n" + albumTitle;
        textViews.get(1).setText(albumTitleWhole);


        String artistName = song.getArtistName();
        if(artistName == null){
            artistName = "Unknown";
        }
        String artistNameWhole = "Artist: " + "\n" + artistName;
        textViews.get(2).setText(artistNameWhole);


        String timeAndDate = song.getTimeAndDate();
        if(timeAndDate == null){
            timeAndDate = "";
        }

        String playedByWho = song.getWhoPlayedSong();
        if(playedByWho == null){
            playedByWho = "";
        }

        String lastPlayed = "Last played " + playedByWho + "\n" + timeAndDate;

        if(timeAndDate == "" && playedByWho == "")
        {
            lastPlayed = "";
        }

        textViews.get(3).setText(lastPlayed);
    }


    public class SongPointsComparator implements Comparator<Song> {
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
        for(int i = 0; i < songObjects.size(); i++){
        /*    for(int j = 0; j < songDatabase.get(i).size(); j++){
                String currName = songDatabase.get(i).get(j).getName();
                if(playedSongs.contains(currName)){
                    songDatabase.get(i).get(j).setPoints(songDatabase.get(i).get(j).getPoints() + 1);
                }*/
                //vibeModePlaylist.add(songDatabase.get(i).get(j));
            Song currentSong = songObjects.get(i);

            if(currentSong.completed) {
                vibeModePlaylist.add(currentSong);
            }
            //}
        }
    }


    public PriorityQueue<Song> getVibeModePlaylist() {
        return vibeModePlaylist;
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
        // true if it will regularModePlay
        // false if it will not regularModePlay
        return true;
    }


    //TODO this should happen after savePrevious
    //TODO this should be able to happen at the same time as the create method
    void playFlashbackSong() {
        // update the current values
        // check if the song is in the binary tree (try to insert)
        // if insertion succeeded
        // regularModePlay the song
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
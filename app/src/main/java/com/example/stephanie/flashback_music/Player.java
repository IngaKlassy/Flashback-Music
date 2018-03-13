package com.example.stephanie.flashback_music;

import android.app.Activity;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Player {

    //////////// Variables ////////////
    static boolean inRegularMode;
    static boolean inVibeMode;


    private MediaPlayer mediaPlayer;

    public ArrayList<Album> albumObjects;
    private ArrayList<Song> songObjects;

    private Map<Uri, Song> urisToSongs;
    private Map<String, Song> urlsToSongs;

    //SortedSet<String> playedSongs;
    //playedSongs = new TreeSet<>();

    private LinkedList<Uri> regularModePlaylist;
    protected PriorityQueue<Song> vibeModePlaylist;



    //////////// Functions ////////////
    public Player() {
        albumObjects = new ArrayList<>();
        songObjects = new ArrayList<>();

        Comparator<Song> comp = new SongPointsComparator();

        urisToSongs = new LinkedHashMap<>();
        urlsToSongs = new LinkedHashMap<>();

        inRegularMode = true;
        inVibeMode = false;

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
            inVibeMode = true;
            return;
        }

        inRegularMode = true;
        inVibeMode = false;
    }


    public void add(String songTitle, String albumName, String artist, String url, Uri uri)
    {
        Song newSong = new Song(songTitle, albumName, artist, url, uri);
        boolean exists = false;
        for (int i = 0; i < songObjects.size(); i++) {
            if (songObjects.get(i).getSongTitle().equals(newSong.getSongTitle())) {
                exists = true;
            }
        }

        if (!exists) {
            songObjects.add(newSong);
            urisToSongs.put(uri, newSong);
            urlsToSongs.put(url, newSong);
        }

        if(albumObjects.size() == 0)
        {
            albumObjects.add(new Album(albumName, artist));
            albumObjects.get(0).addSong(newSong);
            return;
        }

        for(int i = 0; i < albumObjects.size(); i++) {
            if(albumObjects.get(i).getAlbumTitle().equals(albumName))
            {
                albumObjects.get(i).addSong(newSong);
                return;
            }
            else if(albumName == null && albumObjects.get(i).getAlbumTitle().equals("Unknown Album")) {
                albumObjects.get(i).addSong(newSong);
                return;
            }
        }

        albumObjects.add(new Album(albumName, artist));
        albumObjects.get(albumObjects.size() - 1).addSong(newSong);
    }


    protected void playSong(Activity activity, final Uri uri, TextView textView) {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        regularModePlaylist.add(uri);

        regularModePlay(activity, textView);
    }


    protected void playAlbum(Activity activity, Album album, TextView textView)
    {
        if(!regularModePlaylist.isEmpty())
        {
            regularModePlaylist.clear();
        }

        regularModePlaylist.addAll(album.getSongURIs());

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

        final Uri currentURI = regularModePlaylist.poll();
        Song currentPlayingSong = urisToSongs.get(currentURI);

        mediaPlayer = MediaPlayer.create(activity, currentURI);
        updateRegModeSongDataTextview(textView, currentPlayingSong);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Song finishedSong = urisToSongs.get(currentURI);

                finishedSong.update(Calendar.getInstance(), new Location("La Jolla"), "You");
                Toast.makeText(activity.getBaseContext(), "UPDATED!!", Toast.LENGTH_LONG).show();

                if(!regularModePlaylist.isEmpty())
                {
                    regularModePlay(activity, textView);
                }

                updateRegModeNoSongDataTextview(textView);
                mediaPlayer.release();
                mediaPlayer = null;

                //MainActivity.myRef.setValue(finishedSong);
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
        final Uri currentURI = currentSongInPlaylist.getURI();

        mediaPlayer = MediaPlayer.create(activity, currentURI);
        updateVibeModeSongDataTextview(textViews, currentSongInPlaylist);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Song finishedSong = urisToSongs.get(currentURI);
                songObjects.add(finishedSong);

                if(!vibeModePlaylist.isEmpty())
                {
                    vibeModePlay(activity, textViews);
                }

                prioritizeSongs();
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
                prioritizeSongs();
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

        String playedByWho = song.getWhoPlayedSongLast();
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

        String playedByWho = song.getWhoPlayedSongLast();
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

    public void prioritizeSongs() {
        Song currentSong;
        Location current;

        for(int i = 0; i < songObjects.size(); i++){

            currentSong = songObjects.get(i);

            /*if(currentSong.getLocations().size() != 0) {
                currentSong.setPoints(currentSong.getPoints()
                        + setLocationPoints(currentSong.getLocations().get(0))
                        + setRecentlyPlayedPoints(currentSong.getCalendar())
                        + setFriendPlayedPoints(currentSong));

                vibeModePlaylist.add(currentSong);
            }*/

            if(currentSong.completed) {
                vibeModePlaylist.add(currentSong);
            }
        }
        System.out.println("Queue Reprioritized");
        Log.w("Reprioritizing in Player: ", "success!" );
    }


    // TODO ugh it deleted everything :((
    public int setLocationPoints(Location location){
        return 3;
    }
    public int setRecentlyPlayedPoints(Calendar cal) {
        return 2;
    }
    public int setFriendPlayedPoints(Song song) {
        return 1;
    }

    public PriorityQueue<Song> getVibeModePlaylist() {
        return vibeModePlaylist;
    }

}

package com.example.stephanie.flashback_music;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TimeZone;
import java.util.TreeMap;

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

    private LinkedList<Uri> regularModePlaylist;
    protected PriorityQueue<Song> vibeModePlaylist;

    private String currentSongName;
    private Song currentSongObject;
    ArrayList<String> friends;

    private ArrayList<Song> downloadingSongs;


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

        friends = new ArrayList<String>() {{
            add("Amanda Moffitt");
            add("Joel Loo");
            add("Muyao Wu");
            add("Inga Klassy");
            add("Mathias Smyrl");
            add("Stephanie Mitchener");
            add("Test Friend");
        }};

        downloadingSongs = new ArrayList<>();
    }

    public ArrayList<Song> getSongObjects() {
        return songObjects;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getCurrentSongName() {
        return currentSongName;
    }

    public ArrayList<Album> getListOfAlbumObs() {
        return albumObjects;
    }

    public ArrayList<Song> getQueue() { return new ArrayList<Song>(vibeModePlaylist); }


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
        Song newSong = new Song(songTitle, albumName, artist, url);
        boolean exists = false;
        for (int i = 0; i < songObjects.size(); i++) {
            if (songObjects.get(i).getSongTitle().equals(songTitle)) {
                //Toast.makeText(MainActivity.mainContext, songTitle + " already an object", Toast.LENGTH_SHORT).show();
                exists = true;
                songObjects.get(i).setUri(uri);
                if(uri != null && urisToSongs.containsKey(uri)) {
                    urisToSongs.put(uri, newSong);
                }
            }
        }

        if (!exists) {
            newSong.setUri(uri);
            songObjects.add(newSong);
            if(uri != null) {
                urisToSongs.put(uri, newSong);
            }
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

        for(int i = 0; i < songObjects.size(); i++) {
            Song match = songObjects.get(i);
            if(match.getURI() != null && (match.getURI().toString()).equals(uri.toString())) {
                if(match.getDislikeStatus()) {
                    match.setNeutralTrue();
                }
                break;
            }
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

        if(urisToSongs.get(currentURI) == null) {
            Toast.makeText(MainActivity.mainContext, "Couldnt find in map", Toast.LENGTH_SHORT).show();
        }

        currentSongName = currentPlayingSong.getSongTitle();
        currentSongObject = currentPlayingSong;

        mediaPlayer = MediaPlayer.create(activity, currentURI);
        updateRegModeSongDataTextview(textView, currentPlayingSong);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Song finishedSong = urisToSongs.get(currentURI);

                Location currentLocation = new Location(MainActivity.currentCityAndState);
                currentLocation.setLongitude(MainActivity.currentLongitude);
                currentLocation.setLatitude(MainActivity.currentLatitude);

                Calendar currCalendar;
                if(MainActivity.useMockTime) {
                    currCalendar = MainActivity.mockCalendar;
                    finishedSong.update(currCalendar, currentLocation, MainActivity.userName);
                }
                else {
                   currCalendar = Calendar.getInstance();
                   finishedSong.update(currCalendar, currentLocation, MainActivity.userName);
                }

                Toast.makeText(activity.getBaseContext(), "UPDATED!!", Toast.LENGTH_LONG).show();

                if(!regularModePlaylist.isEmpty())
                {
                    regularModePlay(activity, textView);
                }

                updateRegModeNoSongDataTextview(textView);
                mediaPlayer.release();
                mediaPlayer = null;


                String key1 = MainActivity.myRef.push().getKey();
                MainActivity.myRef.child(key1);

                Map<String, Object> test = new TreeMap<>();
                test.put("Song Name", finishedSong.getSongTitle());
                test.put("Song Album", (finishedSong.getAlbumTitle()));
                test.put("Song Artist", (finishedSong.getArtistName()));
                test.put("URL", (finishedSong.getURL()));
                test.put("Played by", (MainActivity.userName));
                test.put("City", (currentLocation.getProvider()));
                test.put("Latitude", (currentLocation.getLatitude()));
                test.put("Longitude", (currentLocation.getLongitude()));
                test.put("Month", (currCalendar.get(Calendar.MONTH)));
                test.put("Day of Month", (currCalendar.get(Calendar.DAY_OF_MONTH)));
                test.put("Hour of day", (currCalendar.get(Calendar.HOUR_OF_DAY)));
                test.put("Minute", (currCalendar.get(Calendar.MINUTE)));
                test.put("Year", (currCalendar.get(Calendar.YEAR)));
                test.put("Second", (currCalendar.get(Calendar.SECOND)));

                Map<String, Object> test2 = new TreeMap<>();
                test2.put(key1, test);

                MainActivity.myRef.updateChildren(test2);
            }
        });
    }


    public Song getCurrentSongObject(){
        return currentSongObject;
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

        if(vibeModePlaylist.isEmpty()){
            if(downloadingSongs.isEmpty()) {
                prioritizeSongs();
                vibeModePlay(activity, textViews);
            }
            else {
                for(int i = 0; i < downloadingSongs.size(); i++) {
                    Song current = downloadingSongs.get(i);
                    if(current.getURI() != null) {
                        vibeModePlaylist.add(current);
                        vibeModePlay(activity, textViews);
                    }
                }
            }
        }

        Song currentSongInPlaylist = vibeModePlaylist.poll();

        currentSongName = currentSongInPlaylist.getSongTitle();
        currentSongObject = currentSongInPlaylist;

        final Uri currentURI = currentSongInPlaylist.getURI();

        if(currentURI == null) {
            String songURL = currentSongInPlaylist.getURL();
            downloadingSongs.add(currentSongInPlaylist);
            MainActivity.downloadEngine.tryToDownload(MainActivity.mainContext, songURL);
            next(activity, textViews);
            return;
        }


        mediaPlayer = MediaPlayer.create(activity, currentURI);
        updateVibeModeSongDataTextview(textViews, currentSongInPlaylist);
        mediaPlayer.start();

        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(!vibeModePlaylist.isEmpty()) {
                    vibeModePlay(activity, textViews);
                }
                else {
                    if(downloadingSongs.isEmpty()) {
                        prioritizeSongs();
                        vibeModePlay(activity, textViews);
                    }
                    else {
                        for(int i = 0; i < downloadingSongs.size(); i++) {
                            Song current = downloadingSongs.get(i);
                            if(current.getURI() != null) {
                                vibeModePlaylist.add(current);
                                vibeModePlay(activity, textViews);
                            }
                        }
                    }
                }
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
            else {
                if(downloadingSongs.isEmpty()) {
                    prioritizeSongs();
                    vibeModePlay(activity, textViews);
                }
                else {
                    for(int i = 0; i < downloadingSongs.size(); i++) {
                        Song current = downloadingSongs.get(i);
                        if(current.getURI() != null) {
                            vibeModePlaylist.add(current);
                            vibeModePlay(activity, textViews);
                        }
                    }
                }
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

        String location = song.getMostRecentLocation();
        if(location == null){
            location = "";
        }

        String lastPlayed = "Last played " + playedByWho + timeAndDate + "\n in " + location;

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

        String location = song.getMostRecentLocation();
        if(location == null){
            location = "";
        }

        String lastPlayed = "Last played " + playedByWho + "\n" + timeAndDate + "\n in " + location;

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

        for(int i = 0; i < songObjects.size(); i++){

            currentSong = songObjects.get(i);

            if(currentSong.getLocations().size() != 0) {
                currentSong.setPoints(currentSong.getPoints()
                        + setLocationPoints(currentSong)
                        + setRecentlyPlayedPoints(currentSong)
                        + setFriendPlayedPoints(currentSong));

                vibeModePlaylist.add(currentSong);
            }
        }
        System.out.println("Queue Reprioritized");
        Log.w("Reprioritizing in Player: ", "success!" );
    }

    public int setLocationPoints(Song song){
        Location currentLocation = null;
        try {
            currentLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation == null){
                currentLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        catch(SecurityException e){
        }
        if(currentLocation != null ) {
            for (int i = 0; i < song.getLocations().size(); i++) {
                if (song.getLocations().get(i).distanceTo(currentLocation) < 100)
                    return 3;
            }
        }
        return 0;
    }


    public int setRecentlyPlayedPoints(Song song) {
        Calendar now = Calendar.getInstance();
        boolean decJan = false;

        // check year
        if((song.getCalendar().get(Calendar.YEAR) - now.get(Calendar.YEAR)) == 1) {
            // check month (only possible would be Dec. and Jan.)
            if (song.getCalendar().get(Calendar.MONTH) != Calendar.DECEMBER)
                return 0;
            else if (now.get(Calendar.MONTH) != Calendar.JANUARY)
                return 0;
            else
                decJan = true;
        }

        // check week day
        if((song.getCalendar().getWeekYear() - now.getWeekYear()) == 1 || decJan == true) {
            if (song.getCalendar().get(Calendar.DAY_OF_WEEK) >
                    now.get(Calendar.DAY_OF_WEEK))
                return 2;
        }
        else if((song.getCalendar().getWeekYear() - now.getWeekYear()) == 0)
            return 2;

        return 0;

    }

    public int setFriendPlayedPoints(Song song) {
        for (int i = 0; i < song.getWhoHasPlayedSong().size(); i++) {
            for (int j = 0; j < friends.size(); j++) {
                if (song.getWhoHasPlayedSong().get(i).equals(friends.get(j)))
                    return 1;
            }
        }
        return 0;
    }

    public PriorityQueue<Song> getVibeModePlaylist() {
        return vibeModePlaylist;
    }


}

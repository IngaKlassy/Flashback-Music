package com.example.stephanie.flashback_music;


import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/*
 * Main Activity:
 *      SongList Screen
 */
public class MainActivity extends AppCompatActivity {
    //VARIABLE DECLARATIONS*****

    private static final String TAG = "MAIN";

    static Player mainActivityPlayerOb;
    static Context mainContext;

    static FirebaseDatabase database;
    static DatabaseReference myRef;
    static FirebaseOptions options;

    static double currentLatitude;
    static double currentLongitude;
    static String currentCityAndState;

    MediaMetadataRetriever metaRetriever;

    ExpandableListView expandableListView;
    static CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    CompoundButton vibeSwitch;

    Map<String, Album> albumTitleToAlbumOb;
    TreeMap<String, List<String>> AlbumTitleToTrackList;
    static Map<String, Uri> songTitleToURI;
    static Map<String, String> uriToUrl;


    // Acquire a reference to the system Location Manager
    static LocationManager locationManager;

    DownloadManager downloadManager;
    static DownloadEngine downloadEngine;

    ImageView statusButton;

    private String saveFileName = "saveState";
    static String userName;

    private int dayMock = 0, monthMock = 0, yearMock = 0, hourMock = 0, minuteMock = 0, secondMock = 0;
    static boolean useMockTime;
    static Calendar mockCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZING VARIABLES*****
        mainActivityPlayerOb = new Player();
        mainContext = getApplicationContext();
        metaRetriever = new MediaMetadataRetriever();
        albumTitleToAlbumOb = new LinkedHashMap<>();
        AlbumTitleToTrackList = new TreeMap<>();
        songTitleToURI = new LinkedHashMap<>();
        uriToUrl = new LinkedHashMap<>();
        useMockTime = false;
        mockCalendar = Calendar.getInstance();

        userName = "Unknown User";
        currentCityAndState = "Unknown Location";

        expandableListView = findViewById(R.id.songlist);

        //PULLING DOWNLOADS FROM PHONE
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            Log.d("test1","ins");
            return;
        }

        addDownloadedSongs(this);

        downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        downloadEngine = new DownloadEngine(downloadManager);


        //DOWNLOAD OPTIONS SETUP

        //SONGINFO LAYOUT
        final LinearLayout songInfoLayout = (LinearLayout) findViewById(R.id.songInfo_layout);
        songInfoLayout.setVisibility(View.VISIBLE);
        songInfoLayout.setEnabled(true);

        final ViewGroup.LayoutParams songInfoParams = songInfoLayout.getLayoutParams();

        //ENTER URL LAYOUT
        final LinearLayout urlEnterLayout = (LinearLayout) findViewById(R.id.url_download_1);
        urlEnterLayout.setVisibility(View.INVISIBLE);
        urlEnterLayout.setEnabled(false);

        final ViewGroup.LayoutParams dwnldButtonParams = urlEnterLayout.getLayoutParams();
        dwnldButtonParams.height = 0;
        urlEnterLayout.setLayoutParams(dwnldButtonParams);

        //DOWNLOAD SONGS BUTTON LAYOUT
        final LinearLayout downloadBtnLayout = (LinearLayout) findViewById(R.id.url_download_btn);
        downloadBtnLayout.setVisibility(View.VISIBLE);

        final ViewGroup.LayoutParams enterUrlParams = downloadBtnLayout.getLayoutParams();
        enterUrlParams.height = 80;
        downloadBtnLayout.setLayoutParams(enterUrlParams);


        final Button downloadButton = (Button) findViewById(R.id.download_Button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadBtnLayout.setVisibility(View.INVISIBLE);
                downloadButton.setEnabled(false);

                enterUrlParams.height = 0;
                downloadBtnLayout.setLayoutParams(enterUrlParams);

                urlEnterLayout.setVisibility(View.VISIBLE);
                urlEnterLayout.setEnabled(true);
                dwnldButtonParams.height = 200;
                urlEnterLayout.setLayoutParams(dwnldButtonParams);

                songInfoParams.height = 0;
                songInfoLayout.setLayoutParams(songInfoParams);


            }
        });

        final Button enterButton = (Button) findViewById(R.id.enter_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText urlBox = (EditText) findViewById(R.id.enter_url);
                String enteredURL = urlBox.getText().toString();
                //urlBox.setText("");

                downloadEngine.tryToDownload(getApplicationContext(), enteredURL);
            }
        });

        final Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlEnterLayout.setVisibility(View.INVISIBLE);
                urlEnterLayout.setEnabled(false);
                dwnldButtonParams.height = 0;
                urlEnterLayout.setLayoutParams(dwnldButtonParams);

                downloadBtnLayout.setVisibility(View.VISIBLE);
                downloadButton.setEnabled(true);

                enterUrlParams.height = 80;
                downloadBtnLayout.setLayoutParams(enterUrlParams);

                songInfoParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                songInfoLayout.setLayoutParams(songInfoParams);
            }
        });


        // VIBE SWITCH SETUP
        vibeSwitch = (CompoundButton) findViewById(R.id.vibe_switch);

        vibeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mainActivityPlayerOb.switchMode();
                    mainActivityPlayerOb.stop();
                    startVibeMode();
                    vibeSwitch.setChecked(false);
                }
            }
        });


        //mock time setup
        final EditText dayText = (EditText) findViewById(R.id.enterDay);
        final EditText monthText = (EditText) findViewById(R.id.enterMonth);
        final EditText yearText = (EditText) findViewById(R.id.enterYear);
        final EditText hourText = (EditText) findViewById(R.id.enterHour);
        final EditText minuteText = (EditText) findViewById(R.id.enterMinute);
        final EditText secondText = (EditText) findViewById(R.id.enterSecond);
        final Button mockTimeButton = (Button) findViewById(R.id.setMockTimeButton);
        final Button realTimeButton = (Button) findViewById(R.id.useRealTimeButton);


        mockTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useMockTime = true;
                dayMock = Integer.parseInt(dayText.getText().toString());
                monthMock = Integer.parseInt(monthText.getText().toString());
                yearMock = Integer.parseInt(yearText.getText().toString());
                hourMock = Integer.parseInt(hourText.getText().toString());
                minuteMock = Integer.parseInt(minuteText.getText().toString());
                secondMock = Integer.parseInt(secondText.getText().toString());
                dayText.setText("");
                monthText.setText("");
                yearText.setText("");
                hourText.setText("");
                minuteText.setText("");
                secondText.setText("");

                mockCalendar.set(yearMock, monthMock, dayMock, hourMock, minuteMock, secondMock);
                Toast.makeText(getBaseContext(), " Using Mock Time! ", Toast.LENGTH_LONG).show();

            }
        });

        realTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useMockTime = false;
                Toast.makeText(getBaseContext(), " Using Real Time! ", Toast.LENGTH_LONG).show();
            }
        });



        //BOTTOM BAR SETUP*****
        statusButton = findViewById(R.id.status);
        ImageView playButton = findViewById(R.id.play);
        ImageView pauseButton = findViewById(R.id.pause);
        ImageView nextButton = findViewById(R.id.next);




        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityPlayerOb.pause();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityPlayerOb.play();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = findViewById(R.id.songInfo);

                ArrayList<TextView> textViews = new ArrayList<>();
                textViews.add(textView);

                mainActivityPlayerOb.next(MainActivity.this, textViews);
                resetStatusButton();
            }
        });



        //ACTION BAR SETUP*****
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);


        /*onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                startVibeMode();
            }
        };*/


        //Pulling Downloads From phone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            Log.d("test1","ins");
            return;
        }

        addDownloadedSongs(this);

        ArrayList<Album> albums = mainActivityPlayerOb.albumObjects;

        for(int i = 0; i < albums.size(); i++)
        {
            Album currentAlbum = albums.get(i);
            String albumTitle = currentAlbum.getAlbumTitle();

            albumTitleToAlbumOb.put(albumTitle, currentAlbum);

            int numSongsAdded = 0;
            ArrayList<Song> songsInAlbum = currentAlbum.getSongObs();
            ArrayList<String> downloadedSongTitles = new ArrayList<>();
            for(Song s: songsInAlbum) {
                if(s.getURI() != null) {
                    downloadedSongTitles.add(s.getSongTitle());
                    numSongsAdded++;
                }
            }

            Toast.makeText(mainContext, "Found " + numSongsAdded + " in " + albumTitle + " album", Toast.LENGTH_SHORT).show();

            if(numSongsAdded > 0) {
                AlbumTitleToTrackList.put(albumTitle, downloadedSongTitles);
            }
        }


        expandableListDetail = AlbumTitleToTrackList;
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {}
        });


        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {}
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String songName = expandableListAdapter.getChild(groupPosition, childPosition).toString();
                Toast.makeText(getBaseContext(), " Clicked on :: " + songName, Toast.LENGTH_LONG).show();
                TextView textView = findViewById(R.id.songInfo);

                if(songName.equals("PLAY ALBUM")) {
                    String albumName = expandableListAdapter.getGroup(groupPosition).toString();
                    Album albumOb = albumTitleToAlbumOb.get(albumName);

                    mainActivityPlayerOb.playAlbum(MainActivity.this, albumOb, textView);
                }
                else {
                    Uri uri = songTitleToURI.get(songName);

                    mainActivityPlayerOb.playSong(MainActivity.this, uri, textView);
                }


                resetStatusButton();
                return true;
            }
        });


        //DATABASE SETUP*****
        options = new FirebaseOptions.Builder()
                .setApplicationId("1:757111785128:android:39aebf8f7043bb7b")
                .setDatabaseUrl("https://cse-110-team-project-team-29.firebaseio.com/").build();

        //database = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(this, options, "secondary"));
        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(this);
        if (firebaseApps != null && !firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } else {
            firebaseApp = FirebaseApp.initializeApp(this, options);
        }
        database = FirebaseDatabase.getInstance(firebaseApp);


        myRef = database.getReference();
        myRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String uniqueKey = dataSnapshot.getKey();

                DatabaseReference currRef = myRef.child(uniqueKey);

                currRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addDatabaseEntries(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Intent output = new Intent();
        setResult(RESULT_OK, output);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        //DEFINE LOCATION LISTENER THAT RESPONDS TO LOCATION CHANGES
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = new ArrayList<>();

                try {
                    addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0) {
                    currentCityAndState = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
                    Toast.makeText(getApplicationContext(), "City and State: " + currentCityAndState , Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No city found ", Toast.LENGTH_LONG).show();

                }

                Toast.makeText(getApplicationContext(), "Coordinates: (" + location.getLongitude()
                        + ", " + location.getLatitude() + ")", Toast.LENGTH_LONG).show();

                mainActivityPlayerOb.prioritizeSongs();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        /*
         if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         requestPermissions(
         new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
         100);
         Log.d("main activity location","ins");
         return;
         }  *///TODO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 31, locationListener);

        resetStatusButton();


        statusButton.setOnClickListener(new View.OnClickListener() {
            Song currentSong = mainActivityPlayerOb.getCurrentSongObject();
            Boolean currentExists = currentSong != null;

            @Override
            public void onClick(View view) {
                if (currentExists){
                    Boolean status = currentSong.getNeutralStatus();

                    if(status){
                      statusButton.setImageResource(R.drawable.check);
                      currentSong.setFavoriteTrue();
                      Toast.makeText(getBaseContext(), "Liked", Toast.LENGTH_SHORT).show();
                      return;
                    }
                    else{
                        currentSong.setDislikeTrue();
                        Toast.makeText(getBaseContext(), "DisLiked", Toast.LENGTH_SHORT).show();
                        TextView textView = findViewById(R.id.songInfo);

                        ArrayList<TextView> textViews = new ArrayList<>();
                        textViews.add(textView);

                        mainActivityPlayerOb.next(MainActivity.this, textViews);
                        resetStatusButton();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Doesn't Exist", Toast.LENGTH_SHORT).show();
                }

                /*Boolean status = mainActivityPlayerOb.getCurrentSongObject().getFavoriteStatus();

                if (status){
                    mainActivityPlayerOb.getCurrentSongObject().setDislikeTrue();
                    TextView textView = findViewById(R.id.songInfo);

                    ArrayList<TextView> textViews = new ArrayList<>();
                    textViews.add(textView);

                    mainActivityPlayerOb.next(MainActivity.this, textViews);
                    resetStatusButton();
                }
                else{
                    mainActivityPlayerOb.getCurrentSongObject().setFavoriteTrue();
                    resetStatusButton();
                }*/
            }
        });

        //readData();
    }

    private void resetStatusButton(){

        Boolean currentExists = mainActivityPlayerOb.getCurrentSongObject() != null;

        if (!currentExists){
            statusButton.setImageResource(R.drawable.plus);
            Toast.makeText(getBaseContext(), "No Current Song Playing", Toast.LENGTH_SHORT).show();
            return;
        }

        Boolean status = mainActivityPlayerOb.getCurrentSongObject().getFavoriteStatus();

        if (status){
            statusButton.setImageResource(R.drawable.check);
        }
        else{
            statusButton.setImageResource(R.drawable.plus);
        }

    }


    public void startVibeMode() {
        Intent intent = new Intent(this, FlashbackActivity.class);
        startActivity(intent);
    }

    //Save data inside of
    public void onDestroy(){
        saveState();
        super.onDestroy();
    }

    public void saveState(){
        FileOutputStream s;

        File file = new File(this.getFilesDir(), saveFileName);

        try {
            if(file == null || !file.exists()){
                file.createNewFile();
            }

            s = new FileOutputStream(file);

            String state = "m";
            String newLine = "\n";

            s.write(state.getBytes());
            s.write(newLine.getBytes());

            MediaPlayer mp = mainActivityPlayerOb.getMediaPlayer();

            if (mp.isPlaying()) {
                String currentPos = "" + mp.getCurrentPosition();
                String currentSongName = mainActivityPlayerOb.getCurrentSongName();

                s.write(currentPos.getBytes());
                s.write(newLine.getBytes());
                s.write(currentSongName.getBytes());
            }
            // write an "n" on the next two lines to show that no song was playing
            else {
                String notPlaying = "n\nn\n";
                s.write(notPlaying.getBytes());
            }

            // TODO write the uriToUrl map to the file
            Object[] uriAndUrlStuff = uriToUrl.keySet().toArray();
            int i = 0;
            // while there are still keys to be written in uriToUrl
            while(i < uriAndUrlStuff.length) {
                // write the string and a new line
                s.write(uriAndUrlStuff[i].toString().getBytes());
                s.write(newLine.getBytes());
                // go to the next line
                i++;
            }

            s.flush();
            s.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void readData(){
        try{
            FileInputStream s = openFileInput(saveFileName);
            InputStreamReader r = new InputStreamReader(s);
            BufferedReader p = new BufferedReader(r);

            String line;

            boolean mainActivity = false;

            // determine if in flashback or main activity
            line = p.readLine();
            if (line.equals("m"))
                mainActivity = true;

            // get the song name if applicable
            line = p.readLine();
            if(line.equals("n")) {
                line = p.readLine();
            }
            else {
                String song = line;

                line = p.readLine();
                int start = Integer.parseInt(line);

                //TODO play "song" at "start" time
            }

            String key;
            String value;

            uriToUrl.clear();

            while ((line = p.readLine()) != null) {
                // get the key and the value
                key = line;
                value = p.readLine();

                // add the key/value to the hash map
                if (value != null) {
                    uriToUrl.put(key, value);
                }

            }
            p.close();

            if (mainActivity == false) {
                //TODO change activities
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public void addDownloadedSongs(Context c) {
        File downloadsDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");

        File[] downloads = downloadsDirectory.listFiles();

        ArrayList<File> mp3Downloads = new ArrayList<>();

        for(File f: downloads) {
            String filename = f.getName();
            int lastPeriodIndex = filename.lastIndexOf(".");

            if(lastPeriodIndex == -1) {
                break;
            }

            int filenameLength = filename.length();
            String extension = filename.substring(lastPeriodIndex, filenameLength);

            if(extension.equals(".mp3")) {
                mp3Downloads.add(f);
            }
        }

        if(mp3Downloads.size() == 0) {
            Toast.makeText(getBaseContext(), "List is empty -- No Downloads", Toast.LENGTH_SHORT).show();
        }
        else {
            for(File f: mp3Downloads){
                //Toast.makeText(getBaseContext(), "File found: " + f.getName(), Toast.LENGTH_LONG).show();

                MediaMetadataRetriever metaRetriever2 = new MediaMetadataRetriever();
                metaRetriever2.setDataSource(f.getAbsolutePath());

                String songTitle = f.getName();
                String songAlbum = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String songArtist = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                Uri uri = Uri.parse(f.toString());
                String currUrl = uriToUrl.get(uri.toString());
                if (currUrl == null) {
                    currUrl = "";
                }
                mainActivityPlayerOb.add(songTitle, songAlbum, songArtist, currUrl, uri);

                songTitleToURI.put(songTitle, uri);
            }
        }
    }


    public void updateExpandableList() {
        ArrayList<Album> albums = mainActivityPlayerOb.albumObjects;

        TreeMap<String, List<String>> newAlbumToTrackListMap = new TreeMap<>();


        for(int i = 0; i < albums.size(); i++)
        {
            Album currentAlbum = albums.get(i);
            String albumTitle = currentAlbum.getAlbumTitle();

            int numSongsAdded = 0;
            ArrayList<Song> songsInAlbum = currentAlbum.getSongObs();
            ArrayList<String> downloadedSongTitles = new ArrayList<>();
            for(Song s: songsInAlbum) {
                if(s.getURI() != null) {
                    downloadedSongTitles.add(s.getSongTitle());
                    numSongsAdded++;
                }
            }

            Toast.makeText(mainContext, "Found " + numSongsAdded + " in " + albumTitle + " album", Toast.LENGTH_SHORT).show();

            if(numSongsAdded > 0) {
                newAlbumToTrackListMap.put(albumTitle, downloadedSongTitles);
            }
        }

        if(expandableListAdapter == null) {
            Toast.makeText(mainContext, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        expandableListAdapter.updateSongsList(newAlbumToTrackListMap);
    }


    public void addDatabaseEntries(DataSnapshot dataSnapshot) {
        //VARIABLES NEEDED TO MAKE AND/OR UPDATE SONG IN ENTRY
        String songName = "", artist = "", album = "", url1 = "", city = "", playedBy = "";
        int second = 0, hour = 0, month = 0, year = 0, dayOfMonth = 0, minute = 0;
        double latitude = 0.0, longitude = 0.0;

        //COUNTER
        int i = 0;

        //ITERATING THROUGH EACH DATABASE ENTRY
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            switch (i) {
                case 0:
                    city = ds.getValue().toString();
                    i++;
                    break;
                case 1:
                    dayOfMonth = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
                case 2:
                    hour = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
                case 3:
                    latitude = Double.parseDouble(ds.getValue().toString());
                    i++;
                    break;
                case 4:
                    longitude = Double.parseDouble(ds.getValue().toString());
                    i++;
                    break;
                case 5:
                    minute = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
                case 6:
                    month = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
                case 7:
                    playedBy = ds.getValue().toString();
                    i++;
                    break;
                case 8:
                    second = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
                case 9:
                    album = ds.getValue().toString();
                    i++;
                    break;
                case 10:
                    artist = ds.getValue().toString();
                    i++;
                    break;
                case 11:
                    songName = ds.getValue().toString();
                    i++;
                    break;
                case 12:
                    url1 = ds.getValue().toString();
                    i++;
                    break;
                case 13:
                    year = Integer.parseInt(ds.getValue().toString());
                    i++;
                    break;
            }

        }

        Toast.makeText(mainContext, "Picked up an entry", Toast.LENGTH_SHORT).show();

        mainActivityPlayerOb.add(songName, album, artist, url1, null);

        Calendar currCal = Calendar.getInstance();
        currCal.set(year, month, dayOfMonth, hour, minute, second);

        Location currLoc = new Location(city);
        currLoc.setLatitude(latitude);
        currLoc.setLongitude(longitude);

        ArrayList<Song> songObjects = mainActivityPlayerOb.getSongObjects();
        for (int j = 0; j < songObjects.size(); j++) {
            if (songObjects.get(j).getSongTitle().equals(songName)) {
                songObjects.get(j).update(currCal, currLoc, playedBy);
            }
        }
    }
}

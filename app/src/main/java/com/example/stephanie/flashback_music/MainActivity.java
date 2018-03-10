package com.example.stephanie.flashback_music;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/*
 * Main Activity:
 *      SongList Screen
 */
public class MainActivity extends AppCompatActivity {
    //VARIABLE DECLARATIONS*****
    static Player mainActivityPlayerOb;

    static FirebaseDatabase database;
    static DatabaseReference myRef;
    static FirebaseOptions options;

    MediaMetadataRetriever metaRetriever;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    CompoundButton vibeSwitch;
    OnSwipeTouchListener onSwipeTouchListener;

    Map<String, Integer> songTitleToResourceId;
    Map<String, Album> albumTitleToAlbumOb;
    Map<String, String> songTitleToAlbumName;
    Map<String, String> songTitleToArtistName;

    TreeMap<String, List<String>> AlbumToTrackListMap;

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    Location l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZING VARIABLES*****
        mainActivityPlayerOb = new Player();
        metaRetriever = new MediaMetadataRetriever();
        songTitleToResourceId = new LinkedHashMap<>();
        albumTitleToAlbumOb = new LinkedHashMap<>();
        AlbumToTrackListMap = new TreeMap<>();
        songTitleToAlbumName = new TreeMap<>();
        songTitleToArtistName = new TreeMap<>();

        expandableListView = findViewById(R.id.songlist);


        //BOTTOM BAR SETUP*****
        ImageView statusButton = findViewById(R.id.status);
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
            }
        });

        final Button downloadButton = (Button) findViewById(R.id.DownloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadButton.setVisibility(View.INVISIBLE);
            }
        });

        // flashback mode activity switch
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


        //DATABASE SETUP*****
        options = new FirebaseOptions.Builder()
                .setApplicationId("1:757111785128:android:39aebf8f7043bb7b")
                .setDatabaseUrl("https://cse-110-team-project-team-29.firebaseio.com/").build();

        database = FirebaseDatabase.getInstance(
                FirebaseApp.initializeApp(this, options, "secondary"));

        myRef = database.getReferenceFromUrl("https://cse-110-team-project-team-29.firebaseio.com/");

        //ACTION BAR SETUP*****
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);

        Uri path;

        //Pulling Downloads From phone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            Log.d("test1","ins");
            return;
        }

        ArrayList<File> downloads = getDownloadedSongs(this);

        /*onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                startVibeMode();
            }
        };*/


        //CREATING SONG OBJECTS AND ALBUM OBJECTS*****
        Field[] fields = R.raw.class.getFields();
        for(Field field : fields)
        {
            String nameOfResourceItem = field.getName();

            int resID = getResources().getIdentifier(nameOfResourceItem, "raw", getPackageName());

            path = Uri.parse("android.resource://" + getPackageName() + "/" + resID);
            metaRetriever.setDataSource(this, path);

            String songTitle = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String songAlbum = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String songArtist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            mainActivityPlayerOb.add(songTitle, songAlbum, songArtist, resID);

            songTitleToResourceId.put(songTitle, resID);
            songTitleToAlbumName.put(songTitle, songAlbum);
            songTitleToArtistName.put(songTitle, songArtist);
        }


        ArrayList<Album> albums = mainActivityPlayerOb.albumObjects;
        for(int i = 0; i < albums.size(); i++)
        {
            Album currentAlbum = albums.get(i);
            String albumTitle = currentAlbum.getAlbumTitle();

            albumTitleToAlbumOb.put(albumTitle, currentAlbum);
            AlbumToTrackListMap.put(albumTitle, currentAlbum.returnSongTitles());
        }


        //EXPANDABLE LIST SETUP*****
        expandableListDetail = AlbumToTrackListMap;
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
                    int resourceID = songTitleToResourceId.get(songName);

                    mainActivityPlayerOb.playSong(MainActivity.this, resourceID, textView);
                }
                return true;
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                l = location;
                Toast.makeText(getApplicationContext(), "(" + location.getLongitude()
                        + ", " + location.getLatitude() + ")", Toast.LENGTH_LONG).show();

                mainActivityPlayerOb.prioritizeSongs();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        /**
         if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         requestPermissions(
         new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
         100);
         Log.d("main activity location","ins");
         return;
         }*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 31, locationListener);
    }

    public void startVibeMode() {
        Intent intent = new Intent(this, FlashbackActivity.class);
        startActivity(intent);
    }


    public ArrayList<File> getDownloadedSongs(Context c) {
        File downloadsDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/");

        File[] downloads = downloadsDirectory.listFiles();

        ArrayList<File> mp3Downloads = new ArrayList<>();

        for(File f: downloads) {
            String filename = f.getName();
            int lastPeriodIndex = filename.lastIndexOf(".");
            int filenameLength = filename.length();
            String extension = filename.substring(lastPeriodIndex, filenameLength);

            if(extension.equals(".mp3")) {
                mp3Downloads.add(f);
            }
        }

        if(mp3Downloads.size() == 0) {
            Toast.makeText(getBaseContext(), "List is empty -- No Downloads", Toast.LENGTH_LONG).show();
        }
        else {
            for(File f: mp3Downloads){
                //Toast.makeText(getBaseContext(), "File found: " + f.getName(), Toast.LENGTH_LONG).show();
                URI uri = f.toURI();
                URL url = null;

                try {
                    /*MediaMetadataRetriever metaRetriever2 = new MediaMetadataRetriever();
                    metaRetriever2.setDataSource(f.getAbsolutePath());//c, Uri.parse(f.getName()));

                    String songTitle = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String songAlbum = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String songArtist = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    Toast.makeText(getBaseContext(), songTitle, Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), songAlbum, Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), songArtist, Toast.LENGTH_LONG).show();*/

                    url = uri.toURL();
                }
                catch(MalformedURLException m){
                    m.printStackTrace();
                }

                //Toast.makeText(getBaseContext(), "From: " + uri.toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(), "From: " + url.toString(), Toast.LENGTH_LONG).show();
            }
        }

        return mp3Downloads;
    }
}

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getDataDirectory;
import static android.os.Environment.getDownloadCacheDirectory;
import static android.os.Environment.getRootDirectory;


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

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            Log.d("test1","ins");
            return;
        }

        File downloadsDirectory = getExternalFilesDir(DIRECTORY_DOWNLOADS).getAbsoluteFile();
        Toast.makeText(getBaseContext(), downloadsDirectory.getAbsolutePath(), Toast.LENGTH_LONG).show();
        File[] temp = downloadsDirectory.listFiles();
        //File[] downloadedFiles = downloadsDirectory.listFiles();

        if(temp.length == 0) {
            Toast.makeText(getBaseContext(), "List is empty -- No Downloads", Toast.LENGTH_LONG).show();
        }
        else {
            for(File f: temp){
                Toast.makeText(getBaseContext(), "File found: " + f.getName(), Toast.LENGTH_LONG).show();
                File[] temp2 = f.listFiles();
                if(temp2 == null || temp2.length == 0) {
                    Toast.makeText(getBaseContext(), f.getName() + "List is empty" ,Toast.LENGTH_LONG).show();
                }
                else {
                    for(File f2:temp2) {
                        Toast.makeText(getBaseContext(), "File found: " + f2.getName() + " in " + f.getName(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }*/

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

        /*onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                startVibeMode();
            }
        };*/

/*
//gets filepath to downloads folder
        File dl_path = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        dl_path.setReadable(true);
        dl_path.setWritable(true);
        Toast.makeText(getBaseContext(), dl_path.getAbsolutePath(), Toast.LENGTH_LONG).show();
        //File dl_path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        File[] songs = dl_path.listFiles();

        if (songs.length == 0) {
            Toast.makeText(getBaseContext(), "Length is 0", Toast.LENGTH_LONG).show();
        }
        else {
            //TODO: Add type check for .mp3, .wav, etc.
            for (File f : songs) {
                String ext = android.webkit.MimeTypeMap.getFileExtensionFromUrl(f.getName());
                if (ext.equals(MP3)) {
                    Toast.makeText(getBaseContext(), "File " + f.getName() + " is an MP3", Toast.LENGTH_LONG).show();

                    metaRetriever.setDataSource(f.getPath());

                    String songTitle = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String songAlbum = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String songArtist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    mainActivityPlayerOb.add(songTitle, songAlbum, songArtist);
                } else {
                    Toast.makeText(getBaseContext(), "File " + f.getName() + " is not MP3", Toast.LENGTH_LONG).show();
                }
            }
        }

*/


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

}

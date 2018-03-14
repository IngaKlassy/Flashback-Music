package com.example.stephanie.flashback_music;


import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
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

    private static final String TAG = "MAIN";

    static Player mainActivityPlayerOb;

    static FirebaseDatabase database;
    static DatabaseReference myRef;
    static FirebaseOptions options;

    static double currentLatitude;
    static double currentLongitude;
    static String currentLocation;

    MediaMetadataRetriever metaRetriever;

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    CompoundButton vibeSwitch;
    OnSwipeTouchListener onSwipeTouchListener;

    static Map<String, Uri> songTitleToURI;
    Map<String, Album> albumTitleToAlbumOb;
    Map<String, String> songTitleToAlbumName;
    Map<String, String> songTitleToArtistName;

    TreeMap<String, List<String>> AlbumToTrackListMap;

    // Acquire a reference to the system Location Manager
    static LocationManager locationManager;

    DownloadManager downloadManager;
    DownloadEngine downloadEngine;
    ImageView statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZING VARIABLES*****
        mainActivityPlayerOb = new Player();
        metaRetriever = new MediaMetadataRetriever();
        songTitleToURI = new LinkedHashMap<>();
        albumTitleToAlbumOb = new LinkedHashMap<>();
        AlbumToTrackListMap = new TreeMap<>();
        songTitleToAlbumName = new TreeMap<>();
        songTitleToArtistName = new TreeMap<>();


        expandableListView = (ExpandableListView) findViewById(R.id.songlist);


        //DATABASE SETUP*****
        options = new FirebaseOptions.Builder()
                .setApplicationId("1:757111785128:android:39aebf8f7043bb7b")
                .setDatabaseUrl("https://cse-110-team-project-team-29.firebaseio.com/").build();

        database = FirebaseDatabase.getInstance(
                FirebaseApp.initializeApp(this, options, "secondary"));

        myRef = database.getReference();//database.getReferenceFromUrl("https://cse-110-team-project-team-29.firebaseio.com/");
        myRef.child("12345").setValue("Hey Inga");

        String key = myRef.child("12345").push().getKey();

        Map<String, Object> test = new TreeMap<>();
        test.put("Amanda", new Integer(28));
        test.put("Inga", new Integer(29));

        Map<String, Object> test2 = new TreeMap<>();
        test2.put("12345", test);

        myRef.updateChildren(test2);

        Intent output = new Intent();
        setResult(RESULT_OK, output);


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


        //Pulling Downloads From phone
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            Log.d("test1","ins");
            return;
        }

        addDownloadedSongs(this);

        /*onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                startVibeMode();
            }
        };*/

        ArrayList<Album> albums = mainActivityPlayerOb.albumObjects;

        for(int i = 0; i < albums.size(); i++)
        {
            Album currentAlbum = albums.get(i);
            String albumTitle = currentAlbum.getAlbumTitle();

            albumTitleToAlbumOb.put(albumTitle, currentAlbum);
            AlbumToTrackListMap.put(albumTitle, currentAlbum.returnSongTitles());
        }


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
                    Uri uri = songTitleToURI.get(songName);

                    mainActivityPlayerOb.playSong(MainActivity.this, uri, textView);
                }
                return true;
            }
        });


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        //DEFINE LOCATION LISTENER THAT RESPONDS TO LOCATION CHANGES
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

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
            Boolean currentExists = mainActivityPlayerOb.getCurrentSongObject() != null;


            @Override
            public void onClick(View view) {
                if (!currentExists){
                    statusButton.setImageResource(R.drawable.check);
                    Toast.makeText(getBaseContext(), "No Current Song Playing", Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean status = mainActivityPlayerOb.getCurrentSongObject().getFavoriteStatus();

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
                }

            }
        });
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
                //Toast.makeText(getBaseContext(), "File path: " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();


                MediaMetadataRetriever metaRetriever2 = new MediaMetadataRetriever();
                //Toast.makeText(getBaseContext(), "File path = " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                metaRetriever2.setDataSource(f.getAbsolutePath());//c, Uri.parse(f.getName()));

                String songTitle = f.getName();//metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String songAlbum = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String songArtist = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                Uri uri = Uri.parse(f.toString());
                mainActivityPlayerOb.add(songTitle, songAlbum, songArtist, "", uri);

                songTitleToURI.put(songTitle, uri);
                songTitleToAlbumName.put(songTitle, songAlbum);
                songTitleToArtistName.put(songTitle, songArtist);
            }
        }
    }


    public void updateExpandableList() {
        ArrayList<Album> albums = mainActivityPlayerOb.albumObjects;

        Map<String, Album> newAlbumTitleToAlbumOb = new LinkedHashMap<>();
        Map<String, List<String>> newAlbumToTrackListMap = new TreeMap<>();


        for(int i = 0; i < albums.size(); i++)
        {
            Album currentAlbum = albums.get(i);
            String albumTitle = currentAlbum.getAlbumTitle();

            newAlbumTitleToAlbumOb.put(albumTitle, currentAlbum);
            newAlbumToTrackListMap.put(albumTitle, currentAlbum.returnSongTitles());
        }

        if(expandableListAdapter == null) {
            //Toast.makeText(getBaseContext(), "adapter is null" , Toast.LENGTH_LONG).show();
            return;
        }

        expandableListAdapter.updateSongsList(AlbumToTrackListMap);
    }
}

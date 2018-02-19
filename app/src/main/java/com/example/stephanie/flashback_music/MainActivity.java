package com.example.stephanie.flashback_music;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/*
 * Main Activity:
 *      SongList Screen
 */
public class MainActivity extends AppCompatActivity implements Serializable {
    //VARIABLE DECLARATIONS*****
    static Player mainActivityPlayerOb;

    MediaMetadataRetriever metaRetriever;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    CompoundButton flashbackSwitch;
    OnSwipeTouchListener onSwipeTouchListener;

    Map<String, Integer> songTitleToResourceId;
    Map<String, Album> albumTitleToAlbumOb;
    Map<String, String> songToAlbum;
    Map<String, String> songToArtist;

    TreeMap<String, List<String>> AlbumToTrackListMap;

    Uri path;
    int count = 0;

    private AlbumService albumService;
    private boolean isRunning = false;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(MainActivity.this, AlbumService.class);

        //ACTION BAR SETUP*****
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);

        //BOTTOM BAR SETUP*****
        ImageView statusButton = findViewById(R.id.status);

        ImageView playButton = findViewById(R.id.play);

        ImageView pauseButton = findViewById(R.id.pause);

        ImageView nextButton = findViewById(R.id.next);

        //INITIALIZING VARIABLES*****
        mainActivityPlayerOb = new Player();
        metaRetriever = new MediaMetadataRetriever();
        songTitleToResourceId = new LinkedHashMap<>();
        albumTitleToAlbumOb = new LinkedHashMap<>();
        AlbumToTrackListMap = new TreeMap<>();
        songToAlbum = new TreeMap<>();
        songToArtist = new TreeMap<>();

        expandableListView = findViewById(R.id.songlist);
        ImageView pauseBt = (ImageView) findViewById(R.id.pause);
        pauseBt.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if(mainActivityPlayerOb.getMp().isPlaying()) {
                                                   mainActivityPlayerOb.getMp().pause();
                                               }
                                           }
                                       }
        );

        final int currentResource;
        ImageView playBt = (ImageView) findViewById(R.id.play);
        playBt.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                                  if(!mainActivityPlayerOb.getMp().isPlaying()) {
                                                      mainActivityPlayerOb.getMp().start();
                                                  }
                                          }
                                      }
        );


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
            songToAlbum.put(songTitle, songAlbum);
            songToArtist.put(songTitle, songArtist);
        }


        ArrayList<Album> albums = mainActivityPlayerOb.albums;
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
                TextView tv = findViewById(R.id.songInfo);

                //Intent intent = new Intent(MainActivity.this, AlbumService.class);

                if(songName.equals("PLAY ALBUM")) {
                    if(isRunning) {
                        stopService(intent);
                        isRunning = false;
                    }

                    Album temp = albumTitleToAlbumOb.get(expandableListAdapter.getGroup(groupPosition).toString());

                    intent.putExtra("Album", temp);

                    startService(intent);
                    isRunning = true;

                    mainActivityPlayerOb.playAlbum(MainActivity.this, temp);
                }
                else {
                    if(isRunning) {
                        stopService(intent);
                        isRunning = false;
                    }

                    Integer resourceID = songTitleToResourceId.get(songName);
                    tv.setText(songName + "\n" + songToAlbum.get(songName) + "\n" + songToArtist.get(songName));

                    mainActivityPlayerOb.playSong(MainActivity.this, resourceID.intValue());
                }
                return true;
            }
        });

        /*mainPlayer.getMp().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });*/

        // flashback mode activity switch
        flashbackSwitch = (CompoundButton) findViewById(R.id.flashback_switch);
        flashbackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    if (mainActivityPlayerOb.getMp().isPlaying()) {
                        mainActivityPlayerOb.getMp().pause();
                        mainActivityPlayerOb.getMp().reset();
                    }
                    // generate priority queue
                    startFlashbackMode();
                    launchActivity();
                    flashbackSwitch.setChecked(false);
                }
            }
        });

        onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                launchActivity();
            }
        };
    }

    public void launchActivity() {
        Intent intent = new Intent(this, FlashbackActivity.class);
        startActivity(intent);
    }

    public void startFlashbackMode() {
        mainActivityPlayerOb.prioritizeSongsPlayed();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

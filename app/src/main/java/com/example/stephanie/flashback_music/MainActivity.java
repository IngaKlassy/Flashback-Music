package com.example.stephanie.flashback_music;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
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
public class MainActivity extends AppCompatActivity {
    //VARIABLE DECLARATIONS*****
    Player mainActivityPlayerOb;

    MediaMetadataRetriever metaRetriever;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    OnSwipeTouchListener onSwipeTouchListener;

    Map<String, Integer> songTitleToResourceId;
    Map<String, Album> albumTitleToAlbumOb;
    Map<String, String> songToAlbum;
    Map<String, String> songToArtist;

    TreeMap<String, List<String>> AlbumToTrackListMap;

    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ACTION BAR SETUP*****
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);

        //INITIALIZING VARIABLES*****
        mainActivityPlayerOb = new Player();
        metaRetriever = new MediaMetadataRetriever();
        songTitleToResourceId = new LinkedHashMap<>();
        albumTitleToAlbumOb = new LinkedHashMap<>();
        AlbumToTrackListMap = new TreeMap<>();
        songToAlbum = new TreeMap<>();
        songToArtist = new TreeMap<>();

        expandableListView = findViewById(R.id.songlist);


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

                Intent intent = new Intent(MainActivity.this, AlbumService.class);

                if(songName.equals("PLAY ALBUM")) {
                    startService(intent);

                    Album temp = albumTitleToAlbumOb.get(expandableListAdapter.getGroup(groupPosition).toString());

                    mainActivityPlayerOb.playAlbum(MainActivity.this, temp);
                }
                else {
                    stopService(intent);

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

        onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                launchActivity();
            }
        };
    }

    public void launchActivity() {
        Intent intent = new Intent(this, Flashback_Activity.class);
        startActivity(intent);
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

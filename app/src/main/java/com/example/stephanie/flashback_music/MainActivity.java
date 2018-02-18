package com.example.stephanie.flashback_music;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * SongList Activity/ Homescreen
 */
public class MainActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    TreeMap<String, List<String>> expandableListDetail;

    MediaMetadataRetriever metaRetriever;

    Player mainPlayer = new Player();

    OnSwipeTouchListener onSwipeTouchListener;


    Map<String, Integer> songToIdMap;
    Map<String, Album> albumToAlbum;
    boolean playNewSong = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expandableListView = (ExpandableListView) findViewById(R.id.songlist);


        songToIdMap = new LinkedHashMap<String, Integer>();
        //list = new list<String>();

        metaRetriever = new MediaMetadataRetriever();
        albumToAlbum = new LinkedHashMap<>();
        Uri path;



        Field[] fields = R.raw.class.getFields();
        for(int i = 0; i < fields.length; i++)
        {
            String temp = fields[i].getName();
            //list.add(temp);
            int resID = getResources().getIdentifier(fields[i].getName(), "raw", getPackageName());

            path = Uri.parse("android.resource://" + getPackageName() + "/" + resID);
            metaRetriever.setDataSource(this, path);
            mainPlayer.add(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), resID);
            songToIdMap.put(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), resID);
        }

        TreeMap<String, List<String>> mapOfSongs = new TreeMap<String, List<String>>();

        ArrayList<Album> albums = mainPlayer.albums;
        for(int i = 0; i < albums.size(); i++)
        {
            albumToAlbum.put(albums.get(i).getAlbumTitle(), albums.get(i));
            mapOfSongs.put(albums.get(i).getAlbumTitle(), albums.get(i).returnSongTitles());
        }

        expandableListDetail = mapOfSongs;
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });


        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String songName = expandableListAdapter.getChild(groupPosition, childPosition).toString();
                Toast.makeText(getBaseContext(), " Clicked on :: " + songName, Toast.LENGTH_LONG).show();


                if(songName.equals("PLAY ALBUM")) {
                    Album temp = albumToAlbum.get(expandableListAdapter.getGroup(groupPosition).toString());

                    mainPlayer.playAlbum(MainActivity.this, temp);
                }
                else {
                    Integer resourceID = songToIdMap.get(songName);

                    mainPlayer.playSong(MainActivity.this, resourceID.intValue());
                }
                return true;
            }
        });

        mainPlayer.getMp().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        onSwipeTouchListener = new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                launchActivity();
            }
        };

        /*
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, list);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(mediaPlayer != null)
                {
                    mediaPlayer.release();
                }

                int resID = getResources().getIdentifier(list.get(i), "raw", getPackageName());
                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                mediaPlayer.start();
            }
        });*/


            /*
            album.setText("Unknown Album");
            artist.setText("Unknown Artist");
            genre.setText("Unknown Genre");*/


        //Read more: http://mrbool.com/how-to-extract-meta-data-from-media-file-in-android/28130#ixzz56fcMhW4p*/
    }

    public void launchActivity() {
        Intent intent = new Intent(this, Flashback_Activity.class);
        startActivity(intent);
    }

    /*@Override
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

package com.example.stephanie.flashback_music;

import android.app.ActionBar;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    //List<String> list;
    Map<String, Integer> songToIdMap;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expandableListView = (ExpandableListView) findViewById(R.id.songlist);

        songToIdMap = new LinkedHashMap<String, Integer>();
        //list = new list<String>();

        metaRetriever = new MediaMetadataRetriever();
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
                            metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            songToIdMap.put(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), resID);
        }

        TreeMap<String, List<String>> mapOfSongs = new TreeMap<String, List<String>>();

        ArrayList<Album> albums = mainPlayer.albums;
        for(int i = 0; i < albums.size(); i++)
        {
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


                String s = v.toString();
                int resID = getResources().getIdentifier(songName, "raw", getPackageName());

                if(mediaPlayer != null)
                {
                    mediaPlayer.release();
                }

                Integer resourceID = songToIdMap.get(songName);
                mediaPlayer = MediaPlayer.create(MainActivity.this, resourceID.intValue());
                mediaPlayer.start();
                return true;

            }
        });



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

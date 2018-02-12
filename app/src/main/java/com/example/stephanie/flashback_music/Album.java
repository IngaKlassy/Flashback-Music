/**
 * Created by Stephanie on 2/6/2018.
 */

package com.example.stephanie.flashback_music;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class Album {
    private String albumTitle;
    private String albumArtist;

    private ArrayList<Song> songObList;
    private List<String> songTitleList;

    public Album (String title, String artist) {
        albumTitle = title;
        albumArtist = artist;

        songObList = new ArrayList<>();
        songTitleList = new ArrayList<String>();
    }

    void addSong (Song song) {
        String inSong = song.getName();
        for(int i = 0; i < songTitleList.size(); i++)
        {
            if(inSong.equals(songTitleList.get(i)))
            {
                //Song already exists in the list
                return;
            }
        }

        songObList.add(song);
        songTitleList.add(inSong);
    }

    void playAlbum() {
        for(int i = 0; i < songObList.size(); i++) {
            // play the song at i
        }
    }


    List<String> returnSongTitles()
    {
        return songTitleList;
    }

    String getAlbumTitle() {
        return albumTitle;
    }

    String getAlbumArtist() {
        return albumArtist;
    }
}
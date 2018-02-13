package com.example.stephanie.flashback_music;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Album {
    private String albumTitle;
    private String albumArtist;

    ArrayList<Song> songObList;
    List<String> songTitleList;
    ArrayList<Integer> songIds;

    public Album (String title, String artist) {
        albumTitle = title;
        albumArtist = artist;
        songIds = new ArrayList<>();
        songObList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songTitleList.add("PLAY ALBUM");
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
        songIds.add((song.getResId()));
    }

    ArrayList<Integer> getSongIds() {
        return songIds;
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
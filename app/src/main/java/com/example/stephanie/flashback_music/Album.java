/**
 * Created by Stephanie on 2/6/2018.
 */

package com.example.stephanie.flashback_music;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Album {
    private String albumTitle;
    private String albumArtist;


    private ArrayList<Song> songObList = new ArrayList<>();
    private List<String> songTitleList = new ArrayList<>();
    private ArrayList<Uri> songURIs = new ArrayList<>();


    public Album (String title, String artist) {
        if(title == null) { albumTitle = "Unknown Album"; }
        else { albumTitle = title; }

        if(artist == null) { albumArtist = "Unknown Artist"; }
        else { albumArtist = title; }

        songObList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songURIs = new ArrayList<>();

        songTitleList.add("PLAY ALBUM");
    }


    public void addSong (Song song) {
        String inSong = song.getSongTitle();
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
        songURIs.add(song.getURI());
    }


    ArrayList<Song> getSongObs() {
        return songObList;
    }

    public ArrayList<Uri> getSongURIs() {
        return songURIs;
    }

    public List<String> returnSongTitles()
    {
        return songTitleList;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }
}
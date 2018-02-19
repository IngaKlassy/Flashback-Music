/**
 * Created by Stephanie on 2/6/2018.
 */

package com.example.stephanie.flashback_music;

import java.util.ArrayList;
import java.util.List;


public class Album {
    private String albumTitle;
    private String albumArtist;


    private ArrayList<Song> songObList;
    private List<String> songTitleList;
    private ArrayList<Integer> songIds;


    public Album (String title, String artist) {
        albumTitle = title;
        albumArtist = artist;
        songObList = new ArrayList<>();
        songTitleList = new ArrayList<>();
        songIds = new ArrayList<>();
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
        songIds.add((song.getResId()));
    }


    public ArrayList<Integer> getSongIds() {
        return songIds;
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
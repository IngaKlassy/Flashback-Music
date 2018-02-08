package com.example.stephanie.flashbackmusic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephanie on 2/6/2018.
 */

public class Album {
    ArrayList<Song> songList;

    void addSong (Song song) {
        // make sure the song isn't in the songList already?
        // add the song to the array list songList
    }

    void playAlbum(Album album) {
        for(int i = 0; i < album.songList.size(); i++) {
            // play the song at i
        }
    }
}
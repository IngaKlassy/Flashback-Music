package JUnitTests;

import android.support.test.rule.ActivityTestRule;

import com.example.stephanie.flashback_music.Album;
import com.example.stephanie.flashback_music.MainActivity;
import com.example.stephanie.flashback_music.Player;
import com.example.stephanie.flashback_music.Song;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by ingaklassy on 2/18/18.
 */

public class UnitTests {
    Song song, song1;
    Album album;
    Player player;
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        song = new Song("Single Ladies", "Formation", "Beyonce", 56);
        song1 = new Song("Dance", "Formation", "Beyonce", 72);
        album = new Album("Formation", "Beyonce");
        player = new Player();
    }

    @Test
    public void testSongClass() {
        String name = song.getSongTitle();
        assertEquals(name, "Single Ladies");
        assertEquals(song.getAlbumTitle(), "Formation");
        assertEquals(song.getAlbumTitle(), "Formation");
    }

    @Test
    public void testAlbumClassConstructor() {
        assertEquals("Formation", album.getAlbumTitle());
        assertEquals("Beyonce", album.getAlbumArtist());
    }

    @Test
    public void testAlbumAddSongs() {
        album.addSong(song);
        album.addSong(song1);

        ArrayList<Integer> songIds = album.getSongIds();
        List<String> songTitles = album.returnSongTitles();
        int i = songIds.get(0);
        int j = songIds.get(1);
        String title1 = songTitles.get(0);
        String title2 = songTitles.get(1);
        assertEquals(i, 56);
        assertEquals(j, 72);
        assertEquals(title1, "PLAY ALBUM");
        assertEquals(title2, "Single Ladies");
    }

    @Test
    public void testPlayer() {
        player.add("Single Ladies", "Formation", "Beyonce", 56);
        player.add("Dance", "My album", "Beyonce", 72);
        ArrayList<Album> albums = player.getListOfAlbumObs();
        assertEquals(albums.get(0).getAlbumTitle(), "Formation");
        assertEquals(albums.get(1).getAlbumTitle(), "My album");
    }
}

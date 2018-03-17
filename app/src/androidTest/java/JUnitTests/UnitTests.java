package JUnitTests;

import android.location.Location;
import android.location.LocationManager;
import android.support.test.espresso.proto.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import com.example.stephanie.flashback_music.Album;
import com.example.stephanie.flashback_music.MainActivity;
import com.example.stephanie.flashback_music.Player;
import com.example.stephanie.flashback_music.Song;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by ingaklassy on 2/18/18.
 */

public class UnitTests {
    Song song, song1, song2, song3;
    Album album, album1;
    Player player;
    Calendar c;
    Location currentLocation;
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        song = new Song("Single Ladies", "Formation", "Beyonce", "http://beyonce.com/singleladies.mp3");
        song1 = new Song("Dance", "Formation", "Beyonce", "http://beyonce.com/dance.mp3");
        song2 = new Song(null, null, null, "http://beyonce.com/song2.mp3");

        album = new Album("Formation", "Beyonce");
        album1 = new Album(null, null);
        player = new Player();

        try {
            currentLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation == null){
                currentLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }catch(Exception e){
        }

        c = Calendar.getInstance();
        song.getWhoHasPlayedSong().add("Test Friend");
    }

    @Test
    public void testSongAttributes() {
        assertEquals(song.getSongTitle(), "Single Ladies");
        assertEquals(song.getArtistName(), "Beyonce");
        assertEquals(song.getURL(), "http://beyonce.com/singleladies.mp3");
        assertEquals(song.getURI(), null);
        assertEquals(song.getNeutralStatus(), true);
    }

    @Test
    public void testNullSongAttributes() {
        assertEquals(song2.getSongTitle(), "Unknown Title");
        assertEquals(song2.getArtistName(), "Unknown Artist");
        assertEquals(song2.getURL(), "http://beyonce.com/song2.mp3");
        assertEquals(song2.getURI(), null);
        assertEquals(song2.getNeutralStatus(), true);
    }

    @Test
    public void testTimeAndDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat ft2 = new SimpleDateFormat("HH:mm");
        Date date = cal.getTime();
        String td = " on " + ft.format(date) + " at " + ft2.format(date);
        song.setTimeAndDate(cal);
        assertEquals(song.getTimeAndDate(), td);
    }

    @Test
    public void testStatus() {
        song.setNeutralTrue();
        assertEquals(song.getNeutralStatus(), true);
        assertEquals(song.getFavoriteStatus(), false);
        assertEquals(song.getDislikeStatus(), false);

        song.setFavoriteTrue();
        assertEquals(song.getNeutralStatus(), false);
        assertEquals(song.getFavoriteStatus(), true);
        assertEquals(song.getDislikeStatus(), false);

        song.setDislikeTrue();
        assertEquals(song.getNeutralStatus(), false);
        assertEquals(song.getFavoriteStatus(), false);
        assertEquals(song.getDislikeStatus(), true);
    }

    @Test
    public void testUpdate() {
        Calendar cal = Calendar.getInstance();
        Location loc = null;
        song.update(cal, loc, "Bill");
        assertEquals(song.getWhoPlayedSongLast(), " by Bill");
    }

    @Test
    public void testAlbumClassConstructor() {
        assertEquals("Formation", album.getAlbumTitle());
        assertEquals("Beyonce", album.getAlbumArtist());
        assertEquals("Unknown Album", album1.getAlbumTitle());
        assertEquals("Unknown Artist", album1.getAlbumArtist());
    }

    @Test
    public void testAlbumAddSongs() {
        album.addSong(song);
        album.addSong(song1);
        album.addSong(song2);

        List<String> songTitles = album.returnSongTitles();
        assertEquals("PLAY ALBUM", songTitles.get(0));
        assertEquals("Single Ladies", songTitles.get(1));
        assertEquals("Dance", songTitles.get(2));
        assertEquals("Unknown Title", songTitles.get(3));

    }

    @Test
    public void testPlayer() {
        player.add("Single Ladies", "Formation", "Beyonce", "http://beyonce.com/singleladies.mp3", null);
        player.add("Dance", "My album", "Beyonce", "http://beyonce.com/dance.mp3", null);
        player.add("Play", null, "50 Cent", "http://50cent.com/play.mp3", null);
        ArrayList<Album> albums = player.getListOfAlbumObs();
        assertEquals(albums.get(0).getAlbumTitle(), "Formation");
        assertEquals(albums.get(1).getAlbumTitle(), "My album");
        assertEquals(albums.get(2).getAlbumTitle(), "Unknown Album");
        assertEquals(albums.get(0).getAlbumArtist(), "Beyonce");
        assertEquals(albums.get(1).getAlbumArtist(), "Beyonce");
        assertEquals(albums.get(2).getAlbumArtist(), "50 Cent");
    }

    @Test
    public void testLocationPts(){
        song.update(c, currentLocation, "Matthias");

        int res = player.setLocationPoints(song);
        assertEquals(3, res);

        Location wrongLoc = new Location("fakeProv");
        wrongLoc.setLatitude(40000);
        wrongLoc.setLongitude(23489762);
        song1.update(c, wrongLoc, "Matthias");
        int wrongRes = player.setLocationPoints(song1);
        assertNotEquals(3, wrongRes);
    }

    @Test
    public void testRecentlyPlayedPts(){
        System.out.println("Testing setRecentlyPlayedPoints() function...");

        int result;
        song.update(c, currentLocation, "Joel");
        song1.update(c, currentLocation, "Joel");

        result = player.setRecentlyPlayedPoints(song);
        assertEquals(2, result);
        assertNotEquals(0, result);

        song1.getCalendar().set(2019, 12,5);

        result = player.setRecentlyPlayedPoints(song1);
        assertEquals(0, result);
        assertNotEquals(2, result);
    }

    @Test
    public void testFriendPts(){
        int result = player.setFriendPlayedPoints(song);
        System.out.println("Testing setFriendPoints function....");
        assertEquals(result, 1);
    }
}

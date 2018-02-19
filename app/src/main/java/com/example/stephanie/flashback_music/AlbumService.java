package com.example.stephanie.flashback_music;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AlbumService extends Service {
    Intent intent;

    MediaPlayer mp;

    public AlbumService() {
    }

    final class myThread implements Runnable {
        int startId;
        Intent intent;
        public myThread(int startId, Intent intent) {
            this.startId = startId;
            this.intent = intent;
        }

        @Override
        public void run() {
            mp = new MediaPlayer();

            Album album = (Album) intent.getSerializableExtra("Album");

            ArrayList<Song> trackList = album.getSongObs();

            for(Song s : trackList)
            {
                playSong(s);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(AlbumService.this, "Service Started", Toast.LENGTH_LONG).show();

        this.intent = intent;

        Thread thread = new Thread(new myThread(startId, intent));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }


    public void playSong(final Song song)
    {
        final Calendar calendar = Calendar.getInstance();
        final Location location = new Location("La Jolla");

        if(mp != null)
        {
            mp.release();
        }

        mp = MediaPlayer.create(AlbumService.this, song.getResId());
        mp.start();

        while(mp.isPlaying())
        {}

        song.update(calendar, location);
        mp.reset();
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        Toast.makeText(AlbumService.this, "Service Stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}

package com.example.stephanie.flashback_music;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AlbumService extends Service {
    public AlbumService() {
    }

    final class myThread implements Runnable {
        int startId;
        public myThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(AlbumService.this, "Service Started", Toast.LENGTH_LONG).show();

        Thread thread = new Thread(new myThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(AlbumService.this, "Service Stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}

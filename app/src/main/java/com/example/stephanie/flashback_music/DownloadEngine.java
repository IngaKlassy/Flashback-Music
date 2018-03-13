package com.example.stephanie.flashback_music;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


/**
 * Created by looki on 3/11/2018.
 */

public class DownloadEngine {
    DownloadManager downloadManager;
    String currentURL;
    Context activityContext;

    public DownloadEngine(DownloadManager dm) {
        downloadManager = dm;
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Download Successful", Toast.LENGTH_SHORT).show();

            addNewDownload();
        }
    };


    public void tryToDownload(Context context, String enteredURL) {
        activityContext = context;
        currentURL = enteredURL;

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if(enteredURL.equals("")) {
            Toast.makeText(context, "Nothing entered", Toast.LENGTH_SHORT).show();
            return;
        }

        int indexOfHTTP = enteredURL.indexOf("//");

        if(indexOfHTTP == -1) {
            Toast.makeText(context, "Invalid entry", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlPrefix = enteredURL.substring(0, indexOfHTTP);

        if(!urlPrefix.equals("http:") && !urlPrefix.equals("https:")) {
            Toast.makeText(context, "Invalid entry, URL must contain http:// or https://", Toast.LENGTH_SHORT).show();
            return;
        }

        Download();
    }


    private long Download() {
        long downloadReference;

        Uri urlToUri = Uri.parse(currentURL);

        int lastSlashIndex = currentURL.lastIndexOf("/");
        int filenameLength = currentURL.length();
        String filename = currentURL.substring(lastSlashIndex + 1, filenameLength);

        DownloadManager.Request request = new DownloadManager.Request(urlToUri);

        //Setting title of request
        request.setTitle(filename);

        //Setting description of request
        request.setDescription("Mp3 download using DownloadManager");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);//"/Download/");

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }


    public void addNewDownload() {
        //RETRIEVING FILENAME TO MATCH
        int lastSlashIndex = currentURL.lastIndexOf("/");
        int filenameLengthX = currentURL.length();
        String filenameX = currentURL.substring(lastSlashIndex + 1, filenameLengthX);

        //PULLING ALL DOWNLOADS
        File downloadsDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
        File[] downloads = downloadsDirectory.listFiles();

        ArrayList<File> mp3Downloads = new ArrayList<>();

        for(File f: downloads) {
            String filename = f.getName();
            int lastPeriodIndex = filename.lastIndexOf(".");

            if(lastPeriodIndex == -1) {
                break;
            }

            int filenameLength = filename.length();
            String extension = filename.substring(lastPeriodIndex, filenameLength);

            if(extension.equals(".mp3")) {
                mp3Downloads.add(f);
            }
        }


        File match = null;

        for(File f: mp3Downloads) {
            if(f.getName().equals(filenameX)) {
                match = f;
                break;
            }
        }

        if(match == null) {
            return;
        }

        MediaMetadataRetriever metaRetriever2 = new MediaMetadataRetriever();
        metaRetriever2.setDataSource(match.getAbsolutePath());

        String songTitle = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String songAlbum = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String songArtist = metaRetriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        Uri uri = Uri.parse(match.getAbsolutePath());

        MainActivity.mainActivityPlayerOb.add(filenameX, songAlbum, songArtist, currentURL, uri);
        Toast.makeText(activityContext, "New song created", Toast.LENGTH_SHORT).show();

        return;
    }
}

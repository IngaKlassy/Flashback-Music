package com.example.stephanie.flashback_music;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;


/**
 * Created by looki on 3/11/2018.
 */

public class DownloadEngine {
    DownloadManager downloadManager;

    public DownloadEngine(DownloadManager dm) {
        downloadManager = dm;
    }


    public void tryToDownload(Context context, String enteredURL) {
        if(enteredURL.equals("")) {
            Toast.makeText(context, "Nothing entered", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri urlToUri = Uri.parse(enteredURL);

        String uriAsString = urlToUri.toString();
        int indexOfHTTP = uriAsString.indexOf("//");

        if(indexOfHTTP == -1) {
            Toast.makeText(context, "Invalid entry", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlName = uriAsString.substring(0, indexOfHTTP);
        if(!urlName.equals("http:")) {
            Toast.makeText(context, "Invalid entry, URL must contain http://", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadData(context, urlToUri);
    }


    private long DownloadData (Context context, Uri uri) {

        long downloadReference;

        String uriAsString = uri.toString();
        int lastSlashIndex = uriAsString.lastIndexOf("/");
        int filenameLength = uriAsString.length();
        String filename = uriAsString.substring(lastSlashIndex + 1, filenameLength);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(filename);

        //Setting description of request
        request.setDescription("Mp3 download using DownloadManager");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Download/");

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }
}

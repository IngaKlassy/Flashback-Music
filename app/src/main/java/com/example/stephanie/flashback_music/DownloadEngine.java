package com.example.stephanie.flashback_music;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by looki on 3/11/2018.
 */

public class DownloadEngine {
    DownloadManager downloadManager;

    public DownloadEngine(DownloadManager dm) {
        String folder_main = "VibeMusic";

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Download/", folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        downloadManager = dm;
    }

    public void tryToDownload(Context context, String enteredURI) {
        if(enteredURI.equals("")) {
            Toast.makeText(context, "Nothing entered", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri urlToUri = Uri.parse(enteredURI);
        DownloadData(context, urlToUri);
    }

    private long DownloadData (Context context, Uri uri) {


        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Download/VibeMusic/");

        long refid = downloadManager.enqueue(request);

        Toast.makeText(context, "No Errors", Toast.LENGTH_SHORT).show();



        return refid;
    }
}

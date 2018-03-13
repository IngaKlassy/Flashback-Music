package com.example.stephanie.flashback_music;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by looki on 3/9/2018.
 */

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {
    protected String doInBackground(String... urls){
        String content = "", line;

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            Log.w("BackgroundTask", "url: "+ url +" content: "+ content);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        FlashbackActivity.getResult = content;


        return content;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // update your UI here
        Toast.makeText(FlashbackActivity.mainContext, result, Toast.LENGTH_LONG).show();
    }
}





/*





<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.example.stephanie.flashback_music.MainActivity">

<LinearLayout
    android:id="@+id/overall_layout"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:layout_margin="0dp"
    tools:context=".MainActivity">

    <android.widget.Toolbar
        android:layout_marginTop="85dp"
        android:id="@+id/my_toolbar"
        android:layout_width="match_parent"
        android:layout_height="215dp"
        android:background="#618670"
        android:elevation="4dp"
        android:popupTheme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        android:theme="@style/ThemeOverlay.AppCompat.ActionBar" >

        <LinearLayout
            android:id="@+id/title_layout"
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:orientation="vertical"
            android:layout_margin="0dp"
            tools:context=".MainActivity">

            <TextView
                android:id="@+id/toolbar_Title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/app_name"
                android:textSize="25sp"
                android:textStyle="bold"
                app:layout_constraintTop_toTopOf="@id/my_toolbar"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent" />

            <TextView
                android:id="@+id/toolbar_Mode"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/regular_mode"
                app:layout_constraintTop_toBottomOf="@+id/toolbar_Title"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"/>

            <TextView
                android:id="@+id/toolbar_Instructions"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/regular_message"
                app:layout_constraintTop_toBottomOf="@+id/toolbar_Mode"

                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"/>

            <TextView
                android:id="@+id/songInfo"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:layout_constraintTop_toBottomOf="@+id/toolbar_Instructions"
                app:layout_constraintBottom_toBottomOf="@+id/my_toolbar"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                android:text="" />


            <LinearLayout
                android:id="@+id/url_download"
                xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="70dp"
                android:gravity="center"
                android:orientation="vertical"
                android:layout_margin="0dp"
                tools:context=".MainActivity">

                <TextView
                    android:id="@+id/download_instructions"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Enter a songs download URL and then hit ENTER"
                    android:textSize="15sp"
                    android:textStyle="bold"
                    app:layout_constraintTop_toBottomOf="@id/songInfo"
                    app:layout_constraintLeft_toLeftOf="parent"
                    app:layout_constraintRight_toRightOf="parent" />

                <LinearLayout
                    android:id="@+id/url_download_2"
                    xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:tools="http://schemas.android.com/tools"
                    android:layout_width="match_parent"
                    android:layout_height="40dp"
                    android:gravity="center"
                    android:orientation="horizontal"
                    android:layout_margin="0dp"
                    tools:context=".MainActivity">

                    <EditText
                        android:id="@+id/enter_url"
                        android:layout_width="200dp"
                        android:layout_height="wrap_content"
                        android:background="@android:color/white"
                        app:layout_constraintRight_toLeftOf="@+id/enter_button"
                        app:layout_constraintTop_toBottomOf="@+id/download_instructions" />


                    <Button
                        android:id="@+id/enter_button"
                        android:text="ENTER"
                        android:layout_width="70dp"
                        android:layout_height="wrap_content"
                        app:layout_constraintLeftt_toRighttOf="@+id/enter_url"
                        app:layout_constraintTop_toBottomOf="@+id/download_instructions"
                        app:layout_constraintBottom_toTopOf="@+id/vibe_switch" />

                </LinearLayout>

            </LinearLayout>





            <Button
                android:id="@+id/DownloadButton"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:layout_constraintTop_toBottomOf="@+id/songInfo"
                app:layout_constraintBottom_toBottomOf="@+id/vibe_switch"
                android:text="DOWNLOAD SONGS"
                />

            <Switch
                android:id="@+id/vibe_switch"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="right"
                android:checked="false"
                android:text="Switch to Vibe mode "
                android:textSize="15dp"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

        </LinearLayout>
    </android.widget.Toolbar>


    <ExpandableListView
        android:id="@+id/songlist"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintBottom_toTopOf="@+id/bottomToolbar"
        app:layout_constraintTop_toBottomOf="@id/my_toolbar" />


    <android.widget.Toolbar
            android:id="@+id/bottomToolbar"
            style="?android:buttonBarStyle"
            android:layout_width="fill_parent"
            android:layout_height="85dp"
            android:layout_marginBottom="86dp"
            android:background="#618670"
            android:minHeight="?attr/actionBarSize"
            android:popupTheme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"

            android:theme="@style/ThemeOverlay.AppCompat.ActionBar">


            <LinearLayout
                android:id="@+id/toolbarmenucontainer"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="horizontal"
                android:weightSum="3">

                <ImageView
                    android:id="@+id/status"
                    android:layout_width="90dp"
                    android:layout_height="90dp"
                    android:src="@drawable/plus" />

                <ImageView
                    android:id="@+id/play"
                    android:layout_width="90dp"
                    android:layout_height="90dp"
                    android:src="@drawable/play" />

                <ImageView
                    android:id="@+id/pause"
                    android:layout_width="90dp"
                    android:layout_height="90dp"
                    android:src="@drawable/pause_green" />

                <ImageView
                    android:id="@+id/next"
                    android:layout_width="90dp"
                    android:layout_height="90dp"
                    android:src="@drawable/skip" />

            </LinearLayout>
        </android.widget.Toolbar>


</LinearLayout>

</android.support.constraint.ConstraintLayout>

 */
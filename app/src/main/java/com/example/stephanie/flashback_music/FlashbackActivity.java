package com.example.stephanie.flashback_music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.stephanie.flashback_music.MainActivity.mainActivityPlayerOb;

public class FlashbackActivity extends AppCompatActivity {

    CompoundButton flashbackSwitch;

    ArrayList<TextView> textviews;

    TextView songName;
    TextView songAlbum;
    TextView songArtist;
    TextView lastPlayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        songName = (TextView) findViewById(R.id.song_title);
        songAlbum = (TextView) findViewById(R.id.song_album);
        songArtist = (TextView) findViewById(R.id.song_artist);
        lastPlayed = (TextView) findViewById(R.id.last_played);

        textviews = new ArrayList<>();
        textviews.add(songName);
        textviews.add(songAlbum);
        textviews.add(songArtist);
        textviews.add(lastPlayed);

        mainActivityPlayerOb.prioritizeSongsPlayed();

        if(mainActivityPlayerOb.getVibeModePlaylist().isEmpty())
        {
            displayNoSongsToPlay(textviews);
        }
        else {
            mainActivityPlayerOb.vibeModePlay(FlashbackActivity.this, textviews);

            //BOTTOM BAR SETUP*****
            ImageView statusButton = findViewById(R.id.status);
            ImageView playButton = findViewById(R.id.play);
            ImageView pauseButton = findViewById(R.id.pause);
            ImageView nextButton = findViewById(R.id.next);

            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivityPlayerOb.pause();
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivityPlayerOb.play();
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivityPlayerOb.next(FlashbackActivity.this, textviews);
                }
            });
        }


        flashbackSwitch = (CompoundButton) findViewById(R.id.flashback_switch);

        flashbackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mainActivityPlayerOb.switchMode();
                if (b) {
                    mainActivityPlayerOb.stop();
                    finish();
                }
            }
        });
    }

    public void displayNoSongsToPlay(ArrayList<TextView> textviews) {
        textviews.get(0).setText("No songs played");

        for(int i = 1; i < textviews.size(); i++) {
         textviews.get(i).setText("");
        }
    }
}
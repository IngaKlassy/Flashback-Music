package com.example.stephanie.flashback_music;

import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.datatype.Duration;

import static com.example.stephanie.flashback_music.MainActivity.mainActivityPlayerOb;

public class FlashbackActivity extends AppCompatActivity {

    CompoundButton flashbackSwitch;
    Button playPause;
    TextView songName;
    TextView songAlbum;
    TextView songArtist;
    TextView lastPlayed;

    boolean song_ended;
    boolean nothingToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        flashbackSwitch = (CompoundButton) findViewById(R.id.flashback_switch);
        flashbackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false)
                    finish();
            }
        });

        playPause = (Button) findViewById(R.id.play_pause);
        songName = (TextView) findViewById(R.id.song_title);
        songAlbum = (TextView) findViewById(R.id.song_album);
        songArtist = (TextView) findViewById(R.id.song_artist);
        lastPlayed = (TextView) findViewById(R.id.last_played);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playPause.getText().toString().equals("Play")) {
                    playPause.setText("Pause");
                    playQueue();
                    if (nothingToPlay == true) {
                        playPause.setText("Play");
                        return;
                    }
                }
                else if (playPause.getText().toString().equals("Pause")){
                    playPause.setText("Play");
                    pauseQueue();
                }
            }
        });

        mainActivityPlayerOb.getMp().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                song_ended = true;
                playQueue();
            }
        });
    }

    public void playQueue() {
        if (song_ended == true) {
            Song song = mainActivityPlayerOb.songPriorities.poll();
            song_ended = false;

            if (song == null) {
                Toast.makeText(getBaseContext(), "Nothing to play!", Toast.LENGTH_SHORT).show();
                nothingToPlay = true;
                return;
            }

            songName.setText(song.getName());
            songAlbum.setText(song.getAlbumTitle());
            songArtist.setText(song.getArtistName());
            lastPlayed.setText("La Jolla");
            mainActivityPlayerOb.playSong(FlashbackActivity.this, song.getResId());
        }
        else {
            mainActivityPlayerOb.getMp().start();
        }
    }

    public void pauseQueue() {
        mainActivityPlayerOb.getMp().pause();
    }
}
package com.example.stephanie.flashback_music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

public class FlashbackActivity extends AppCompatActivity {

    CompoundButton flashbackSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        Toast.makeText(getBaseContext(), "Entered Flashback Mode!", Toast.LENGTH_LONG).show();

        flashbackSwitch = (CompoundButton) findViewById(R.id.flashback_switch);
        flashbackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false)
                    finish();
            }
        });
    }
}
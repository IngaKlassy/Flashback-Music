package com.example.stephanie.flashback_music;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;


public class Flashback_Activity extends AppCompatActivity {

    OnSwipeTouchListener onSwipeTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback_);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);

        onSwipeTouchListener = new OnSwipeTouchListener(Flashback_Activity.this) {
            @Override
            public void onSwipeRight() {
                finish();
            }
        };
    }

}


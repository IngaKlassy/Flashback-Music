package com.example.stephanie.flashback_music;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.example.stephanie.flashback_music.MainActivity.mainActivityPlayerOb;



public class FlashbackActivity extends AppCompatActivity {
    private static final String TAG = "FLASHBACK";

    private static final int RC_SIGN_IN = 111;

    static String getResult;
    static Context mainContext;

    CompoundButton flashbackSwitch;

    ArrayList<TextView> textviews;

    QueueDialogFragment qdf;

    TextView songName;
    TextView songAlbum;
    TextView songArtist;
    TextView lastPlayed;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount myAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mainContext = getApplicationContext();


        songName = (TextView) findViewById(R.id.song_title);
        songAlbum = (TextView) findViewById(R.id.song_album);
        songArtist = (TextView) findViewById(R.id.song_artist);
        lastPlayed = (TextView) findViewById(R.id.last_played);

        textviews = new ArrayList<>();
        textviews.add(songName);
        textviews.add(songAlbum);
        textviews.add(songArtist);
        textviews.add(lastPlayed);

        mainActivityPlayerOb.prioritizeSongs();

        qdf = new QueueDialogFragment();

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

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signOut();
                        signIn();
                        break;
                    // ...
                }

            }
        });

        Button queueButton = findViewById(R.id.showQueue);
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qdf.show(getFragmentManager(),"NoticeDialogFrag");
            }
        });



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
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Toast.makeText(getApplicationContext(), "hello "+account.getDisplayName(), Toast.LENGTH_LONG).show();

            GetUrlContentTask result = new GetUrlContentTask();
            result.execute("https://people.googleapis.com/v1/{resourceName=people/me}/connections");
        }
        else{
            Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_LONG).show();
        }
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //
                }
            });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            myAccount = account;

            GetUrlContentTask result = new GetUrlContentTask();
            result.execute("https://people.googleapis.com/v1/{resourceName=people/me}/connections");
            
            Toast.makeText(getApplicationContext(), account.getDisplayName(), Toast.LENGTH_LONG).show();

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_LONG).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            e.printStackTrace();
        }
    }


    public void displayNoSongsToPlay(ArrayList<TextView> textviews) {
        textviews.get(0).setText("No songs played");

        for(int i = 1; i < textviews.size(); i++) {
         textviews.get(i).setText("");
        }
    }

    public static class QueueDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String[] k = new String[25];
            for (int ctr =0; ctr<25; ctr++){
                k[ctr]= "Song "+(ctr+1);
            }

            builder.setTitle("Coming up next")
                    .setItems(k, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
package com.example.unquote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

public class GameOverActivity extends AppCompatActivity {

    private VideoView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_over);

        ImageButton playPauseButton = findViewById(R.id.music_toggle_game_over);
        Button playAgainButton = findViewById(R.id.play_again_button);
        Button mainMenuButton = findViewById(R.id.main_menu_button);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        TextView score = findViewById(R.id.score);
        TextView numCorrect = findViewById(R.id.numberOfCorrect);
        TextView numIncorrect = findViewById(R.id.numberOfIncorrect);
        TextView accuracy = findViewById(R.id.accuracy);
        playAgainButton.startAnimation(pulseAnimation);
        mainMenuButton.startAnimation(pulseAnimation);
        ratingBar.startAnimation(pulseAnimation);

        double totalCorrect = GameActivity.totalCorrect;
        double totalQuestions = GameActivity.totalQuestions;

        score.setText(String.valueOf((int)(totalCorrect*100)));
        numCorrect.setText(String.valueOf((int)(totalCorrect)));
        numIncorrect.setText(String.valueOf((int)(totalQuestions-totalCorrect)));
        accuracy.setText(String.valueOf(Math.round((totalCorrect/totalQuestions)*100)) + "%");
        ratingBar.setRating((float)((totalCorrect/totalQuestions)*5));


        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GameActivity.class));
            }
        });

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        if (MainActivity.musicPaused) {
            playPauseButton.setImageResource(R.drawable.volume_off_24px);
        } else {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mediaPlayer.isPlaying()) {
                    playPauseButton.setImageResource(R.drawable.volume_off_24px);
                    MainActivity.mediaPlayer.pause();
                    MainActivity.musicPaused = true;
                } else {
                    playPauseButton.setImageResource(R.drawable.volume_up_24px);
                    MainActivity.mediaPlayer.start();
                    MainActivity.mediaPlayer.setLooping(true);
                    MainActivity.musicPaused = false;
                }
            }
        });

        background = findViewById(R.id.gameOverVideoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_over_background);
        background.setVideoURI(videoUri);

        // Start playing the video in a loop
        background.start();
        background.setOnPreparedListener(mp -> mp.setLooping(true));


    }



    @Override
    public void onPause() {
        super.onPause();
        background.pause();
        MainActivity.mediaPlayer.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        background.start();

        if (!MainActivity.musicPaused) {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }
    }
}

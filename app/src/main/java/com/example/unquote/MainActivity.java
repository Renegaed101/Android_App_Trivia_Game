package com.example.unquote;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.VideoView;
import android.net.Uri;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;


public class MainActivity extends AppCompatActivity {
    public static MediaPlayer mediaPlayer;
    private VideoView videoView;
    public static boolean musicPaused = false;

    public static SoundPool soundPool;
    public static int soundCorrectAnswer;
    public static int soundWrongAnswer;
    public static int soundNextQuestion;
    public static int soundTapAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //To clean up prev running audio
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setAudioAttributes(audioAttributes).setMaxStreams(4); // Maximum simultaneous streams
        soundPool = builder.build();

        soundCorrectAnswer = soundPool.load(this,R.raw.sound_correct_answer,1);
        soundWrongAnswer = soundPool.load(this,R.raw.sound_wrong_answer,1);
        soundNextQuestion = soundPool.load(this,R.raw.sound_next_question,1);
        soundTapAnswer = soundPool.load(this,R.raw.sound_tap_button,1);

        Button startButton = findViewById(R.id.start_button);
        mediaPlayer = MediaPlayer.create(this, R.raw.space_pirates);
        ImageButton playPauseButton = findViewById(R.id.music_toggle_start);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);


        startButton.startAnimation(pulseAnimation);

        if (musicPaused) {
            playPauseButton.setImageResource(R.drawable.volume_off_24px);
        }

        // Initialize the VideoView and set the video source
        videoView = findViewById(R.id.startMenuVideoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_background);
        videoView.setVideoURI(videoUri);

        // Start playing the video in a loop
        videoView.start();
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGame = new Intent (getApplicationContext(),GameOptionsActivity.class);
                startActivity(startGame);
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playPauseButton.setImageResource(R.drawable.volume_off_24px);
                    mediaPlayer.pause();
                    musicPaused = true;
                } else {
                    playPauseButton.setImageResource(R.drawable.volume_up_24px);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    musicPaused = false;
                }
            }
        });

        if (!musicPaused) {
            mediaPlayer.setVolume(0.1f,0.1f);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        ImageView titleImage = findViewById(R.id.titleImage);

        // Create a scale animation
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(titleImage, "scaleX", 1.1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(titleImage, "scaleY", 1.1f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(titleImage, "scaleX", 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(titleImage, "scaleY", 1.0f);

        // Create an AnimatorSet for the animation sequence
        AnimatorSet scaleAnim = new AnimatorSet();
        scaleAnim.play(scaleUpX).with(scaleUpY);
        scaleAnim.play(scaleDownX).with(scaleDownY).after(scaleUpX);

        scaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnim.setDuration(1000); // Set the duration in milliseconds

        scaleAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation ended
                // Start your main activity or other actions here

                // Restart the animation when it ends
                scaleAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Animation canceled
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Animation repeated
                // This method will be called when the animation completes and restarts
            }
        });

        scaleAnim.start();

    }

    @Override
    protected void onPause(){
     super.onPause();
     videoView.pause();
     mediaPlayer.pause();

    }

    @Override
    protected void onResume(){
        super.onResume();
        videoView.start();
        if (!musicPaused) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (soundPool != null) {
            soundPool.release();
        }
    }

}


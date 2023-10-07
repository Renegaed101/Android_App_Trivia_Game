package com.example.unquote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameOptionsActivity extends AppCompatActivity {

    private List<Category> categoryList;
    private VideoView background;
    public static int numCategories;
    public static int numberQuestions = 0;
    public static List<Category> selectedCategories;
    private String cantStartMessage;
    private Animation pulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_options);

        ImageButton playPauseButton = findViewById(R.id.music_toggle_game_options);
        Button questionsButton20 = findViewById(R.id.questionsButton20);
        Button questionsButton40 = findViewById(R.id.questionsButton40);
        Button questionsButton60 = findViewById(R.id.questionsButton60);
        Button startGameButton = findViewById(R.id.startGameButton);
        TextView categoriesText = findViewById(R.id.selectCategoriesTextView);
        pulse = AnimationUtils.loadAnimation(this,R.anim.pulse_animation);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startcheck()) {
                    selectedCategories = new ArrayList<>();
                    for (Category i: categoryList) {
                        if (i.included) {
                            selectedCategories.add(i);
                        }
                    }
                    Intent startGame = new Intent (getApplicationContext(),GameActivity.class);
                    startActivity(startGame);
                    MainActivity.soundPool.play(MainActivity.soundNextQuestion, 1.5f, 1.5f, 1, 0, 1.0f);

                } else {
                    AlertDialog.Builder gameOverDialogBuilder = new AlertDialog.Builder(GameOptionsActivity.this);
                    gameOverDialogBuilder.setTitle("Oops");
                    gameOverDialogBuilder.setMessage(cantStartMessage);
                    MainActivity.soundPool.play(MainActivity.soundCantStartGame, 1.0f, 1.0f, 1, 0, 1.0f);
                    gameOverDialogBuilder.create().show();
                }
            }
        });


        if (MainActivity.musicPaused) {
            playPauseButton.setImageResource(R.drawable.volume_off_24px);
        } else {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }

        background = findViewById(R.id.gameOptionsVideoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_background_dark);
        background.setVideoURI(videoUri);

        // Start playing the video in a loop
        background.start();
        background.setOnPreparedListener(mp -> mp.setLooping(true));

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


            // Set a click listener for each button
            questionsButton20.setOnClickListener(view -> {
                setButtonSelected(questionsButton20);
                setButtonNotSelected(questionsButton40);
                setButtonNotSelected(questionsButton60);
                numberQuestions = 20;
                categoriesText.setText("Select at least 1 category");
            });

            questionsButton40.setOnClickListener(view -> {
                setButtonSelected(questionsButton40);
                setButtonNotSelected(questionsButton20);
                setButtonNotSelected(questionsButton60);
                numberQuestions = 40;
                categoriesText.setText("Select at least 2 categories");
            });

            questionsButton60.setOnClickListener(view -> {
                setButtonSelected(questionsButton60);
                setButtonNotSelected(questionsButton20);
                setButtonNotSelected(questionsButton40);
                numberQuestions = 60;
                categoriesText.setText("Select at least 3 categories");
            });

            // Initially, make one button visible and the others faded
            setButtonNotSelected(questionsButton20);
            setButtonNotSelected(questionsButton40);
            setButtonNotSelected(questionsButton60);




        categoryList = new ArrayList<>();
        categoryList.add(new Category("Animals",R.raw.animals,R.drawable.animal19,R.color.selectedButtonColor,R.color.black,"animal"));
        categoryList.add(new Category("Entertainment",R.raw.entertainment,R.drawable.categoryentertainment,R.color.magenta,R.color.white,"mov"));
        categoryList.add(new Category("General Knowledge",R.raw.general,R.drawable.categorygeneralknowledge,R.color.white, R.color.black,"gen"));
        categoryList.add(new Category("Geography",R.raw.geography,R.drawable.categorygeography,R.color.Green,R.color.white,"geo"));
        categoryList.add(new Category("Food",R.raw.food,R.drawable.categoryfood,R.color.orange,R.color.white,"food"));
        categoryList.add(new Category("History",R.raw.history,R.drawable.categoryhistory,R.color.grey,R.color.Green,"hist"));
        categoryList.add(new Category("Science",R.raw.science,R.drawable.categoryscience,R.color.white,R.color.blue,"sci"));
        categoryList.add(new Category("Music",R.raw.music,R.drawable.categorymusic,R.color.purple,R.color.white,"music"));
        categoryList.add(new Category("Sports",R.raw.sports,R.drawable.categorysports,R.color.teal,R.color.white,"sports"));
        categoryList.add(new Category("Technology",R.raw.technology,R.drawable.categorytechnology,R.color.blue,R.color.white,"tech"));

        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        CategoryAdapter adapter = new CategoryAdapter(categoryList,this);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        categoryRecyclerView.setAdapter(adapter);

    }

    private boolean startcheck() {
        setNumCategories();
        if (numCategories < 1 || numberQuestions < 1) {
            cantStartMessage = "Please select the number of questions as well as at least one category.";
            return false;
        } else if (numberQuestions > 40 && numCategories < 3) {
            cantStartMessage = "For 60 questions, please select at least three categories.";
            return false;
        } else if (numberQuestions > 20 && numCategories < 2) {
             cantStartMessage = "For 40 questions, please select at least two categories.";
            return false;
        } else {
            return true;
        }
    }

    private void setNumCategories() {
        numCategories = 0;
        for (Category i: categoryList) {
            if (i.included) {
                numCategories++;
            }
        }
    }

    private void setButtonSelected(Button button) {
        button.setBackgroundColor(getColor(R.color.correctButtonColor));
        button.setTextColor(getColor(R.color.white));
        button.setAlpha(1.0f);
        button.startAnimation(pulse);
        MainActivity.soundPool.play(MainActivity.soundTapAnswer, 1.0f, 1.0f, 1, 0, 1.0f);

    }

    private void setButtonNotSelected(Button button) {
        button.setBackgroundColor(getColor(R.color.grey));
        button.setTextColor(getColor(R.color.white));
        button.setAlpha(0.8f);
        button.clearAnimation();
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

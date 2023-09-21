package com.example.unquote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
                } else {
                    AlertDialog.Builder gameOverDialogBuilder = new AlertDialog.Builder(GameOptionsActivity.this);
                    gameOverDialogBuilder.setTitle("Oops");
                    gameOverDialogBuilder.setMessage(cantStartMessage);
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
                setButtonVisible(questionsButton20);
                setButtonFaded(questionsButton40);
                setButtonFaded(questionsButton60);
                numberQuestions = 20;
                categoriesText.setText("Select at least 1 category");
            });

            questionsButton40.setOnClickListener(view -> {
                setButtonVisible(questionsButton40);
                setButtonFaded(questionsButton20);
                setButtonFaded(questionsButton60);
                numberQuestions = 40;
                categoriesText.setText("Select at least 2 categories");
            });

            questionsButton60.setOnClickListener(view -> {
                setButtonVisible(questionsButton60);
                setButtonFaded(questionsButton20);
                setButtonFaded(questionsButton40);
                numberQuestions = 60;
                categoriesText.setText("Select at least 3 categories");
            });

            // Initially, make one button visible and the others faded
            setButtonFaded(questionsButton20);
            setButtonFaded(questionsButton40);
            setButtonFaded(questionsButton60);




        categoryList = new ArrayList<>();
        categoryList.add(new Category("Animals",R.raw.animals,R.drawable.animal19,R.color.selectedButtonColor,R.color.black));
        categoryList.add(new Category("Entertainment",R.raw.entertainment,R.drawable.categoryentertainment,R.color.magenta,R.color.white));
        categoryList.add(new Category("General Knowledge",R.raw.general,R.drawable.categorygeneralknowledge,R.color.white, R.color.black));
        categoryList.add(new Category("Geography",R.raw.geography,R.drawable.categorygeography,R.color.Green,R.color.white));
        categoryList.add(new Category("Food",R.raw.food,R.drawable.categoryfood,R.color.orange,R.color.white));
        categoryList.add(new Category("History",R.raw.history,R.drawable.categoryhistory,R.color.grey,R.color.Green));
        categoryList.add(new Category("Science",R.raw.science,R.drawable.categoryscience,R.color.white,R.color.blue));
        categoryList.add(new Category("Music",R.raw.music,R.drawable.categorymusic,R.color.purple,R.color.white));
        categoryList.add(new Category("Sports",R.raw.sports,R.drawable.categorysports,R.color.teal,R.color.white));
        categoryList.add(new Category("Technology",R.raw.technology,R.drawable.categorytechnology,R.color.blue,R.color.white));

        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        CategoryAdapter adapter = new CategoryAdapter(categoryList,getResources());
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void setButtonVisible(Button button) {
        button.setAlpha(1.0f); // Fully visible
    }

    private void setButtonFaded(Button button) {
        button.setAlpha(0.2f); // Adjust the alpha value as needed
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

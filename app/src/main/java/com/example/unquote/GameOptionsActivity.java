package com.example.unquote;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameOptionsActivity extends AppCompatActivity {

    private static List<Category> categoryList;
    private VideoView background;
    public static int numCategories;
    public static int numberQuestions = 0;
    public static List<Category> selectedCategories;
    private String cantStartMessage;
    private boolean allCategoriesSelected = false;
    Button questionsButton20;
    Button questionsButton40;
    Button questionsButton60;
    Button specCatButton;
    Button allCatButton;
    private Animation pulse;
    RecyclerView recyclerView;
    HorizontalScrollView horizontalScrollView;
    TextView numSelectedCatTextView;
    TextView selectedCatTextView;
    ConstraintLayout gameOptionsConstraintLayout;
    ConstraintSet allCategoriesConstraintSet;
    ConstraintSet specificCategoriesConstraintSet;
    Transition transition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_options);

        ImageButton playPauseButton = findViewById(R.id.music_toggle_game_options);
        questionsButton20 = findViewById(R.id.questionsButton20);
        questionsButton40 = findViewById(R.id.questionsButton40);
        questionsButton60 = findViewById(R.id.questionsButton60);
        specCatButton = findViewById(R.id.specCatButton);
        allCatButton = findViewById(R.id.allCatButton);
        Button startGameButton = findViewById(R.id.startGameButton);
        TextView categoriesText = findViewById(R.id.selectCategoriesTextView);
        pulse = AnimationUtils.loadAnimation(this,R.anim.pulse_animation);
        recyclerView = findViewById(R.id.categoryRecyclerView);
        horizontalScrollView = findViewById(R.id.selectedCategoriesScrollView);
        numSelectedCatTextView = findViewById(R.id.numberOfSelectedCategoriesTextView);
        selectedCatTextView = findViewById(R.id.selectCategoriesTextView);
        gameOptionsConstraintLayout = findViewById(R.id.gameOptionsConstraintLayout);

        allCategoriesConstraintSet = new ConstraintSet();
        allCategoriesConstraintSet.clone(gameOptionsConstraintLayout);

        specificCategoriesConstraintSet = new ConstraintSet();
        specificCategoriesConstraintSet.clone(this,R.layout.activity_game_options_alternate);

        setButtonNotSelected(questionsButton20);
        setButtonNotSelected(questionsButton40);
        setButtonNotSelected(questionsButton60);
        setCatButtonNotSelected(specCatButton);
        setCatButtonNotSelected(allCatButton);

        transition = new ChangeBounds();

        // Set a TransitionListener
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                removeButtonText();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setButtonText();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                // Called when the transition is cancelled
            }

            @Override
            public void onTransitionPause(Transition transition) {
                // Called when the transition is paused
            }

            @Override
            public void onTransitionResume(Transition transition) {
                // Called when the transition resumes
            }
        });


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((numberQuestions != 0  && allCategoriesSelected) || startcheck()) {
                    selectedCategories = new ArrayList<>();
                    for (Category i: categoryList) {
                        if (allCategoriesSelected || i.included) {
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
                categoriesText.setText("Select at least 1 category");
            });

            questionsButton60.setOnClickListener(view -> {
                setButtonSelected(questionsButton60);
                setButtonNotSelected(questionsButton20);
                setButtonNotSelected(questionsButton40);
                numberQuestions = 60;
                categoriesText.setText("Select at least 2 categories");
            });

            specCatButton.setOnClickListener(view -> {
                allCategoriesSelected = false;
                setSpecificCategoriesConstraintLayout();
                setCatButtonSelected(specCatButton);
                setCatButtonNotSelected(allCatButton);
                allCatButton.setTextSize(19);
                specCatButton.setTextSize(19);
                questionsButton20.setTextSize(30);
                questionsButton40.setTextSize(30);
                questionsButton60.setTextSize(30);
            });

            allCatButton.setOnClickListener(view -> {
                allCategoriesSelected = true;
                setAllCategoriesConstraintLayout();
                setCatButtonSelected(allCatButton);
                setCatButtonNotSelected(specCatButton);
                allCatButton.setTextSize(34);
                specCatButton.setTextSize(34);
                questionsButton20.setTextSize(60);
                questionsButton40.setTextSize(60);
                questionsButton60.setTextSize(60);
            });


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
        if (numberQuestions < 1) {
            cantStartMessage = "Please select the number of questions";
            return false;
        } else if (numCategories < 1) {
            cantStartMessage = "Please choose your categories";
            return false;
        } else if (numberQuestions > 40 && numCategories < 2) {
            cantStartMessage = "For 60 questions, please select at least 2 categories.";
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

    private void setCatButtonNotSelected(Button button) {
        button.setBackgroundColor(getColor(R.color.grey));
        button.setTextColor(getColor(R.color.white));
        button.setAlpha(0.8f);
    }

    private void setCatButtonSelected(Button button) {
        button.setBackgroundColor(getColor(R.color.selectedButtonColor));
        button.setTextColor(getColor(R.color.black));
        button.setAlpha(1.0f);
        MainActivity.soundPool.play(MainActivity.soundTapAnswer, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private void setButtonNotSelected(Button button) {
        button.setBackgroundColor(getColor(R.color.grey));
        button.setTextColor(getColor(R.color.white));
        button.setAlpha(0.8f);
        button.clearAnimation();
    }

    private void setSpecificCategoriesConstraintLayout() {
        TransitionManager.beginDelayedTransition(gameOptionsConstraintLayout, transition);
        specificCategoriesConstraintSet.applyTo(gameOptionsConstraintLayout);
        numberQuestionButtonUpdate();
    }

    private void setAllCategoriesConstraintLayout() {
        TransitionManager.beginDelayedTransition(gameOptionsConstraintLayout, transition);
        allCategoriesConstraintSet.applyTo(gameOptionsConstraintLayout);
        numberQuestionButtonUpdate();
    }


    private void numberQuestionButtonUpdate() {
        switch(numberQuestions) {
            case 0:
                break;
            case 20:
                questionsButton20.setAlpha(1);
                break;
            case 40:
                questionsButton40.setAlpha(1);
                break;
            case 60:
                questionsButton60.setAlpha(1);
                break;
        }
    }

    private void removeButtonText() {
        questionsButton60.setText("");
        questionsButton40.setText("");
        questionsButton20.setText("");
        allCatButton.setText("");
        specCatButton.setText("");
    }

    private void setButtonText() {
        questionsButton60.setText("60");
        questionsButton40.setText("40");
        questionsButton20.setText("20");
        allCatButton.setText("All Categories");
        specCatButton.setText("Specific Categories");
    }

    public static void resetState() {
        numberQuestions = 0;
        for (Category i: categoryList) {
            i.included = false;
        }
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

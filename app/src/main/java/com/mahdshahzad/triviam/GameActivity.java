package com.mahdshahzad.triviam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;
import android.os.Handler;
import android.os.Looper;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class GameActivity extends AppCompatActivity {

    int currentQuestionIndex;
    static public int totalCorrect;
    static public int totalQuestions;
    ArrayList<Question> questions;
    ImageView questionImageView;
    TextView questionTextView;
    TextView restartGameTextView;
    View questionRemainingTextView;
    Button answer0Button;
    Button answer1Button;
    Button answer2Button;
    Button answer3Button;
    Button submitButton;
    Animation pulseAnimation;
    Animation fadeAnimation;
    TextView scoreTextView;
    static public int score = 0;
    private static boolean currentQuestionAnswered;
    private int consecutiveCorrect;
    private int currentIncrement;
    TextView scoreChangeTextView;
    TextView multiplierTextView;
    TextView multiplierActivatedTextView;
    VerticalBarView multiplierBar;
    VerticalBarView multiplierBarSkeleton;
    private VideoView background;
    private VideoView[] cardBorderAnimations = new VideoView[5];
    private int[] cardBorderAnimationResources = {R.raw.question_card_background,
    R.raw.question_card_background_multx1_5,R.raw.question_card_background_multx2,
    R.raw.question_card_background_multx3,R.raw.question_card_background_multx4};
    View imageBlackOverlay;
    View cardAnimationBlackOverlay;
    View fullscreenBlackOverlay;
    Handler handler;
    Executor executor;
    private InterstitialAd gameInterstitialAd;
    private String TAG = "GameActivity";

    @Override
    public void onBackPressed() {
        //To cancel back button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        loadInterstitialAd();

        // To improve performance via parallelization
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        ImageButton playPauseButton = findViewById(R.id.music_toggle_game);

        if (MainActivity.musicPaused) {
            playPauseButton.setImageResource(R.drawable.volume_off_24px);
        }

        executor.execute(() -> {
            preloadCardBorderAnimations();
        });

        imageBlackOverlay = findViewById(R.id.imageBlackOverlay);
        cardAnimationBlackOverlay = findViewById(R.id.cardAnimationBlackOverlay);

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);


        questionImageView = findViewById(R.id.iv_main_question_image);
        questionTextView = findViewById(R.id.tv_main_question_title);
        questionRemainingTextView = findViewById(R.id.tv_main_questions_remaining_count);
        scoreTextView = findViewById(R.id.scoreTextView);
        scoreChangeTextView = findViewById(R.id.scoreChangeTextView);
        multiplierTextView = findViewById(R.id.multiplierTextView);
        multiplierActivatedTextView = findViewById(R.id.multiplierActivatedTextVeiw);
        multiplierBar = findViewById(R.id.multiplierBar);
        multiplierBarSkeleton = findViewById(R.id.multiplierBarSkeleton);
        answer0Button = findViewById(R.id.btn_main_answer_0);
        answer1Button = findViewById(R.id.btn_main_answer_1);
        answer2Button = findViewById(R.id.btn_main_answer_2);
        answer3Button = findViewById(R.id.btn_main_answer_3);
        submitButton = findViewById(R.id.btn_main_submit_answer);
        fullscreenBlackOverlay = findViewById(R.id.fullscreenBlackOverlay);
        restartGameTextView = findViewById(R.id.restartGameTextView);

        multiplierBarSkeleton.setAsSkeleton();

        background = findViewById(R.id.gameVideoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_background_dark);
        background.setVideoURI(videoUri);

        // Start playing the video in a loop
        background.start();
        background.setOnPreparedListener(mp -> mp.setLooping(true));

        final ImageButton dropdownButton = findViewById(R.id.dropdownButton);

        dropdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(GameActivity.this, dropdownButton);

                // Inflate your menu XML
                popup.getMenuInflater().inflate(R.menu.dropdown_menu, popup.getMenu());

                // Add click listener for menu items
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.Main_Menu) {
                            GameOptionsActivity.resetState();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.Restart_Game) {
                            restartGame();
                            return true;
                        }
                        return false;
                    }

                });

                popup.show();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.musicPaused) {
                    playPauseButton.setImageResource(R.drawable.volume_off_24px);
                    MainActivity.mediaPlayer.pause();
                    MainActivity.musicPaused = true;
                } else {
                    playPauseButton.setImageResource(R.drawable.volume_up_24px);
                    MainActivity.mediaPlayer.start();
                    MainActivity.mediaPlayer.setVolume(0.2f,0.2f);
                    MainActivity.mediaPlayer.setLooping(true);
                    MainActivity.musicPaused = false;
                }
            }
        });

        answer0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnswerSelected(0);
            }
        });
        answer1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnswerSelected(1);
            }
        });
        answer2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnswerSelected(2);
            }
        });
        answer3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnswerSelected(3);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAnswerSubmission();
            }
        });

        startNewGame();
    }


    void displayQuestion(Question question){

        setQuestionImageView(question);
        questionTextView.setText(question.questionText);
        answer0Button.setText(question.answer0);
        answer1Button.setText(question.answer1);
        answer2Button.setText(question.answer2);
        answer3Button.setText(question.answer3);
        answer0Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer1Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer2Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer3Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer0Button.setTextColor(getColor(R.color.white));
        answer1Button.setTextColor(getColor(R.color.white));
        answer2Button.setTextColor(getColor(R.color.white));
        answer3Button.setTextColor(getColor(R.color.white));
        answer0Button.clearAnimation();
        answer1Button.clearAnimation();
        answer2Button.clearAnimation();
        answer3Button.clearAnimation();
        submitButton.setAlpha(0.3f);
        answer0Button.setAlpha(1.0f);
        answer1Button.setAlpha(1.0f);
        answer2Button.setAlpha(1.0f);
        answer3Button.setAlpha(1.0f);
        submitButton.setTextColor(getColor(R.color.white));
        submitButton.setBackgroundColor(getColor(R.color.black));
        submitButton.setVisibility(View.VISIBLE);
        currentQuestionAnswered = false;
        if (questions.size() % 5 == 0) {
            if (gameInterstitialAd != null) {
                MainActivity.mediaPlayer.pause();
                gameInterstitialAd.show(this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        }

    }

    void displayQuestionsRemaining(int questionRemaining) {
        ((TextView)questionRemainingTextView).setText(String.valueOf(questionRemaining));
    }

    void onAnswerSelected(int answerSelected) {
        if (currentQuestionAnswered){return;}
        Question currentQuestion = getCurrentQuestion();
        submitButton.setAlpha(1.0f);
        submitButton.setBackgroundColor(getColor(R.color.white));
        submitButton.setTextColor(getColor(R.color.black));
        submitButton.startAnimation(fadeAnimation);
        currentQuestion.playerAnswer = answerSelected;
        answer0Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer1Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer2Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        answer3Button.setBackgroundColor(getColor(R.color.notSelectedButtonColor));
        ((Button)answer0Button).setTextColor(getColor(R.color.white));
        ((Button)answer1Button).setTextColor(getColor(R.color.white));
        ((Button)answer2Button).setTextColor(getColor(R.color.white));
        ((Button)answer3Button).setTextColor(getColor(R.color.white));
        answer0Button.clearAnimation();
        answer1Button.clearAnimation();
        answer2Button.clearAnimation();
        answer3Button.clearAnimation();
        switch (answerSelected) {
            case 0:
                answer0Button.setBackgroundColor(getColor(R.color.selectedButtonColor));
                ((Button)answer0Button).setTextColor(getColor(R.color.black));
                answer0Button.startAnimation(pulseAnimation);
                break;
            case 1:
                answer1Button.setBackgroundColor(getColor(R.color.selectedButtonColor));
                ((Button)answer1Button).setTextColor(getColor(R.color.black));
                answer1Button.startAnimation(pulseAnimation);
                break;
            case 2:
                answer2Button.setBackgroundColor(getColor(R.color.selectedButtonColor));
                ((Button)answer2Button).setTextColor(getColor(R.color.black));
                answer2Button.startAnimation(pulseAnimation);
                break;

            case 3:
                answer3Button.setBackgroundColor(getColor(R.color.selectedButtonColor));
                ((Button)answer3Button).setTextColor(getColor(R.color.black));
                answer3Button.startAnimation(pulseAnimation);
                break;
        }

        MainActivity.soundPool.play(MainActivity.soundTapAnswer, 1.0f, 1.0f, 1, 0, 1.0f);


    }
    void onAnswerSubmission() {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion.playerAnswer == -1) {
            return;
        }
        submitButton.clearAnimation();
        submitButton.setVisibility(View.INVISIBLE);
        currentQuestionAnswered = true;
        answer0Button.setAlpha(0.3f);
        answer1Button.setAlpha(0.3f);
        answer2Button.setAlpha(0.3f);
        answer3Button.setAlpha(0.3f);
        if (currentQuestion.isCorrect()) {
            totalCorrect ++;
            consecutiveCorrect ++;
            updateScore(currentIncrement);
            switch (currentQuestion.playerAnswer) {
                case 0:
                    answer0Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer0Button).setTextColor(getColor(R.color.white));
                    answer0Button.setAlpha(1.0f);
                    break;
                case 1:
                    answer1Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer1Button).setTextColor(getColor(R.color.white));
                    answer1Button.setAlpha(1.0f);
                    break;
                case 2:
                    answer2Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer2Button).setTextColor(getColor(R.color.white));
                    answer2Button.setAlpha(1.0f);
                    break;

                case 3:
                    answer3Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer3Button).setTextColor(getColor(R.color.white));
                    answer3Button.setAlpha(1.0f);
                    break;
            }

            MainActivity.soundPool.play(MainActivity.soundCorrectAnswer, 1.5f, 1.5f, 1, 0, 1.0f);

        } else {
            switch (currentQuestion.playerAnswer) {
                case 0:
                    answer0Button.setBackgroundColor(getColor(R.color.wrongButtonColor));
                    ((Button) answer0Button).setTextColor(getColor(R.color.black));
                    answer0Button.setAlpha(1.0f);
                    break;
                case 1:
                    answer1Button.setBackgroundColor(getColor(R.color.wrongButtonColor));
                    ((Button) answer1Button).setTextColor(getColor(R.color.black));
                    answer1Button.setAlpha(1.0f);
                    break;
                case 2:
                    answer2Button.setBackgroundColor(getColor(R.color.wrongButtonColor));
                    ((Button) answer2Button).setTextColor(getColor(R.color.black));
                    answer2Button.setAlpha(1.0f);
                    break;

                case 3:
                    answer3Button.setBackgroundColor(getColor(R.color.wrongButtonColor));
                    ((Button) answer3Button).setTextColor(getColor(R.color.black));
                    answer3Button.setAlpha(1.0f);
                    break;
            }
            switch (currentQuestion.correctAnswer) {
                case 0:
                    answer0Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer0Button).setTextColor(getColor(R.color.white));
                    answer0Button.setAlpha(1.0f);
                    break;
                case 1:
                    answer1Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer1Button).setTextColor(getColor(R.color.white));
                    answer1Button.setAlpha(1.0f);
                    break;
                case 2:
                    answer2Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer2Button).setTextColor(getColor(R.color.white));
                    answer2Button.setAlpha(1.0f);
                    break;

                case 3:
                    answer3Button.setBackgroundColor(getColor(R.color.correctButtonColor));
                    ((Button) answer3Button).setTextColor(getColor(R.color.white));
                    answer3Button.setAlpha(1.0f);
                    break;
            }
            consecutiveCorrect = 0;
            MainActivity.soundPool.play(MainActivity.soundWrongAnswer, 0.08f, 0.08f, 1, 0, 1.0f);

        }
        multiplierBar.setConsecutiveCorrectAnswers(consecutiveCorrect);
        questions.remove(currentQuestion);

        handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMultiplier(); //Note this also handles moving onto next question
            }
        }, 1000);


    }

    void moveToNextQuestion() {

        displayQuestionsRemaining(questions.size());

        if (questions.size() == 0) {

            /* Old implementation, game over now has its own activity.


            String gameOverMessage = getGameOverMessage(totalCorrect, totalQuestions);

            // TODO 5-D: Show a popup instead
            AlertDialog.Builder gameOverDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            gameOverDialogBuilder.setCancelable(false);
            gameOverDialogBuilder.setTitle("Game Over!");
            gameOverDialogBuilder.setMessage(gameOverMessage);
            gameOverDialogBuilder.setPositiveButton("Play Again!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startNewGame();
                }
            });
            gameOverDialogBuilder.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent (getApplicationContext(),MainActivity.class));
                }
            });


            gameOverDialogBuilder.create().show();

            */

            startActivity(new Intent(getApplicationContext(),GameOverActivity.class));

        } else {
            chooseNewQuestion();

            displayQuestion(getCurrentQuestion());
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAnswerSubmission();
                }
            });
            submitButton.setText("Submit");
        }
    }

    void startNewGame() {

        List<Pair<Integer, Integer>> filesAndQuestions = selectQuestions();

        // Pass the Resources instance to the method (replace "your.package.name" with your actual package name)
        questions = TriviaQuestionParser.parseTriviaQuestionsFromFiles(filesAndQuestions, getResources(),getPackageName());


        totalCorrect = 0;
        consecutiveCorrect = 0;
        multiplierBar.setConsecutiveCorrectAnswers(consecutiveCorrect);
        multiplierTextView.setTextColor(getColor(R.color.white));
        multiplierTextView.setText("Multiplier Inactive");
        multiplierTextView.clearAnimation();
        currentIncrement = 100;
        score = 0;
        totalQuestions = questions.size();
        scoreTextView.setText("Score: 0");
        resumeCardBorderAnimations();

        Question firstQuestion = chooseNewQuestion();

        displayQuestionsRemaining(questions.size());

        displayQuestion(firstQuestion);
        setMusic(MainActivity.musicResources[5]);

    }

    Question chooseNewQuestion() {
        int newQuestionIndex = generateRandomNumber(questions.size());
        currentQuestionIndex = newQuestionIndex;
        return questions.get(currentQuestionIndex);
    }

    public List<Pair<Integer,Integer>>selectQuestions(){
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        int numQuestions = GameOptionsActivity.numberQuestions;
        int numCategories = GameOptionsActivity.selectedCategories.size();
        int questionsPerCategory = numQuestions/numCategories;
        int remainingQuestions = numQuestions%numCategories;

        for (int i = 0; i < numCategories; i++) {
                int resourceId = GameOptionsActivity.selectedCategories.get(i).questionsResourceId;
                int finalNumQuestions = questionsPerCategory + (i < remainingQuestions? 1 : 0);

                result.add(new Pair<>(resourceId,finalNumQuestions));
        }

        return result;
    }

    int generateRandomNumber(int max) {
        double randomNumber = Math.random();
        double result = max * randomNumber;
        return (int) result;
    }

    Question getCurrentQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        return currentQuestion;
    }

    void updateScore(int increment) {
        ValueAnimator animator = ValueAnimator.ofInt(score, score + increment);
        score += increment;
        animator.setDuration(2000); // Animation duration in milliseconds (adjust as needed)

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                scoreTextView.setText("Score: " + String.valueOf(animatedValue));
            }
        });

        executeScoreChangeAnimation(increment);
        animator.start();
        scoreTextView.startAnimation(fadeAnimation);
        int streamId = MainActivity.soundPool.play(MainActivity.soundScoreIncrease, 0.05f, 0.05f, 1, 0, 1.0f);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.soundPool.stop(streamId);// Stop the sound
                scoreTextView.clearAnimation();
            }
        }, 2000); // Delayed for 2 seconds (2000 milliseconds)
    }

    void executeScoreChangeAnimation(int increment){

        executeRisingFadeAnimation(scoreChangeTextView,"+ " + increment );

    }

    void prepareMultiplierActivatedTextView(float verticalBias,int color) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) multiplierActivatedTextView.getLayoutParams();
        layoutParams.verticalBias = verticalBias;
        multiplierActivatedTextView.setLayoutParams(layoutParams);
        multiplierActivatedTextView.setTextColor(color);
    }

    void updateMultiplier() {
            int color;
            boolean wontMoveToNextQuestion = true;
            switch (consecutiveCorrect) {
                case 0:
                    color = getColor(R.color.white);
                    if (currentIncrement != 100) {
                        setMusic(MainActivity.musicResources[5]);
                        prepareMultiplierActivatedTextView(0.5f, color);
                        executeMoveRightFadeAnimation(multiplierActivatedTextView,"Deactivated Multiplier" );
                        transitionCardBorderAnimations();
                        wontMoveToNextQuestion = false;
                    }
                    currentIncrement = 100;
                    multiplierTextView.setTextColor(color);
                    multiplierTextView.setText("Multiplier Inactive");
                    multiplierTextView.clearAnimation();
                    if (wontMoveToNextQuestion) {
                        MainActivity.soundPool.play(MainActivity.soundNextQuestion, 1.5f, 1.5f, 1, 0, 1.0f);
                        moveToNextQuestion();
                    }
                    break;
                case 2:
                    transitionCardBorderAnimations();
                    currentIncrement = 150;
                    color = getColor(R.color.lightBlue);
                    prepareMultiplierActivatedTextView(0.79f, color);
                    executeMoveRightFadeAnimation(multiplierActivatedTextView,"x1.5 Multiplier Activated" );
                    multiplierTextView.setText("x1.5 Multiplier");
                    multiplierTextView.startAnimation(fadeAnimation);
                    multiplierTextView.setTextColor(color);
                    setMusic(MainActivity.musicResources[1]);
                    break;

                case 4:
                    transitionCardBorderAnimations();
                    currentIncrement = 200;
                    color = getColor(R.color.purple);
                    prepareMultiplierActivatedTextView(0.53f, color);
                    executeMoveRightFadeAnimation(multiplierActivatedTextView,"x2 Multiplier Activated" );
                    multiplierTextView.setText("x2 Multiplier");
                    multiplierTextView.setTextColor(color);
                    setMusic(MainActivity.musicResources[2]);
                    break;

                case 6:
                    transitionCardBorderAnimations();
                    currentIncrement = 300;
                    color = getColor(R.color.orange);
                    prepareMultiplierActivatedTextView(0.26f, color);
                    executeMoveRightFadeAnimation(multiplierActivatedTextView,"x3 Multiplier Activated" );
                    multiplierTextView.setText("x3 Multiplier");
                    multiplierTextView.setTextColor(color);
                    setMusic(MainActivity.musicResources[3]);
                    break;

                case 8:
                    transitionCardBorderAnimations();
                    currentIncrement = 400;
                    color = getColor(R.color.wrongButtonColor);
                    prepareMultiplierActivatedTextView(0f, color);
                    executeMoveRightFadeAnimation(multiplierActivatedTextView,"x4 Multiplier Activated" );
                    multiplierTextView.setText("x4 Multiplier");
                    multiplierTextView.setTextColor(color);
                    setMusic(MainActivity.musicResources[4]);
                    break;

                default:
                    MainActivity.soundPool.play(MainActivity.soundNextQuestion, 1.5f, 1.5f, 1, 0, 1.0f);
                    moveToNextQuestion();
            }

    }


    void executeRisingFadeAnimation(TextView textView, String text) {

        AnimationSet animationSet = new AnimationSet(true);

        // Fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000); // 1 second for fade-in

        // Translate animation (rise)
        TranslateAnimation rise = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -1.0f
        );
        rise.setDuration(1000); // 1 second for rising

        // Fade-out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000); // 1 second for fade-out
        fadeOut.setStartOffset(1000); // Start after fade-in and rise animations

        // Add animations to the set
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(rise);
        animationSet.addAnimation(fadeOut);


        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started (e.g., start playing sound, update score)
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended (e.g., stop playing sound)
                textView.setVisibility(View.INVISIBLE); // Hide the TextView
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if needed)
            }
        });

        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        textView.startAnimation(animationSet);
    }


    void executeMoveRightFadeAnimation(TextView textView, String text) {

        AnimationSet animationSet = new AnimationSet(true);

        // Fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);

        // Translate animation (move to the right)
        TranslateAnimation moveRight = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.01f, Animation.RELATIVE_TO_SELF, 0.03f,  // X-axis (start and end position)
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f   // Y-axis (start and end position)
        );
        moveRight.setDuration(1400);

        // Fade-out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500); // 1 second for fade-out
        fadeOut.setStartOffset(900); // Start after fade-in and move-right animations

        // Add animations to the set
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(moveRight);
        animationSet.addAnimation(fadeOut);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started (e.g., start playing sound, update score)
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended (e.g., stop playing sound)
                textView.setVisibility(View.INVISIBLE); // Hide the TextView
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (if needed)
            }
        });

        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        textView.startAnimation(animationSet);
    }


    private void setMusic(int musicResource) {

        if (MainActivity.mediaPlayer != null) {
            fadeOutMediaPlayer(new OnFadeComplete() {
                @Override
                public void onFadeComplete() {
                    MainActivity.mediaPlayer.reset(); // Reset the current media player
                    try {
                        AssetFileDescriptor afd = getResources().openRawResourceFd(musicResource);
                        if (afd != null) {
                            MainActivity.mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                            MainActivity.mediaPlayer.prepareAsync();
                            MainActivity.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    fadeInMediaPlayer();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            MainActivity.mediaPlayer = MediaPlayer.create(this, musicResource);
            MainActivity.mediaPlayer.setVolume(0.2f, 0.2f);
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }


    }

    private void fadeOutMediaPlayer(OnFadeComplete listener) {
        ValueAnimator fadeOut = ValueAnimator.ofFloat(0.2f, 0.0f);
        fadeOut.setDuration(1500);
        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MainActivity.mediaPlayer.setVolume((float) animation.getAnimatedValue(), (float) animation.getAnimatedValue());
            }
        });
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                MainActivity.mediaPlayer.stop();
                if (listener != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFadeComplete();
                        }
                    },150);

                }
            }
        });
        fadeOut.start();

    }

    private void fadeInMediaPlayer() {
        if (!MainActivity.musicPaused) {
            MainActivity.mediaPlayer.start();
            ValueAnimator fadeIn = ValueAnimator.ofFloat(0.0f, 0.2f);
            fadeIn.setDuration(1000);
            fadeIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MainActivity.mediaPlayer.setVolume((float) animation.getAnimatedValue(), (float) animation.getAnimatedValue());
                }
            });
            fadeIn.start();

            MainActivity.mediaPlayer.setLooping(true);
        }
    }

    interface OnFadeComplete {
        void onFadeComplete();
    }

    void setQuestionImageView(Question question) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final int imageResourceId = selectQuestionImageResourceId(question);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        questionImageView.setImageResource(imageResourceId);
                    }
                });
            }
        });
    }


    int selectQuestionImageResourceId(Question question) {
        String resourceName = "";
        for (Category category: GameOptionsActivity.selectedCategories) {
            if (question.categoryId.contains(category.categoryId)) {
                switch (currentIncrement) {
                    case 100:
                        resourceName = category.categoryId + "0" + randomImgOffset();
                        break;
                    case 150:
                        resourceName = category.categoryId + "1" + randomImgOffset();
                        break;

                    case 200:
                        resourceName = category.categoryId + "2" + randomImgOffset();
                        break;

                    case 300:
                        resourceName = category.categoryId + "3" + randomImgOffset();
                        break;

                    case 400:
                        resourceName = category.categoryId + "4" + randomImgOffset();
                        break;

                }
                return getResources().getIdentifier(resourceName, "drawable", getPackageName());
            }
        }
        return 0; //Indicates Error
    }

    private String randomImgOffset () {
        int number = (int) (Math.random()*2);
        if (number == 0) {
            return "0";
        } else {
            return "";
        }
    }


    void preloadCardBorderAnimations() {

        cardBorderAnimations[0] = findViewById(R.id.questionCardVideoView0);
        cardBorderAnimations[1] = findViewById(R.id.questionCardVideoView1);
        cardBorderAnimations[2] = findViewById(R.id.questionCardVideoView2);
        cardBorderAnimations[3] = findViewById(R.id.questionCardVideoView3);
        cardBorderAnimations[4] = findViewById(R.id.questionCardVideoView4);

        for (int i = 0; i < cardBorderAnimations.length; i++) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() +
                    "/" + cardBorderAnimationResources[i]);
            cardBorderAnimations[i].setOnPreparedListener(mp -> {
                mp.setLooping(true);
            });
            cardBorderAnimations[i].setVideoURI(videoUri);
        }
    }

    void transitionImageAnimations() {
        fadeOut(imageBlackOverlay,1000,new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveToNextQuestion();
                        fadeIn(imageBlackOverlay,1000);
                    }
                },700);
            }
        });
    }

    void restartGame() {
        fadeOut(fullscreenBlackOverlay,1000,new Runnable() {
            @Override
            public void run() {
                executeRisingFadeAnimation(restartGameTextView,"Restarting game with same settings");
                startNewGame();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeIn(fullscreenBlackOverlay,1000);
                    }
                },2000);
            }
        });
    }

    void transitionCardBorderAnimations() {

        transitionImageAnimations();

        //As fadeOut completes, this value will have changed to the new increment
        int oldIncrement = currentIncrement;
        fadeOut(cardAnimationBlackOverlay,1000,new Runnable() {

            @Override
            public void run() {
                pauseCardBorderAnimations(oldIncrement);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resumeCardBorderAnimations();
                    }
                },700);
            }
        });
    }

    void fadeOut(View blackOverlay, int duration,Runnable onEndAction) {
        blackOverlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        blackOverlay.animate().alpha(1f).setDuration(duration).withEndAction(onEndAction).start();
        blackOverlay.setLayerType(View.LAYER_TYPE_NONE,null);
    }

    void fadeIn(View blackOverlay, int duration) {
        blackOverlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        blackOverlay.animate().alpha(0f).setDuration(duration).start();
        blackOverlay.setLayerType(View.LAYER_TYPE_NONE,null);
    }

    void pauseCardBorderAnimations(int increment) {
        switch (increment) {
            case 100:
                cardBorderAnimations[0].pause();
                cardBorderAnimations[0].setVisibility(View.INVISIBLE);
                break;
            case 150:
                cardBorderAnimations[1].pause();
                cardBorderAnimations[1].setVisibility(View.INVISIBLE);
                break;
            case 200:
                cardBorderAnimations[2].pause();
                cardBorderAnimations[2].setVisibility(View.INVISIBLE);
                break;
            case 300:
                cardBorderAnimations[3].pause();
                cardBorderAnimations[3].setVisibility(View.INVISIBLE);
                break;
            case 400:
                cardBorderAnimations[4].pause();
                cardBorderAnimations[4].setVisibility(View.INVISIBLE);
                break;
        }
    }

    void resumeCardBorderAnimations() {
        switch (currentIncrement) {
            case 100:
                cardBorderAnimations[0].start();
                cardBorderAnimations[0].setVisibility(View.VISIBLE);
                break;
            case 150:
                cardBorderAnimations[1].start();
                cardBorderAnimations[1].setVisibility(View.VISIBLE);
                break;
            case 200:
                cardBorderAnimations[2].start();
                cardBorderAnimations[2].setVisibility(View.VISIBLE);
                break;
            case 300:
                cardBorderAnimations[3].start();
                cardBorderAnimations[3].setVisibility(View.VISIBLE);
                break;
            case 400:
                cardBorderAnimations[4].start();
                cardBorderAnimations[4].setVisibility(View.VISIBLE);
                break;
        }
        fadeIn(cardAnimationBlackOverlay, 1500);
    }

    void setEventHandlersForAd() {
        gameInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                gameInterstitialAd = null;
                loadInterstitialAd();
                if (!MainActivity.musicPaused) {
                    MainActivity.mediaPlayer.start();
                    MainActivity.mediaPlayer.setLooping(true);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                gameInterstitialAd = null;
                loadInterstitialAd();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        gameInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        setEventHandlersForAd();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        gameInterstitialAd = null;
                    }
                });
    }
    @Override
    public void onPause() {
        super.onPause();
        background.pause();
        pauseCardBorderAnimations(currentIncrement);
        MainActivity.mediaPlayer.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        background.start();
        resumeCardBorderAnimations();
        if (!MainActivity.musicPaused) {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }
    }

}
package com.example.unquote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.os.Handler;
import android.os.Looper;


import java.util.ArrayList;
import java.util.List;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class GameActivity extends AppCompatActivity {

    int currentQuestionIndex;
    static public int totalCorrect;
    static public int totalQuestions;
    ArrayList<Question> questions;
    View questionImageView;
    View questionTextView;
    View questionRemainingTextView;
    View answer0Button;
    View answer1Button;
    View answer2Button;
    View answer3Button;
    Button submitButton;
    Animation pulseAnimation;
    Animation fadeAnimation;
    TextView scoreTextView;
    static public int score = 0;
    private int consecutiveCorrect;
    private int currentIncrement;
    TextView scoreChangeTextView;
    TextView multiplierTextView;
    TextView multiplierActivatedTextView;
    VerticalBarView multiplierBar;



    private VideoView background;

    private VideoView cardBorderAnimation;

    @Override
    public void onBackPressed() {
        //To cancel back button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        ImageButton playPauseButton = findViewById(R.id.music_toggle_game);

        if (MainActivity.musicPaused) {
            playPauseButton.setImageResource(R.drawable.volume_off_24px);
        } else {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }


        cardBorderAnimation = findViewById(R.id.questionCardVideoView);
        setQuestionCardAnimation(R.raw.question_card_background);

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
        answer0Button = findViewById(R.id.btn_main_answer_0);
        answer1Button = findViewById(R.id.btn_main_answer_1);
        answer2Button = findViewById(R.id.btn_main_answer_2);
        answer3Button = findViewById(R.id.btn_main_answer_3);
        submitButton = findViewById(R.id.btn_main_submit_answer);

        background = findViewById(R.id.gameVideoView);
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

        ((ImageView)questionImageView).setImageResource(selectQuestionImageResourceId(question));
        ((TextView)questionTextView).setText(question.questionText);
        ((Button)answer0Button).setText(question.answer0);
        ((Button)answer1Button).setText(question.answer1);
        ((Button)answer2Button).setText(question.answer2);
        ((Button)answer3Button).setText(question.answer3);
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
        submitButton.setAlpha(0.3f);
        answer0Button.setAlpha(1.0f);
        answer1Button.setAlpha(1.0f);
        answer2Button.setAlpha(1.0f);
        answer3Button.setAlpha(1.0f);
        submitButton.setTextColor(getColor(R.color.white));
        submitButton.setBackgroundColor(getColor(R.color.black));
        submitButton.setVisibility(View.VISIBLE);

    }

    void displayQuestionsRemaining(int questionRemaining) {
        ((TextView)questionRemainingTextView).setText(String.valueOf(questionRemaining));
    }

    void onAnswerSelected(int answerSelected) {
        if (getCurrentQuestion().answered){return;}
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
        currentQuestion.answered = true;
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

        Handler handler = new Handler(Looper.getMainLooper());

        int delayMillis = 1000; //
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMultiplier();
                MainActivity.soundPool.play(MainActivity.soundNextQuestion, 1.5f, 1.5f, 1, 0, 1.0f);
                moveToNextQuestion(currentQuestion);
            }
        }, delayMillis);


    }

    void moveToNextQuestion(Question currentQuestion) {


        questions.remove(currentQuestion);

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
        currentIncrement = 100;
        score = 0;
        totalQuestions = questions.size();
        scoreTextView.setText("Score: 0");

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
        int numCategories = GameOptionsActivity.numCategories;
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

    void updateMultiplier() {
            switch (consecutiveCorrect) {
                case 0:
                    if (currentIncrement != 100) {
                        setMusic(MainActivity.musicResources[5]);
                        executeRisingFadeAnimation(multiplierActivatedTextView,"Multiplier Lost" );
                    }
                    currentIncrement = 100;
                    multiplierTextView.setTextColor(getColor(R.color.white));
                    multiplierTextView.setText("Multiplier Inactive");
                    multiplierTextView.clearAnimation();
                    setQuestionCardAnimation(R.raw.question_card_background);
                    break;
                case 2:
                    currentIncrement = 150;
                    executeRisingFadeAnimation(multiplierActivatedTextView,"x1.5 Multiplier Activated" );
                    multiplierTextView.setText("x1.5 Multiplier");
                    multiplierTextView.startAnimation(fadeAnimation);
                    multiplierTextView.setTextColor(getColor(R.color.lightBlue));
                    setQuestionCardAnimation(R.raw.question_card_background_multx1_5);
                    setMusic(MainActivity.musicResources[1]);
                    break;

                case 4:
                    currentIncrement = 200;
                    executeRisingFadeAnimation(multiplierActivatedTextView,"x2 Multiplier Activated" );
                    multiplierTextView.setText("x2 Multiplier");
                    multiplierTextView.setTextColor(getColor(R.color.purple));
                    setQuestionCardAnimation(R.raw.question_card_background_multx2);
                    setMusic(MainActivity.musicResources[2]);
                    break;

                case 6:
                    currentIncrement = 300;
                    executeRisingFadeAnimation(multiplierActivatedTextView,"x3 Multiplier Activated" );
                    multiplierTextView.setText("x3 Multiplier");
                    multiplierTextView.setTextColor(getColor(R.color.orange));
                    setQuestionCardAnimation(R.raw.question_card_background_multx3);
                    setMusic(MainActivity.musicResources[3]);
                    break;

                case 8:
                    currentIncrement = 400;
                    executeRisingFadeAnimation(multiplierActivatedTextView,"x4 Multiplier Activated" );
                    multiplierTextView.setText("x4 Multiplier");
                    multiplierTextView.setTextColor(getColor(R.color.wrongButtonColor));
                    setQuestionCardAnimation(R.raw.question_card_background_multx4);
                    setMusic(MainActivity.musicResources[4]);
                    break;
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

    void setQuestionCardAnimation(int resourceId) {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
        cardBorderAnimation.setVideoURI(videoUri);
        cardBorderAnimation.start();
        cardBorderAnimation.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    void setMusic(int musicResource) {
        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer.release();
        MainActivity.mediaPlayer = null;
        MainActivity.mediaPlayer = MediaPlayer.create(this,musicResource);
        MainActivity.mediaPlayer.setVolume(0.2f,0.2f);
        MainActivity.mediaPlayer.start();
        MainActivity.mediaPlayer.setLooping(true);
    }

    int selectQuestionImageResourceId(Question question) {
        String resourceName = "";
        for (Category category: GameOptionsActivity.selectedCategories) {
            if (question.categoryId.contains(category.categoryId)) {
                switch (currentIncrement) {
                    case 100:
                        resourceName = category.categoryId + "0";
                        break;
                    case 150:
                        resourceName = category.categoryId + "1";
                        break;

                    case 200:
                        resourceName = category.categoryId + "2";
                        break;

                    case 300:
                        resourceName = category.categoryId + "3";
                        break;

                    case 400:
                        resourceName = category.categoryId + "4";
                        break;

                }
                return getResources().getIdentifier(resourceName, "drawable", getPackageName());
            }
        }
        return 0; //Indicates Error
    }


    @Override
    public void onPause() {
        super.onPause();
        background.pause();
        cardBorderAnimation.pause();
        MainActivity.mediaPlayer.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        background.start();
        cardBorderAnimation.start();
        if (!MainActivity.musicPaused) {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.setLooping(true);
        }
    }

}
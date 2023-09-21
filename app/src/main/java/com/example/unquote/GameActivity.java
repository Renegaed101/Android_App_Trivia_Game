package com.example.unquote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

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
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.question_card_background);
        cardBorderAnimation.setVideoURI(videoUri);
        cardBorderAnimation.start();
        cardBorderAnimation.setOnPreparedListener(mp -> mp.setLooping(true));

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);


        questionImageView = findViewById(R.id.iv_main_question_image);
        questionTextView = findViewById(R.id.tv_main_question_title);
        questionRemainingTextView = findViewById(R.id.tv_main_questions_remaining_count);
        answer0Button = findViewById(R.id.btn_main_answer_0);
        answer1Button = findViewById(R.id.btn_main_answer_1);
        answer2Button = findViewById(R.id.btn_main_answer_2);
        answer3Button = findViewById(R.id.btn_main_answer_3);
        submitButton = findViewById(R.id.btn_main_submit_answer);

        background = findViewById(R.id.gameVideoView);
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_background_dark);
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
        ((ImageView)questionImageView).setImageResource(question.imageId);
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
        submitButton.clearAnimation();

    }

    void displayQuestionsRemaining(int questionRemaining) {
        ((TextView)questionRemainingTextView).setText(String.valueOf(questionRemaining));
    }

    void onAnswerSelected(int answerSelected) {
        if (getCurrentQuestion().answered){return;}
        Question currentQuestion = getCurrentQuestion();
        submitButton.setAlpha(1.0f);
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
        currentQuestion.answered = true;
        answer0Button.setAlpha(0.3f);
        answer1Button.setAlpha(0.3f);
        answer2Button.setAlpha(0.3f);
        answer3Button.setAlpha(0.3f);
        if (currentQuestion.isCorrect()) {
            totalCorrect = totalCorrect + 1;
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

            MainActivity.soundPool.play(MainActivity.soundWrongAnswer, 0.08f, 0.08f, 1, 0, 1.0f);

        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.soundPool.play(MainActivity.soundNextQuestion, 1.5f, 1.5f, 1, 0, 1.0f);
                moveToNextQuestion(currentQuestion);
            }
        });
        submitButton.setBackgroundColor(getColor(R.color.white));
        submitButton.setTextColor(getColor(R.color.black));
        submitButton.startAnimation(fadeAnimation);
        submitButton.setText("Continue");

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
        totalQuestions = questions.size();

        Question firstQuestion = chooseNewQuestion();

        displayQuestionsRemaining(questions.size());

        displayQuestion(firstQuestion);
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

    String getGameOverMessage(int totalCorrect, int totalQuestions) {
        if (totalCorrect == totalQuestions) {
            return "You got all " + totalQuestions + " right! You won!";
        } else {
            return "You got " + totalCorrect + " right out of " + totalQuestions + ". Better luck next time!";
        }
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
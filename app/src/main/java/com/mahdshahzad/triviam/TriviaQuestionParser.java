package com.mahdshahzad.triviam;

import android.content.res.Resources;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TriviaQuestionParser {

    public static ArrayList<Question> parseTriviaQuestionsFromFiles(List<Pair<Integer, Integer>> filesAndQuestions, Resources resources, String packageName) {
        ArrayList<Question> questionList = new ArrayList<>();

        for (Pair<Integer, Integer> fileAndQuestions : filesAndQuestions) {
            int resourceId = fileAndQuestions.first;
            int numberOfQuestions = fileAndQuestions.second;

            try (InputStream inputStream = resources.openRawResource(resourceId)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                List<Question> allQuestions = new ArrayList<>(); // Store all questions from the file

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");

                    if (parts.length == 7) { // Ensure a valid line
                        String categoryId = parts[0]; // New format: Resource name
                        String questionString = parts[1];
                        String answerZero = parts[2];
                        String answerOne = parts[3];
                        String answerTwo = parts[4];
                        String answerThree = parts[5];
                        int correctAnswerIndex = Integer.parseInt(parts[6]);

                        Question question = new Question(
                                categoryId,
                                questionString,
                                answerZero,
                                answerOne,
                                answerTwo,
                                answerThree,
                                correctAnswerIndex
                        );

                        allQuestions.add(question);
                    }
                }

                // Shuffle the list of all questions randomly
                Collections.shuffle(allQuestions);

                // Pick the desired number of questions from the shuffled list
                for (int i = 0; i < numberOfQuestions; i++) {
                    questionList.add(allQuestions.get(i));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return questionList;
    }
}

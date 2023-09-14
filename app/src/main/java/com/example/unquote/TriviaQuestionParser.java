package com.example.unquote;

import android.content.res.Resources;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TriviaQuestionParser {

    public static ArrayList<Question> parseTriviaQuestionsFromFiles(List<Pair<Integer, Integer>> filesAndQuestions, Resources resources,String packageName) {
        ArrayList<Question> questionList = new ArrayList<>();

        for (Pair<Integer, Integer> fileAndQuestions : filesAndQuestions) {
            int resourceId = fileAndQuestions.first;
            int numberOfQuestions = fileAndQuestions.second;

            try (InputStream inputStream = resources.openRawResource(resourceId)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                int questionsRead = 0;

                while ((line = br.readLine()) != null && questionsRead < numberOfQuestions) {
                    String[] parts = line.split("\\|");

                    if (parts.length == 7) { // Ensure a valid line
                        String resourceName = parts[0]; // New format: Resource name
                        String questionString = parts[1];
                        String answerZero = parts[2];
                        String answerOne = parts[3];
                        String answerTwo = parts[4];
                        String answerThree = parts[5];
                        int correctAnswerIndex = Integer.parseInt(parts[6]);

                        // Calculate the image identifier using the resource name
                        int imageIdentifier = resources.getIdentifier(resourceName, "drawable",packageName);

                        Question question = new Question(
                                imageIdentifier,
                                questionString,
                                answerZero,
                                answerOne,
                                answerTwo,
                                answerThree,
                                correctAnswerIndex
                        );

                        questionList.add(question);
                        questionsRead++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return questionList;
    }


}

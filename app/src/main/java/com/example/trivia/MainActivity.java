package com.example.trivia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private TextView scoreTextView;
    private TextView highestScoreTextView;
    private Button trueBtn;
    private Button falseBtn;
    private ImageButton nextBtn;
    private ImageButton prevBtn;
    // Questions
    private int currentQIndex = 0;
    private List<Question> questionList;
    // Score
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.score_text);
        highestScoreTextView = findViewById(R.id.highest_score_text);
        nextBtn = findViewById(R.id.next_btn);
        prevBtn = findViewById(R.id.prev_btn);
        trueBtn = findViewById(R.id.true_btn);
        falseBtn = findViewById(R.id.false_btn);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);

        nextBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        falseBtn.setOnClickListener(this);
        trueBtn.setOnClickListener(this);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        highestScoreTextView.setText(MessageFormat.format("Highest Score: {0}", String.valueOf(prefs.getHighScore())));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQIndex).getAnswer());
                questionCounterTextView.setText((currentQIndex + 1) + " of " + questionList.size());
//                Log.d("Inside", "onCreate: " + questionArrayList);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev_btn:
                if (currentQIndex > 0)
                    currentQIndex = (currentQIndex - 1) % questionList.size();

                updateQuestion();
                break;
            case R.id.next_btn:
                currentQIndex = (currentQIndex + 1) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_btn:
                checkAnswer(true);
                break;
            case R.id.false_btn:
                checkAnswer(false);
                break;
        }
    }

    private void checkAnswer(boolean answer) {
        boolean answerIsTrue = questionList.get(currentQIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (answer == answerIsTrue) {
            addPoints();
            toastMessageId = R.string.correct_answer;
        } else {
            removePoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }

    private void addPoints() {
        scoreCounter += 10;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
    }

    private void removePoints() {
        scoreCounter -= 3;
        if (scoreCounter > 3) {
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        } else {
            score.setScore(0);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }
    }

    //                Update question depending on current index
    private void updateQuestion() {
        String question = questionList.get(currentQIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText((currentQIndex + 1) + " of " + questionList.size());
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        super.onPause();
    }
}
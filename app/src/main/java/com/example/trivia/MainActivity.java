package com.example.trivia;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trivia.data.QuestionBank;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new QuestionBank().getQuestions();
    }
}
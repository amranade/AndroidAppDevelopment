package com.example.captchaking.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.captchaking.R;
import com.example.captchaking.data.GameState;
import com.example.captchaking.data.GameStateManager;
import com.example.captchaking.data.UserAnswer;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private LinearLayout mUserAnswersView;
    private TextView mScoreView;
    private TextView mNetTimeView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        initViews();
        GameStateManager gameStateManager = GameStateManager.getGameStateManager();
        GameState gameState = gameStateManager.getGameState();
        bindViews(gameState);
    }

    private void bindViews(GameState gameState) {
        ArrayList<UserAnswer> userAnswers = gameState.getUserAnswers();
        for (UserAnswer userAnswer : userAnswers) {
            LinearLayout answerView = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.user_answer_view,null, false);
            mUserAnswersView.addView(answerView);
            ImageView captchaView = answerView.findViewById(R.id.result_captcha_view);
            TextView userAnswerView = answerView.findViewById(R.id.result_user_answer);
            TextView answerCorrectnessView = answerView.findViewById(R.id.result_user_answer_correctness);
            captchaView.setImageResource(userAnswer.captchaRes);
            userAnswerView.setText(userAnswer.userAnswer);
            answerCorrectnessView.setText(userAnswer.isCorrect ? "Correct" : "Incorrect");
        }
        mScoreView.setText("" + gameState.getGameScore() + "/" + gameState.getTotalReounds());
        mNetTimeView.setText("" + gameState.getTimeSpent() + "/" + gameState.getMaxGameTime());
    }

    private void initViews() {
        mUserAnswersView = findViewById(R.id.result_user_answers);
        mScoreView = findViewById(R.id.result_score_view);
        mNetTimeView = findViewById(R.id.result_time_view);
    }
}

package com.example.captchaking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.captchaking.R;
import com.example.captchaking.data.GameData;
import com.example.captchaking.data.GameState;
import com.example.captchaking.data.GameStateManager;

public class GamePlayActivity extends AppCompatActivity {
    public static final String GAME_START_DIFFICULTY_LEVEL = "game_start_difficulty_level";
    public static final int DEFAULT_DIFFICULTY = 3;

    private TextView mTimerView;
    private ImageView mCaptchaView;
    private EditText mUserAnswerView;
    private Button mUserSumbitButton;
    private CountDownTimer mCountDownTimer;
    private int mTimeSpent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Intent intent = getIntent();
        int startDifficulty = intent.getIntExtra(GAME_START_DIFFICULTY_LEVEL, DEFAULT_DIFFICULTY);
        intiViews();
        initGame(startDifficulty);
    }

    private void intiViews() {
        mTimerView = findViewById(R.id.gameplay_timer_view);
        mCaptchaView = findViewById(R.id.gameplay_captcha_view);
        mUserAnswerView = findViewById(R.id.gameplay_user_answer_view);
        mUserSumbitButton = findViewById(R.id.gameplay_submit_answer_view);
        mUserSumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCurrentAnswer(mUserAnswerView.getText().toString());
            }
        });
    }

    private void submitCurrentAnswer(String userAnswerStr) {
        GameState gameState = GameStateManager.getGameStateManager().getGameState();
        gameState.addUserAnswer(userAnswerStr, mTimeSpent);
        if (gameState.isGameFinished()) {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        } else {
            updateWithGameData(gameState.getCurrentGameData());
        }
    }

    private void updateWithGameData(GameData currentGameData) {
        updateViewsWithGame(currentGameData);
        updateTimerWithGame(currentGameData);
    }

    private void initGame(int startDifficulty) {
        // Get this from state later
        GameState gameState = GameStateManager.getGameStateManager().startNewGame(this, startDifficulty);
        GameData currentGameData = gameState.getCurrentGameData();
        updateWithGameData(currentGameData);
    }

    private void updateTimerWithGame(GameData currentGameData) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        final int maxTime = currentGameData.maxTime;
        setRemainingTime(maxTime);
        mTimeSpent = 0;
        mCountDownTimer = new CountDownTimer(maxTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int timeRemainMs = (int) (millisUntilFinished / 1000);
                setRemainingTime(timeRemainMs);
                mTimeSpent = maxTime - timeRemainMs;
            }

            @Override
            public void onFinish() {
                mTimeSpent = maxTime;
                submitCurrentAnswer("");
            }
        };
        mCountDownTimer.start();
    }

    private void setRemainingTime(int timeRemainMs) {
        String timeRemainingStr =
                String.format("%02d:%02d", timeRemainMs / 60, timeRemainMs % 60);
        mTimerView.setText(timeRemainingStr);
    }

    private void updateViewsWithGame(GameData gameData) {
        mCaptchaView.setImageResource(gameData.captchaRes);
        mUserAnswerView.setText("");
        mUserAnswerView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mUserAnswerView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
}

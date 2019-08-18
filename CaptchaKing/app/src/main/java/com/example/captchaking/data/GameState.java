package com.example.captchaking.data;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.captchaking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    private static final int MAX_DIFFICULTY = 5;
    private static final int MAX_ROUNDS = 5;


    private ArrayList<UserAnswer> mUserAnswers;
    private HashMap<Integer, ArrayList<GameData>> mPossibleGames;
    private HashMap<Integer, Integer> mDifficultyGame;
    private boolean isGameFinished;
    private int mGameScore;
    private int mTimeSpent;
    private int mMaxGameTime;
    private int mCurrentDifficulty;

    public int getRoundsPlayed() {
        return mRoundsPlayed;
    }

    private int mRoundsPlayed;

    public GameState(Context context, int startDifficultyLevel) {
        mUserAnswers = new ArrayList<>();
        mPossibleGames = new HashMap<>();
        mDifficultyGame = new HashMap<>();
        mCurrentDifficulty = startDifficultyLevel;
        mMaxGameTime = 0;
        mGameScore = 0;
        mRoundsPlayed = 0;
        initPossibleData(
                context,
                "../../../assets/captcha.json");
    }

    @SuppressLint("NewApi")
    private void initPossibleData(Context context, String jsonPath) {
        try {
            InputStream is = context.getAssets().open("captcha.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonData = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GameData gameData = new GameData();
                int difficulty = jsonObject.getInt("difficulty");
                gameData.maxTime = getMaxTime(difficulty);
                String captchaName = jsonObject.getString("name");
                int drawable = context.getResources()
                        .getIdentifier(captchaName, "drawable", context.getPackageName());
                gameData.captchaRes = drawable;
                gameData.expectedAnswer = jsonObject.getString("answer");
                if (!mPossibleGames.containsKey(difficulty-1)) {
                    mPossibleGames.put(difficulty-1, new ArrayList<GameData>());
                }
                mPossibleGames.get(difficulty-1).add(gameData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0;i<MAX_DIFFICULTY;++i) {
            mDifficultyGame.put(i, 0);
        }
    }

    private int getMaxTime(int difficulty) {
        return 15 + 5*difficulty;
    }

    public GameData getCurrentGameData() {
        return mPossibleGames.get(mCurrentDifficulty-1).get(mDifficultyGame.get(mCurrentDifficulty-1));
    }

    public ArrayList<UserAnswer> getUserAnswers() {
        return mUserAnswers;
    }

    public void addUserAnswer(String userAnswerStr, int timeTaken) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.userAnswer = userAnswerStr;
        GameData currentGameData = getCurrentGameData();
        userAnswer.captchaRes = currentGameData.captchaRes;
        boolean isCorrect = currentGameData.expectedAnswer.equals(userAnswerStr);
        userAnswer.isCorrect = isCorrect;
        mUserAnswers.add(userAnswer);

        mMaxGameTime += getMaxTime(mCurrentDifficulty);
        mDifficultyGame.put(mCurrentDifficulty-1, mDifficultyGame.get(mCurrentDifficulty-1) + 1);
        mTimeSpent += timeTaken;
        mRoundsPlayed++;
        if (isCorrect) {
            mGameScore++;
            if (mCurrentDifficulty < MAX_DIFFICULTY) {
                mCurrentDifficulty++;
            }
        } else {
            mCurrentDifficulty--;
            if (mCurrentDifficulty == 0) {
                endGame();
            }
        }
        if (mRoundsPlayed == MAX_ROUNDS) {
            endGame();
        }
    }

    private void endGame() {
        isGameFinished = true;
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public int getTotalReounds() {
        return MAX_ROUNDS;
    }

    public int getGameScore() {
        return mGameScore;
    }

    public int getTimeSpent() {
        return mTimeSpent;
    }

    public int getMaxGameTime() {
        return mMaxGameTime;
    }
}

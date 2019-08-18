package com.example.captchaking.data;

import android.content.Context;

public class GameStateManager {
    private static GameStateManager gameStateManagerInstance;
    public static GameStateManager getGameStateManager() {
        if (gameStateManagerInstance == null) {
            gameStateManagerInstance = new GameStateManager();
        }
        return gameStateManagerInstance;
    }
    private GameState mGameState;

    public GameState getGameState() {
        return mGameState;
    }

    public GameState startNewGame(Context context, int startDifficultyLevel) {
        mGameState = new GameState(context, startDifficultyLevel);
        return mGameState;
    }
}

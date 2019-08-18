package com.example.picturepuzzle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class MainActivity extends AppCompatActivity {

    private static final int TILE_SIZE = 250;
    private static final int TILE_MARGIN = 10;

    GameState mGameState;
    FrameLayout mPuzzleParent;
    Button mGamePlayButton;
    int mCurrentTileIdx;
    float mTileMoveStartX, mTileMoveStartY;
    boolean mIsTileMoving;
    Spring mSpring;
    boolean preventTouch;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPuzzleParent = findViewById(R.id.puzzle_parent);
        mGamePlayButton = findViewById(R.id.game_play_button);
        mGamePlayButton.setText(getString(R.string.try_again_button));
        mGamePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameState.newGamePositionSet();
                renderWithGame();
            }
        });
        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();
        mSpring.setOvershootClampingEnabled(true);
        mSpring.setSpringConfig(new SpringConfig(80, 7));

        int tileSize = TILE_SIZE;
        mGameState = new GameState(this);
        for (int i=1;i<4*4;++i) {
            final int imageId = i;
            ImageView tileView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tileSize, tileSize);
            tileView.setLayoutParams(layoutParams);
            tileView.setId(i);
            tileView.setClickable(false);
            tileView.setImageBitmap(mGameState.tileImages.get(i));
            mPuzzleParent.addView(tileView);
//            tileView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onImageClicked(imageId);
//                }
//            });
        }
        mPuzzleParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (preventTouch) return false;
                float touchX = event.getX(), touchY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int tileX = (int) (touchX / (TILE_SIZE + TILE_MARGIN));
                    int tileY = (int) (touchY / (TILE_SIZE + TILE_MARGIN));
                    if (!(tileX >=0 && tileY >= 0 && tileX < 4 && tileY < 4)) {
                        return false;
                    }
                    int idx = tileY * 4 + tileX;
                    if (!mGameState.canMoveToEmpty(mGameState.tilePositions[idx]))
                        return false;
                    mIsTileMoving = true;
                    mCurrentTileIdx = idx;
                    mTileMoveStartX = touchX;
                    mTileMoveStartY = touchY;
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!mIsTileMoving) return false;
                    moveCurrentTile(
                            event.getX() - mTileMoveStartX, event.getY() - mTileMoveStartY);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!mIsTileMoving) return false;
                    moveCurrentTileToEnd(
                            event.getX() - mTileMoveStartX, event.getY() - mTileMoveStartY);
                    return true;
                }
                return false;
            }
        });
        renderWithGame();
    }

    private void moveCurrentTile(float currentX, float currentY) {
        Log.e("MyDebug", "currentMove: " + currentX + " " + currentY);
        ImageView currentTileView =
                mPuzzleParent.findViewById(mGameState.tilePositions[mCurrentTileIdx]);
        int defaultTopX = (mCurrentTileIdx % 4) * (TILE_MARGIN + TILE_SIZE);
        int defaultTopY = (mCurrentTileIdx / 4) * (TILE_MARGIN + TILE_SIZE);

        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) currentTileView.getLayoutParams();
        int emptyTileIdx = mGameState.getImageIdx(0);
        if (emptyTileIdx == (mCurrentTileIdx - 4)) {
            // moving up.
            int moveY = (int) currentY;
            if (moveY > 0) return;
            if (moveY + TILE_MARGIN + TILE_SIZE < 0) return;
            layoutParams.topMargin = defaultTopY + moveY;
        } else if (emptyTileIdx == (mCurrentTileIdx + 4)) {
            // moving down.
            int moveY = (int) currentY;
            if (moveY < 0) return;
            if (TILE_MARGIN + TILE_SIZE < moveY) return;
            layoutParams.topMargin = defaultTopY + moveY;
        } else if (emptyTileIdx == (mCurrentTileIdx + 1)) {
            // moving right
            int moveX = (int) currentX;
            if (moveX < 0) return;
            if (TILE_MARGIN + TILE_SIZE < moveX) return;
            layoutParams.leftMargin = defaultTopX + moveX;
        } else if (emptyTileIdx == (mCurrentTileIdx - 1)) {
            // moving left
            int moveX = (int) currentX;
            if (moveX > 0) return;
            if (TILE_MARGIN + TILE_SIZE + moveX < 0) return;
            layoutParams.leftMargin = defaultTopX + moveX;
        }
        currentTileView.requestLayout();
    }

    private void moveCurrentTileToEnd(float moveX, float moveY) {
        final int defaultTopX = (mCurrentTileIdx % 4) * (TILE_MARGIN + TILE_SIZE);
        final int defaultTopY = (mCurrentTileIdx / 4) * (TILE_MARGIN + TILE_SIZE);

        int emptyTileIdx = mGameState.getImageIdx(0);
        final int finalTopX = (emptyTileIdx % 4) * (TILE_MARGIN + TILE_SIZE);
        final int finalTopY = (emptyTileIdx / 4) * (TILE_MARGIN + TILE_SIZE);

        float currentMove = 0;

        if (emptyTileIdx == (mCurrentTileIdx - 4)) {
            // moving up.
            currentMove = -moveY / (TILE_MARGIN + TILE_SIZE);
//            int moveY = (int) currentY;
//            if (moveY > 0) return;
//            if (moveY + TILE_MARGIN + TILE_SIZE < 0) return;
//            layoutParams.topMargin = defaultTopY + moveY;
        } else if (emptyTileIdx == (mCurrentTileIdx + 4)) {
            currentMove = moveY / (TILE_MARGIN + TILE_SIZE);
//            // moving down.
//            int moveY = (int) currentY;
//            if (moveY < 0) return;
//            if (TILE_MARGIN + TILE_SIZE > moveY) return;
//            layoutParams.topMargin = defaultTopY + moveY;
        } else if (emptyTileIdx == (mCurrentTileIdx + 1)) {
            currentMove = moveX / (TILE_MARGIN + TILE_SIZE);
//            // moving right
//            int moveX = (int) currentX;
//            if (moveX < 0) return;
//            if (TILE_MARGIN + TILE_SIZE > moveX) return;
//            layoutParams.leftMargin = defaultTopX + moveX;
        } else if (emptyTileIdx == (mCurrentTileIdx - 1)) {
            currentMove = -moveX / (TILE_MARGIN + TILE_SIZE);
//            // moving left
//            int moveX = (int) currentX;
//            if (moveX > 0) return;
//            if (TILE_MARGIN + TILE_SIZE + moveX < 0) return;
//            layoutParams.leftMargin = defaultTopX + moveX;
        }

        currentMove = Math.min(1.0f, Math.max(currentMove, 0.0f));
        Log.e("MyDebug", "ending tile move: " + mCurrentTileIdx + " to move: "+currentMove);
        if (currentMove == 0.0f) return;
        if (currentMove == 1.0f) {
            Log.e("MyDebug", "update game with click at: " + mCurrentTileIdx);
            mGameState.onImageClicked(mGameState.tilePositions[mCurrentTileIdx]);
            renderWithGame();
            return;
        }
        float moveTo = 0.0f;
        if (currentMove > 0.5f) {
            moveTo = 1.0f;
        }
//        if (moveTo == 0.0f) {
//            moveCurrentTileTo(defaultTopX, defaultTopY);
//        } else {
//            moveCurrentTileTo(finalTopX, finalTopY);
//            mGameState.onImageClicked(mGameState.tilePositions[mCurrentTileIdx]);
//        }

        mSpring.setCurrentValue(currentMove);
        mSpring.addListener(new SimpleSpringListener() {
            public void onSpringUpdate(Spring spring) {
                Log.e("MyDebug", "moving tile: " + mCurrentTileIdx);
                double currentMove = spring.getCurrentValue();
                ImageView currentTileView =
                        mPuzzleParent.findViewById(mGameState.tilePositions[mCurrentTileIdx]);
                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) currentTileView.getLayoutParams();
                layoutParams.leftMargin =
                        (int) (defaultTopX * (1 - currentMove) + finalTopX * currentMove);
                layoutParams.topMargin =
                        (int) (defaultTopY * (1 - currentMove) + finalTopY * currentMove);
                mPuzzleParent.requestLayout();
            }

            public void onSpringAtRest(Spring spring) {
                preventTouch = false;
                if (spring.getCurrentValue() == 0.0) return;
                Log.e("MyDebug", "update game with click at: " + mCurrentTileIdx);
                mGameState.onImageClicked(mGameState.tilePositions[mCurrentTileIdx]);
                renderWithGame();
                mSpring.removeAllListeners();
            }
        });
        mSpring.setEndValue(moveTo);
        preventTouch = true;
    }

    private void moveCurrentTileTo(int x, int y) {
        ImageView currentTileView =
                mPuzzleParent.findViewById(mGameState.tilePositions[mCurrentTileIdx]);
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) currentTileView.getLayoutParams();
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        mPuzzleParent.requestLayout();
    }

    private void renderWithGame() {
        if (mGameState.isWon()) {
            mGamePlayButton.setText(getString(R.string.you_won_text));
        } else {
            mGamePlayButton.setText(getString(R.string.try_again_button));
        }
        int tileOffset = TILE_SIZE + TILE_MARGIN;
        for (int i=0;i < mGameState.tilePositions.length; ++i) {
            if (mGameState.tilePositions[i] == 0) continue;
            ImageView tileView = mPuzzleParent.findViewById(mGameState.tilePositions[i]);
            FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) tileView.getLayoutParams();
            layoutParams.topMargin = (i / 4) * tileOffset;
            layoutParams.leftMargin = (i%4) * tileOffset;
            tileView.setLayoutParams(layoutParams);
        }
        mPuzzleParent.requestLayout();
    }
}

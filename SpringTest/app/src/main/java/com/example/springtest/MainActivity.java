package com.example.springtest;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

public class MainActivity extends AppCompatActivity {

    FrameLayout mJumpingBox;
    FrameLayout mMainView;
    Spring mSpring;
    float mStartTouchY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainView = findViewById(android.R.id.content);
        mJumpingBox = findViewById(R.id.jumping_box);
        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();
        mSpring.setOvershootClampingEnabled(true);
        mSpring.setCurrentValue(0);
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                putBoxToY((int) spring.getCurrentValue());
            }
        });

        mMainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mStartTouchY = event.getY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    mSpring.setCurrentValue(event.getY() - mStartTouchY);
                } else if (action == MotionEvent.ACTION_UP) {
                    mSpring.setEndValue(0);
                }
                return true;
            }
        });
    }

    private void putBoxToY(int position) {
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) mJumpingBox.getLayoutParams();
        layoutParams.topMargin = position;
        mJumpingBox.requestLayout();
    }
}

package com.example.captchaking.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.captchaking.R;

public class MainActivity extends AppCompatActivity {


    private Button mStartNewGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartNewGameButton = findViewById(R.id.new_game_start_button);
        mStartNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameplay();
            }
        });
    }

    private void startGameplay() {
        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);
        intent.putExtra(
                GamePlayActivity.GAME_START_DIFFICULTY_LEVEL,
                GamePlayActivity.DEFAULT_DIFFICULTY);
        startActivity(intent);
    }
}

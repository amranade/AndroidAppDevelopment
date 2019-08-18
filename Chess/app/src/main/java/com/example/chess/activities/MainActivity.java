package com.example.chess.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chess.R;

/**
 * This activity shows the games going on for users.
 * User should be able to make game requests to other users using dialog
 * It shows list of:
 * 1. User sent request
 * 2. Notification from other user
 * 3. Ongoing game
 * 4. Past games
 * If the user has not logged in, redirect to LoginActivity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

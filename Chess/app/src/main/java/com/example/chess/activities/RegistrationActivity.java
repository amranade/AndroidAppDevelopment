package com.example.chess.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.chess.R;

/**
 * Asks for username, password, name etc.
 * Cancel moves back to LoginActivity
 * Registration failure: Show failure info
 * Registration success: Redirect to LoginActivity with message
 */
public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle(getString(R.string.registraction_activity_title));
    }
}

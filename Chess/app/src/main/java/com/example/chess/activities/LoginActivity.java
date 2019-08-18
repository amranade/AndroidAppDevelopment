package com.example.chess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.chess.R;

/**
 * It makes request for login to get auth token back
 * If succeed: Redirect to MainActivity
 * If failed: Show error
 * Link to Registration activity
 */
public class LoginActivity extends AppCompatActivity {

    TextView mRegistrationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRegistrationLink = findViewById(R.id.registration_link);
        mRegistrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.example.kharcha.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.kharcha.R;
import com.example.kharcha.data.LoginInformation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView userNameTextView = findViewById(R.id.user_name);
        userNameTextView.setText(LoginInformation.getName());
    }
}

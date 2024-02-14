package com.travelapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Chat_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ativity);

        String name = getIntent().getStringExtra("Name");

        TextView userNameTextView = findViewById(R.id.usernameTextView);
        userNameTextView.setText(name);
    }
}

package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManagingDirectorMyAgentSimpleActivity extends AppCompatActivity {

    private static final String TAG = "ManagingDirectorMyAgentSimple";

    // UI Elements
    private TextView welcomeText;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_my_agent_simple);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // View Agents Box
        findViewById(R.id.viewAgentsBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorAgentListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        
    }

    private void updateWelcomeMessage() {
        if (firstName != null && lastName != null) {
            welcomeText.setText("Welcome, " + firstName + " " + lastName + "!");
        } else if (username != null) {
            welcomeText.setText("Welcome, " + username + "!");
        } else {
            welcomeText.setText("Welcome, Managing Director!");
        }
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }
}

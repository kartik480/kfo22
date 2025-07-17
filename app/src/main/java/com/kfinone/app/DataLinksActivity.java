package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DataLinksActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout dataLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Data Links boxes
    private LinearLayout myDataLinksBox;
    private LinearLayout teamDataLinksBox;

    // Count displays
    private TextView myDataLinksCount;
    private TextView teamDataLinksCount;

    // Welcome elements
    private TextView welcomeText;
    private TextView userInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_links);

        initializeViews();
        setupClickListeners();
        loadDataLinksData();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        dataLinksButton = findViewById(R.id.dataLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Data Links boxes
        myDataLinksBox = findViewById(R.id.myDataLinksBox);
        teamDataLinksBox = findViewById(R.id.teamDataLinksBox);

        // Count displays
        myDataLinksCount = findViewById(R.id.myDataLinksCount);
        teamDataLinksCount = findViewById(R.id.teamDataLinksCount);

        // Welcome elements
        welcomeText = findViewById(R.id.welcomeText);
        userInfoText = findViewById(R.id.userInfoText);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewDataLink());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        dataLinksButton.setOnClickListener(v -> {
            // Already on data links page, just show feedback
            Toast.makeText(this, "Data Links", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // Data Links box click listeners
        myDataLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyDataLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamDataLinksBox.setOnClickListener(v -> {
            Toast.makeText(this, "Team Data Links - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Team Data Links activity
        });
    }

    private void goBack() {
        // Go back to special panel
        Intent intent = new Intent(this, SpecialPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing data links...", Toast.LENGTH_SHORT).show();
        loadDataLinksData();
    }

    private void addNewDataLink() {
        Toast.makeText(this, "Add New Data Link - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Data Link activity
    }

    private void loadDataLinksData() {
        // TODO: Load real data links from server
        // For now, set some sample data
        myDataLinksCount.setText("8 connections");
        teamDataLinksCount.setText("32 connections");
    }

    private void updateWelcomeMessage() {
        // Get user info from intent
        Intent intent = getIntent();
        if (intent != null) {
            String firstName = intent.getStringExtra("FIRST_NAME");
            String lastName = intent.getStringExtra("LAST_NAME");
            String fullName = intent.getStringExtra("USERNAME");
            
            String welcomeMessage;
            if (firstName != null && !firstName.isEmpty() && firstName.length() > 1) {
                welcomeMessage = "Welcome, " + firstName + "!";
            } else if (fullName != null && !fullName.isEmpty()) {
                String[] nameParts = fullName.split("\\s+");
                if (nameParts.length > 0 && nameParts[0].length() > 1) {
                    welcomeMessage = "Welcome, " + nameParts[0] + "!";
                } else {
                    welcomeMessage = "Welcome, " + fullName + "!";
                }
            } else {
                welcomeMessage = "Welcome to Data Links!";
            }
            
            welcomeText.setText(welcomeMessage);
        }
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            if (userId == null) {
                userId = currentIntent.getStringExtra("userId");
            }
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (userId != null) intent.putExtra("userId", userId);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RBHPolicyManagementActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    private String sourcePanel;
    
    // UI elements
    private TextView welcomeText;
    private TextView policyCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Debug logging
        android.util.Log.d("RBHPolicy", "RBHPolicyManagementActivity onCreate started");
        
        setContentView(R.layout.activity_rbh_policy_management);
        
        android.util.Log.d("RBHPolicy", "Layout set successfully");

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        android.util.Log.d("RBHPolicy", "User data received - Username: " + userName + ", UserID: " + userId);
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        
        android.util.Log.d("RBHPolicy", "RBHPolicyManagementActivity onCreate completed");
    }
    
    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        
        // Policy views
        welcomeText = findViewById(R.id.welcomeText);
        policyCount = findViewById(R.id.policyCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPolicy());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void goBack() {
        Intent intent;
        
        // Navigate back based on source panel
        if ("RBH_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        } else {
            // Default fallback
            intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        }
        
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing policy data...", Toast.LENGTH_SHORT).show();
        // TODO: Implement policy data refresh
    }

    private void addNewPolicy() {
        Toast.makeText(this, "Add New Policy - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to add policy activity
    }
    
    private void updateWelcomeMessage() {
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " " + lastName);
        } else if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName);
        } else if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Welcome, " + userName);
        } else {
            welcomeText.setText("Welcome, Regional Business Head");
        }
    }
    
    private void passUserDataToIntent(Intent intent) {
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}

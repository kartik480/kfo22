package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOWorkLinksActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Work Links boxes
    private LinearLayout myWorkLinksBox;
    private LinearLayout teamWorkLinksBox;

    // Count displays
    private TextView myWorkLinksCount;
    private TextView teamWorkLinksCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_work_links);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadCBOWorkLinksData();
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

        // CBO Work Links boxes
        myWorkLinksBox = findViewById(R.id.myWorkLinksBox);
        teamWorkLinksBox = findViewById(R.id.teamWorkLinksBox);

        // Count displays
        myWorkLinksCount = findViewById(R.id.myWorkLinksCount);
        teamWorkLinksCount = findViewById(R.id.teamWorkLinksCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewWorkLink());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // CBO Work Links boxes click listeners
        myWorkLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOMyWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamWorkLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOTeamWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing CBO work links...", Toast.LENGTH_SHORT).show();
        loadCBOWorkLinksData();
    }

    private void addNewWorkLink() {
        Toast.makeText(this, "Add New Work Link - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Work Link activity
    }

    private void loadCBOWorkLinksData() {
        // TODO: Load real CBO work links data from server
        // For now, set some sample data
        myWorkLinksCount.setText("My Work Links");
        teamWorkLinksCount.setText("Team Work Links");
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
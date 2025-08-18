package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RBHSdsaActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // RBH SDSA boxes
    private CardView mySdsaBox;
    private CardView teamSdsaBox;

    // Count displays
    private TextView mySdsaCount;
    private TextView teamSdsaCount;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadRBHSdsaData();
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

        // RBH SDSA boxes
        mySdsaBox = findViewById(R.id.mySdsaBox);
        teamSdsaBox = findViewById(R.id.teamSdsaBox);

        // Count displays
        mySdsaCount = findViewById(R.id.mySdsaCount);
        teamSdsaCount = findViewById(R.id.teamSdsaCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewSdsa());

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
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // RBH SDSA box click listeners
        mySdsaBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHMySdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamSdsaBox.setOnClickListener(v -> {
            Toast.makeText(this, "Team SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Team SDSA activity
        });
    }

    private void goBack() {
        // Navigate back to RBH Panel
        Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("FIRST_NAME", firstName);
        intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing RBH SDSA data...", Toast.LENGTH_SHORT).show();
        loadRBHSdsaData();
    }

    private void addNewSdsa() {
        Toast.makeText(this, "Add New SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add SDSA activity
    }

    private void loadRBHSdsaData() {
        // TODO: Load real RBH SDSA data from server
        // For now, set some sample data
        mySdsaCount.setText("My SDSA");
        teamSdsaCount.setText("Team SDSA");
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
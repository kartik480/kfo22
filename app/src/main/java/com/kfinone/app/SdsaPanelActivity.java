package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SdsaPanelActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout sdsaButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // SDSA boxes
    private LinearLayout mySdsaBox;
    private LinearLayout sdsaTeamBox;

    // Count displays
    private TextView mySdsaCount;
    private TextView sdsaTeamCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdsa_panel);

        initializeViews();
        setupClickListeners();
        loadSdsaData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        sdsaButton = findViewById(R.id.sdsaButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // SDSA boxes
        mySdsaBox = findViewById(R.id.mySdsaBox);
        sdsaTeamBox = findViewById(R.id.sdsaTeamBox);

        // Count displays
        mySdsaCount = findViewById(R.id.mySdsaCount);
        sdsaTeamCount = findViewById(R.id.sdsaTeamCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewSdsa());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            startActivity(intent);
            finish();
        });

        sdsaButton.setOnClickListener(v -> {
            // Already on SDSA page, just show feedback
            Toast.makeText(this, "SDSA Management", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // SDSA box click listeners
        mySdsaBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MySdsaActivity.class);
            startActivity(intent);
        });

        sdsaTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, SdsaTeamActivity.class);
            startActivity(intent);
        });
    }

    private void goBack() {
        // Go back to special panel (home)
        Intent intent = new Intent(this, SpecialPanelActivity.class);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing SDSA data...", Toast.LENGTH_SHORT).show();
        loadSdsaData();
    }

    private void addNewSdsa() {
        Toast.makeText(this, "Add New SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add SDSA activity
    }

    private void loadSdsaData() {
        // TODO: Load real SDSA data from server
        // For now, set some sample data
        mySdsaCount.setText("25 SDSA");
        sdsaTeamCount.setText("15 teams");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
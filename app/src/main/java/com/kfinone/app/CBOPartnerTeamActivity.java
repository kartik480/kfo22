package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOPartnerTeamActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_partner_team);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadPartnerTeamData();
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
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPartner());

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
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOPartnerActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing partner team data...", Toast.LENGTH_SHORT).show();
        loadPartnerTeamData();
    }

    private void addNewPartner() {
        Toast.makeText(this, "Add New Partner - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Partner activity
    }

    private void loadPartnerTeamData() {
        // TODO: Load real partner team data from server
        // For now, show placeholder content
        Toast.makeText(this, "Loading partner team data...", Toast.LENGTH_SHORT).show();
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
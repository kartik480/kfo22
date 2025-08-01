package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOEmpLinksActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Emp Links boxes
    private LinearLayout myEmpLinksBox;
    private LinearLayout teamEmpLinksBox;

    // Count displays
    private TextView myEmpLinksCount;
    private TextView teamEmpLinksCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadCBOEmpLinksData();
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

        // CBO Emp Links boxes
        myEmpLinksBox = findViewById(R.id.myEmpLinksBox);
        teamEmpLinksBox = findViewById(R.id.teamEmpLinksBox);

        // Count displays
        myEmpLinksCount = findViewById(R.id.myEmpLinksCount);
        teamEmpLinksCount = findViewById(R.id.teamEmpLinksCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmpLink());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            // Already on emp links page, just show feedback
            Toast.makeText(this, "Employee Links", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // CBO Emp Links boxes click listeners
        myEmpLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOMyEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamEmpLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOTeamEmpLinksActivity.class);
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
        Toast.makeText(this, "Refreshing CBO employee links data...", Toast.LENGTH_SHORT).show();
        loadCBOEmpLinksData();
    }

    private void addNewEmpLink() {
        Toast.makeText(this, "Add New Emp Link - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Emp Link activity
    }

    private void loadCBOEmpLinksData() {
        // TODO: Load real CBO employee links data from server
        // For now, set some sample data
        myEmpLinksCount.setText("My Employee Links");
        teamEmpLinksCount.setText("Team Employee Links");
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
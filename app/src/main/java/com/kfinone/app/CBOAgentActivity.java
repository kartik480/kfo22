package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOAgentActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Agent boxes
    private LinearLayout addAgentBox;
    private LinearLayout myAgentBox;
    private LinearLayout agentTeamBox;

    // Count displays
    private TextView addAgentCount;
    private TextView myAgentCount;
    private TextView agentTeamCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_agent);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadCBOAgentData();
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

        // CBO Agent boxes
        addAgentBox = findViewById(R.id.addAgentBox);
        myAgentBox = findViewById(R.id.myAgentBox);
        agentTeamBox = findViewById(R.id.agentTeamBox);

        // Count displays
        addAgentCount = findViewById(R.id.addAgentCount);
        myAgentCount = findViewById(R.id.myAgentCount);
        agentTeamCount = findViewById(R.id.agentTeamCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewAgent());

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

        // CBO Agent box click listeners
        addAgentBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAddAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        myAgentBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOMyAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        agentTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAgentTeamActivity.class);
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
        Toast.makeText(this, "Refreshing CBO agent data...", Toast.LENGTH_SHORT).show();
        loadCBOAgentData();
    }

    private void addNewAgent() {
        Toast.makeText(this, "Add New Agent - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Agent activity
    }

    private void loadCBOAgentData() {
        // TODO: Load real CBO agent data from server
        // For now, set some sample data
        addAgentCount.setText("Add Agent");
        myAgentCount.setText("My Agent");
        agentTeamCount.setText("Agent Team");
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
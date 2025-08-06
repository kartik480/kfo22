package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOPartnerActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Partner boxes
    private LinearLayout addPartnerBox;
    private LinearLayout myPartnerBox;
    private LinearLayout partnerTeamBox;

    // Count displays
    private TextView addPartnerCount;
    private TextView myPartnerCount;
    private TextView partnerTeamCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_partner);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadCBOPartnerData();
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

        // CBO Partner boxes
        addPartnerBox = findViewById(R.id.addPartnerBox);
        myPartnerBox = findViewById(R.id.myPartnerBox);
        partnerTeamBox = findViewById(R.id.partnerTeamBox);

        // Count displays
        addPartnerCount = findViewById(R.id.addPartnerCount);
        myPartnerCount = findViewById(R.id.myPartnerCount);
        partnerTeamCount = findViewById(R.id.partnerTeamCount);
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

        // CBO Partner box click listeners
        addPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAddPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        myPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CboMyPartnerUsersActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        partnerTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPartnerTeamActivity.class);
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
        Toast.makeText(this, "Refreshing CBO partner data...", Toast.LENGTH_SHORT).show();
        loadCBOPartnerData();
    }

    private void addNewPartner() {
        Toast.makeText(this, "Add New Partner - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Partner activity
    }

    private void loadCBOPartnerData() {
        // TODO: Load real CBO partner data from server
        // For now, set some sample data
        addPartnerCount.setText("Add Partner");
        myPartnerCount.setText("My Partner");
        partnerTeamCount.setText("Partner Team");
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
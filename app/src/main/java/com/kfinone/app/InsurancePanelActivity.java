package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class InsurancePanelActivity extends AppCompatActivity {

    private MaterialCardView addInsuranceBox;
    private MaterialCardView myInsuranceBox;
    private MaterialCardView insuranceTeamBox;
    private TextView backButton;
    private View refreshButton;
    private View addButton;
    private View dashboardButton;
    private View insuranceButton;
    private View reportsButton;
    private View settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_panel);

        initializeViews();
        setupClickListeners();
        loadInsuranceData();
    }

    private void initializeViews() {
        addInsuranceBox = findViewById(R.id.addInsuranceBox);
        myInsuranceBox = findViewById(R.id.myInsuranceBox);
        insuranceTeamBox = findViewById(R.id.insuranceTeamBox);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        dashboardButton = findViewById(R.id.dashboardButton);
        insuranceButton = findViewById(R.id.insuranceButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewInsurance());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            startActivity(intent);
            finish();
        });

        insuranceButton.setOnClickListener(v -> {
            // Already on insurance page, just show feedback
            Toast.makeText(this, "Insurance Management", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // Insurance box click listeners
        addInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddInsuranceActivity.class);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });

        myInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyInsuranceActivity.class);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });

        insuranceTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsuranceTeamActivity.class);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("SPECIAL_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("CBO_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default to SpecialPanelActivity
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        }
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
        }
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing insurance data...", Toast.LENGTH_SHORT).show();
        loadInsuranceData();
    }

    private void addNewInsurance() {
        Intent intent = new Intent(this, AddInsuranceActivity.class);
        intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
        startActivity(intent);
    }

    private void loadInsuranceData() {
        // TODO: Load insurance data from server
        // For now, just show a loading message
        Toast.makeText(this, "Loading insurance data...", Toast.LENGTH_SHORT).show();
    }
} 
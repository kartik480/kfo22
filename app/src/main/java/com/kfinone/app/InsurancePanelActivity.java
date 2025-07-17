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
            startActivity(intent);
        });

        myInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyInsuranceActivity.class);
            startActivity(intent);
        });

        insuranceTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsuranceTeamActivity.class);
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
        Toast.makeText(this, "Refreshing insurance data...", Toast.LENGTH_SHORT).show();
        loadInsuranceData();
    }

    private void addNewInsurance() {
        Intent intent = new Intent(this, AddInsuranceActivity.class);
        startActivity(intent);
    }

    private void loadInsuranceData() {
        // TODO: Load insurance data from server
        // For now, just show a loading message
        Toast.makeText(this, "Loading insurance data...", Toast.LENGTH_SHORT).show();
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class RBHInsurancePanelActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_rbh_insurance_panel);

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
            // Simply finish the current activity to go back to the previous screen
            finish();
        });

        insuranceButton.setOnClickListener(v -> {
            // Already on insurance page, just show feedback
            Toast.makeText(this, "RBH Insurance Management", Toast.LENGTH_SHORT).show();
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
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });

        myInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyInsuranceActivity.class);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });

        insuranceTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsuranceTeamActivity.class);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
    }

    private void goBack() {
        // Simply finish the current activity to go back to the previous screen
        finish();
    }

    private void refreshData() {
        // TODO: Implement refresh functionality
        Toast.makeText(this, "Refreshing insurance data...", Toast.LENGTH_SHORT).show();
        loadInsuranceData();
    }

    private void addNewInsurance() {
        Intent intent = new Intent(this, AddInsuranceActivity.class);
        intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
        startActivity(intent);
    }

    private void loadInsuranceData() {
        // TODO: Load insurance data from API or database
        // This method can be implemented to fetch and display insurance statistics
    }

    private void passUserDataToIntent(Intent intent) {
        // Pass any user data that was received from the previous activity
        if (getIntent().hasExtra("USERNAME")) {
            intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
        }
        if (getIntent().hasExtra("USER_ID")) {
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
        }
        if (getIntent().hasExtra("FIRST_NAME")) {
            intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
        }
        if (getIntent().hasExtra("LAST_NAME")) {
            intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
        }
    }
    
    @Override
    public void onBackPressed() {
        // Handle hardware back button press
        goBack();
    }
} 
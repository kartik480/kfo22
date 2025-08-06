package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RBHEmpMasterActivity extends AppCompatActivity {
    private TextView titleText, welcomeText, activeEmpCount, inactiveEmpCount;
    private View backButton, refreshButton, addButton;
    private View activeEmpListBox, inactiveEmpListBox;
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_rbh_emp_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        updateStats();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        activeEmpCount = findViewById(R.id.activeEmpCount);
        inactiveEmpCount = findViewById(R.id.inactiveEmpCount);
        
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        activeEmpListBox = findViewById(R.id.activeEmpListBox);
        inactiveEmpListBox = findViewById(R.id.inactiveEmpListBox);
        
        // rbhBottomNav = findViewById(R.id.rbhBottomNav); // Removed as per edit hint
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Refresh button
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
            updateStats();
        });

        // Add button
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add employee activity
        });

        // Active Emp List Box
        activeEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegionalBusinessHeadActiveEmpListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userName); // Using username as user ID for now
            intent.putExtra("SOURCE_PANEL", "RBH_EMP_MASTER");
            startActivity(intent);
        });

        // Inactive Emp List Box
        inactiveEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHInactiveEmpListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userName); // Using username as user ID for now
            intent.putExtra("SOURCE_PANEL", "RBH_EMP_MASTER");
            startActivity(intent);
        });

        // Bottom Navigation // Removed as per edit hint
        // rbhBottomNav.setOnItemSelectedListener(item -> {
        //     int itemId = item.getItemId();
        //     if (itemId == R.id.nav_dashboard) {
        //         Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        //         intent.putExtra("USERNAME", userName);
        //         startActivity(intent);
        //         finish();
        //         return true;
        //     } else if (itemId == R.id.nav_team) {
        //         Intent intent = new Intent(this, RBHTeamActivity.class);
        //         intent.putExtra("USERNAME", userName);
        //         startActivity(intent);
        //         finish();
        //         return true;
        //     } else if (itemId == R.id.nav_portfolio) {
        //         Intent intent = new Intent(this, RBHPortfolioActivity.class);
        //         intent.putExtra("USERNAME", userName);
        //         startActivity(intent);
        //         finish();
        //         return true;
        //     } else if (itemId == R.id.nav_reports) {
        //         Intent intent = new Intent(this, RBHReportsActivity.class);
        //         intent.putExtra("USERNAME", userName);
        //         startActivity(intent);
        //         finish();
        //         return true;
        //     }
        //     return false;
        // });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Manage Employee Master Data");
    }

    private void updateStats() {
        // TODO: Fetch real statistics from API
        activeEmpCount.setText("0 Active");
        inactiveEmpCount.setText("0 Inactive");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 
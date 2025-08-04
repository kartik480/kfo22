package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RBHInactiveEmpListActivity extends AppCompatActivity {
    private TextView titleText, welcomeText;
    private View backButton, refreshButton, addButton;
    private String userName;
    private BottomNavigationView rbhBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_rbh_inactive_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Refresh button
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing inactive employee list...", Toast.LENGTH_SHORT).show();
            // TODO: Refresh inactive employee data
        });

        // Add button
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add New Inactive Employee - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add inactive employee activity
        });

        // Bottom Navigation
        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_team) {
                Intent intent = new Intent(this, RBHTeamActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                Intent intent = new Intent(this, RBHPortfolioActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_reports) {
                Intent intent = new Intent(this, RBHReportsActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Inactive Employee Management");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 
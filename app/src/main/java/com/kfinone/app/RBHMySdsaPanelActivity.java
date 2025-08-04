package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RBHMySdsaPanelActivity extends AppCompatActivity {
    private TextView welcomeText;
    private View notificationIcon, profileIcon, menuButton, backButton;
    private String userName;
    private BottomNavigationView rbhBottomNav;
    
    // Action card views
    private View cardMySdsa, cardSdsaManagement, cardSdsaReports, cardSdsaAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_rbh_my_sdsa_panel);
        
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
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize action cards
        cardMySdsa = findViewById(R.id.cardMySdsa);
        cardSdsaManagement = findViewById(R.id.cardSdsaManagement);
        cardSdsaReports = findViewById(R.id.cardSdsaReports);
        cardSdsaAnalytics = findViewById(R.id.cardSdsaAnalytics);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        backButton = findViewById(R.id.backButton);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
    }

    private void setupClickListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open profile settings
        });

        // Action Card Click Listeners
        cardMySdsa.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHMySdsaListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getUserIdFromUsername(userName));
            startActivity(intent);
        });
        
        cardSdsaManagement.setOnClickListener(v -> {
            Toast.makeText(this, "SDSA Management - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Implement SDSA Management functionality
        });
        
        cardSdsaReports.setOnClickListener(v -> {
            Toast.makeText(this, "SDSA Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Implement SDSA Reports functionality
        });
        
        cardSdsaAnalytics.setOnClickListener(v -> {
            Toast.makeText(this, "SDSA Analytics - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Implement SDSA Analytics functionality
        });

        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, RegionalBusinessHeadPanelActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_team) {
                startActivity(new Intent(this, RBHTeamActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                startActivity(new Intent(this, RBHPortfolioActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, RBHReportsActivity.class).putExtra("USERNAME", userName));
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Welcome back, " + userName);
    }

    private void showMenuOptions() {
        String[] options = {"About", "Help", "Logout"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Menu Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAboutDialog();
                    break;
                case 1:
                    Toast.makeText(this, "Help - Coming Soon", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showLogoutConfirmation();
                    break;
            }
        });
        builder.show();
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About RBH My SDSA Panel");
        builder.setMessage("RBH My SDSA Panel v1.0\n\n" +
                "This panel provides comprehensive SDSA management tools for Regional Business Heads to oversee their SDSA operations, performance, and analytics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        startActivity(intent);
        finish();
    }
    
    private String getUserIdFromUsername(String username) {
        // This is a simplified implementation
        // In a real app, you would fetch this from SharedPreferences or a local database
        // For now, we'll return a placeholder that the API can handle
        return "1"; // Default user ID for testing
    }
} 
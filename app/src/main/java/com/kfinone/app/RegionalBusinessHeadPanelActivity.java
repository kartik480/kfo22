package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegionalBusinessHeadPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private String userId;
    
    // Action card views
    private View cardTotalEmp, cardTotalSdsa, cardTotalPartner, cardTotalAgent, cardTeam, cardReports, cardAnalytics, cardStrategy, 
                 cardPerformance, cardGrowth, cardInnovation, cardPartnerships, 
                 cardMarketAnalysis, cardCompliance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_regional_business_head_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize action cards
        cardTotalEmp = findViewById(R.id.cardTotalEmp);
        cardTotalSdsa = findViewById(R.id.cardTotalSdsa);
        cardTotalPartner = findViewById(R.id.cardTotalPartner);
        cardTotalAgent = findViewById(R.id.cardTotalAgent);
        cardTeam = findViewById(R.id.cardTeam);
        cardReports = findViewById(R.id.cardReports);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        cardStrategy = findViewById(R.id.cardStrategy);
        cardPerformance = findViewById(R.id.cardPerformance);
        cardGrowth = findViewById(R.id.cardGrowth);
        cardInnovation = findViewById(R.id.cardInnovation);
        cardPartnerships = findViewById(R.id.cardPartnerships);
        cardMarketAnalysis = findViewById(R.id.cardMarketAnalysis);
        cardCompliance = findViewById(R.id.cardCompliance);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        
        // Log card initialization
        android.util.Log.d("RBHPanel", "Card initialization:");
        android.util.Log.d("RBHPanel", "cardTotalEmp: " + (cardTotalEmp != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalSdsa: " + (cardTotalSdsa != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalPartner: " + (cardTotalPartner != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalAgent: " + (cardTotalAgent != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTeam: " + (cardTeam != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardReports: " + (cardReports != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardAnalytics: " + (cardAnalytics != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardStrategy: " + (cardStrategy != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardPerformance: " + (cardPerformance != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardGrowth: " + (cardGrowth != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardInnovation: " + (cardInnovation != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardPartnerships: " + (cardPartnerships != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardMarketAnalysis: " + (cardMarketAnalysis != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardCompliance: " + (cardCompliance != null ? "FOUND" : "NULL"));
    }

    private void setupClickListeners() {
        // Menu button click listener
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                showMenuOptions();
            });
        }

        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                // TODO: Open notifications panel
            });
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                // TODO: Open profile settings
            });
        }

        // Action Card Click Listeners
        if (cardTotalEmp != null) {
            cardTotalEmp.setOnClickListener(v -> {
                // Navigate to RBH Active Emp List
                android.util.Log.d("RBHPanel", "Total Emp Card clicked! Navigating to RBHActiveEmpListActivity");
                Intent intent = new Intent(this, RBHActiveEmpListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardTotalSdsa != null) {
            cardTotalSdsa.setOnClickListener(v -> {
                // Navigate to RBH My SDSA Panel
                android.util.Log.d("RBHPanel", "Total SDSA Card clicked! Navigating to RBHMySdsaPanelActivity");
                Intent intent = new Intent(this, RBHMySdsaPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardTotalPartner != null) {
            cardTotalPartner.setOnClickListener(v -> {
                // Navigate to RBH My Partner Panel
                android.util.Log.d("RBHPanel", "Total Partner Card clicked! Navigating to RegionalBusinessHeadMyPartnerPanelActivity");
                Intent intent = new Intent(this, RegionalBusinessHeadMyPartnerPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardTotalAgent != null) {
            cardTotalAgent.setOnClickListener(v -> {
                // Navigate to RBH My Agent List
                android.util.Log.d("RBHPanel", "Total Agent Card clicked! Navigating to RBHMyAgentListActivity");
                Intent intent = new Intent(this, RBHMyAgentListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardTeam != null) {
            cardTeam.setOnClickListener(v -> {
                Toast.makeText(this, "Data Analytics - Coming Soon!", Toast.LENGTH_SHORT).show();
                // TODO: Implement Data Analytics functionality
            });
        }
        
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> {
                Toast.makeText(this, "Work Links - Coming Soon!", Toast.LENGTH_SHORT).show();
                // TODO: Implement Work Links functionality
            });
        }
        
        if (cardAnalytics != null) {
            cardAnalytics.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHEmpMasterActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardStrategy != null) {
            cardStrategy.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHActiveEmpListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", getUserIdFromUsername(userName));
                startActivity(intent);
            });
        }
        
        if (cardPerformance != null) {
            cardPerformance.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHMySdsaPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardGrowth != null) {
            cardGrowth.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegionalBusinessHeadMyPartnerPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardInnovation != null) {
            cardInnovation.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHMyAgentPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId); // Pass the actual user ID
                startActivity(intent);
            });
        }
        
        if (cardPartnerships != null) {
            cardPartnerships.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHPayoutPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardMarketAnalysis != null) {
            cardMarketAnalysis.setOnClickListener(v -> {
                Intent intent = new Intent(this, DsaCodeListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardCompliance != null) {
            cardCompliance.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHPortfolioPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
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
        builder.setTitle("About Regional Business Head Panel");
        builder.setMessage("Regional Business Head Panel v1.0\n\n" +
                "This panel provides comprehensive management tools for Regional Business Heads to oversee their regional operations, team performance, and business metrics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void updateStats() {
        // TODO: Fetch real statistics from API
        totalEmpCount.setText("0");
        totalSdsaCount.setText("0");
        totalPartnerCount.setText("0");
        totalAgentCount.setText("0");
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
        showLogoutConfirmation();
    }
    
    private String getUserIdFromUsername(String username) {
        // This is a simplified implementation
        // In a real app, you would fetch this from SharedPreferences or a local database
        // For now, we'll return a placeholder that the API can handle
        return "1"; // Default user ID for testing
    }
} 
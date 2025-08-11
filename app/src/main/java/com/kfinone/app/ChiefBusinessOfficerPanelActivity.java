package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChiefBusinessOfficerPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalPortfolioCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private String userId;
    
    // Action card views
    private View cardPortfolio, cardTeam, cardReports, cardAnalytics, cardStrategy, 
                 cardPerformance, cardGrowth, cardInnovation, cardPartnerships, 
                 cardMarketAnalysis, cardRiskManagement, cardCompliance, cardBudget, 
                 cardGoals, cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_business_officer_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
        }
        
        // Debug: Log received user data
        Log.d("CBOPanel", "Received userName: " + userName);
        Log.d("CBOPanel", "Received userId: " + userId);
        
        // Critical check: Ensure we have a valid userId
        if (userId == null || userId.isEmpty()) {
            Log.e("CBOPanel", "CRITICAL ERROR: No valid userId received!");
            Log.e("CBOPanel", "This will cause navigation issues in downstream activities");
            Log.e("CBOPanel", "Expected: numeric ID like '21', Got: '" + userId + "'");
            
            // Try to get userId from SharedPreferences as fallback
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String savedUserId = prefs.getString("USER_ID", null);
                if (savedUserId != null && !savedUserId.isEmpty()) {
                    userId = savedUserId;
                    Log.d("CBOPanel", "Using userId from SharedPreferences: " + userId);
                } else {
                    Log.e("CBOPanel", "No userId found in SharedPreferences either");
                }
            } catch (Exception e) {
                Log.e("CBOPanel", "Error reading from SharedPreferences: " + e.getMessage());
            }
        } else {
            Log.d("CBOPanel", "✓ Valid userId received: " + userId);
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
        totalPortfolioCount = findViewById(R.id.totalPortfolioCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize action cards
        cardPortfolio = findViewById(R.id.cardPortfolio);
        cardTeam = findViewById(R.id.cardTeam);
        cardReports = findViewById(R.id.cardReports);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        cardStrategy = findViewById(R.id.cardStrategy);
        cardPerformance = findViewById(R.id.cardPerformance);
        cardGrowth = findViewById(R.id.cardGrowth);
        cardInnovation = findViewById(R.id.cardInnovation);
        cardPartnerships = findViewById(R.id.cardPartnerships);
        cardMarketAnalysis = findViewById(R.id.cardMarketAnalysis);
        cardRiskManagement = findViewById(R.id.cardRiskManagement);
        cardCompliance = findViewById(R.id.cardCompliance);
        cardBudget = findViewById(R.id.cardBudget);
        cardGoals = findViewById(R.id.cardGoals);
        cardSettings = findViewById(R.id.cardSettings);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
    }

    private void setupClickListeners() {
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
        cardPortfolio.setOnClickListener(v -> {
            Toast.makeText(this, "Employee Management - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Employee Management functionality
        });
        
        cardTeam.setOnClickListener(v -> {
            Toast.makeText(this, "Data Analytics - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Data Analytics functionality
        });
        
        cardReports.setOnClickListener(v -> {
            Toast.makeText(this, "Work Links - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Work Links functionality
        });
        
        cardAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpMasterActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardStrategy.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmployeeActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOSdsaActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPartnerActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardInnovation.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAgentActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPayoutPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardRiskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOBankersPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPortfolioPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOInsurancePanelActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardGoals.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocCheckListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        // Update welcome message with username
        String welcomeMessage = "Welcome back, " + userName;
        welcomeText.setText(welcomeMessage);
    }

    private void showMenuOptions() {
        String[] menuOptions = {
            "Dashboard",
            "Team Management", 
            "Portfolio Management",
            "Reports & Analytics",
            "Profile",
            "Notifications",
            "Help & Support",
            "About",
            "Logout"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(menuOptions, (dialog, which) -> {
                switch (which) {
                    case 0: // Dashboard
                        // Already on dashboard
                        break;
                    case 1: // Team Management
                        Intent teamIntent = new Intent(this, CBOTeamActivity.class);
                        teamIntent.putExtra("USERNAME", userName);
                        teamIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(teamIntent);
                        break;
                    case 2: // Portfolio Management
                        Intent portfolioIntent = new Intent(this, CBOPortfolioActivity.class);
                        portfolioIntent.putExtra("USERNAME", userName);
                        portfolioIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(portfolioIntent);
                        break;
                    case 3: // Reports & Analytics
                        Intent reportsIntent = new Intent(this, CBOReportsActivity.class);
                        reportsIntent.putExtra("USERNAME", userName);
                        reportsIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(reportsIntent);
                        break;
                    case 4: // Profile
                        Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case 5: // Notifications
                        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case 6: // Help & Support
                        Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case 7: // About
                        showAboutDialog();
                        break;
                    case 8: // Logout
                        showLogoutConfirmation();
                        break;
                }
            })
            .show();
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About KfinOne CBO Panel")
            .setMessage("Chief Business Officer Dashboard\n\n" +
                       "Version: 1.0\n" +
                       "Designed for executive management\n" +
                       "© 2024 KfinOne Technologies")
            .setPositiveButton("OK", null)
            .show();
    }

    private void updateStats() {
        // Update statistics with sample data
        totalEmpCount.setText("0");
        totalSdsaCount.setText("0");
        totalPartnerCount.setText("0");
        totalPortfolioCount.setText("0");
        totalAgentCount.setText("0");
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Navigate back to login
                Intent intent = new Intent(this, EnhancedLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void passUserDataToIntent(Intent intent) {
        Log.d("CBOPanel", "=== Passing User Data to Intent ===");
        Log.d("CBOPanel", "Passing USERNAME: " + userName);
        Log.d("CBOPanel", "Passing USER_ID: " + userId);
        
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
        
        Log.d("CBOPanel", "=================================");
    }

    @Override
    public void onBackPressed() {
        // Show logout confirmation when back button is pressed
        showLogoutConfirmation();
    }
} 
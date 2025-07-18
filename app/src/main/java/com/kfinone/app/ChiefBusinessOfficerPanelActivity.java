package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChiefBusinessOfficerPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalPortfolioCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private BottomNavigationView cboBottomNav;
    
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
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
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
        cboBottomNav = findViewById(R.id.cboBottomNav);
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
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, DataLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmpMasterActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardStrategy.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeePanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(this, SdsaPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorPartnerMasterActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardInnovation.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgentActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorPayoutPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardRiskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, BankersPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardBudget.setOnClickListener(v -> {
            Toast.makeText(this, "Add Insurance - Coming Soon", Toast.LENGTH_SHORT).show();
        });
        
        cardGoals.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocCheckListActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });

        cboBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                // Already on dashboard
                return true;
            } else if (itemId == R.id.nav_team) {
                startActivity(new Intent(this, CBOTeamActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                startActivity(new Intent(this, CBOPortfolioActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, CBOReportsActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, CBOSettingsActivity.class).putExtra("USERNAME", userName));
                return true;
            }
            return false;
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
            "Settings",
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
                        startActivity(teamIntent);
                        break;
                    case 2: // Portfolio Management
                        Intent portfolioIntent = new Intent(this, CBOPortfolioActivity.class);
                        portfolioIntent.putExtra("USERNAME", userName);
                        startActivity(portfolioIntent);
                        break;
                    case 3: // Reports & Analytics
                        Intent reportsIntent = new Intent(this, CBOReportsActivity.class);
                        reportsIntent.putExtra("USERNAME", userName);
                        startActivity(reportsIntent);
                        break;
                    case 4: // Settings
                        Intent settingsIntent = new Intent(this, CBOSettingsActivity.class);
                        settingsIntent.putExtra("USERNAME", userName);
                        startActivity(settingsIntent);
                        break;
                    case 5: // Profile
                        Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case 6: // Notifications
                        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case 7: // Help & Support
                        Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case 8: // About
                        showAboutDialog();
                        break;
                    case 9: // Logout
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
        totalEmpCount.setText("156");
        totalSdsaCount.setText("89");
        totalPartnerCount.setText("45");
        totalPortfolioCount.setText("234");
        totalAgentCount.setText("67");
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

    @Override
    public void onBackPressed() {
        // Show logout confirmation when back button is pressed
        showLogoutConfirmation();
    }
} 
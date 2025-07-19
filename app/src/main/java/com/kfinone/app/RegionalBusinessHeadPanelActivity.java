package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegionalBusinessHeadPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalPortfolioCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private BottomNavigationView rbhBottomNav;
    
    // Action card views
    private View cardPortfolio, cardTeam, cardReports, cardAnalytics, cardStrategy, 
                 cardPerformance, cardGrowth, cardInnovation, cardPartnerships, 
                 cardMarketAnalysis, cardRiskManagement, cardCompliance, cardBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regional_business_head_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
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
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
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
            Intent intent = new Intent(this, RBHEmpLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHDataLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHWorkLinksActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHEmpMasterActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardStrategy.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHEmployeeActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHSdsaActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPartnerActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardInnovation.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHAgentActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPayoutPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
            startActivity(intent);
        });
        
        cardRiskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHBankersPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPortfolioPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });
        
        cardBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHInsurancePanelActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                // Already on dashboard
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
        builder.setTitle("About Regional Business Head Panel");
        builder.setMessage("Regional Business Head Panel v1.0\n\n" +
                "This panel provides comprehensive management tools for Regional Business Heads to oversee their regional operations, team performance, and business metrics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void updateStats() {
        // TODO: Fetch real statistics from API
        totalEmpCount.setText("89");
        totalSdsaCount.setText("45");
        totalPartnerCount.setText("23");
        totalPortfolioCount.setText("156");
        totalAgentCount.setText("67");
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, LoginActivity.class);
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
} 
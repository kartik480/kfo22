package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class PortfolioPanelActivity extends AppCompatActivity {

    private MaterialCardView addPortfolioBox;
    private MaterialCardView myPortfolioBox;
    private MaterialCardView portfolioTeamBox;
    private TextView backButton;
    private View refreshButton;
    private View addButton;
    private View dashboardButton;
    private View portfolioButton;
    private View reportsButton;
    private View settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_panel);

        initializeViews();
        setupClickListeners();
        loadPortfolioData();
    }

    private void initializeViews() {
        addPortfolioBox = findViewById(R.id.addPortfolioBox);
        myPortfolioBox = findViewById(R.id.myPortfolioBox);
        portfolioTeamBox = findViewById(R.id.portfolioTeamBox);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        dashboardButton = findViewById(R.id.dashboardButton);
        portfolioButton = findViewById(R.id.portfolioButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPortfolio());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            startActivity(intent);
            finish();
        });

        portfolioButton.setOnClickListener(v -> {
            // Already on portfolio page, just show feedback
            Toast.makeText(this, "Portfolio Management", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // Portfolio box click listeners
        addPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPortfolioActivity.class);
            startActivity(intent);
        });

        myPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyPortfolioActivity.class);
            startActivity(intent);
        });

        portfolioTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioTeamActivity.class);
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
        Toast.makeText(this, "Refreshing portfolio data...", Toast.LENGTH_SHORT).show();
        loadPortfolioData();
    }

    private void addNewPortfolio() {
        Intent intent = new Intent(this, AddPortfolioActivity.class);
        startActivity(intent);
    }

    private void loadPortfolioData() {
        // TODO: Load portfolio data from server
        // For now, just show a loading message
        Toast.makeText(this, "Loading portfolio data...", Toast.LENGTH_SHORT).show();
    }
} 
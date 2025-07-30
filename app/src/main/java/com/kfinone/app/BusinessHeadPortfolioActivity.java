package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class BusinessHeadPortfolioActivity extends AppCompatActivity {

    private MaterialCardView addPortfolioBox;
    private MaterialCardView myPortfolioBox;
    private TextView backButton;
    private View refreshButton;
    private View addButton;
    private View dashboardButton;
    private View portfolioButton;
    private View reportsButton;
    private View settingsButton;
    
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_portfolio);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadPortfolioData();
    }

    private void initializeViews() {
        addPortfolioBox = findViewById(R.id.addPortfolioBox);
        myPortfolioBox = findViewById(R.id.myPortfolioBox);
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
            Intent intent = new Intent(this, BusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
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
            Intent intent = new Intent(this, BusinessHeadAddPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        myPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadMyPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });


    }

    private void goBack() {
        // Go back to Business Head panel
        Intent intent = new Intent(this, BusinessHeadPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing portfolio data...", Toast.LENGTH_SHORT).show();
        loadPortfolioData();
    }

    private void addNewPortfolio() {
        Intent intent = new Intent(this, BusinessHeadAddPortfolioActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
    }

    private void loadPortfolioData() {
        // TODO: Load portfolio data from server
        // For now, just show a loading message
        Toast.makeText(this, "Loading portfolio data...", Toast.LENGTH_SHORT).show();
    }
    
    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegionalBusinessHeadMyPartnerPanelActivity extends AppCompatActivity {
    private TextView welcomeText, totalPartnerCount, activePartnerCount;
    private View cardMyPartner;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_regional_business_head_my_partner_panel);
        
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
        welcomeText = findViewById(R.id.welcomeText);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        activePartnerCount = findViewById(R.id.activePartnerCount);
        
        // Initialize action cards
        cardMyPartner = findViewById(R.id.cardMyPartner);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Partner Management");
        }
    }

    private void setupClickListeners() {
        // My Partner card click listener
        cardMyPartner.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegionalBusinessHeadMyPartnerListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getUserIdFromUsername(userName));
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    private void updateStats() {
        // TODO: Implement API call to fetch partner statistics
        // For now, set placeholder values
        totalPartnerCount.setText("0");
        activePartnerCount.setText("0");
    }

    private String getUserIdFromUsername(String username) {
        // TODO: Implement proper user ID retrieval from username
        // For now, return a placeholder
        return "1";
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 
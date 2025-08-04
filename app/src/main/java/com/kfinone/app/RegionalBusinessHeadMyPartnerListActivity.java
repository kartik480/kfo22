package com.kfinone.app;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegionalBusinessHeadMyPartnerListActivity extends AppCompatActivity {
    private TextView totalPartnerCount, activePartnerCount, welcomeText;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_regional_business_head_my_partner_list);
        
        // Get user data from intent
        String userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupToolbar();
        loadPartnerData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        activePartnerCount = findViewById(R.id.activePartnerCount);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Partner List");
        }
    }

    private void loadPartnerData() {
        // TODO: Implement API call to fetch partner data
        // For now, set placeholder values
        totalPartnerCount.setText("0");
        activePartnerCount.setText("0");
        Toast.makeText(this, "My Partner functionality coming soon!", Toast.LENGTH_LONG).show();
    }

    private void updateWelcomeMessage(String userName) {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
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
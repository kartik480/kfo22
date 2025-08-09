package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RBHPayoutPanelActivity extends AppCompatActivity {
    private TextView welcomeText, totalPayoutCount, pendingPayoutCount;
    private View cardMyPayout;
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_rbh_payout_panel);
        
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupToolbar();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalPayoutCount = findViewById(R.id.totalPayoutCount);
        pendingPayoutCount = findViewById(R.id.pendingPayoutCount);
        cardMyPayout = findViewById(R.id.cardMyPayout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Payout Management");
        }
    }

    private void setupClickListeners() {
        cardMyPayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    private void updateStats() {
        // TODO: Implement API call to fetch payout statistics
        totalPayoutCount.setText("0");
        pendingPayoutCount.setText("0");
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
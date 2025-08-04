package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RBHMyAgentPanelActivity extends AppCompatActivity {
    private TextView welcomeText, totalAgentCount, activeAgentCount;
    private View cardMyAgent, cardAgentManagement, cardAgentReports, cardAgentAnalytics;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_rbh_my_agent_panel);
        
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        activeAgentCount = findViewById(R.id.activeAgentCount);
        cardMyAgent = findViewById(R.id.cardMyAgent);
        cardAgentManagement = findViewById(R.id.cardAgentManagement);
        cardAgentReports = findViewById(R.id.cardAgentReports);
        cardAgentAnalytics = findViewById(R.id.cardAgentAnalytics);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Agent Management");
        }
    }

    private void setupClickListeners() {
        cardMyAgent.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHMyAgentListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getUserIdFromUsername(userName));
            startActivity(intent);
        });
        
        cardAgentManagement.setOnClickListener(v -> {
            Toast.makeText(this, "Agent Management functionality coming soon!", Toast.LENGTH_LONG).show();
        });
        
        cardAgentReports.setOnClickListener(v -> {
            Toast.makeText(this, "Agent Reports functionality coming soon!", Toast.LENGTH_LONG).show();
        });
        
        cardAgentAnalytics.setOnClickListener(v -> {
            Toast.makeText(this, "Agent Analytics functionality coming soon!", Toast.LENGTH_LONG).show();
        });
    }

    private void updateWelcomeMessage() {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    private void updateStats() {
        // TODO: Implement API call to fetch agent statistics
        totalAgentCount.setText("0");
        activeAgentCount.setText("0");
    }

    private String getUserIdFromUsername(String username) {
        // TODO: Implement proper user ID retrieval
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
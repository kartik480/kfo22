package com.kfinone.app;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RBHMyAgentListActivity extends AppCompatActivity {
    private TextView totalAgentCount, activeAgentCount, welcomeText;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_rbh_my_agent_list);
        
        String userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupToolbar();
        loadAgentData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        activeAgentCount = findViewById(R.id.activeAgentCount);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Agent List");
        }
    }

    private void loadAgentData() {
        // TODO: Implement API call to fetch agent data
        totalAgentCount.setText("0");
        activeAgentCount.setText("0");
        Toast.makeText(this, "My Agent functionality coming soon!", Toast.LENGTH_LONG).show();
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
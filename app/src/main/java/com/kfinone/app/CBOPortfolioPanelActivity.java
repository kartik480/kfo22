package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class CBOPortfolioPanelActivity extends AppCompatActivity {
    private MaterialCardView addPortfolioBox, myPortfolioBox, teamPortfolioBox;
    private ImageButton backButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_portfolio_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        addPortfolioBox = findViewById(R.id.addPortfolioBox);
        myPortfolioBox = findViewById(R.id.myPortfolioBox);
        teamPortfolioBox = findViewById(R.id.teamPortfolioBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        addPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPortfolioActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        myPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyPortfolioActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        teamPortfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
    }
} 
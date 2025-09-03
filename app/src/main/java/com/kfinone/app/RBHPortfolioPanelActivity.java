package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RBHPortfolioPanelActivity extends AppCompatActivity {
    
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    
    // Action card views
    private View myPortfolioBox;
    private View portfolioTeamBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_portfolio_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        myPortfolioBox = findViewById(R.id.myPortfolioBox);
        portfolioTeamBox = findViewById(R.id.portfolioTeamBox);
    }

    private void setupClickListeners() {
        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // My Portfolio box click listener
        if (myPortfolioBox != null) {
            myPortfolioBox.setOnClickListener(v -> {
                android.util.Log.d("RBHPortfolio", "My Portfolio Box clicked! Navigating to RBHMyPortfolioActivity");
                Intent intent = new Intent(this, RBHMyPortfolioActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
            });
        }
        
        // Portfolio Team box click listener
        if (portfolioTeamBox != null) {
            portfolioTeamBox.setOnClickListener(v -> {
                android.util.Log.d("RBHPortfolio", "Portfolio Team Box clicked! Navigating to RBHPortfolioTeamActivity");
                Intent intent = new Intent(this, RBHPortfolioTeamActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
            });
        }
    }
    
    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }
}
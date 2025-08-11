package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class CBOPayoutPanelActivity extends AppCompatActivity {
    private MaterialCardView payoutTeamBox;
    private ImageButton backButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_payout_panel);
        
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
        payoutTeamBox = findViewById(R.id.payoutTeamBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        
        payoutTeamBox.setOnClickListener(v -> {
            // TODO: Navigate to Payout Team Activity
            // For now, show a toast message
            android.widget.Toast.makeText(this, "Payout Team - Coming Soon", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
    
    private void goBack() {
        // Navigate back to CBO home page
        Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        goBack();
    }
} 
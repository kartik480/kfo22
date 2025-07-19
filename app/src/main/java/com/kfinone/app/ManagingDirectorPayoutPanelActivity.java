package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class ManagingDirectorPayoutPanelActivity extends AppCompatActivity {
    private MaterialCardView branchFullPayoutBox, servicePartnerPayoutBox, leadBaseAgentPayoutBox, connectorReferralPayoutBox, interBranchPayoutBox, rbhPayoutBox, payoutTeamBox;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        setContentView(R.layout.activity_managing_director_payout_panel);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        branchFullPayoutBox = findViewById(R.id.branchFullPayoutBox);
        servicePartnerPayoutBox = findViewById(R.id.servicePartnerPayoutBox);
        leadBaseAgentPayoutBox = findViewById(R.id.leadBaseAgentPayoutBox);
        connectorReferralPayoutBox = findViewById(R.id.connectorReferralPayoutBox);
        interBranchPayoutBox = findViewById(R.id.interBranchPayoutBox);
        rbhPayoutBox = findViewById(R.id.rbhPayoutBox);
        payoutTeamBox = findViewById(R.id.payoutTeamBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        branchFullPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchFullPayoutActivity.class);
            startActivity(intent);
        });
        servicePartnerPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServicePartnerPayoutActivity.class);
            startActivity(intent);
        });
        leadBaseAgentPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeadBaseAgentPayoutActivity.class);
            startActivity(intent);
        });
        connectorReferralPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConnectorReferralPayoutActivity.class);
            startActivity(intent);
        });
        interBranchPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InterBranchPayoutActivity.class);
            startActivity(intent);
        });
        rbhPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPayoutActivity.class);
            startActivity(intent);
        });
        payoutTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, PayoutTeamActivity.class);
            startActivity(intent);
        });
    }
} 
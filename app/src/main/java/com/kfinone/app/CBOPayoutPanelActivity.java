package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class CBOPayoutPanelActivity extends AppCompatActivity {
    private MaterialCardView branchFullPayoutBox, servicePartnerPayoutBox, leadBaseAgentPayoutBox, 
                           connectorReferralPayoutBox, interBranchPayoutBox, rbhPayoutBox, payoutTeamBox;
    private ImageButton backButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_payout_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        String userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
        }
        
        // Debug logging
        Log.d("CBOPayoutPanel", "Received userName: " + userName);
        Log.d("CBOPayoutPanel", "Received userId: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            Log.e("CBOPayoutPanel", "CRITICAL ERROR: No valid userId received!");
        } else {
            Log.d("CBOPayoutPanel", "✓ Valid userId received: " + userId);
        }
        
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
        backButton.setOnClickListener(v -> goBack());
        
        // Branch/Full Payout
        branchFullPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchFullPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // Service/Partner Payout
        servicePartnerPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServicePartnerPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // Lead Base/Agent Payout
        leadBaseAgentPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeadBaseAgentPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // Connector/Referral Payout
        connectorReferralPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConnectorReferralPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // Inter Branch Payout
        interBranchPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InterBranchPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // RBH Payout
        rbhPayoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHPayoutActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
        });
        
        // Payout Team
        payoutTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPayoutTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "CBO");
            startActivity(intent);
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
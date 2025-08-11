package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class CBOInsurancePanelActivity extends AppCompatActivity {
    private MaterialCardView myInsuranceBox, teamInsuranceBox;
    private ImageButton backButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_insurance_panel);
        
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
        myInsuranceBox = findViewById(R.id.myInsuranceBox);
        teamInsuranceBox = findViewById(R.id.teamInsuranceBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        
        myInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyInsuranceActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        teamInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsuranceTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
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
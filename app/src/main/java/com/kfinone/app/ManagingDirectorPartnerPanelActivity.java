package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManagingDirectorPartnerPanelActivity extends AppCompatActivity {

    private View addPartnerBox;
    private View myPartnerBox;
    private View partnerTeamBox;
    private TextView backButton;

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
        
        setContentView(R.layout.activity_managing_director_partner_panel);

        initializeViews();
        setupClickListeners();
        loadPartnerData();
    }

    private void initializeViews() {
        addPartnerBox = findViewById(R.id.addPartnerBox);
        myPartnerBox = findViewById(R.id.myPartnerBox);
        partnerTeamBox = findViewById(R.id.partnerTeamBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> goBack());

        // Add Partner box
        addPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(ManagingDirectorPartnerPanelActivity.this, AddPartnerActivity.class);
            startActivity(intent);
        });

        // My Partner box
        myPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(ManagingDirectorPartnerPanelActivity.this, MyPartnerActivity.class);
            startActivity(intent);
        });

        // Partner Team box
        partnerTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(ManagingDirectorPartnerPanelActivity.this, PartnerTeamActivity.class);
            startActivity(intent);
        });
    }

    private void loadPartnerData() {
        // TODO: Load real partner data from server
        // For now, set some sample data
        TextView addPartnerCount = findViewById(R.id.addPartnerCount);
        TextView myPartnerCount = findViewById(R.id.myPartnerCount);
        TextView partnerTeamCount = findViewById(R.id.partnerTeamCount);

        addPartnerCount.setText("Add New");
        myPartnerCount.setText("25 Partners");
        partnerTeamCount.setText("15 Team Members");
    }

    private void goBack() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
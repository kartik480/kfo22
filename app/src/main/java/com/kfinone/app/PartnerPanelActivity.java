package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PartnerPanelActivity extends AppCompatActivity {

    private View addPartnerBox;
    private View typeOfPartnerBox;
    private View myPartnerBox;
    private View partnerTeamBox;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_panel);

        initializeViews();
        setupClickListeners();
        loadPartnerData();
    }

    private void initializeViews() {
        addPartnerBox = findViewById(R.id.addPartnerBox);
        typeOfPartnerBox = findViewById(R.id.typeOfPartnerBox);
        myPartnerBox = findViewById(R.id.myPartnerBox);
        partnerTeamBox = findViewById(R.id.partnerTeamBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> goBack());

        // Add Partner box
        addPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerPanelActivity.this, AddPartnerActivity.class);
            startActivity(intent);
        });

        // Type of Partner box
        typeOfPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerPanelActivity.this, PartnerTypeActivity.class);
            startActivity(intent);
        });

        // Active Partner box
        myPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerPanelActivity.this, MyPartnerActivity.class);
            startActivity(intent);
        });

        // Partner Inactive box
        partnerTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerPanelActivity.this, PartnerInactiveActivity.class);
            startActivity(intent);
        });
    }

    private void loadPartnerData() {
        // TODO: Load real partner data from server
        // For now, set some sample data
        TextView addPartnerCount = findViewById(R.id.addPartnerCount);
        TextView typeOfPartnerCount = findViewById(R.id.typeOfPartnerCount);
        TextView myPartnerCount = findViewById(R.id.myPartnerCount);
        TextView partnerTeamCount = findViewById(R.id.partnerTeamCount);

        addPartnerCount.setText("Add New");
        typeOfPartnerCount.setText("Manage Types");
        myPartnerCount.setText("25 Active");
        partnerTeamCount.setText("15 Inactive");
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
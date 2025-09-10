package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DirectorAddInsurancePanelActivity extends AppCompatActivity {

    private ImageView backButton;
    private MaterialCardView myInsuranceBox;
    private MaterialCardView teamInsuranceBox;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_add_insurance_panel);

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
        backButton = findViewById(R.id.backButton);
        myInsuranceBox = findViewById(R.id.myInsuranceBox);
        teamInsuranceBox = findViewById(R.id.teamInsuranceBox);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // My Insurance box
        myInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorMyInsuranceListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Team Insurance box
        teamInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorTeamInsuranceActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class SuperAdminRBHActivity extends AppCompatActivity {

    private MaterialCardView activeEmpListCard;
    private MaterialCardView inactiveEmpListCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_rbh);
        
        // Get user information from Intent
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");
        String fullName = intent.getStringExtra("USERNAME");
        String userId = intent.getStringExtra("USER_ID");
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("RBH Panel");
        backButton.setOnClickListener(v -> finish());
        
        // Set up welcome message
        TextView welcomeText = findViewById(R.id.welcomeText);
        String welcomeMessage;
        if (firstName != null && !firstName.isEmpty()) {
            welcomeMessage = "Welcome, " + firstName + "!";
        } else if (fullName != null && !fullName.isEmpty()) {
            welcomeMessage = "Welcome, " + fullName + "!";
        } else {
            welcomeMessage = "Welcome!";
        }
        welcomeMessage += "\nRBH (Regional Business Head) Panel";
        welcomeText.setText(welcomeMessage);
        
        // Initialize cards
        activeEmpListCard = findViewById(R.id.activeEmpListCard);
        inactiveEmpListCard = findViewById(R.id.inactiveEmpListCard);
        
        // Set click listeners
        activeEmpListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuperAdminRBHActivity.this, RegionalBusinessHeadActiveEmpListActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            }
        });
        
        inactiveEmpListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuperAdminRBHActivity.this, CBOInactiveEmpListActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            }
        });
    }
} 
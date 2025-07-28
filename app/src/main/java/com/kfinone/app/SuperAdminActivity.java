package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SuperAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
        
        // Get user information from Intent
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");
        String fullName = intent.getStringExtra("USERNAME");
        String userId = intent.getStringExtra("USER_ID");
        String adminType = intent.getStringExtra("ADMIN_TYPE");
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        
        // Set title based on admin type
        if ("CBO".equals(adminType)) {
            titleText.setText("CBO Panel");
        } else if ("RBH".equals(adminType)) {
            titleText.setText("RBH Panel");
        } else if ("BH_EMP_MASTER".equals(adminType)) {
            titleText.setText("BH Emp Master Panel");
        } else {
            titleText.setText("Super Admin Panel");
        }
        
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
        
        // Add admin type to welcome message
        if ("CBO".equals(adminType)) {
            welcomeMessage += "\nCBO (Chief Business Officer) Panel";
        } else if ("RBH".equals(adminType)) {
            welcomeMessage += "\nRBH (Regional Business Head) Panel";
        } else if ("BH_EMP_MASTER".equals(adminType)) {
            welcomeMessage += "\nBH Emp Master Panel";
        } else {
            welcomeMessage += "\nSuper Admin Panel";
        }
        
        welcomeText.setText(welcomeMessage);
    }
} 
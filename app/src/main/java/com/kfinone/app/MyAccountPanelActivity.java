package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MyAccountPanelActivity extends AppCompatActivity {
    private static final String TAG = "MyAccountPanel";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    // Header Views
    private ImageView backButton;
    private TextView headerTitle;
    private ImageView profileImage;
    private TextView userNameText;
    private TextView userRoleText;
    
    // Account Sections
    private CardView cardPersonalInfo;
    private CardView cardSecurity;
    private CardView cardPreferences;
    private CardView cardNotifications;
    private CardView cardPrivacy;
    private CardView cardHelp;
    private CardView cardAbout;
    private CardView cardLogout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MyAccountPanelActivity received USER_ID: " + userId);
        
        initializeViews();
        setupClickListeners();
        updateUserInfo();
    }
    
    private void initializeViews() {
        // Header Views
        backButton = findViewById(R.id.backButton);
        headerTitle = findViewById(R.id.headerTitle);
        profileImage = findViewById(R.id.profileImage);
        userNameText = findViewById(R.id.userNameText);
        userRoleText = findViewById(R.id.userRoleText);
        
        // Account Section Cards
        cardPersonalInfo = findViewById(R.id.cardPersonalInfo);
        cardSecurity = findViewById(R.id.cardSecurity);
        cardPreferences = findViewById(R.id.cardPreferences);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardPrivacy = findViewById(R.id.cardPrivacy);
        cardHelp = findViewById(R.id.cardHelp);
        cardAbout = findViewById(R.id.cardAbout);
        cardLogout = findViewById(R.id.cardLogout);
    }
    
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        // Personal Information
        cardPersonalInfo.setOnClickListener(v -> {
            showPersonalInfoDialog();
        });
        
        // Security Settings
        cardSecurity.setOnClickListener(v -> {
            showSecurityDialog();
        });
        
        // Preferences
        cardPreferences.setOnClickListener(v -> {
            showPreferencesDialog();
        });
        
        // Notifications
        cardNotifications.setOnClickListener(v -> {
            showNotificationsDialog();
        });
        
        // Privacy Settings
        cardPrivacy.setOnClickListener(v -> {
            showPrivacyDialog();
        });
        
        // Help & Support
        cardHelp.setOnClickListener(v -> {
            showHelpDialog();
        });
        
        // About
        cardAbout.setOnClickListener(v -> {
            showAboutDialog();
        });
        
        // Logout
        cardLogout.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }
    
    private void updateUserInfo() {
        if (firstName != null && lastName != null) {
            userNameText.setText(firstName + " " + lastName);
        } else if (username != null) {
            userNameText.setText(username);
        } else {
            userNameText.setText("User");
        }
        
        userRoleText.setText("Business Head");
    }
    
    private void showPersonalInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Personal Information");
        builder.setMessage("Name: " + (firstName != null ? firstName + " " + (lastName != null ? lastName : "") : "N/A") + "\n" +
                "Username: " + (username != null ? username : "N/A") + "\n" +
                "Role: Business Head\n" +
                "User ID: " + (userId != null ? userId : "N/A"));
        builder.setPositiveButton("Edit", (dialog, which) -> {
            showToast("Edit Personal Info - Coming Soon!");
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showSecurityDialog() {
        String[] options = {"Change Password", "Two-Factor Authentication", "Login History", "Security Questions"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Settings");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showToast("Change Password - Coming Soon!");
                    break;
                case 1:
                    showToast("Two-Factor Authentication - Coming Soon!");
                    break;
                case 2:
                    showToast("Login History - Coming Soon!");
                    break;
                case 3:
                    showToast("Security Questions - Coming Soon!");
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showPreferencesDialog() {
        String[] options = {"Language Settings", "Theme Settings", "Display Settings", "Sound Settings"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Preferences");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showToast("Language Settings - Coming Soon!");
                    break;
                case 1:
                    showToast("Theme Settings - Coming Soon!");
                    break;
                case 2:
                    showToast("Display Settings - Coming Soon!");
                    break;
                case 3:
                    showToast("Sound Settings - Coming Soon!");
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showNotificationsDialog() {
        String[] options = {"Push Notifications", "Email Notifications", "SMS Notifications", "Notification Schedule"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Settings");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showToast("Push Notifications - Coming Soon!");
                    break;
                case 1:
                    showToast("Email Notifications - Coming Soon!");
                    break;
                case 2:
                    showToast("SMS Notifications - Coming Soon!");
                    break;
                case 3:
                    showToast("Notification Schedule - Coming Soon!");
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showPrivacyDialog() {
        String[] options = {"Data Privacy", "Account Privacy", "Location Services", "Data Sharing"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Settings");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showToast("Data Privacy - Coming Soon!");
                    break;
                case 1:
                    showToast("Account Privacy - Coming Soon!");
                    break;
                case 2:
                    showToast("Location Services - Coming Soon!");
                    break;
                case 3:
                    showToast("Data Sharing - Coming Soon!");
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showHelpDialog() {
        String[] options = {"FAQ", "Contact Support", "User Guide", "Report Issue"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help & Support");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showToast("FAQ - Coming Soon!");
                    break;
                case 1:
                    showToast("Contact Support - Coming Soon!");
                    break;
                case 2:
                    showToast("User Guide - Coming Soon!");
                    break;
                case 3:
                    showToast("Report Issue - Coming Soon!");
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About KfinOne App");
        builder.setMessage("KfinOne App v1.0\n\n" +
                "A comprehensive business management application for KfinOne employees.\n\n" +
                "Features:\n" +
                "• Business Head Panel\n" +
                "• Team Management\n" +
                "• Analytics & Reports\n" +
                "• Performance Tracking\n" +
                "• Strategic Planning\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
} 
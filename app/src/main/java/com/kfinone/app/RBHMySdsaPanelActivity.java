package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RBHMySdsaPanelActivity extends AppCompatActivity {
    private TextView welcomeText;
    private View notificationIcon, profileIcon, menuButton, backButton;
    private String userName;
    private String userId;
    
    // Action card views
    private View cardMySdsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_rbh_my_sdsa_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize action cards
        cardMySdsa = findViewById(R.id.cardMySdsa);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        backButton = findViewById(R.id.backButton); // Assuming backButton is in the layout
    }

    private void setupClickListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open profile settings
        });

        // Action Card Click Listeners
        cardMySdsa.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHMySdsaActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getUserIdFromUsername(userName));
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Welcome back, " + userName);
    }

    private void showMenuOptions() {
        String[] options = {"About", "Help", "Logout"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Menu Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAboutDialog();
                    break;
                case 1:
                    Toast.makeText(this, "Help - Coming Soon", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showLogoutConfirmation();
                    break;
            }
        });
        builder.show();
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About RBH My SDSA Panel");
        builder.setMessage("RBH My SDSA Panel v1.0\n\n" +
                "This panel provides comprehensive SDSA management tools for Regional Business Heads to oversee their SDSA operations, performance, and analytics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        startActivity(intent);
        finish();
    }
    
    private String getUserIdFromUsername(String username) {
        // This is a simplified implementation
        // In a real app, you would fetch this from SharedPreferences or a local database
        // For now, we'll return a placeholder that the API can handle
        return "1"; // Default user ID for testing
    }
} 
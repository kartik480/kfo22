package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MarketingHeadLocationMasterActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadLocationMaster";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    
    // Card Views
    private CardView cardState;
    private CardView cardLocation;
    private CardView cardSubLocation;
    private CardView cardPincode;
    private CardView cardBranchState;
    private CardView cardBranchLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_location_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadLocationMasterActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadLocationMasterActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupClickListeners();
        updateWelcomeText();
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        
        cardState = findViewById(R.id.cardState);
        cardLocation = findViewById(R.id.cardLocation);
        cardSubLocation = findViewById(R.id.cardSubLocation);
        cardPincode = findViewById(R.id.cardPincode);
        cardBranchState = findViewById(R.id.cardBranchState);
        cardBranchLocation = findViewById(R.id.cardBranchLocation);
    }
    
    private void setupClickListeners() {
        // State
        cardState.setOnClickListener(v -> {
            showToast("State Management - Coming Soon!");
            // TODO: Launch State Management Activity
        });
        
        // Location
        cardLocation.setOnClickListener(v -> {
            showToast("Location Management - Coming Soon!");
            // TODO: Launch Location Management Activity
        });
        
        // Sub Location
        cardSubLocation.setOnClickListener(v -> {
            showToast("Sub Location Management - Coming Soon!");
            // TODO: Launch Sub Location Management Activity
        });
        
        // Pincode
        cardPincode.setOnClickListener(v -> {
            showToast("Pincode Management - Coming Soon!");
            // TODO: Launch Pincode Management Activity
        });
        
        // Branch State
        cardBranchState.setOnClickListener(v -> {
            showToast("Branch State Management - Coming Soon!");
            // TODO: Launch Branch State Management Activity
        });
        
        // Branch Location
        cardBranchLocation.setOnClickListener(v -> {
            showToast("Branch Location Management - Coming Soon!");
            // TODO: Launch Branch Location Management Activity
        });
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - Location Master");
        } else {
            welcomeText.setText("Welcome, Marketing Head - Location Master");
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            showToast("Settings - Coming Soon!");
            return true;
        } else if (id == R.id.action_help) {
            showToast("Help - Coming Soon!");
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Location Master");
        builder.setMessage("Location Master Panel v1.0\n\n" +
                "This panel provides comprehensive location management tools including:\n" +
                "• State Management\n" +
                "• Location Management\n" +
                "• Sub Location Management\n" +
                "• Pincode Management\n" +
                "• Branch State Management\n" +
                "• Branch Location Management\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

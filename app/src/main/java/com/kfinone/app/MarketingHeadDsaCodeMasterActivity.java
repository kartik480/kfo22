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

public class MarketingHeadDsaCodeMasterActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadDsaCodeMaster";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    
    // Card Views
    private CardView cardDsaList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_dsa_code_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadDsaCodeMasterActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadDsaCodeMasterActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupClickListeners();
        updateWelcomeText();
        
        // Setup back button - navigate back to Marketing Head Panel
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked - navigating to MarketingHeadPanelActivity");
            // Force navigation back to Marketing Head Panel and clear activity stack
            Intent backIntent = new Intent(MarketingHeadDsaCodeMasterActivity.this, MarketingHeadPanelActivity.class);
            backIntent.putExtra("USERNAME", username);
            backIntent.putExtra("FIRST_NAME", firstName);
            backIntent.putExtra("LAST_NAME", lastName);
            backIntent.putExtra("USER_ID", userId);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "Starting MarketingHeadPanelActivity with flags: CLEAR_TOP | NEW_TASK");
            startActivity(backIntent);
            finish(); // Close current activity
        });
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        
        cardDsaList = findViewById(R.id.cardDsaList);
    }
    
    private void setupClickListeners() {
        // DSA List goes to DSA Code List
        cardDsaList.setOnClickListener(v -> {
            Intent intent = new Intent(MarketingHeadDsaCodeMasterActivity.this, DsaCodeListActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "MARKETING_HEAD_PANEL");
            startActivity(intent);
        });
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - DSA Code Master");
        } else {
            welcomeText.setText("Welcome, Marketing Head - DSA Code Master");
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "System back button pressed - navigating to MarketingHeadPanelActivity");
        // Override system back button to navigate to Marketing Head Panel
        Intent backIntent = new Intent(MarketingHeadDsaCodeMasterActivity.this, MarketingHeadPanelActivity.class);
        backIntent.putExtra("USERNAME", username);
        backIntent.putExtra("FIRST_NAME", firstName);
        backIntent.putExtra("LAST_NAME", lastName);
        backIntent.putExtra("USER_ID", userId);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "Starting MarketingHeadPanelActivity with flags: CLEAR_TOP | NEW_TASK");
        startActivity(backIntent);
        finish(); // Close current activity
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
        builder.setTitle("About DSA Code Master");
        builder.setMessage("DSA Code Master Panel v1.0\n\n" +
                "This panel provides comprehensive DSA code management tools including:\n" +
                "• Add New DSA Codes\n" +
                "• View and Manage DSA Code Lists\n" +
                "• DSA Code Operations\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

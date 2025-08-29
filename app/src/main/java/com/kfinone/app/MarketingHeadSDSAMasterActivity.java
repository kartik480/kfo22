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

public class MarketingHeadSDSAMasterActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadSDSAMaster";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView activeSDSACount;
    private TextView inactiveSDSACount;
    
    // Card Views
    private CardView cardActiveSDSAList;
    private CardView cardInactiveSDSAList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_sdsa_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadSDSAMasterActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadSDSAMasterActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupClickListeners();
        updateWelcomeText();
        setInitialStats();
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        activeSDSACount = findViewById(R.id.activeSDSACount);
        inactiveSDSACount = findViewById(R.id.inactiveSDSACount);
        
        cardActiveSDSAList = findViewById(R.id.cardActiveSDSAList);
        cardInactiveSDSAList = findViewById(R.id.cardInactiveSDSAList);
    }
    
    private void setupClickListeners() {
        // Active SDSA List
        cardActiveSDSAList.setOnClickListener(v -> {
            Intent intent = new Intent(MarketingHeadSDSAMasterActivity.this, MarketingHeadActiveSDSAListActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
        
        // Inactive SDSA List
        cardInactiveSDSAList.setOnClickListener(v -> {
            showToast("Inactive SDSA List - Coming Soon!");
            // TODO: Launch Inactive SDSA List Activity
        });
    }
    
    private void setInitialStats() {
        activeSDSACount.setText("0");
        inactiveSDSACount.setText("0");
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - SDSA Master");
        } else {
            welcomeText.setText("Welcome, Marketing Head - SDSA Master");
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
        builder.setTitle("About SDSA Master");
        builder.setMessage("SDSA Master Panel v1.0\n\n" +
                "This panel provides comprehensive SDSA management tools including:\n" +
                "• Active SDSA List\n" +
                "• Inactive SDSA List\n" +
                "• SDSA Status Management\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

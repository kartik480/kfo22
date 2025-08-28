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

public class MarketingHeadEmpMasterActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadEmpMaster";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView activeEmpCount;
    private TextView inactiveEmpCount;
    
    // Card Views
    private CardView cardActiveEmpList;
    private CardView cardInactiveEmpList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_emp_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadEmpMasterActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadEmpMasterActivity received USERNAME: " + username);
        
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
        activeEmpCount = findViewById(R.id.activeEmpCount);
        inactiveEmpCount = findViewById(R.id.inactiveEmpCount);
        
        cardActiveEmpList = findViewById(R.id.cardActiveEmpList);
        cardInactiveEmpList = findViewById(R.id.cardInactiveEmpList);
    }
    
    private void setupClickListeners() {
        // Active Emp List
        cardActiveEmpList.setOnClickListener(v -> {
            Intent activeEmpListIntent = new Intent(MarketingHeadEmpMasterActivity.this, MarketingHeadActiveEmpListActivity.class);
            activeEmpListIntent.putExtra("USERNAME", username);
            activeEmpListIntent.putExtra("FIRST_NAME", firstName);
            activeEmpListIntent.putExtra("LAST_NAME", lastName);
            activeEmpListIntent.putExtra("USER_ID", userId);
            startActivity(activeEmpListIntent);
        });
        
        // Inactive Emp List
        cardInactiveEmpList.setOnClickListener(v -> {
            showToast("Inactive Employee List - Coming Soon!");
            // TODO: Launch Inactive Employee List Activity
        });
    }
    
    private void setInitialStats() {
        activeEmpCount.setText("0");
        inactiveEmpCount.setText("0");
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - Employee Master");
        } else {
            welcomeText.setText("Welcome, Marketing Head - Employee Master");
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
        builder.setTitle("About Employee Master");
        builder.setMessage("Employee Master Panel v1.0\n\n" +
                "This panel provides comprehensive employee management tools including:\n" +
                "• Active Employee List\n" +
                "• Inactive Employee List\n" +
                "• Employee Status Management\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

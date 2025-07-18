package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOSettingsActivity extends AppCompatActivity {
    
    // UI Components
    private TextView titleText;
    private Button backButton;
    private Button saveButton;
    
    // Data
    private String userName;
    private String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_settings);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        initializeViews();
        setupClickListeners();
    }
    
    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        
        // Set title
        titleText.setText("Settings - " + (userName != null ? userName : "CBO"));
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
    }
} 
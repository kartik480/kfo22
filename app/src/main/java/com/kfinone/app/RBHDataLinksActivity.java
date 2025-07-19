package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RBHDataLinksActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView titleText;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_data_links);
        
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        titleText.setText("RBH Data Links");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }
} 
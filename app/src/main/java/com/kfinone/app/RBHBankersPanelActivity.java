package com.kfinone.app;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RBHBankersPanelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankers);
        
        // Find views
        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        
        // Set title
        if (titleText != null) {
            titleText.setText("RBH Bankers Panel");
        }
        
        // Set back button click listener
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
} 
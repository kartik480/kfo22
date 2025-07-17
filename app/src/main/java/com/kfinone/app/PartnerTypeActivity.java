package com.kfinone.app;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PartnerTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_type);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up header title
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Type of Partner");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 

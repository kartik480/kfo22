package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DataLinksActivity extends AppCompatActivity {

    private CardView myDataLinksCard;
    private CardView teamDataLinksCard;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_links);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        myDataLinksCard = findViewById(R.id.myDataLinksCard);
        teamDataLinksCard = findViewById(R.id.teamDataLinksCard);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // My Data Links Card
        myDataLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "My Data Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        // Team Data Links Card
        teamDataLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "Team Data Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 
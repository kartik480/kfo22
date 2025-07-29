package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BusinessHeadWorkLinksActivity extends AppCompatActivity {

    private CardView myWorkLinksCard;
    private CardView teamWorkLinksCard;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_work_links);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        myWorkLinksCard = findViewById(R.id.myWorkLinksCard);
        teamWorkLinksCard = findViewById(R.id.teamWorkLinksCard);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // My Work Links Card
        myWorkLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "My Work Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        // Team Work Links Card
        teamWorkLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "Team Work Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 
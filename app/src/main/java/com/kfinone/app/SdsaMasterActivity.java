package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class SdsaMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdsa_master);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        MaterialCardView addSdsaCard = findViewById(R.id.addSdsaCard);
        MaterialCardView activeSdsaListCard = findViewById(R.id.activeSdsaListCard);
        MaterialCardView inactiveSdsaListCard = findViewById(R.id.inactiveSdsaListCard);

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Add SDSA card click listener
        addSdsaCard.setOnClickListener(v -> {
            Intent intent = new Intent(SdsaMasterActivity.this, AddSdsaActivity.class);
            startActivity(intent);
        });

        // Active SDSA List card click listener
        activeSdsaListCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActiveSdsaListActivity.class);
            startActivity(intent);
        });

        // Inactive SDSA List card click listener
        inactiveSdsaListCard.setOnClickListener(v -> {
            // Launch Inactive SDSA List activity
            Intent intent = new Intent(this, InactiveSdsaListActivity.class);
            startActivity(intent);
        });
    }
} 

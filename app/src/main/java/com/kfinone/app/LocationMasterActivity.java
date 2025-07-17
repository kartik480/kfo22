package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class LocationMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_master);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize card views
        MaterialCardView stateCard = findViewById(R.id.stateCard);
        MaterialCardView locationCard = findViewById(R.id.locationCard);
        MaterialCardView subLocationCard = findViewById(R.id.subLocationCard);
        MaterialCardView pincodeCard = findViewById(R.id.pincodeCard);
        MaterialCardView branchStateCard = findViewById(R.id.branchStateCard);
        MaterialCardView branchLocationCard = findViewById(R.id.branchLocationCard);

        // Set click listeners
        stateCard.setOnClickListener(v -> {
            Intent intent = new Intent(LocationMasterActivity.this, StateActivity.class);
            startActivity(intent);
        });

        locationCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });

        subLocationCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubLocationActivity.class);
            startActivity(intent);
        });

        pincodeCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, PincodeActivity.class);
            startActivity(intent);
        });

        branchStateCard.setOnClickListener(v -> {
            Intent intent = new Intent(LocationMasterActivity.this, BranchStateActivity.class);
            startActivity(intent);
        });

        branchLocationCard.setOnClickListener(v -> {
            Intent intent = new Intent(LocationMasterActivity.this, BranchLocationActivity.class);
            startActivity(intent);
        });
    }
} 

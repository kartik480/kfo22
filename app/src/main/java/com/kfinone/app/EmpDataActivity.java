package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class EmpDataActivity extends AppCompatActivity {

    private MaterialCardView addIconsCard;
    private MaterialCardView teamLinksCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_data);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        addIconsCard = findViewById(R.id.addIconsCard);
        teamLinksCard = findViewById(R.id.teamLinksCard);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup Add Icons card click
        addIconsCard.setOnClickListener(v -> {
            Intent intent = new Intent(EmpDataActivity.this, AddIconsActivity.class);
            startActivity(intent);
        });

        // Setup Team Links card click
        teamLinksCard.setOnClickListener(v -> {
            Intent intent = new Intent(EmpDataActivity.this, TeamLinksActivity.class);
            startActivity(intent);
        });
    }
} 

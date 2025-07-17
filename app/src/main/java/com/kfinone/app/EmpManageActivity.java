package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class EmpManageActivity extends AppCompatActivity {
    private MaterialCardView addIconsCard;
    private MaterialCardView teamLinksCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_manage);

        // Initialize views
        addIconsCard = findViewById(R.id.addIconsCard);
        teamLinksCard = findViewById(R.id.teamLinksCard);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Setup card click listeners
        addIconsCard.setOnClickListener(v -> {
            Intent intent = new Intent(EmpManageActivity.this, AddIconsActivity.class);
            startActivity(intent);
        });

        teamLinksCard.setOnClickListener(v -> {
            Intent intent = new Intent(EmpManageActivity.this, TeamLinksActivity.class);
            startActivity(intent);
        });
    }
} 

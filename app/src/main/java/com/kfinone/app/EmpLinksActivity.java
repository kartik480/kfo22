package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EmpLinksActivity extends AppCompatActivity {

    private CardView myEmpLinksCard;
    private CardView teamEmpLinksCard;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_links);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        myEmpLinksCard = findViewById(R.id.myEmpLinksCard);
        teamEmpLinksCard = findViewById(R.id.teamEmpLinksCard);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // My Emp Links Card
        myEmpLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "My Emp Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        // Team Emp Links Card
        teamEmpLinksCard.setOnClickListener(v -> {
            Toast.makeText(this, "Team Emp Links - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }
} 

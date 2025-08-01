package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        // Debug logging
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("EmpLinksActivity", "Received USER_ID: " + intent.getStringExtra("USER_ID"));
            Log.d("EmpLinksActivity", "Received USERNAME: " + intent.getStringExtra("USERNAME"));
        }

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
            Intent currentIntent = getIntent();
            String sourcePanel = currentIntent.getStringExtra("SOURCE_PANEL");
            
            Intent intent;
            if ("SPECIAL_PANEL".equals(sourcePanel)) {
                // Managing Director panel - navigate to Director Emp Links
                intent = new Intent(EmpLinksActivity.this, DirectorEmpLinksActivity.class);
            } else {
                // Business Head panel - navigate to Business Head Emp Links
                intent = new Intent(EmpLinksActivity.this, BusinessHeadMyEmpLinksActivity.class);
            }
            
            // Pass user data from current intent
            if (currentIntent != null) {
                String userId = currentIntent.getStringExtra("USER_ID");
                if (userId != null) intent.putExtra("USER_ID", userId);
                String firstName = currentIntent.getStringExtra("FIRST_NAME");
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                String lastName = currentIntent.getStringExtra("LAST_NAME");
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                String fullName = currentIntent.getStringExtra("USERNAME");
                if (fullName != null) intent.putExtra("USERNAME", fullName);
            }
            startActivity(intent);
        });

        // Team Emp Links Card
        teamEmpLinksCard.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            String sourcePanel = currentIntent.getStringExtra("SOURCE_PANEL");
            
            if ("SPECIAL_PANEL".equals(sourcePanel)) {
                // Managing Director panel - navigate to Director Team Emp Links
                Intent intent = new Intent(EmpLinksActivity.this, DirectorTeamEmpLinksActivity.class);
                // Pass user data from current intent
                if (currentIntent != null) {
                    String userId = currentIntent.getStringExtra("USER_ID");
                    if (userId != null) intent.putExtra("USER_ID", userId);
                    String firstName = currentIntent.getStringExtra("FIRST_NAME");
                    if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                    String lastName = currentIntent.getStringExtra("LAST_NAME");
                    if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                    String fullName = currentIntent.getStringExtra("USERNAME");
                    if (fullName != null) intent.putExtra("USERNAME", fullName);
                }
                startActivity(intent);
            } else {
                // Business Head panel - show coming soon
                Toast.makeText(this, "Team Emp Links - Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 

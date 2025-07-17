package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DirectorPayoutActivity extends AppCompatActivity {

    private LinearLayout viewPayoutsButton;
    private LinearLayout addPayoutButton;
    private LinearLayout payoutAnalyticsButton;
    private LinearLayout payoutReportsButton;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_payout);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        initializeViews();
        setupClickListeners();
        setupToolbar();
    }

    private void initializeViews() {
        viewPayoutsButton = findViewById(R.id.viewPayoutsButton);
        addPayoutButton = findViewById(R.id.addPayoutButton);
        payoutAnalyticsButton = findViewById(R.id.payoutAnalyticsButton);
        payoutReportsButton = findViewById(R.id.payoutReportsButton);
    }

    private void setupClickListeners() {
        viewPayoutsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PayoutActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        addPayoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPayoutActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        payoutAnalyticsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Payout Analytics - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        payoutReportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Payout Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director Payout Management");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
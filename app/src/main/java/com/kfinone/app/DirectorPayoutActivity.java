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
    private LinearLayout payoutTeamButton;
    
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
        payoutTeamButton = findViewById(R.id.payoutTeamButton);
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

        payoutTeamButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorPayoutTeamActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("USER_DESIGNATION", "Director");
            startActivity(intent);
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

    @Override
    public void onBackPressed() {
        // Navigate back to Director panel
        Intent intent = new Intent(this, User10002PanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
} 
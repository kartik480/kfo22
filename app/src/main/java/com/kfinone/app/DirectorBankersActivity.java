package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DirectorBankersActivity extends AppCompatActivity {

    private LinearLayout viewBankersButton;
    private LinearLayout addBankerButton;
    private LinearLayout bankerAnalyticsButton;
    private LinearLayout bankerReportsButton;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_bankers);

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
        viewBankersButton = findViewById(R.id.viewBankersButton);
        addBankerButton = findViewById(R.id.addBankerButton);
        bankerAnalyticsButton = findViewById(R.id.bankerAnalyticsButton);
        bankerReportsButton = findViewById(R.id.bankerReportsButton);
    }

    private void setupClickListeners() {
        viewBankersButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BankerListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        addBankerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBankerActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        bankerAnalyticsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Banker Analytics - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        bankerReportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Banker Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director Bankers Management");
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
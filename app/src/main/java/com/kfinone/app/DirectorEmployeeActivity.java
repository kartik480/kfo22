package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DirectorEmployeeActivity extends AppCompatActivity {

    private LinearLayout activeEmpButton;
    private LinearLayout inactiveEmpButton;
    private LinearLayout addEmpButton;
    private LinearLayout empAnalyticsButton;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_employee);

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
        activeEmpButton = findViewById(R.id.activeEmpButton);
        inactiveEmpButton = findViewById(R.id.inactiveEmpButton);
        addEmpButton = findViewById(R.id.addEmpButton);
        empAnalyticsButton = findViewById(R.id.empAnalyticsButton);
    }

    private void setupClickListeners() {
        activeEmpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        inactiveEmpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InactiveEmpListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        addEmpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEmpDetailsActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
            startActivity(intent);
        });

        empAnalyticsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Employee Analytics - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director Employee Management");
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
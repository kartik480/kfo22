package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOEmpMasterActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Emp Master boxes
    private LinearLayout addEmpBox;
    private LinearLayout activeEmpListBox;
    private LinearLayout inactiveEmpListBox;

    // Count displays
    private TextView addEmpCount;
    private TextView activeEmpListCount;
    private TextView inactiveEmpListCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_emp_master);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadCBOEmpMasterData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // CBO Emp Master boxes
        addEmpBox = findViewById(R.id.addEmpBox);
        activeEmpListBox = findViewById(R.id.activeEmpListBox);
        inactiveEmpListBox = findViewById(R.id.inactiveEmpListBox);

        // Count displays
        addEmpCount = findViewById(R.id.addEmpCount);
        activeEmpListCount = findViewById(R.id.activeEmpListCount);
        inactiveEmpListCount = findViewById(R.id.inactiveEmpListCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmployee());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // CBO Emp Master boxes click listeners
        addEmpBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAddEmpActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        activeEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        inactiveEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOInactiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing CBO employee master data...", Toast.LENGTH_SHORT).show();
        loadCBOEmpMasterData();
    }

    private void addNewEmployee() {
        Intent intent = new Intent(this, CBOAddEmpActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
    }

    private void loadCBOEmpMasterData() {
        // TODO: Load real CBO employee master data from server
        // For now, set some sample data
        addEmpCount.setText("Add New Employee");
        activeEmpListCount.setText("Active Employees");
        inactiveEmpListCount.setText("Inactive Employees");
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
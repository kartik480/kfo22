package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOEmployeeActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // CBO Employee box
    private LinearLayout myEmpBox;

    // Count display
    private TextView myEmpCount;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_employee);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug: Log what we received from the previous activity
        Log.d("CBOEmployeeActivity", "Received userName: " + userName);
        Log.d("CBOEmployeeActivity", "Received userId: " + userId);
        
        // Critical check: Ensure we have a numeric user ID
        if (userId == null || !userId.matches("\\d+")) {
            Log.e("CBOEmployeeActivity", "ERROR: No numeric user ID received!");
            Log.e("CBOEmployeeActivity", "This will cause issues in downstream activities");
            Log.e("CBOEmployeeActivity", "Expected: numeric ID like '21', Got: '" + userId + "'");
            
            // Try to get userId from SharedPreferences as fallback
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String savedUserId = prefs.getString("USER_ID", null);
                if (savedUserId != null && !savedUserId.isEmpty() && savedUserId.matches("\\d+")) {
                    userId = savedUserId;
                    Log.d("CBOEmployeeActivity", "Using userId from SharedPreferences: " + userId);
                } else {
                    Log.e("CBOEmployeeActivity", "No valid userId found in SharedPreferences either");
                }
            } catch (Exception e) {
                Log.e("CBOEmployeeActivity", "Error reading from SharedPreferences: " + e.getMessage());
            }
        } else {
            Log.d("CBOEmployeeActivity", "✓ Valid numeric userId received: " + userId);
        }
        
        // Debug: Log ALL intent extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d("CBOEmployeeActivity", "=== All Intent Extras ===");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("CBOEmployeeActivity", key + " = " + value);
            }
            Log.d("CBOEmployeeActivity", "========================");
        }

        initializeViews();
        setupClickListeners();
        loadCBOEmployeeData();
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

        // CBO Employee box
        myEmpBox = findViewById(R.id.myEmpBox);

        // Count display
        myEmpCount = findViewById(R.id.myEmpCount);
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

        // CBO Employee box click listener - Navigate to new CBO Active Employee List
        myEmpBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOMyActiveEmpListActivity.class);
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
        Toast.makeText(this, "Refreshing CBO employee data...", Toast.LENGTH_SHORT).show();
        loadCBOEmployeeData();
    }

    private void addNewEmployee() {
        Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Employee activity
    }

    private void loadCBOEmployeeData() {
        // TODO: Load real CBO employee data from server
        // For now, set some sample data
        myEmpCount.setText("My Employees");
    }

    private void passUserDataToIntent(Intent intent) {
        Log.d("CBOEmployeeActivity", "=== Passing User Data to Intent ===");
        Log.d("CBOEmployeeActivity", "Passing USERNAME: " + userName);
        Log.d("CBOEmployeeActivity", "Passing USER_ID: " + userId);
        
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
        
        Log.d("CBOEmployeeActivity", "=================================");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
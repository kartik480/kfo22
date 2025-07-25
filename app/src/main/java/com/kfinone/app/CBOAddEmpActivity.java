package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CBOAddEmpActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // User data
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_add_emp);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadAddEmployeeData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOEmpMasterActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing add employee form...", Toast.LENGTH_SHORT).show();
        loadAddEmployeeData();
    }

    private void saveNewEmployee() {
        Toast.makeText(this, "Saving new employee...", Toast.LENGTH_SHORT).show();
        // TODO: Implement employee saving logic
        // For now, just show a success message and go back
        Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_SHORT).show();
        goBack();
    }

    private void loadAddEmployeeData() {
        // TODO: Load any necessary data for the add employee form
        // For now, show placeholder content
        Toast.makeText(this, "Loading add employee form...", Toast.LENGTH_SHORT).show();
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
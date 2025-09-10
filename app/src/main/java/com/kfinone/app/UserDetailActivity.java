package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserDetailActivity extends AppCompatActivity {
    
    private TextView fullNameText, usernameText, employeeNoText, mobileText, emailText, 
                     designationText, departmentText, statusText, manageIconsText, 
                     managerNameText, managerDesignationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        setupToolbar();
        initializeViews();
        populateUserData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("User Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void initializeViews() {
        fullNameText = findViewById(R.id.fullNameText);
        usernameText = findViewById(R.id.usernameText);
        employeeNoText = findViewById(R.id.employeeNoText);
        mobileText = findViewById(R.id.mobileText);
        emailText = findViewById(R.id.emailText);
        designationText = findViewById(R.id.designationText);
        departmentText = findViewById(R.id.departmentText);
        statusText = findViewById(R.id.statusText);
        manageIconsText = findViewById(R.id.manageIconsText);
        managerNameText = findViewById(R.id.managerNameText);
        managerDesignationText = findViewById(R.id.managerDesignationText);
    }

    private void populateUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Basic Information
            String fullName = intent.getStringExtra("FULLNAME");
            String username = intent.getStringExtra("USERNAME");
            String employeeNo = intent.getStringExtra("EMPLOYEE_NO");
            String mobile = intent.getStringExtra("MOBILE");
            String email = intent.getStringExtra("EMAIL");
            
            // Professional Information
            String designation = intent.getStringExtra("DESIGNATION");
            String department = intent.getStringExtra("DEPARTMENT");
            String status = intent.getStringExtra("STATUS");
            String manageIcons = intent.getStringExtra("MANAGE_ICONS");
            
            // Manager Information
            String managerName = intent.getStringExtra("MANAGER_NAME");
            String managerDesignation = intent.getStringExtra("MANAGER_DESIGNATION");
            
            // Set the data
            fullNameText.setText(fullName != null ? fullName : "N/A");
            usernameText.setText("Username: " + (username != null ? username : "N/A"));
            employeeNoText.setText("Employee ID: " + (employeeNo != null ? employeeNo : "N/A"));
            mobileText.setText("Mobile: " + (mobile != null ? mobile : "N/A"));
            emailText.setText("Email: " + (email != null ? email : "N/A"));
            designationText.setText("Designation: " + (designation != null ? designation : "N/A"));
            departmentText.setText("Department: " + (department != null ? department : "N/A"));
            statusText.setText("Status: " + (status != null && status.equals("1") ? "Active" : "Inactive"));
            manageIconsText.setText("Manage Icons: " + (manageIcons != null && !manageIcons.isEmpty() ? manageIcons : "None"));
            managerNameText.setText("Manager: " + (managerName != null && !managerName.isEmpty() ? managerName : "N/A"));
            managerDesignationText.setText("Manager Designation: " + (managerDesignation != null && !managerDesignation.isEmpty() ? managerDesignation : "N/A"));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

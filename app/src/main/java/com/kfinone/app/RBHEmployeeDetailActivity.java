package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;

public class RBHEmployeeDetailActivity extends AppCompatActivity {

    // UI Elements
    private TextView nameText, usernameText, emailText, phoneText, employeeNoText;
    private TextView departmentText, designationText, statusText, reportingToText;
    private Button backButton;
    
    // Employee data
    private Map<String, Object> employeeData;
    private String sourcePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_employee_detail);
        
        // Get employee data from intent
        Intent intent = getIntent();
        if (intent.hasExtra("EMPLOYEE_DATA")) {
            employeeData = (Map<String, Object>) intent.getSerializableExtra("EMPLOYEE_DATA");
        }
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        initializeViews();
        displayEmployeeData();
        setupClickListeners();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.employeeNameText);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        employeeNoText = findViewById(R.id.employeeNoText);
        departmentText = findViewById(R.id.departmentText);
        designationText = findViewById(R.id.designationText);
        statusText = findViewById(R.id.statusText);
        reportingToText = findViewById(R.id.reportingToText);
        backButton = findViewById(R.id.backButton);
    }

    private void displayEmployeeData() {
        if (employeeData == null) {
            finish();
            return;
        }

        // Display employee information
        String firstName = (String) employeeData.get("firstName");
        String lastName = (String) employeeData.get("lastName");
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        nameText.setText(fullName.trim());

        usernameText.setText("Username: " + (String) employeeData.get("username"));
        emailText.setText("Email: " + (String) employeeData.get("email_id"));
        phoneText.setText("Mobile: " + (String) employeeData.get("mobile"));
        employeeNoText.setText("Employee No: " + (String) employeeData.get("employee_no"));
        departmentText.setText("Department: " + (String) employeeData.get("department_name"));
        designationText.setText("Designation: " + (String) employeeData.get("designation_name"));
        
        // Handle status display
        Object status = employeeData.get("status");
        String statusTextValue = "Status: ";
        if (status != null) {
            if (status.toString().equals("1")) {
                statusTextValue += "Active";
            } else if (status.toString().equals("0")) {
                statusTextValue += "Inactive";
            } else {
                statusTextValue += status.toString();
            }
        } else {
            statusTextValue += "N/A";
        }
        statusText.setText(statusTextValue);
        
        reportingToText.setText("Reports To: " + (String) employeeData.get("reportingTo"));
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent;
        
        // Navigate back based on source panel
        if ("RBH_EMPLOYEE_USERS".equals(sourcePanel)) {
            intent = new Intent(this, RBHEmployeeUsersActivity.class);
        } else {
            // Default fallback
            intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        }
        
        // Pass user data back
        if (getIntent().hasExtra("USER_ID")) {
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
        }
        if (getIntent().hasExtra("USERNAME")) {
            intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
        }
        if (getIntent().hasExtra("FIRST_NAME")) {
            intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
        }
        if (getIntent().hasExtra("LAST_NAME")) {
            intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
        }
        
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
}

package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;

public class BusinessHeadPartnerDetailActivity extends AppCompatActivity {

    // UI Elements
    private TextView nameText, usernameText, emailText, phoneText, companyText, statusText, rankText;
    private TextView employeeNoText, departmentText, designationText, branchStateText, branchLocationText;
    private TextView bankNameText, accountTypeText, createdAtText, createdByText;
    private Button backButton;
    
    // Partner data
    private Map<String, Object> partnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_partner_detail);
        
        // Get partner data from intent
        Intent intent = getIntent();
        if (intent.hasExtra("PARTNER_DATA")) {
            partnerData = (Map<String, Object>) intent.getSerializableExtra("PARTNER_DATA");
        }
        
        initializeViews();
        displayPartnerData();
        setupClickListeners();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.partnerNameText);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        companyText = findViewById(R.id.companyText);
        statusText = findViewById(R.id.statusText);
        rankText = findViewById(R.id.rankText);
        employeeNoText = findViewById(R.id.employeeNoText);
        departmentText = findViewById(R.id.departmentText);
        designationText = findViewById(R.id.designationText);
        branchStateText = findViewById(R.id.branchStateText);
        branchLocationText = findViewById(R.id.branchLocationText);
        bankNameText = findViewById(R.id.bankNameText);
        accountTypeText = findViewById(R.id.accountTypeText);
        createdAtText = findViewById(R.id.createdAtText);
        createdByText = findViewById(R.id.createdByText);
        backButton = findViewById(R.id.backButton);
    }

    private void displayPartnerData() {
        if (partnerData == null) {
            finish();
            return;
        }

        // Display partner information
        String firstName = (String) partnerData.get("first_name");
        String lastName = (String) partnerData.get("last_name");
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        nameText.setText(fullName.trim());

        usernameText.setText("Username: " + (String) partnerData.get("username"));
        emailText.setText("Email: " + (String) partnerData.get("email_id"));
        phoneText.setText("Phone: " + (String) partnerData.get("Phone_number"));
        companyText.setText("Company: " + (String) partnerData.get("company_name"));
        statusText.setText("Status: " + (String) partnerData.get("status"));
        rankText.setText("Rank: " + (String) partnerData.get("rank"));
        employeeNoText.setText("Employee No: " + (String) partnerData.get("employee_no"));
        departmentText.setText("Department: " + (String) partnerData.get("department"));
        designationText.setText("Designation: " + (String) partnerData.get("designation"));
        branchStateText.setText("Branch State: " + (String) partnerData.get("branchstate"));
        branchLocationText.setText("Branch Location: " + (String) partnerData.get("branchloaction"));
        bankNameText.setText("Bank Name: " + (String) partnerData.get("bank_name"));
        accountTypeText.setText("Account Type: " + (String) partnerData.get("account_type"));
        createdAtText.setText("Created At: " + (String) partnerData.get("created_at"));
        createdByText.setText("Created By: " + (String) partnerData.get("createdBy"));
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
}

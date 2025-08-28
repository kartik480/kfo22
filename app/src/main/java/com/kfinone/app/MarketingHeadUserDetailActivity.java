package com.kfinone.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class MarketingHeadUserDetailActivity extends AppCompatActivity {
    
    private TextView nameText, usernameText, emailText, phoneText, statusText, employeeNoText;
    private TextView designationText, departmentText, birthDateText, rankText, workStateText, workLocationText;
    private TextView residentialAddressText, officeAddressText, panNumberText, aadhaarNumberText;
    private TextView accHolderNameText, bankNameText, accountTypeText, branchNameText, accountNumberText, ifscCodeText;
    private TextView refName1Text, refRelation1Text, refMobile1Text, refAddress1Text;
    private TextView refName2Text, refRelation2Text, refMobile2Text, refAddress2Text;
    private TextView reportingToText, officialPhoneText, officialEmailText;
    private TextView manageIconsText, dataIconsText;
    private TextView createdByText, createdAtText, updatedAtText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_user_detail);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Details");
        }
        
        // Initialize views
        initializeViews();
        
        // Get user data from intent
        populateUserData();
    }
    
    private void initializeViews() {
        // Basic Information
        nameText = findViewById(R.id.nameText);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        statusText = findViewById(R.id.statusText);
        employeeNoText = findViewById(R.id.employeeNoText);
        designationText = findViewById(R.id.designationText);
        departmentText = findViewById(R.id.departmentText);
        birthDateText = findViewById(R.id.birthDateText);
        rankText = findViewById(R.id.rankText);
        
        // Work Information
        workStateText = findViewById(R.id.workStateText);
        workLocationText = findViewById(R.id.workLocationText);
        reportingToText = findViewById(R.id.reportingToText);
        officialPhoneText = findViewById(R.id.officialPhoneText);
        officialEmailText = findViewById(R.id.officialEmailText);
        
        // Address Information
        residentialAddressText = findViewById(R.id.residentialAddressText);
        officeAddressText = findViewById(R.id.officeAddressText);
        
        // Identity Information
        panNumberText = findViewById(R.id.panNumberText);
        aadhaarNumberText = findViewById(R.id.aadhaarNumberText);
        
        // Bank Information
        accHolderNameText = findViewById(R.id.accHolderNameText);
        bankNameText = findViewById(R.id.bankNameText);
        accountTypeText = findViewById(R.id.accountTypeText);
        branchNameText = findViewById(R.id.branchNameText);
        accountNumberText = findViewById(R.id.accountNumberText);
        ifscCodeText = findViewById(R.id.ifscCodeText);
        
        // Reference Information
        refName1Text = findViewById(R.id.refName1Text);
        refRelation1Text = findViewById(R.id.refRelation1Text);
        refMobile1Text = findViewById(R.id.refMobile1Text);
        refAddress1Text = findViewById(R.id.refAddress1Text);
        
        refName2Text = findViewById(R.id.refName2Text);
        refRelation2Text = findViewById(R.id.refRelation2Text);
        refMobile2Text = findViewById(R.id.refMobile2Text);
        refAddress2Text = findViewById(R.id.refAddress2Text);
        
        // System Information
        manageIconsText = findViewById(R.id.manageIconsText);
        dataIconsText = findViewById(R.id.dataIconsText);
        createdByText = findViewById(R.id.createdByText);
        createdAtText = findViewById(R.id.createdAtText);
        updatedAtText = findViewById(R.id.updatedAtText);
    }
    
    private void populateUserData() {
        // Get user data from intent
        String id = getIntent().getStringExtra("USER_ID");
        String username = getIntent().getStringExtra("USERNAME");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String phone = getIntent().getStringExtra("PHONE");
        String status = getIntent().getStringExtra("STATUS");
        String employeeNo = getIntent().getStringExtra("EMPLOYEE_NO");
        String designation = getIntent().getStringExtra("DESIGNATION");
        String department = getIntent().getStringExtra("DEPARTMENT");
        String birthDate = getIntent().getStringExtra("BIRTH_DATE");
        String rank = getIntent().getStringExtra("RANK");
        String workState = getIntent().getStringExtra("WORK_STATE");
        String workLocation = getIntent().getStringExtra("WORK_LOCATION");
        String residentialAddress = getIntent().getStringExtra("RESIDENTIAL_ADDRESS");
        String officeAddress = getIntent().getStringExtra("OFFICE_ADDRESS");
        String panNumber = getIntent().getStringExtra("PAN_NUMBER");
        String aadhaarNumber = getIntent().getStringExtra("AADHAAR_NUMBER");
        String accHolderName = getIntent().getStringExtra("ACC_HOLDER_NAME");
        String bankName = getIntent().getStringExtra("BANK_NAME");
        String accountType = getIntent().getStringExtra("ACCOUNT_TYPE");
        String branchName = getIntent().getStringExtra("BRANCH_NAME");
        String accountNumber = getIntent().getStringExtra("ACCOUNT_NUMBER");
        String ifscCode = getIntent().getStringExtra("IFSC_CODE");
        String refName1 = getIntent().getStringExtra("REF_NAME_1");
        String refRelation1 = getIntent().getStringExtra("REF_RELATION_1");
        String refMobile1 = getIntent().getStringExtra("REF_MOBILE_1");
        String refAddress1 = getIntent().getStringExtra("REF_ADDRESS_1");
        String refName2 = getIntent().getStringExtra("REF_NAME_2");
        String refRelation2 = getIntent().getStringExtra("REF_RELATION_2");
        String refMobile2 = getIntent().getStringExtra("REF_MOBILE_2");
        String refAddress2 = getIntent().getStringExtra("REF_ADDRESS_2");
        String reportingTo = getIntent().getStringExtra("REPORTING_TO");
        String officialPhone = getIntent().getStringExtra("OFFICIAL_PHONE");
        String officialEmail = getIntent().getStringExtra("OFFICIAL_EMAIL");
        String manageIcons = getIntent().getStringExtra("MANAGE_ICONS");
        String dataIcons = getIntent().getStringExtra("DATA_ICONS");
        String createdBy = getIntent().getStringExtra("CREATED_BY");
        String createdAt = getIntent().getStringExtra("CREATED_AT");
        String updatedAt = getIntent().getStringExtra("UPDATED_AT");
        
        // Set the data to views
        nameText.setText(getDisplayText("Full Name", firstName + " " + lastName));
        usernameText.setText(getDisplayText("Username", username));
        emailText.setText(getDisplayText("Email", email));
        phoneText.setText(getDisplayText("Phone", phone));
        statusText.setText(getDisplayText("Status", status));
        employeeNoText.setText(getDisplayText("Employee No", employeeNo));
        designationText.setText(getDisplayText("Designation", designation));
        departmentText.setText(getDisplayText("Department", department));
        birthDateText.setText(getDisplayText("Birth Date", birthDate));
        rankText.setText(getDisplayText("Rank", rank));
        
        workStateText.setText(getDisplayText("Work State", workState));
        workLocationText.setText(getDisplayText("Work Location", workLocation));
        reportingToText.setText(getDisplayText("Reporting To", reportingTo));
        officialPhoneText.setText(getDisplayText("Official Phone", officialPhone));
        officialEmailText.setText(getDisplayText("Official Email", officialEmail));
        
        residentialAddressText.setText(getDisplayText("Residential Address", residentialAddress));
        officeAddressText.setText(getDisplayText("Office Address", officeAddress));
        
        panNumberText.setText(getDisplayText("PAN Number", panNumber));
        aadhaarNumberText.setText(getDisplayText("Aadhaar Number", aadhaarNumber));
        
        accHolderNameText.setText(getDisplayText("Account Holder Name", accHolderName));
        bankNameText.setText(getDisplayText("Bank Name", bankName));
        accountTypeText.setText(getDisplayText("Account Type", accountType));
        branchNameText.setText(getDisplayText("Branch Name", branchName));
        accountNumberText.setText(getDisplayText("Account Number", accountNumber));
        ifscCodeText.setText(getDisplayText("IFSC Code", ifscCode));
        
        refName1Text.setText(getDisplayText("Reference 1 Name", refName1));
        refRelation1Text.setText(getDisplayText("Reference 1 Relation", refRelation1));
        refMobile1Text.setText(getDisplayText("Reference 1 Mobile", refMobile1));
        refAddress1Text.setText(getDisplayText("Reference 1 Address", refAddress1));
        
        refName2Text.setText(getDisplayText("Reference 2 Name", refName2));
        refRelation2Text.setText(getDisplayText("Reference 2 Relation", refRelation2));
        refMobile2Text.setText(getDisplayText("Reference 2 Mobile", refMobile2));
        refAddress2Text.setText(getDisplayText("Reference 2 Address", refAddress2));
        
        manageIconsText.setText(getDisplayText("Manage Icons", manageIcons));
        dataIconsText.setText(getDisplayText("Data Icons", dataIcons));
        createdByText.setText(getDisplayText("Created By", createdBy));
        createdAtText.setText(getDisplayText("Created At", createdAt));
        updatedAtText.setText(getDisplayText("Updated At", updatedAt));
    }
    
    private String getDisplayText(String label, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return label + ": N/A";
        }
        return label + ": " + value;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

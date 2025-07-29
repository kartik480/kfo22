package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class BusinessHeadAddAgentActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Form fields
    private EditText phoneInput;
    private EditText fullNameInput;
    private EditText companyNameInput;
    private EditText emailInput;
    private EditText alternativePhoneInput;
    private TextView typeOfPartnerSpinner;
    private TextView branchStateSpinner;
    private TextView branchLocationSpinner;
    private EditText addressInput;

    // File upload fields
    private Button visitingCardChooseFileButton;
    private TextView visitingCardFileName;

    // Submit button
    private Button submitButton;

    // File picker launcher
    private ActivityResultLauncher<String> filePickerLauncher;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_add_agent);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        setupFilePicker();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);

        // Form fields
        phoneInput = findViewById(R.id.phoneInput);
        fullNameInput = findViewById(R.id.fullNameInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        emailInput = findViewById(R.id.emailInput);
        alternativePhoneInput = findViewById(R.id.alternativePhoneInput);
        typeOfPartnerSpinner = findViewById(R.id.typeOfPartnerSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        addressInput = findViewById(R.id.addressInput);

        // File upload fields
        visitingCardChooseFileButton = findViewById(R.id.visitingCardChooseFileButton);
        visitingCardFileName = findViewById(R.id.visitingCardFileName);

        // Submit button
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Spinner click listeners
        typeOfPartnerSpinner.setOnClickListener(v -> showTypeOfPartnerDialog());
        branchStateSpinner.setOnClickListener(v -> showBranchStateDialog());
        branchLocationSpinner.setOnClickListener(v -> showBranchLocationDialog());

        // File upload click listeners
        visitingCardChooseFileButton.setOnClickListener(v -> pickFile("visitingCard"));

        // Submit button
        submitButton.setOnClickListener(v -> submitForm());
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Handle the selected file
                    String fileName = getFileName(uri);
                    updateFileName(fileName);
                }
            }
        );
    }

    private void pickFile(String fileType) {
        // Store the current file type for handling the result
        // In a real implementation, you might want to use a more sophisticated approach
        filePickerLauncher.launch("*/*");
    }

    private String getFileName(Uri uri) {
        // In a real implementation, you would extract the actual filename
        // For now, return a placeholder
        return "selected_file.pdf";
    }

    private void updateFileName(String fileName) {
        // Update the appropriate file name TextView based on context
        // In a real implementation, you would track which button was clicked
        visitingCardFileName.setText(fileName);
    }

    private void showTypeOfPartnerDialog() {
        // TODO: Implement type of partner selection dialog
        Toast.makeText(this, "Type of Partner selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showBranchStateDialog() {
        // TODO: Implement branch state selection dialog
        Toast.makeText(this, "Branch State selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showBranchLocationDialog() {
        // TODO: Implement branch location selection dialog
        Toast.makeText(this, "Branch Location selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void submitForm() {
        // Validate required fields
        if (!validateForm()) {
            return;
        }

        // TODO: Implement API call to submit agent data
        // For now, show success message
        Toast.makeText(this, "Agent added successfully!", Toast.LENGTH_LONG).show();
        
        // Go back to previous screen
        goBack();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Phone validation (required)
        String phone = phoneInput.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneInput.setError("Phone is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneInput.setError("Invalid phone number");
            isValid = false;
        }

        // Full Name validation (required)
        if (fullNameInput.getText().toString().trim().isEmpty()) {
            fullNameInput.setError("Full Name is required");
            isValid = false;
        }

        // Company Name validation (required)
        if (companyNameInput.getText().toString().trim().isEmpty()) {
            companyNameInput.setError("Company Name is required");
            isValid = false;
        }

        // Email validation (optional but if provided, should be valid)
        String email = emailInput.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            isValid = false;
        }

        // Alternative Phone validation (optional but if provided, should be valid)
        String altPhone = alternativePhoneInput.getText().toString().trim();
        if (!altPhone.isEmpty() && altPhone.length() < 10) {
            alternativePhoneInput.setError("Invalid phone number");
            isValid = false;
        }

        // Type Of Partner validation (required)
        if (typeOfPartnerSpinner.getText().toString().equals("Select Partner Type")) {
            typeOfPartnerSpinner.setError("Type of Partner is required");
            isValid = false;
        }

        // Branch State validation (required)
        if (branchStateSpinner.getText().toString().equals("Select Branch State")) {
            branchStateSpinner.setError("Branch State is required");
            isValid = false;
        }

        // Branch Location validation (required)
        if (branchLocationSpinner.getText().toString().equals("Select Branch Location")) {
            branchLocationSpinner.setError("Branch Location is required");
            isValid = false;
        }

        return isValid;
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadAgentActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
} 
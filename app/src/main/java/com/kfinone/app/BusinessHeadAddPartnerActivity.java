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

public class BusinessHeadAddPartnerActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Form fields - Left Column
    private EditText emailInput;
    private EditText firstNameInput;
    private EditText aliasNameInput;
    private EditText alternativePhoneInput;
    private TextView branchStateSpinner;
    private EditText officeAddressInput;
    private EditText aadhaarInput;
    private EditText accountNumberInput;
    private TextView bankNameSpinner;

    // Form fields - Right Column
    private EditText passwordInput;
    private EditText lastNameInput;
    private EditText phoneInput;
    private TextView partnerTypeSpinner;
    private TextView branchLocationSpinner;
    private EditText residentialAddressInput;
    private EditText panNumberInput;
    private EditText ifscCodeInput;
    private TextView accountTypeSpinner;

    // File upload fields
    private Button panCardChooseFileButton;
    private TextView panCardFileName;
    private Button aadhaarChooseFileButton;
    private TextView aadhaarFileName;
    private Button photoChooseFileButton;
    private TextView photoFileName;
    private Button bankProofChooseFileButton;
    private TextView bankProofFileName;

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
        setContentView(R.layout.activity_business_head_add_partner);

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

        // Form fields - Left Column
        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        alternativePhoneInput = findViewById(R.id.alternativePhoneInput);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        aadhaarInput = findViewById(R.id.aadhaarInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        bankNameSpinner = findViewById(R.id.bankNameSpinner);

        // Form fields - Right Column
        passwordInput = findViewById(R.id.passwordInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        partnerTypeSpinner = findViewById(R.id.partnerTypeSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        panNumberInput = findViewById(R.id.panNumberInput);
        ifscCodeInput = findViewById(R.id.ifscCodeInput);
        accountTypeSpinner = findViewById(R.id.accountTypeSpinner);

        // File upload fields
        panCardChooseFileButton = findViewById(R.id.panCardChooseFileButton);
        panCardFileName = findViewById(R.id.panCardFileName);
        aadhaarChooseFileButton = findViewById(R.id.aadhaarChooseFileButton);
        aadhaarFileName = findViewById(R.id.aadhaarFileName);
        photoChooseFileButton = findViewById(R.id.photoChooseFileButton);
        photoFileName = findViewById(R.id.photoFileName);
        bankProofChooseFileButton = findViewById(R.id.bankProofChooseFileButton);
        bankProofFileName = findViewById(R.id.bankProofFileName);

        // Submit button
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Spinner click listeners
        branchStateSpinner.setOnClickListener(v -> showBranchStateDialog());
        bankNameSpinner.setOnClickListener(v -> showBankNameDialog());
        partnerTypeSpinner.setOnClickListener(v -> showPartnerTypeDialog());
        branchLocationSpinner.setOnClickListener(v -> showBranchLocationDialog());
        accountTypeSpinner.setOnClickListener(v -> showAccountTypeDialog());

        // File upload click listeners
        panCardChooseFileButton.setOnClickListener(v -> pickFile("panCard"));
        aadhaarChooseFileButton.setOnClickListener(v -> pickFile("aadhaar"));
        photoChooseFileButton.setOnClickListener(v -> pickFile("photo"));
        bankProofChooseFileButton.setOnClickListener(v -> pickFile("bankProof"));

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
        panCardFileName.setText(fileName);
    }

    private void showBranchStateDialog() {
        // TODO: Implement branch state selection dialog
        Toast.makeText(this, "Branch State selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showBankNameDialog() {
        // TODO: Implement bank name selection dialog
        Toast.makeText(this, "Bank Name selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showPartnerTypeDialog() {
        // TODO: Implement partner type selection dialog
        Toast.makeText(this, "Partner Type selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showBranchLocationDialog() {
        // TODO: Implement branch location selection dialog
        Toast.makeText(this, "Branch Location selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showAccountTypeDialog() {
        // TODO: Implement account type selection dialog
        Toast.makeText(this, "Account Type selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void submitForm() {
        // Validate required fields
        if (!validateForm()) {
            return;
        }

        // TODO: Implement API call to submit partner data
        // For now, show success message
        Toast.makeText(this, "Partner added successfully!", Toast.LENGTH_LONG).show();
        
        // Go back to previous screen
        goBack();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Email validation
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            isValid = false;
        }

        // First Name validation
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First Name is required");
            isValid = false;
        }

        // Alias Name validation
        if (aliasNameInput.getText().toString().trim().isEmpty()) {
            aliasNameInput.setError("Alias Name is required");
            isValid = false;
        }

        // Alternative Phone validation
        String altPhone = alternativePhoneInput.getText().toString().trim();
        if (altPhone.isEmpty()) {
            alternativePhoneInput.setError("Alternative Phone is required");
            isValid = false;
        } else if (altPhone.length() < 10) {
            alternativePhoneInput.setError("Invalid phone number");
            isValid = false;
        }

        // Branch State validation
        if (branchStateSpinner.getText().toString().equals("Select Branch State")) {
            branchStateSpinner.setError("Branch State is required");
            isValid = false;
        }

        // Office Address validation
        if (officeAddressInput.getText().toString().trim().isEmpty()) {
            officeAddressInput.setError("Office Address is required");
            isValid = false;
        }

        // Aadhaar validation
        String aadhaar = aadhaarInput.getText().toString().trim();
        if (aadhaar.isEmpty()) {
            aadhaarInput.setError("Aadhaar Number is required");
            isValid = false;
        } else if (aadhaar.length() != 14) {
            aadhaarInput.setError("Aadhaar number must be 14 digits");
            isValid = false;
        }

        // Account Number validation
        String accountNumber = accountNumberInput.getText().toString().trim();
        if (accountNumber.isEmpty()) {
            accountNumberInput.setError("Account Number is required");
            isValid = false;
        } else if (accountNumber.length() < 9) {
            accountNumberInput.setError("Invalid account number");
            isValid = false;
        }

        // Bank Name validation
        if (bankNameSpinner.getText().toString().equals("Select Bank name")) {
            bankNameSpinner.setError("Bank Name is required");
            isValid = false;
        }

        // Password validation
        if (passwordInput.getText().toString().trim().isEmpty()) {
            passwordInput.setError("Password is required");
            isValid = false;
        }

        // Last Name validation
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last Name is required");
            isValid = false;
        }

        // Phone validation
        String phone = phoneInput.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneInput.setError("Phone is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneInput.setError("Invalid phone number");
            isValid = false;
        }

        // Partner Type validation
        if (partnerTypeSpinner.getText().toString().equals("Select Partner Type")) {
            partnerTypeSpinner.setError("Partner Type is required");
            isValid = false;
        }

        // Branch Location validation
        if (branchLocationSpinner.getText().toString().equals("Select Branch Location")) {
            branchLocationSpinner.setError("Branch Location is required");
            isValid = false;
        }

        // Residential Address validation
        if (residentialAddressInput.getText().toString().trim().isEmpty()) {
            residentialAddressInput.setError("Residential Address is required");
            isValid = false;
        }

        // Pan Number validation
        String panNumber = panNumberInput.getText().toString().trim();
        if (panNumber.isEmpty()) {
            panNumberInput.setError("Pan Number is required");
            isValid = false;
        } else if (panNumber.length() != 10) {
            panNumberInput.setError("Pan number must be 10 characters");
            isValid = false;
        }

        // IFSC Code validation
        String ifscCode = ifscCodeInput.getText().toString().trim();
        if (ifscCode.isEmpty()) {
            ifscCodeInput.setError("IFSC Code is required");
            isValid = false;
        } else if (ifscCode.length() != 11) {
            ifscCodeInput.setError("IFSC code must be 11 characters");
            isValid = false;
        }

        // Account Type validation
        if (accountTypeSpinner.getText().toString().equals("Select Account Type")) {
            accountTypeSpinner.setError("Account Type is required");
            isValid = false;
        }

        return isValid;
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadPartnerActivity.class);
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
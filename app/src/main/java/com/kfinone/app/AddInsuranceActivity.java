package com.kfinone.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddInsuranceActivity extends AppCompatActivity {

    private TextInputEditText customerNameInput, mobileNumberInput, emailInput, dobInput;
    private TextInputEditText addressInput, policyNumberInput, premiumAmountInput, coverageAmountInput;
    private TextInputEditText policyStartDateInput, policyEndDateInput;
    private AutoCompleteTextView insuranceTypeDropdown;
    private Button uploadIdProofButton, uploadAddressProofButton, uploadMedicalCertButton;
    private Button submitButton;
    private TextView backButton, saveButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_insurance);

        initializeViews();
        setupDatePickers();
        setupDropdowns();
        setupClickListeners();
        loadSampleData();
    }

    private void initializeViews() {
        // Input fields
        customerNameInput = findViewById(R.id.customerNameInput);
        mobileNumberInput = findViewById(R.id.mobileNumberInput);
        emailInput = findViewById(R.id.emailInput);
        dobInput = findViewById(R.id.dobInput);
        addressInput = findViewById(R.id.addressInput);
        policyNumberInput = findViewById(R.id.policyNumberInput);
        premiumAmountInput = findViewById(R.id.premiumAmountInput);
        coverageAmountInput = findViewById(R.id.coverageAmountInput);
        policyStartDateInput = findViewById(R.id.policyStartDateInput);
        policyEndDateInput = findViewById(R.id.policyEndDateInput);
        insuranceTypeDropdown = findViewById(R.id.insuranceTypeDropdown);

        // Buttons
        uploadIdProofButton = findViewById(R.id.uploadIdProofButton);
        uploadAddressProofButton = findViewById(R.id.uploadAddressProofButton);
        uploadMedicalCertButton = findViewById(R.id.uploadMedicalCertButton);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        // Initialize calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void setupDatePickers() {
        // Date of Birth picker
        dobInput.setOnClickListener(v -> showDatePicker(dobInput, "Select Date of Birth"));

        // Policy Start Date picker
        policyStartDateInput.setOnClickListener(v -> showDatePicker(policyStartDateInput, "Select Policy Start Date"));

        // Policy End Date picker
        policyEndDateInput.setOnClickListener(v -> showDatePicker(policyEndDateInput, "Select Policy End Date"));
    }

    private void showDatePicker(TextInputEditText inputField, String title) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                inputField.setText(dateFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    private void setupDropdowns() {
        // Insurance Type dropdown
        String[] insuranceTypes = {
            "Life Insurance",
            "Health Insurance",
            "Motor Insurance",
            "Home Insurance",
            "Travel Insurance",
            "Term Insurance",
            "Endowment Policy",
            "ULIP",
            "Child Plan",
            "Pension Plan"
        };

        ArrayAdapter<String> insuranceTypeAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            insuranceTypes
        );
        insuranceTypeDropdown.setAdapter(insuranceTypeAdapter);
    }

    private void setupClickListeners() {
        // Navigation
        backButton.setOnClickListener(v -> goBack());
        saveButton.setOnClickListener(v -> saveInsurance());

        // Document upload buttons
        uploadIdProofButton.setOnClickListener(v -> uploadDocument("ID Proof"));
        uploadAddressProofButton.setOnClickListener(v -> uploadDocument("Address Proof"));
        uploadMedicalCertButton.setOnClickListener(v -> uploadDocument("Medical Certificate"));

        // Submit button
        submitButton.setOnClickListener(v -> submitInsurance());
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("CBO_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, CBOInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, RBHInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default to InsurancePanelActivity (Special Panel)
            Intent intent = new Intent(this, InsurancePanelActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
        }
    }

    private void saveInsurance() {
        if (validateForm()) {
            // Save insurance data
            Toast.makeText(this, "Insurance saved successfully!", Toast.LENGTH_SHORT).show();
            goBack();
        }
    }

    private void uploadDocument(String documentType) {
        // TODO: Implement document upload functionality
        Toast.makeText(this, "Upload " + documentType + " - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void submitInsurance() {
        if (validateForm()) {
            // Submit insurance application
            Toast.makeText(this, "Insurance application submitted successfully!", Toast.LENGTH_LONG).show();
            goBack();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate customer name
        if (customerNameInput.getText().toString().trim().isEmpty()) {
            customerNameInput.setError("Customer name is required");
            isValid = false;
        }

        // Validate mobile number
        String mobile = mobileNumberInput.getText().toString().trim();
        if (mobile.isEmpty()) {
            mobileNumberInput.setError("Mobile number is required");
            isValid = false;
        } else if (mobile.length() != 10) {
            mobileNumberInput.setError("Mobile number must be 10 digits");
            isValid = false;
        }

        // Validate email
        String email = emailInput.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            isValid = false;
        }

        // Validate policy number
        if (policyNumberInput.getText().toString().trim().isEmpty()) {
            policyNumberInput.setError("Policy number is required");
            isValid = false;
        }

        // Validate premium amount
        String premium = premiumAmountInput.getText().toString().trim();
        if (premium.isEmpty()) {
            premiumAmountInput.setError("Premium amount is required");
            isValid = false;
        } else {
            try {
                double premiumValue = Double.parseDouble(premium);
                if (premiumValue <= 0) {
                    premiumAmountInput.setError("Premium amount must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                premiumAmountInput.setError("Invalid premium amount");
                isValid = false;
            }
        }

        // Validate coverage amount
        String coverage = coverageAmountInput.getText().toString().trim();
        if (coverage.isEmpty()) {
            coverageAmountInput.setError("Coverage amount is required");
            isValid = false;
        } else {
            try {
                double coverageValue = Double.parseDouble(coverage);
                if (coverageValue <= 0) {
                    coverageAmountInput.setError("Coverage amount must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                coverageAmountInput.setError("Invalid coverage amount");
                isValid = false;
            }
        }

        // Validate insurance type
        if (insuranceTypeDropdown.getText().toString().trim().isEmpty()) {
            insuranceTypeDropdown.setError("Insurance type is required");
            isValid = false;
        }

        return isValid;
    }

    private void loadSampleData() {
        // Only fetch real data here. No sample data.
    }
} 
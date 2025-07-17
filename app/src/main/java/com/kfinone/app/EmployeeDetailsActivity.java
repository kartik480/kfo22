package com.kfinone.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EmployeeDetailsActivity extends AppCompatActivity {
    private TextInputEditText firstNameInput, lastNameInput, employeeIdInput, passwordInput;
    private TextInputEditText phoneInput, emailInput, officialPhoneInput, officialEmailInput;
    private TextInputEditText branchStateInput, branchLocationInput, departmentInput, designationInput;
    private TextInputEditText presentAddressInput, permanentAddressInput, reportingToInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize views
        initializeViews();
        setupSubmitButton();
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        employeeIdInput = findViewById(R.id.employeeIdInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        officialPhoneInput = findViewById(R.id.officialPhoneInput);
        officialEmailInput = findViewById(R.id.officialEmailInput);
        branchStateInput = findViewById(R.id.branchStateInput);
        branchLocationInput = findViewById(R.id.branchLocationInput);
        departmentInput = findViewById(R.id.departmentInput);
        designationInput = findViewById(R.id.designationInput);
        presentAddressInput = findViewById(R.id.presentAddressInput);
        permanentAddressInput = findViewById(R.id.permanentAddressInput);
        reportingToInput = findViewById(R.id.reportingToInput);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                // TODO: Implement form submission logic
                Toast.makeText(this, "Form submitted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate required fields
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First Name is required");
            isValid = false;
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last Name is required");
            isValid = false;
        }
        if (employeeIdInput.getText().toString().trim().isEmpty()) {
            employeeIdInput.setError("Employee ID is required");
            isValid = false;
        }
        if (passwordInput.getText().toString().trim().isEmpty()) {
            passwordInput.setError("Password is required");
            isValid = false;
        }
        if (phoneInput.getText().toString().trim().isEmpty()) {
            phoneInput.setError("Phone Number is required");
            isValid = false;
        }
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        }
        if (officialPhoneInput.getText().toString().trim().isEmpty()) {
            officialPhoneInput.setError("Official Phone Number is required");
            isValid = false;
        }
        if (officialEmailInput.getText().toString().trim().isEmpty()) {
            officialEmailInput.setError("Official Email is required");
            isValid = false;
        }
        if (branchStateInput.getText().toString().trim().isEmpty()) {
            branchStateInput.setError("Branch State is required");
            isValid = false;
        }
        if (branchLocationInput.getText().toString().trim().isEmpty()) {
            branchLocationInput.setError("Branch Location is required");
            isValid = false;
        }
        if (departmentInput.getText().toString().trim().isEmpty()) {
            departmentInput.setError("Department is required");
            isValid = false;
        }
        if (designationInput.getText().toString().trim().isEmpty()) {
            designationInput.setError("Designation is required");
            isValid = false;
        }
        if (presentAddressInput.getText().toString().trim().isEmpty()) {
            presentAddressInput.setError("Present Address is required");
            isValid = false;
        }
        if (permanentAddressInput.getText().toString().trim().isEmpty()) {
            permanentAddressInput.setError("Permanent Address is required");
            isValid = false;
        }
        if (reportingToInput.getText().toString().trim().isEmpty()) {
            reportingToInput.setError("Reporting To is required");
            isValid = false;
        }

        return isValid;
    }
} 

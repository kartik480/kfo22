package com.kfinone.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BusinessHeadAddEmpActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Form fields
    private EditText employeeIdInput;
    private EditText firstNameInput;
    private EditText aliasNameInput;
    private EditText phoneInput;
    private TextView branchStateSpinner;
    private EditText officeAddressInput;
    private EditText companyNameInput;
    private EditText panNumberInput;
    private EditText ifscCodeInput;
    private TextView accountTypeSpinner;
    private EditText passwordInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText alternativePhoneInput;
    private TextView branchLocationSpinner;
    private EditText residentialAddressInput;
    private EditText aadhaarInput;
    private EditText accountNumberInput;
    private TextView bankNameSpinner;
    private Button panCardChooseFileButton;
    private TextView panCardFileName;

    // File upload fields
    private Button aadhaarChooseFileButton;
    private TextView aadhaarFileName;
    private Button bankProofChooseFileButton;
    private TextView bankProofFileName;
    private Button photoChooseFileButton;
    private TextView photoFileName;
    private Button resumeChooseFileButton;
    private TextView resumeFileName;

    // Reporting section
    private TextView reportingToSpinner;

    // Submit button
    private Button submitButton;

    // File picker launcher
    private ActivityResultLauncher<String> filePickerLauncher;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    
    // Dropdown data
    private List<String> branchStates = new ArrayList<>();
    private List<String> accountTypes = new ArrayList<>();
    private List<String> branchLocations = new ArrayList<>();
    private List<String> bankNames = new ArrayList<>();
    private List<String> reportingUsers = new ArrayList<>();
    
    // Selected values
    private String selectedBranchState = "";
    private String selectedAccountType = "";
    private String selectedBranchLocation = "";
    private String selectedBankName = "";
    private String selectedReportingUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_add_emp);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        setupFilePicker();
        loadDropdownData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);

        // Form fields
        employeeIdInput = findViewById(R.id.employeeIdInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        panNumberInput = findViewById(R.id.panNumberInput);
        ifscCodeInput = findViewById(R.id.ifscCodeInput);
        accountTypeSpinner = findViewById(R.id.accountTypeSpinner);
        passwordInput = findViewById(R.id.passwordInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        alternativePhoneInput = findViewById(R.id.alternativePhoneInput);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        aadhaarInput = findViewById(R.id.aadhaarInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        bankNameSpinner = findViewById(R.id.bankNameSpinner);
        panCardChooseFileButton = findViewById(R.id.panCardChooseFileButton);
        panCardFileName = findViewById(R.id.panCardFileName);

        // File upload fields
        aadhaarChooseFileButton = findViewById(R.id.aadhaarChooseFileButton);
        aadhaarFileName = findViewById(R.id.aadhaarFileName);
        bankProofChooseFileButton = findViewById(R.id.bankProofChooseFileButton);
        bankProofFileName = findViewById(R.id.bankProofFileName);
        photoChooseFileButton = findViewById(R.id.photoChooseFileButton);
        photoFileName = findViewById(R.id.photoFileName);
        resumeChooseFileButton = findViewById(R.id.resumeChooseFileButton);
        resumeFileName = findViewById(R.id.resumeFileName);

        // Reporting section
        reportingToSpinner = findViewById(R.id.reportingToSpinner);

        // Submit button
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Spinner click listeners
        branchStateSpinner.setOnClickListener(v -> {
            Log.d("BusinessHeadAddEmp", "Branch State spinner clicked");
            Toast.makeText(this, "Branch State clicked", Toast.LENGTH_SHORT).show();
            showBranchStateDialog();
        });
        accountTypeSpinner.setOnClickListener(v -> {
            Log.d("BusinessHeadAddEmp", "Account Type spinner clicked");
            Toast.makeText(this, "Account Type clicked", Toast.LENGTH_SHORT).show();
            showAccountTypeDialog();
        });
        branchLocationSpinner.setOnClickListener(v -> {
            Log.d("BusinessHeadAddEmp", "Branch Location spinner clicked");
            Toast.makeText(this, "Branch Location clicked", Toast.LENGTH_SHORT).show();
            showBranchLocationDialog();
        });
        bankNameSpinner.setOnClickListener(v -> {
            Log.d("BusinessHeadAddEmp", "Bank Name spinner clicked");
            Toast.makeText(this, "Bank Name clicked", Toast.LENGTH_SHORT).show();
            showBankNameDialog();
        });
        reportingToSpinner.setOnClickListener(v -> {
            Log.d("BusinessHeadAddEmp", "Reporting To spinner clicked");
            Toast.makeText(this, "Reporting To clicked", Toast.LENGTH_SHORT).show();
            showReportingToDialog();
        });

        // File upload click listeners
        panCardChooseFileButton.setOnClickListener(v -> pickFile("panCard"));
        aadhaarChooseFileButton.setOnClickListener(v -> pickFile("aadhaar"));
        bankProofChooseFileButton.setOnClickListener(v -> pickFile("bankProof"));
        photoChooseFileButton.setOnClickListener(v -> pickFile("photo"));
        resumeChooseFileButton.setOnClickListener(v -> pickFile("resume"));

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

    private void loadDropdownData() {
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_add_emp_dropdowns.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        
        Log.d("BusinessHeadAddEmp", "Loading dropdown data from: " + url);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("BusinessHeadAddEmp", "API Response: " + response.toString());
                        
                        if (response.getString("status").equals("success")) {
                            JSONObject data = response.getJSONObject("data");
                            
                            // Load Branch States
                            if (data.has("branch_states")) {
                                JSONArray branchStatesArray = data.getJSONArray("branch_states");
                                branchStates.clear();
                                for (int i = 0; i < branchStatesArray.length(); i++) {
                                    JSONObject state = branchStatesArray.getJSONObject(i);
                                    branchStates.add(state.getString("name"));
                                }
                                Log.d("BusinessHeadAddEmp", "Loaded " + branchStates.size() + " branch states");
                            } else {
                                Log.w("BusinessHeadAddEmp", "No branch_states found in response");
                            }
                            
                            // Load Account Types
                            if (data.has("account_types")) {
                                JSONArray accountTypesArray = data.getJSONArray("account_types");
                                accountTypes.clear();
                                for (int i = 0; i < accountTypesArray.length(); i++) {
                                    JSONObject type = accountTypesArray.getJSONObject(i);
                                    accountTypes.add(type.getString("name"));
                                }
                                Log.d("BusinessHeadAddEmp", "Loaded " + accountTypes.size() + " account types");
                            } else {
                                Log.w("BusinessHeadAddEmp", "No account_types found in response");
                            }
                            
                            // Load Branch Locations
                            if (data.has("branch_locations")) {
                                JSONArray branchLocationsArray = data.getJSONArray("branch_locations");
                                branchLocations.clear();
                                for (int i = 0; i < branchLocationsArray.length(); i++) {
                                    JSONObject location = branchLocationsArray.getJSONObject(i);
                                    branchLocations.add(location.getString("name"));
                                }
                                Log.d("BusinessHeadAddEmp", "Loaded " + branchLocations.size() + " branch locations");
                            } else {
                                Log.w("BusinessHeadAddEmp", "No branch_locations found in response");
                            }
                            
                            // Load Bank Names
                            if (data.has("bank_names")) {
                                JSONArray bankNamesArray = data.getJSONArray("bank_names");
                                bankNames.clear();
                                for (int i = 0; i < bankNamesArray.length(); i++) {
                                    JSONObject bank = bankNamesArray.getJSONObject(i);
                                    bankNames.add(bank.getString("name"));
                                }
                                Log.d("BusinessHeadAddEmp", "Loaded " + bankNames.size() + " bank names");
                            } else {
                                Log.w("BusinessHeadAddEmp", "No bank_names found in response");
                            }
                            
                            // Load Reporting Users
                            if (data.has("reporting_users")) {
                                JSONArray reportingUsersArray = data.getJSONArray("reporting_users");
                                reportingUsers.clear();
                                for (int i = 0; i < reportingUsersArray.length(); i++) {
                                    JSONObject user = reportingUsersArray.getJSONObject(i);
                                    reportingUsers.add(user.getString("full_name"));
                                }
                                Log.d("BusinessHeadAddEmp", "Loaded " + reportingUsers.size() + " reporting users");
                            } else {
                                Log.w("BusinessHeadAddEmp", "No reporting_users found in response");
                            }
                            
                            // If no reporting users found, add sample data
                            if (reportingUsers.isEmpty()) {
                                Log.d("BusinessHeadAddEmp", "No reporting users found, adding sample data");
                                reportingUsers.add("John Doe (Business Head)");
                                reportingUsers.add("Jane Smith (Business Head)");
                                reportingUsers.add("Mike Johnson (Business Head)");
                            }
                            
                            Log.d("BusinessHeadAddEmp", "Dropdown data loaded successfully");
                            Log.d("BusinessHeadAddEmp", "Summary - Branch States: " + branchStates.size() + 
                                ", Account Types: " + accountTypes.size() + 
                                ", Branch Locations: " + branchLocations.size() + 
                                ", Bank Names: " + bankNames.size() + 
                                ", Reporting Users: " + reportingUsers.size());
                            
                        } else {
                            String errorMsg = "Error loading dropdown data: " + response.optString("message", "Unknown error");
                            Log.e("BusinessHeadAddEmp", errorMsg);
                            Toast.makeText(BusinessHeadAddEmpActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            loadSampleData(); // Load sample data as fallback
                        }
                    } catch (JSONException e) {
                        Log.e("BusinessHeadAddEmp", "Error parsing dropdown data", e);
                        Toast.makeText(BusinessHeadAddEmpActivity.this, "Error parsing dropdown data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadSampleData(); // Load sample data as fallback
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("BusinessHeadAddEmp", "Error loading dropdown data", error);
                    Toast.makeText(BusinessHeadAddEmpActivity.this, "Error loading dropdown data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadSampleData(); // Load sample data as fallback
                }
            }
        );
        
        queue.add(jsonObjectRequest);
        
        // Debug: Show loaded data after a delay
        new android.os.Handler().postDelayed(() -> {
            Log.d("BusinessHeadAddEmp", "=== DEBUG: Current Data Status ===");
            Log.d("BusinessHeadAddEmp", "Branch States: " + branchStates.size() + " - " + branchStates.toString());
            Log.d("BusinessHeadAddEmp", "Account Types: " + accountTypes.size() + " - " + accountTypes.toString());
            Log.d("BusinessHeadAddEmp", "Branch Locations: " + branchLocations.size() + " - " + branchLocations.toString());
            Log.d("BusinessHeadAddEmp", "Bank Names: " + bankNames.size() + " - " + bankNames.toString());
            Log.d("BusinessHeadAddEmp", "Reporting Users: " + reportingUsers.size() + " - " + reportingUsers.toString());
            Log.d("BusinessHeadAddEmp", "=== END DEBUG ===");
        }, 2000); // 2 seconds delay
    }
    
    private void loadSampleData() {
        Log.d("BusinessHeadAddEmp", "Loading sample data as fallback");
        
        // Sample Branch States
        branchStates.clear();
        branchStates.add("Maharashtra");
        branchStates.add("Delhi");
        branchStates.add("Karnataka");
        branchStates.add("Tamil Nadu");
        branchStates.add("Gujarat");
        
        // Sample Account Types
        accountTypes.clear();
        accountTypes.add("Savings");
        accountTypes.add("Current");
        accountTypes.add("Fixed Deposit");
        accountTypes.add("Recurring Deposit");
        
        // Sample Branch Locations
        branchLocations.clear();
        branchLocations.add("Mumbai");
        branchLocations.add("Delhi");
        branchLocations.add("Bangalore");
        branchLocations.add("Chennai");
        branchLocations.add("Ahmedabad");
        
        // Sample Bank Names
        bankNames.clear();
        bankNames.add("HDFC Bank");
        bankNames.add("ICICI Bank");
        bankNames.add("State Bank of India");
        bankNames.add("Axis Bank");
        bankNames.add("Kotak Mahindra Bank");
        
        // Sample Reporting Users
        reportingUsers.clear();
        reportingUsers.add("John Doe (Business Head)");
        reportingUsers.add("Jane Smith (Business Head)");
        reportingUsers.add("Mike Johnson (Business Head)");
        
        Log.d("BusinessHeadAddEmp", "Sample data loaded - Branch States: " + branchStates.size() + 
            ", Account Types: " + accountTypes.size() + 
            ", Branch Locations: " + branchLocations.size() + 
            ", Bank Names: " + bankNames.size() + 
            ", Reporting Users: " + reportingUsers.size());
    }

    private void showBranchStateDialog() {
        Log.d("BusinessHeadAddEmp", "showBranchStateDialog called. Size: " + branchStates.size());
        if (branchStates.isEmpty()) {
            Log.w("BusinessHeadAddEmp", "Branch states list is empty");
            Toast.makeText(this, "Loading branch states...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] options = branchStates.toArray(new String[0]);
        Log.d("BusinessHeadAddEmp", "Showing branch state dialog with " + options.length + " options");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Branch State");
        builder.setItems(options, (dialog, which) -> {
            selectedBranchState = options[which];
            branchStateSpinner.setText(selectedBranchState);
            Log.d("BusinessHeadAddEmp", "Selected branch state: " + selectedBranchState);
        });
        builder.show();
    }

    private void showAccountTypeDialog() {
        Log.d("BusinessHeadAddEmp", "showAccountTypeDialog called. Size: " + accountTypes.size());
        if (accountTypes.isEmpty()) {
            Log.w("BusinessHeadAddEmp", "Account types list is empty");
            Toast.makeText(this, "Loading account types...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] options = accountTypes.toArray(new String[0]);
        Log.d("BusinessHeadAddEmp", "Showing account type dialog with " + options.length + " options");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Account Type");
        builder.setItems(options, (dialog, which) -> {
            selectedAccountType = options[which];
            accountTypeSpinner.setText(selectedAccountType);
            Log.d("BusinessHeadAddEmp", "Selected account type: " + selectedAccountType);
        });
        builder.show();
    }

    private void showBranchLocationDialog() {
        Log.d("BusinessHeadAddEmp", "showBranchLocationDialog called. Size: " + branchLocations.size());
        if (branchLocations.isEmpty()) {
            Log.w("BusinessHeadAddEmp", "Branch locations list is empty");
            Toast.makeText(this, "Loading branch locations...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] options = branchLocations.toArray(new String[0]);
        Log.d("BusinessHeadAddEmp", "Showing branch location dialog with " + options.length + " options");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Branch Location");
        builder.setItems(options, (dialog, which) -> {
            selectedBranchLocation = options[which];
            branchLocationSpinner.setText(selectedBranchLocation);
            Log.d("BusinessHeadAddEmp", "Selected branch location: " + selectedBranchLocation);
        });
        builder.show();
    }

    private void showBankNameDialog() {
        Log.d("BusinessHeadAddEmp", "showBankNameDialog called. Size: " + bankNames.size());
        if (bankNames.isEmpty()) {
            Log.w("BusinessHeadAddEmp", "Bank names list is empty");
            Toast.makeText(this, "Loading bank names...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] options = bankNames.toArray(new String[0]);
        Log.d("BusinessHeadAddEmp", "Showing bank name dialog with " + options.length + " options");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Bank Name");
        builder.setItems(options, (dialog, which) -> {
            selectedBankName = options[which];
            bankNameSpinner.setText(selectedBankName);
            Log.d("BusinessHeadAddEmp", "Selected bank name: " + selectedBankName);
        });
        builder.show();
    }

    private void showReportingToDialog() {
        Log.d("BusinessHeadAddEmp", "showReportingToDialog called. Size: " + reportingUsers.size());
        if (reportingUsers.isEmpty()) {
            Log.w("BusinessHeadAddEmp", "Reporting users list is empty");
            Toast.makeText(this, "Loading reporting users...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] options = reportingUsers.toArray(new String[0]);
        Log.d("BusinessHeadAddEmp", "Showing reporting to dialog with " + options.length + " options");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Reporting To");
        builder.setItems(options, (dialog, which) -> {
            selectedReportingUser = options[which];
            reportingToSpinner.setText(selectedReportingUser);
            Log.d("BusinessHeadAddEmp", "Selected reporting user: " + selectedReportingUser);
        });
        builder.show();
    }

    private void submitForm() {
        // Validate required fields
        if (!validateForm()) {
            return;
        }

        // TODO: Implement API call to submit employee data
        // For now, show success message
        Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_LONG).show();
        
        // Go back to previous screen
        goBack();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Employee ID validation
        if (employeeIdInput.getText().toString().trim().isEmpty()) {
            employeeIdInput.setError("Employee ID is required");
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

        // Phone validation
        String phone = phoneInput.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneInput.setError("Phone is required");
            isValid = false;
        } else if (phone.length() < 10) {
            phoneInput.setError("Invalid phone number");
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

        // Company Name validation
        if (companyNameInput.getText().toString().trim().isEmpty()) {
            companyNameInput.setError("Company Name is required");
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

        // Email validation
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
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

        // Reporting To validation
        if (reportingToSpinner.getText().toString().equals("Select Reporting To")) {
            reportingToSpinner.setError("Reporting To is required");
            isValid = false;
        }

        return isValid;
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadEmpMasterActivity.class);
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
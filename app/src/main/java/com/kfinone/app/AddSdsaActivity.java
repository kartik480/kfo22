package com.kfinone.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AddSdsaActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput, firstNameInput, lastNameInput, aliasNameInput,
            phoneInput, altPhoneInput, companyNameInput, officeAddressInput, residentialAddressInput,
            aadharInput, panInput, accountNumberInput, ifscInput;
    private AutoCompleteTextView branchStateDropdown, branchLocationDropdown, bankNameDropdown,
            accountTypeDropdown, reportingToDropdown;
    private MaterialButton panUploadButton, aadharUploadButton, photoUploadButton,
            bankProofUploadButton, companyLogoUploadButton, submitButton;
    private ImageButton backButton;

    private ActivityResultLauncher<Intent> documentPickerLauncher;
    private Uri selectedDocumentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sdsa);

        initializeViews();
        setupDropdowns();
        setupDocumentPicker();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize all input fields
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        altPhoneInput = findViewById(R.id.altPhoneInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        aadharInput = findViewById(R.id.aadharInput);
        panInput = findViewById(R.id.panInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        ifscInput = findViewById(R.id.ifscInput);

        // Initialize dropdowns
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        branchLocationDropdown = findViewById(R.id.branchLocationDropdown);
        bankNameDropdown = findViewById(R.id.bankNameDropdown);
        accountTypeDropdown = findViewById(R.id.accountTypeDropdown);
        reportingToDropdown = findViewById(R.id.reportingToDropdown);

        // Initialize buttons
        panUploadButton = findViewById(R.id.panUploadButton);
        aadharUploadButton = findViewById(R.id.aadharUploadButton);
        photoUploadButton = findViewById(R.id.photoUploadButton);
        bankProofUploadButton = findViewById(R.id.bankProofUploadButton);
        companyLogoUploadButton = findViewById(R.id.companyLogoUploadButton);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupDropdowns() {
        // Load all dropdown options from database
        loadBranchStates();
        loadBranchLocations();
        loadBankNames();
        loadAccountTypes();
        loadReportingToList();
    }

    private void loadBranchStates() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_sdsa_branch_states.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> branchStates = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            branchStates.add(dataArray.getJSONObject(i).getString("name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, branchStates);
                            branchStateDropdown.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadBranchLocations() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_sdsa_branch_locations.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> branchLocations = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            branchLocations.add(dataArray.getJSONObject(i).getString("name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, branchLocations);
                            branchLocationDropdown.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadBankNames() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_sdsa_bank_names.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> bankNames = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            bankNames.add(dataArray.getJSONObject(i).getString("name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bankNames);
                            bankNameDropdown.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadAccountTypes() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_sdsa_account_types.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> accountTypes = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            accountTypes.add(dataArray.getJSONObject(i).getString("name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accountTypes);
                            accountTypeDropdown.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadReportingToList() {
        // Show loading indicator
        runOnUiThread(() -> {
            reportingToDropdown.setText("Loading...");
            reportingToDropdown.setEnabled(false);
        });

        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_reporting_to_dropdown.php";
                android.util.Log.d("AddSdsaActivity", "🔍 Starting API call to: " + urlString);
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000); // Increased timeout
                connection.setReadTimeout(10000);
                
                android.util.Log.d("AddSdsaActivity", "🔍 Making HTTP request...");
                int responseCode = connection.getResponseCode();
                android.util.Log.d("AddSdsaActivity", "📡 HTTP Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    android.util.Log.d("AddSdsaActivity", "📄 Raw API Response: " + response.toString());
                    
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    android.util.Log.d("AddSdsaActivity", "🔍 JSON Status: " + jsonResponse.optString("status", "NO_STATUS"));
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        android.util.Log.d("AddSdsaActivity", "📊 Data array length: " + dataArray.length());
                        
                        java.util.List<String> reportingToList = new java.util.ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            org.json.JSONObject user = dataArray.getJSONObject(i);
                            String fullName = user.getString("name");
                            android.util.Log.d("AddSdsaActivity", "👤 User " + i + ": " + fullName);
                            if (fullName != null && !fullName.trim().isEmpty()) {
                                reportingToList.add(fullName.trim());
                            }
                        }
                        
                        android.util.Log.d("AddSdsaActivity", "📋 Final reporting list size: " + reportingToList.size());
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                reportingToList
                            );
                            reportingToDropdown.setAdapter(adapter);
                            reportingToDropdown.setText("", false);
                            reportingToDropdown.setEnabled(true);
                            
                            android.util.Log.d("AddSdsaActivity", "Reporting to dropdown adapter set with " + reportingToList.size() + " items");
                            
                            // Show success message if users were loaded
                            if (reportingToList.size() > 0) {
                                Toast.makeText(this, "Loaded " + reportingToList.size() + " reporting users", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No active users found for reporting", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        String errorMessage = jsonResponse.optString("message", "Unknown error");
                        android.util.Log.e("AddSdsaActivity", "❌ API Error: " + errorMessage);
                        android.util.Log.e("AddSdsaActivity", "❌ Full JSON Response: " + jsonResponse.toString());
                        
                        runOnUiThread(() -> {
                            reportingToDropdown.setText("Error loading users");
                            reportingToDropdown.setEnabled(false);
                            Toast.makeText(this, "Failed to load reporting users: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    android.util.Log.e("AddSdsaActivity", "❌ HTTP Error: " + responseCode);
                    
                    // Try to read error stream for more details
                    try {
                        java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        errorReader.close();
                        android.util.Log.e("AddSdsaActivity", "❌ Error Response: " + errorResponse.toString());
                    } catch (Exception e) {
                        android.util.Log.e("AddSdsaActivity", "❌ Could not read error stream: " + e.getMessage());
                    }
                    
                    runOnUiThread(() -> {
                        reportingToDropdown.setText("Network error");
                        reportingToDropdown.setEnabled(false);
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                android.util.Log.e("AddSdsaActivity", "❌ Exception during API call", e);
                android.util.Log.e("AddSdsaActivity", "❌ Exception message: " + e.getMessage());
                android.util.Log.e("AddSdsaActivity", "❌ Exception type: " + e.getClass().getSimpleName());
                e.printStackTrace();
                
                runOnUiThread(() -> {
                    reportingToDropdown.setText("Error loading users");
                    reportingToDropdown.setEnabled(false);
                    Toast.makeText(this, "Failed to load reporting users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void setupDocumentPicker() {
        documentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedDocumentUri = data.getData();
                        // Handle the selected document
                        Toast.makeText(this, "Document selected successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Document upload buttons
        panUploadButton.setOnClickListener(v -> openDocumentPicker("PAN Card"));
        aadharUploadButton.setOnClickListener(v -> openDocumentPicker("Aadhar Card"));
        photoUploadButton.setOnClickListener(v -> openDocumentPicker("Photo"));
        bankProofUploadButton.setOnClickListener(v -> openDocumentPicker("Bank Proof"));
        companyLogoUploadButton.setOnClickListener(v -> openDocumentPicker("Company Logo"));

        // Submit button
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitSdsaForm();
            }
        });
    }

    private void openDocumentPicker(String documentType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        // Validate all required fields
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (passwordInput.getText().toString().trim().isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }
        if (phoneInput.getText().toString().trim().isEmpty()) {
            phoneInput.setError("Phone number is required");
            return false;
        }
        if (companyNameInput.getText().toString().trim().isEmpty()) {
            companyNameInput.setError("Company name is required");
            return false;
        }
        if (branchStateDropdown.getText().toString().trim().isEmpty()) {
            branchStateDropdown.setError("Branch state is required");
            return false;
        }
        if (branchLocationDropdown.getText().toString().trim().isEmpty()) {
            branchLocationDropdown.setError("Branch location is required");
            return false;
        }
        if (aadharInput.getText().toString().trim().isEmpty()) {
            aadharInput.setError("Aadhar number is required");
            return false;
        }
        if (panInput.getText().toString().trim().isEmpty()) {
            panInput.setError("PAN number is required");
            return false;
        }
        if (accountNumberInput.getText().toString().trim().isEmpty()) {
            accountNumberInput.setError("Account number is required");
            return false;
        }
        if (ifscInput.getText().toString().trim().isEmpty()) {
            ifscInput.setError("IFSC code is required");
            return false;
        }
        if (bankNameDropdown.getText().toString().trim().isEmpty()) {
            bankNameDropdown.setError("Bank name is required");
            return false;
        }
        if (accountTypeDropdown.getText().toString().trim().isEmpty()) {
            accountTypeDropdown.setError("Account type is required");
            return false;
        }
        if (reportingToDropdown.getText().toString().trim().isEmpty()) {
            reportingToDropdown.setError("Please select a reporting user");
            return false;
        }

        return true;
    }

    private void submitSdsaForm() {
        // Disable submit button to prevent multiple submissions
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        new Thread(() -> {
            try {
                // Collect all form data
                String aliasName = aliasNameInput.getText().toString().trim();
                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String phoneNumber = phoneInput.getText().toString().trim();
                String emailId = emailInput.getText().toString().trim();
                String altPhoneNumber = altPhoneInput.getText().toString().trim();
                String companyName = companyNameInput.getText().toString().trim();
                String officeAddress = officeAddressInput.getText().toString().trim();
                String residentialAddress = residentialAddressInput.getText().toString().trim();
                String aadhaarNumber = aadharInput.getText().toString().trim();
                String panNumber = panInput.getText().toString().trim();
                String accountNumber = accountNumberInput.getText().toString().trim();
                String ifscCode = ifscInput.getText().toString().trim();
                
                // Get dropdown values
                String branchState = branchStateDropdown.getText().toString().trim();
                String branchLocation = branchLocationDropdown.getText().toString().trim();
                String bankName = bankNameDropdown.getText().toString().trim();
                String accountType = accountTypeDropdown.getText().toString().trim();
                String reportingTo = reportingToDropdown.getText().toString().trim();

                // Convert uploaded files to base64 (placeholder for now)
                String panImg = ""; // TODO: Convert selected PAN image to base64
                String aadhaarImg = ""; // TODO: Convert selected Aadhaar image to base64
                String photoImg = ""; // TODO: Convert selected photo to base64
                String bankproofImg = ""; // TODO: Convert selected bank proof to base64
                String companyLogo = ""; // TODO: Convert selected company logo to base64

                // Prepare JSON data
                org.json.JSONObject jsonData = new org.json.JSONObject();
                jsonData.put("alias_name", aliasName);
                jsonData.put("first_name", firstName);
                jsonData.put("last_name", lastName);
                jsonData.put("password", password);
                jsonData.put("phone_number", phoneNumber);
                jsonData.put("email_id", emailId);
                jsonData.put("alternative_mobile_number", altPhoneNumber);
                jsonData.put("company_name", companyName);
                jsonData.put("branch_state_name_id", ""); // TODO: Get ID from dropdown
                jsonData.put("branch_location_id", ""); // TODO: Get ID from dropdown
                jsonData.put("bank_id", ""); // TODO: Get ID from dropdown
                jsonData.put("account_type_id", ""); // TODO: Get ID from dropdown
                jsonData.put("office_address", officeAddress);
                jsonData.put("residential_address", residentialAddress);
                jsonData.put("aadhaar_number", aadhaarNumber);
                jsonData.put("pan_number", panNumber);
                jsonData.put("account_number", accountNumber);
                jsonData.put("ifsc_code", ifscCode);
                jsonData.put("rank", "");
                jsonData.put("status", "Active");
                jsonData.put("reportingTo", reportingTo);
                jsonData.put("employee_no", "");
                jsonData.put("department", "");
                jsonData.put("designation", "");
                jsonData.put("branchstate", branchState);
                jsonData.put("branchloaction", branchLocation);
                jsonData.put("bank_name", bankName);
                jsonData.put("account_type", accountType);
                jsonData.put("pan_img", panImg);
                jsonData.put("aadhaar_img", aadhaarImg);
                jsonData.put("photo_img", photoImg);
                jsonData.put("bankproof_img", bankproofImg);
                jsonData.put("company_logo", companyLogo);

                // Create request body
                String jsonString = jsonData.toString();
                byte[] postData = jsonString.getBytes("UTF-8");

                String urlString = "https://emp.kfinone.com/mobile/api/add_sdsa_user.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Send data
                java.io.OutputStream os = connection.getOutputStream();
                os.write(postData);
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddSdsaActivity.this, "SDSA user added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        String error = jsonResponse.optString("error", "Unknown error occurred");
                        runOnUiThread(() -> {
                            Toast.makeText(AddSdsaActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AddSdsaActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(AddSdsaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                });
            }
        }).start();
    }
} 

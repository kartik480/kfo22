package com.kfinone.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddPartnerActivity extends AppCompatActivity {

    private static final String TAG = "AddPartnerActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_FILE_REQUEST_CODE = 200;

    // Form fields
    private EditText emailInput, passwordInput, firstNameInput, lastNameInput, aliasNameInput;
    private EditText phoneNoInput, alternativePhoneInput, officeAddressInput, residentialAddressInput;
    private EditText aadhaarNumberInput, panNumberInput, accountNumberInput, ifscCodeInput;
    
    // Spinners
    private Spinner partnerTypeSpinner, branchStateSpinner, branchLocationSpinner;
    private Spinner bankNameSpinner, accountTypeSpinner;
    
    // File upload buttons and file names
    private Button panCardUploadButton, aadhaarCardUploadButton, photoUploadButton, bankProofUploadButton;
    private TextView panCardFileName, aadhaarCardFileName, photoFileName, bankProofFileName;
    
    // Action buttons
    private Button submitButton;
    
    // File URIs
    private Uri panCardUri, aadhaarCardUri, photoUri, bankProofUri;
    
    // Data for spinners
    private List<SpinnerItem> partnerTypes = new ArrayList<>();
    private List<SpinnerItem> branchStates = new ArrayList<>();
    private List<SpinnerItem> branchLocations = new ArrayList<>();
    private List<SpinnerItem> banks = new ArrayList<>();
    private List<SpinnerItem> accountTypes = new ArrayList<>();
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Partner");
        }

        initializeViews();
        setupClickListeners();
        loadDropdownData();
    }

    private void initializeViews() {
        // Initialize EditText fields
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        phoneNoInput = findViewById(R.id.phoneNoInput);
        alternativePhoneInput = findViewById(R.id.alternativePhoneInput);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        aadhaarNumberInput = findViewById(R.id.aadhaarNumberInput);
        panNumberInput = findViewById(R.id.panNumberInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        ifscCodeInput = findViewById(R.id.ifscCodeInput);

        // Initialize Spinners
        partnerTypeSpinner = findViewById(R.id.partnerTypeSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        bankNameSpinner = findViewById(R.id.bankNameSpinner);
        accountTypeSpinner = findViewById(R.id.accountTypeSpinner);

        // Initialize file upload buttons
        panCardUploadButton = findViewById(R.id.panCardUploadButton);
        aadhaarCardUploadButton = findViewById(R.id.aadhaarCardUploadButton);
        photoUploadButton = findViewById(R.id.photoUploadButton);
        bankProofUploadButton = findViewById(R.id.bankProofUploadButton);

        // Initialize file name text views
        panCardFileName = findViewById(R.id.panCardFileName);
        aadhaarCardFileName = findViewById(R.id.aadhaarCardFileName);
        photoFileName = findViewById(R.id.photoFileName);
        bankProofFileName = findViewById(R.id.bankProofFileName);

        // Initialize action buttons
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupClickListeners() {
        // File upload buttons
        panCardUploadButton.setOnClickListener(v -> selectFile("panCard"));
        aadhaarCardUploadButton.setOnClickListener(v -> selectFile("aadhaarCard"));
        photoUploadButton.setOnClickListener(v -> selectFile("photo"));
        bankProofUploadButton.setOnClickListener(v -> selectFile("bankProof"));

        // Action buttons
        submitButton.setOnClickListener(v -> submitPartnerData());
    }

    private void loadDropdownData() {
        loadPartnerTypes();
        loadBranchStates();
        loadBranchLocations();
        loadBanks();
        loadAccountTypes();
        
        // Add fallback data in case API fails
        addFallbackData();
    }
    
    private void addFallbackData() {
        // Add fallback partner types
        if (partnerTypes.size() <= 1) {
            partnerTypes.clear();
            partnerTypes.add(new SpinnerItem("0", "Select Partner Type"));
            partnerTypes.add(new SpinnerItem("1", "Individual"));
            partnerTypes.add(new SpinnerItem("2", "Company"));
            partnerTypes.add(new SpinnerItem("3", "Partnership"));
            partnerTypes.add(new SpinnerItem("4", "LLP"));
            partnerTypes.add(new SpinnerItem("5", "Proprietorship"));
            
            runOnUiThread(() -> {
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                    AddPartnerActivity.this,
                    android.R.layout.simple_spinner_item,
                    partnerTypes
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                partnerTypeSpinner.setAdapter(adapter);
            });
        }
        
        // Add fallback branch states
        if (branchStates.size() <= 1) {
            branchStates.clear();
            branchStates.add(new SpinnerItem("0", "Select Branch State"));
            branchStates.add(new SpinnerItem("1", "Maharashtra"));
            branchStates.add(new SpinnerItem("2", "Delhi"));
            branchStates.add(new SpinnerItem("3", "Karnataka"));
            branchStates.add(new SpinnerItem("4", "Tamil Nadu"));
            branchStates.add(new SpinnerItem("5", "Gujarat"));
            
            runOnUiThread(() -> {
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                    AddPartnerActivity.this,
                    android.R.layout.simple_spinner_item,
                    branchStates
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchStateSpinner.setAdapter(adapter);
            });
        }
        
        // Add fallback branch locations
        if (branchLocations.size() <= 1) {
            branchLocations.clear();
            branchLocations.add(new SpinnerItem("0", "Select Branch Location"));
            branchLocations.add(new SpinnerItem("1", "Mumbai"));
            branchLocations.add(new SpinnerItem("2", "Delhi"));
            branchLocations.add(new SpinnerItem("3", "Bangalore"));
            branchLocations.add(new SpinnerItem("4", "Chennai"));
            branchLocations.add(new SpinnerItem("5", "Ahmedabad"));
            
            runOnUiThread(() -> {
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                    AddPartnerActivity.this,
                    android.R.layout.simple_spinner_item,
                    branchLocations
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchLocationSpinner.setAdapter(adapter);
            });
        }
        
        // Add fallback banks
        if (banks.size() <= 1) {
            banks.clear();
            banks.add(new SpinnerItem("0", "Select Bank"));
            banks.add(new SpinnerItem("1", "State Bank of India"));
            banks.add(new SpinnerItem("2", "HDFC Bank"));
            banks.add(new SpinnerItem("3", "ICICI Bank"));
            banks.add(new SpinnerItem("4", "Axis Bank"));
            banks.add(new SpinnerItem("5", "Punjab National Bank"));
            
            runOnUiThread(() -> {
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                    AddPartnerActivity.this,
                    android.R.layout.simple_spinner_item,
                    banks
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bankNameSpinner.setAdapter(adapter);
            });
        }
        
        // Add fallback account types
        if (accountTypes.size() <= 1) {
            accountTypes.clear();
            accountTypes.add(new SpinnerItem("0", "Select Account Type"));
            accountTypes.add(new SpinnerItem("1", "Savings Account"));
            accountTypes.add(new SpinnerItem("2", "Current Account"));
            accountTypes.add(new SpinnerItem("3", "Fixed Deposit"));
            accountTypes.add(new SpinnerItem("4", "Recurring Deposit"));
            accountTypes.add(new SpinnerItem("5", "Salary Account"));
            
            runOnUiThread(() -> {
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                    AddPartnerActivity.this,
                    android.R.layout.simple_spinner_item,
                    accountTypes
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accountTypeSpinner.setAdapter(adapter);
            });
        }
    }

    private void loadPartnerTypes() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_partner_type_list.php";
                Log.d(TAG, "Loading partner types from: " + urlString);
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Partner types response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseBody = response.toString();
                    Log.d(TAG, "Partner types response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            Log.d(TAG, "Partner types count: " + dataArray.length());
                            
                            partnerTypes.clear();
                            partnerTypes.add(new SpinnerItem("0", "Select Partner Type"));
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                String id = item.getString("id");
                                String name = item.getString("partner_type");
                                Log.d(TAG, "Partner type: " + id + " - " + name);
                                partnerTypes.add(new SpinnerItem(id, name));
                            }

                            runOnUiThread(() -> {
                                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                                    AddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    partnerTypes
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                partnerTypeSpinner.setAdapter(adapter);
                                Log.d(TAG, "Partner types adapter set with " + partnerTypes.size() + " items");
                            });
                        } else {
                            Log.e(TAG, "Partner types API returned success=false");
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Error parsing partner types JSON: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Partner types HTTP error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading partner types: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loadBranchStates() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_branch_states_dropdown.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            branchStates.clear();
                            branchStates.add(new SpinnerItem("0", "Select Branch State"));
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                branchStates.add(new SpinnerItem(
                                    item.getString("id"),
                                    item.getString("name")
                                ));
                            }

                            runOnUiThread(() -> {
                                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                                    AddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    branchStates
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                branchStateSpinner.setAdapter(adapter);
                            });
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Error parsing branch states JSON: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading branch states: " + e.getMessage());
            }
        });
    }

    private void loadBranchLocations() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_branch_locations_dropdown.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            branchLocations.clear();
                            branchLocations.add(new SpinnerItem("0", "Select Branch Location"));
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                branchLocations.add(new SpinnerItem(
                                    item.getString("id"),
                                    item.getString("name")
                                ));
                            }

                            runOnUiThread(() -> {
                                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                                    AddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    branchLocations
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                branchLocationSpinner.setAdapter(adapter);
                            });
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Error parsing branch locations JSON: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading branch locations: " + e.getMessage());
            }
        });
    }

    private void loadBanks() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/director_bank_dropdown.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONArray dataArray = jsonResponse.optJSONArray("data");
                        if (dataArray != null) {
                            banks.clear();
                            banks.add(new SpinnerItem("0", "Select Bank"));
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                banks.add(new SpinnerItem(
                                    item.getString("id"),
                                    item.getString("bank_name")
                                ));
                            }
                            runOnUiThread(() -> {
                                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                                    AddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    banks
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                bankNameSpinner.setAdapter(adapter);
                            });
                        } else {
                            Log.e(TAG, "Bank API JSON missing 'data': " + response.toString());
                            runOnUiThread(() -> Toast.makeText(AddPartnerActivity.this, "Bank API error: No data", Toast.LENGTH_LONG).show());
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Error parsing banks JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(AddPartnerActivity.this, "Bank API error: Parse error", Toast.LENGTH_LONG).show());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading banks: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(AddPartnerActivity.this, "Bank API error: Exception", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void loadAccountTypes() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_account_type_list.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            accountTypes.clear();
                            accountTypes.add(new SpinnerItem("0", "Select Account Type"));
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                accountTypes.add(new SpinnerItem(
                                    item.getString("id"),
                                    item.getString("account_type")
                                ));
                            }

                            runOnUiThread(() -> {
                                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(
                                    AddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    accountTypes
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                accountTypeSpinner.setAdapter(adapter);
                            });
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "Error parsing account types JSON: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading account types: " + e.getMessage());
            }
        });
    }

    private void selectFile(String fileType) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, can proceed with file selection
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                String fileName = getFileName(uri);
                
                // For now, we'll just store the URI and show the filename
                // In a real implementation, you'd upload the file to the server
                panCardUri = uri;
                panCardFileName.setText(fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                if (inputStream != null) {
                    // For simplicity, just return a generic name
                    result = "selected_file";
                }
            } catch (IOException e) {
                Log.e(TAG, "Error getting file name: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "unknown_file";
    }

    private void submitPartnerData() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        // Disable submit button
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        executor.execute(() -> {
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("email", emailInput.getText().toString().trim());
                jsonData.put("password", passwordInput.getText().toString().trim());
                jsonData.put("first_name", firstNameInput.getText().toString().trim());
                jsonData.put("last_name", lastNameInput.getText().toString().trim());
                jsonData.put("alias_name", aliasNameInput.getText().toString().trim());
                jsonData.put("phone_no", phoneNoInput.getText().toString().trim());
                jsonData.put("alternative_phone_no", alternativePhoneInput.getText().toString().trim());
                
                // Get selected spinner values
                SpinnerItem selectedPartnerType = (SpinnerItem) partnerTypeSpinner.getSelectedItem();
                SpinnerItem selectedBranchState = (SpinnerItem) branchStateSpinner.getSelectedItem();
                SpinnerItem selectedBranchLocation = (SpinnerItem) branchLocationSpinner.getSelectedItem();
                SpinnerItem selectedBank = (SpinnerItem) bankNameSpinner.getSelectedItem();
                SpinnerItem selectedAccountType = (SpinnerItem) accountTypeSpinner.getSelectedItem();
                
                jsonData.put("partner_type_id", selectedPartnerType.getId());
                jsonData.put("branch_state_id", selectedBranchState.getId());
                jsonData.put("branch_location_id", selectedBranchLocation.getId());
                jsonData.put("bank_id", selectedBank.getId());
                jsonData.put("account_type_id", selectedAccountType.getId());
                
                jsonData.put("office_address", officeAddressInput.getText().toString().trim());
                jsonData.put("residential_address", residentialAddressInput.getText().toString().trim());
                jsonData.put("aadhaar_number", aadhaarNumberInput.getText().toString().trim());
                jsonData.put("pan_number", panNumberInput.getText().toString().trim());
                jsonData.put("account_number", accountNumberInput.getText().toString().trim());
                jsonData.put("ifsc_code", ifscCodeInput.getText().toString().trim());
                
                // File uploads (for now, just placeholder strings)
                jsonData.put("pan_card_upload", panCardUri != null ? panCardUri.toString() : "");
                jsonData.put("aadhaar_card_upload", aadhaarCardUri != null ? aadhaarCardUri.toString() : "");
                jsonData.put("photo_upload", photoUri != null ? photoUri.toString() : "");
                jsonData.put("bank_proof_upload", bankProofUri != null ? bankProofUri.toString() : "");

                String urlString = "https://emp.kfinone.com/mobile/api/add_partner.php";
                
                Log.d(TAG, "Submitting partner data to: " + urlString);
                Log.d(TAG, "JSON data: " + jsonData.toString());
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Write JSON data to output stream
                connection.getOutputStream().write(jsonData.toString().getBytes("UTF-8"));
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                BufferedReader reader;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String responseBody = response.toString();
                Log.d(TAG, "Response body: " + responseBody);
                
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    
                    try {
                        if (jsonResponse.getBoolean("success")) {
                            Toast.makeText(AddPartnerActivity.this, 
                                "Partner added successfully!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String errorMessage = jsonResponse.getString("message");
                            Toast.makeText(AddPartnerActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(AddPartnerActivity.this, 
                            "Error parsing server response", Toast.LENGTH_LONG).show();
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error submitting partner data: " + e.getMessage());
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    Toast.makeText(AddPartnerActivity.this, 
                        "Error submitting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;
        
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
        }
        
        if (passwordInput.getText().toString().trim().isEmpty()) {
            passwordInput.setError("Password is required");
            isValid = false;
        }
        
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            firstNameInput.setError("First name is required");
            isValid = false;
        }
        
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            lastNameInput.setError("Last name is required");
            isValid = false;
        }
        
        if (phoneNoInput.getText().toString().trim().isEmpty()) {
            phoneNoInput.setError("Phone number is required");
            isValid = false;
        }
        
        // Check if required spinners have valid selections
        SpinnerItem selectedPartnerType = (SpinnerItem) partnerTypeSpinner.getSelectedItem();
        SpinnerItem selectedBranchState = (SpinnerItem) branchStateSpinner.getSelectedItem();
        SpinnerItem selectedBranchLocation = (SpinnerItem) branchLocationSpinner.getSelectedItem();
        SpinnerItem selectedBank = (SpinnerItem) bankNameSpinner.getSelectedItem();
        SpinnerItem selectedAccountType = (SpinnerItem) accountTypeSpinner.getSelectedItem();
        
        if (selectedPartnerType.getId().equals("0")) {
            Toast.makeText(this, "Please select a partner type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        if (selectedBranchState.getId().equals("0")) {
            Toast.makeText(this, "Please select a branch state", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        if (selectedBranchLocation.getId().equals("0")) {
            Toast.makeText(this, "Please select a branch location", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        if (selectedBank.getId().equals("0")) {
            Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        if (selectedAccountType.getId().equals("0")) {
            Toast.makeText(this, "Please select an account type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        return isValid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Helper class for spinner items
    private static class SpinnerItem {
        private String id;
        private String name;

        public SpinnerItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    private void submitPartnerForm() {
        try {
            JSONObject partnerData = new JSONObject();
            partnerData.put("email_id", ((EditText) findViewById(R.id.emailInput)).getText().toString().trim());
            partnerData.put("password", ((EditText) findViewById(R.id.passwordInput)).getText().toString().trim());
            partnerData.put("first_name", ((EditText) findViewById(R.id.firstNameInput)).getText().toString().trim());
            partnerData.put("last_name", ((EditText) findViewById(R.id.lastNameInput)).getText().toString().trim());
            partnerData.put("alias_name", ((EditText) findViewById(R.id.aliasNameInput)).getText().toString().trim());
            partnerData.put("Phone_number", ((EditText) findViewById(R.id.phoneNoInput)).getText().toString().trim());
            partnerData.put("alternative_mobile_number", ((EditText) findViewById(R.id.alternativePhoneInput)).getText().toString().trim());
            partnerData.put("partner_type_id", getSelectedPartnerTypeId()); // TODO: implement this
            partnerData.put("branch_state_name_id", getSelectedBranchStateId()); // TODO: implement this
            partnerData.put("branch_location_id", getSelectedBranchLocationId()); // TODO: implement this
            partnerData.put("office_address", ((EditText) findViewById(R.id.officeAddressInput)).getText().toString().trim());
            partnerData.put("residential_address", ((EditText) findViewById(R.id.residentialAddressInput)).getText().toString().trim());
            partnerData.put("aadhaar_number", ((EditText) findViewById(R.id.aadhaarNumberInput)).getText().toString().trim());
            partnerData.put("pan_number", ((EditText) findViewById(R.id.panNumberInput)).getText().toString().trim());
            partnerData.put("account_number", ((EditText) findViewById(R.id.accountNumberInput)).getText().toString().trim());
            partnerData.put("ifsc_code", ((EditText) findViewById(R.id.ifscCodeInput)).getText().toString().trim());
            partnerData.put("bank_id", getSelectedBankId()); // TODO: implement this
            partnerData.put("account_type_id", getSelectedAccountTypeId()); // TODO: implement this
            partnerData.put("pan_img", panImgPathOrUrl); // TODO: set after upload
            partnerData.put("aadhaar_img", aadhaarImgPathOrUrl); // TODO: set after upload
            partnerData.put("photo_img", photoImgPathOrUrl); // TODO: set after upload
            partnerData.put("bankproof_img", bankproofImgPathOrUrl); // TODO: set after upload

            new Thread(() -> {
                try {
                    URL url = new URL("https://emp.kfinone.com/mobile/api/add_partner.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    try (java.io.OutputStream os = conn.getOutputStream()) {
                        byte[] input = partnerData.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        JSONObject json = new JSONObject(response.toString());
                        runOnUiThread(() -> {
                            if (json.optString("status").equals("success")) {
                                Toast.makeText(this, "Partner added successfully!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Add these methods to get selected IDs from spinners
    private String getSelectedPartnerTypeId() {
        SpinnerItem selected = (SpinnerItem) partnerTypeSpinner.getSelectedItem();
        return selected != null ? selected.getId() : "";
    }

    private String getSelectedBranchStateId() {
        SpinnerItem selected = (SpinnerItem) branchStateSpinner.getSelectedItem();
        return selected != null ? selected.getId() : "";
    }

    private String getSelectedBranchLocationId() {
        SpinnerItem selected = (SpinnerItem) branchLocationSpinner.getSelectedItem();
        return selected != null ? selected.getId() : "";
    }

    private String getSelectedBankId() {
        SpinnerItem selected = (SpinnerItem) bankNameSpinner.getSelectedItem();
        return selected != null ? selected.getId() : "";
    }

    private String getSelectedAccountTypeId() {
        SpinnerItem selected = (SpinnerItem) accountTypeSpinner.getSelectedItem();
        return selected != null ? selected.getId() : "";
    }

    // Add image path variables for upload results
    private String panImgPathOrUrl = "";
    private String aadhaarImgPathOrUrl = "";
    private String photoImgPathOrUrl = "";
    private String bankproofImgPathOrUrl = "";

    // When you handle file uploads, set these variables accordingly, e.g.:
    // panImgPathOrUrl = uploadedPanImageUrlOrPath;
    // aadhaarImgPathOrUrl = uploadedAadhaarImageUrlOrPath;
    // photoImgPathOrUrl = uploadedPhotoImageUrlOrPath;
    // bankproofImgPathOrUrl = uploadedBankProofImageUrlOrPath;
} 
package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddBankerActivity extends AppCompatActivity {

    private static final String TAG = "AddBankerActivity";
    private AutoCompleteTextView vendorBankDropdown;
    private TextInputEditText bankerNameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText emailInput;
    private AutoCompleteTextView designationDropdown;
    private AutoCompleteTextView loanTypeDropdown;
    private AutoCompleteTextView stateDropdown;
    private AutoCompleteTextView locationDropdown;
    private MaterialButton uploadButton;
    private TextInputEditText addressInput;
    private MaterialButton submitButton;
    private ImageButton backButton;
    private LinearLayout bankTableLayout;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banker);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        vendorBankDropdown = findViewById(R.id.vendorBankDropdown);
        bankerNameInput = findViewById(R.id.bankerNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        designationDropdown = findViewById(R.id.designationDropdown);
        loanTypeDropdown = findViewById(R.id.loanTypeDropdown);
        stateDropdown = findViewById(R.id.stateDropdown);
        locationDropdown = findViewById(R.id.locationDropdown);
        uploadButton = findViewById(R.id.uploadButton);
        addressInput = findViewById(R.id.addressInput);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        bankTableLayout = findViewById(R.id.bankTableLayout);

        // Load all dropdown data from database
        loadAllDropdownData();

        // Load bank data
        loadBankData();

        // Set click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement file picker for visiting card
                Toast.makeText(AddBankerActivity.this, "File picker will be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    submitBankerData();
                }
            }
        });
    }

    private void loadAllDropdownData() {
        // Load vendor bank data
        loadVendorBankData();
        
        // Load banker designation data
        loadBankerDesignationData();
        
        // Load loan type data
        loadLoanTypeData();
        
        // Load branch state data
        loadBranchStateData();
        
        // Load branch location data
        loadBranchLocationData();
    }

    private void loadVendorBankData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/get_vendor_bank_list.php";
                
                Log.d(TAG, "Loading vendor bank data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> vendorBankNames = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String vendorBankName = item.getString("vendor_bank_name");
                            vendorBankNames.add(vendorBankName);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup vendor bank dropdown with data from database
                            ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                                AddBankerActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                vendorBankNames
                            );
        vendorBankDropdown.setAdapter(vendorBankAdapter);

                            Log.d(TAG, "Loaded " + vendorBankNames.size() + " vendor banks from database");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_vendor_bank_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load vendor bank data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_vendor_bank_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_vendor_bank_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load vendor bank data", e);
                runOnUiThread(() -> showError("Failed to load vendor bank data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void loadBankerDesignationData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/get_banker_designation_list.php";
                
                Log.d(TAG, "Loading banker designation data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> designationNames = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String designationName = item.getString("designation_name");
                            designationNames.add(designationName);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup designation dropdown with data from database
                            ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(
                                AddBankerActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                designationNames
                            );
        designationDropdown.setAdapter(designationAdapter);

                            Log.d(TAG, "Loaded " + designationNames.size() + " banker designations from database");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_banker_designation_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load banker designation data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_banker_designation_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_banker_designation_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load banker designation data", e);
                runOnUiThread(() -> showError("Failed to load banker designation data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void loadLoanTypeData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/get_loan_type_list.php";
                
                Log.d(TAG, "Loading loan type data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> loanTypes = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String loanType = item.getString("loan_type");
                            loanTypes.add(loanType);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup loan type dropdown with data from database
                            ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                                AddBankerActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                loanTypes
                            );
        loanTypeDropdown.setAdapter(loanTypeAdapter);

                            Log.d(TAG, "Loaded " + loanTypes.size() + " loan types from database");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_loan_type_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load loan type data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_loan_type_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_loan_type_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load loan type data", e);
                runOnUiThread(() -> showError("Failed to load loan type data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void loadBranchStateData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/fetch_banker_branch_states.php";
                
                Log.d(TAG, "Loading branch state data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> branchStates = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            String branchStateName = dataArray.getString(i);
                            branchStates.add(branchStateName);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup branch state dropdown with data from database
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                                AddBankerActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                branchStates
                            );
                            stateDropdown.setAdapter(stateAdapter);

                            Log.d(TAG, "Loaded " + branchStates.size() + " branch states from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "fetch_banker_branch_states.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load branch state data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from fetch_banker_branch_states.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from fetch_banker_branch_states.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load branch state data", e);
                runOnUiThread(() -> showError("Failed to load branch state data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void loadBranchLocationData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/fetch_banker_branch_locations.php";
                
                Log.d(TAG, "Loading branch location data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> branchLocations = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            String branchLocation = dataArray.getString(i);
                            branchLocations.add(branchLocation);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup branch location dropdown with data from database
                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                                AddBankerActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                branchLocations
                            );
                            locationDropdown.setAdapter(locationAdapter);
                            
                            Log.d(TAG, "Loaded " + branchLocations.size() + " branch locations from database");
                            Toast.makeText(AddBankerActivity.this, 
                                "All dropdown data loaded successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "fetch_banker_branch_locations.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load branch location data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from fetch_banker_branch_locations.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from fetch_banker_branch_locations.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load branch location data", e);
                runOnUiThread(() -> showError("Failed to load branch location data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (vendorBankDropdown.getText().toString().trim().isEmpty()) {
            vendorBankDropdown.setError("Please select a vendor bank");
            return false;
        }
        if (bankerNameInput.getText().toString().trim().isEmpty()) {
            bankerNameInput.setError("Please enter banker name");
            return false;
        }
        if (phoneInput.getText().toString().trim().isEmpty()) {
            phoneInput.setError("Please enter phone number");
            return false;
        }
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Please enter email");
            return false;
        }
        if (designationDropdown.getText().toString().trim().isEmpty()) {
            designationDropdown.setError("Please select designation");
            return false;
        }
        if (loanTypeDropdown.getText().toString().trim().isEmpty()) {
            loanTypeDropdown.setError("Please select loan type");
            return false;
        }
        if (stateDropdown.getText().toString().trim().isEmpty()) {
            stateDropdown.setError("Please select state");
            return false;
        }
        if (locationDropdown.getText().toString().trim().isEmpty()) {
            locationDropdown.setError("Please select location");
            return false;
        }
        if (addressInput.getText().toString().trim().isEmpty()) {
            addressInput.setError("Please enter address");
            return false;
        }
        return true;
    }

    private void clearInputs() {
        vendorBankDropdown.setText("");
        bankerNameInput.setText("");
        phoneInput.setText("");
        emailInput.setText("");
        designationDropdown.setText("");
        loanTypeDropdown.setText("");
        stateDropdown.setText("");
        locationDropdown.setText("");
        addressInput.setText("");
    }

    private void loadBankData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://pznstudio.shop/kfinone/fetch_banker_banks.php";
                
                Log.d(TAG, "Loading bank data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<BankItem> bankItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            String bankName = dataArray.getString(i);
                            BankItem bankItem = new BankItem(i + 1, bankName);
                            bankItems.add(bankItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateBankTable(bankItems);
                            Log.d(TAG, "Loaded " + bankItems.size() + " banks from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "fetch_banker_banks.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load bank data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from fetch_banker_banks.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from fetch_banker_banks.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load bank data", e);
                runOnUiThread(() -> showError("Failed to load bank data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void populateBankTable(List<BankItem> bankItems) {
        bankTableLayout.removeAllViews();
        
        for (BankItem item : bankItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Bank Name
            TextView bankNameText = createTableCell(item.getBankName());
            row.addView(bankNameText);
            
            // Status
            TextView statusText = createTableCell("Active");
            row.addView(statusText);
            
            // Actions
            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
            ));
            actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionsLayout.setGravity(Gravity.CENTER);
            
            ImageButton editButton = new ImageButton(this);
            editButton.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            editButton.setImageResource(R.drawable.ic_edit);
            editButton.setBackgroundResource(android.R.drawable.btn_default);
            editButton.setContentDescription("Edit");
            editButton.setPadding(16, 16, 16, 16);
            editButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
            
            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(96, 96));
            deleteButton.setImageResource(R.drawable.ic_delete);
            deleteButton.setBackgroundResource(android.R.drawable.btn_default);
            deleteButton.setContentDescription("Delete");
            deleteButton.setPadding(16, 16, 16, 16);
            deleteButton.setColorFilter(getResources().getColor(R.color.red));
            
            actionsLayout.addView(editButton);
            actionsLayout.addView(deleteButton);
            row.addView(actionsLayout);
            
            bankTableLayout.addView(row);
        }
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        ));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private static class BankItem {
        private int id;
        private String bankName;

        public BankItem(int id, String bankName) {
            this.id = id;
            this.bankName = bankName;
        }

        public int getId() { return id; }
        public String getBankName() { return bankName; }
    }

    private void submitBankerData() {
        // Disable submit button to prevent multiple submissions
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("vendor_bank", vendorBankDropdown.getText().toString().trim());
                jsonData.put("banker_name", bankerNameInput.getText().toString().trim());
                jsonData.put("phone_number", phoneInput.getText().toString().trim());
                jsonData.put("email_id", emailInput.getText().toString().trim());
                jsonData.put("banker_designation", designationDropdown.getText().toString().trim());
                jsonData.put("loan_type", loanTypeDropdown.getText().toString().trim());
                jsonData.put("state", stateDropdown.getText().toString().trim());
                jsonData.put("location", locationDropdown.getText().toString().trim());
                jsonData.put("visiting_card", ""); // TODO: Implement file upload
                jsonData.put("address", addressInput.getText().toString().trim());

                String urlString = "https://pznstudio.shop/kfinone/add_banker.php";
                
                Log.d(TAG, "Submitting banker data to: " + urlString);
                Log.d(TAG, "JSON data: " + jsonData.toString());
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                // Write JSON data to output stream
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddBankerActivity.this, 
                                "Banker added successfully!", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "add_banker.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to add banker: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from add_banker.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from add_banker.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to submit banker data", e);
                runOnUiThread(() -> showError("Failed to submit banker data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
                
                // Re-enable submit button
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                });
            }
        });
    }
} 

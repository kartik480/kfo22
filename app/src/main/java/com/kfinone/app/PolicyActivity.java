package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PolicyActivity extends AppCompatActivity {

    private static final String TAG = "PolicyActivity";

    // Add Policy Section
    private AutoCompleteTextView loanTypeDropdown, vendorBankDropdown;
    private Button chooseFileButton, submitButton;
    private TextView selectedFileName;
    private TextInputEditText contentInput;
    private Uri selectedFileUri;

    // Policy List Section
    private AutoCompleteTextView filterVendorBankDropdown, filterLoanTypeDropdown;
    private Button filterButton, resetButton;
    private RecyclerView policyRecyclerView;
    private PolicyAdapter policyAdapter;
    private List<PolicyItem> policyList;
    private List<PolicyItem> filteredList;
    private LinearLayout emptyStateLayout, loadingLayout;
    private TextView backButton;
    private View refreshButton;

    // Activity Result Launcher for file picker
    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        uri -> {
            if (uri != null) {
                selectedFileUri = uri;
                String fileName = getFileName(uri);
                selectedFileName.setText(fileName);
                Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PolicyActivity onCreate started");
        
        try {
            setContentView(R.layout.activity_policy);
            Log.d(TAG, "PolicyActivity layout set successfully");
            
            // Initialize basic views first
            backButton = findViewById(R.id.backButton);
            if (backButton != null) {
                backButton.setOnClickListener(v -> goBack());
                Log.d(TAG, "Back button initialized");
            }
            
            // Show success message
            Toast.makeText(this, "Policy panel loaded successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "PolicyActivity basic setup completed");
            
            // Initialize other views in background to avoid blocking UI
            new Thread(() -> {
                try {
                    runOnUiThread(() -> {
                        try {
                            initializeViews();
                            setupClickListeners();
                            loadDropdownData();
                            loadPolicyData();
                            Log.d(TAG, "All PolicyActivity components initialized successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "Error in UI initialization: " + e.getMessage(), e);
                            Toast.makeText(PolicyActivity.this, "Error initializing components: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error in background initialization: " + e.getMessage(), e);
                }
            }).start();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in PolicyActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading Policy panel: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        // Add Policy Section
        loanTypeDropdown = findViewById(R.id.loanTypeDropdown);
        vendorBankDropdown = findViewById(R.id.vendorBankDropdown);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        submitButton = findViewById(R.id.submitButton);
        selectedFileName = findViewById(R.id.selectedFileName);
        contentInput = findViewById(R.id.contentInput);

        // Policy List Section
        filterVendorBankDropdown = findViewById(R.id.filterVendorBankDropdown);
        filterLoanTypeDropdown = findViewById(R.id.filterLoanTypeDropdown);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        policyRecyclerView = findViewById(R.id.policyRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);

        // Setup RecyclerView
        policyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        policyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        policyAdapter = new PolicyAdapter(filteredList, this);
        policyRecyclerView.setAdapter(policyAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        
        // Add Policy Section
        chooseFileButton.setOnClickListener(v -> openFilePicker());
        submitButton.setOnClickListener(v -> submitPolicy());
        
        // Policy List Section
        filterButton.setOnClickListener(v -> filterPolicies());
        resetButton.setOnClickListener(v -> resetFilter());
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("SPECIAL_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("CBO_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default to HomeActivity (KfinOne panel)
            Intent intent = new Intent(this, HomeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        }
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing policy data...", Toast.LENGTH_SHORT).show();
        loadDropdownData();
        loadPolicyData();
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

    private void openFilePicker() {
        filePickerLauncher.launch("*/*");
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitPolicy() {
        String selectedLoanType = loanTypeDropdown.getText().toString().trim();
        String selectedVendorBank = vendorBankDropdown.getText().toString().trim();
        String content = contentInput.getText().toString().trim();

        if (selectedLoanType.isEmpty()) {
            Toast.makeText(this, "Please select a loan type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedVendorBank.isEmpty()) {
            Toast.makeText(this, "Please select a vendor bank", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Please choose a file", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter content", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        // Submit policy to server
        submitPolicyToServer(selectedVendorBank, selectedLoanType, selectedFileUri, content);
    }

    private void submitPolicyToServer(String vendorBankName, String loanTypeName, Uri fileUri, String content) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Submitting policy to server");
                URL url = new URL("https://emp.kfinone.com/mobile/api/add_policy.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                // Create JSON request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("vendor_bank_name", vendorBankName);
                requestBody.put("loan_type_name", loanTypeName);
                requestBody.put("image", getFileName(fileUri)); // Use filename as image path
                requestBody.put("content", content);

                String jsonInputString = requestBody.toString();
                Log.d(TAG, "Request body: " + jsonInputString);

                // Send request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Submit policy response code: " + responseCode);

                // Read response
                BufferedReader in;
                if (responseCode >= 200 && responseCode < 300) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String responseString = response.toString();
                Log.d(TAG, "Submit policy response: " + responseString);

                JSONObject json = new JSONObject(responseString);
                String status = json.getString("status");

                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");

                    if ("success".equals(status)) {
                        Toast.makeText(PolicyActivity.this, "Policy submitted successfully!", Toast.LENGTH_LONG).show();
                        
                        // Clear form
                        loanTypeDropdown.setText("");
                        vendorBankDropdown.setText("");
                        selectedFileName.setText("No file selected");
                        contentInput.setText("");
                        selectedFileUri = null;
                        
                        // Refresh policy list
                        loadPolicyData();
                    } else {
                        String errorMsg = json.optString("message", "Unknown error occurred");
                        Toast.makeText(PolicyActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception submitting policy: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    Toast.makeText(PolicyActivity.this, "Error submitting policy: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadDropdownData() {
        // Load loan types from server
        loadLoanTypesFromServer();
        
        // Load vendor banks from server
        loadVendorBanksFromServer();
    }

    private void loadLoanTypesFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching loan types from server");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_loan_types.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Loan types response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Loan types response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> loanTypes = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject loanType = data.getJSONObject(i);
                            loanTypes.add(loanType.getString("loan_type"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                                PolicyActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                loanTypes
                            );
                            loanTypeDropdown.setAdapter(loanTypeAdapter);
                            filterLoanTypeDropdown.setAdapter(loanTypeAdapter);
                            Log.d(TAG, "Updated loan types dropdown with " + loanTypes.size() + " items");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            loadFallbackLoanTypes();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        loadFallbackLoanTypes();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading loan types: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    loadFallbackLoanTypes();
                });
            }
        }).start();
    }

    private void loadVendorBanksFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching vendor banks from server");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_vendor_banks.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Vendor banks response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Vendor banks response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> vendorBanks = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject vendorBank = data.getJSONObject(i);
                            vendorBanks.add(vendorBank.getString("vendor_bank_name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                                PolicyActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                vendorBanks
                            );
                            vendorBankDropdown.setAdapter(vendorBankAdapter);
                            filterVendorBankDropdown.setAdapter(vendorBankAdapter);
                            Log.d(TAG, "Updated vendor banks dropdown with " + vendorBanks.size() + " items");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            loadFallbackVendorBanks();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        loadFallbackVendorBanks();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading vendor banks: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    loadFallbackVendorBanks();
                });
            }
        }).start();
    }

    private void loadFallbackLoanTypes() {
        // Fallback to hardcoded loan types if server fails
        String[] loanTypes = {"Personal Loan", "Home Loan", "Business Loan", "Vehicle Loan", "Education Loan"};
        ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loanTypes);
        loanTypeDropdown.setAdapter(loanTypeAdapter);
        filterLoanTypeDropdown.setAdapter(loanTypeAdapter);
        Log.d(TAG, "Using fallback loan types");
    }

    private void loadFallbackVendorBanks() {
        // Fallback to hardcoded vendor banks if server fails
        String[] vendorBanks = {"HDFC Bank", "ICICI Bank", "SBI Bank", "Axis Bank", "Kotak Bank"};
        ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vendorBanks);
        vendorBankDropdown.setAdapter(vendorBankAdapter);
        filterVendorBankDropdown.setAdapter(vendorBankAdapter);
        Log.d(TAG, "Using fallback vendor banks");
    }

    private void loadPolicyData() {
        showLoading(true);
        
        // Fetch data from server
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching policy data from server");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_policies.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Policy data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Policy data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        policyList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject policy = data.getJSONObject(i);
                            policyList.add(new PolicyItem(
                                policy.optString("vendor_bank_name", "Unknown Bank"),
                                policy.optString("loan_type", "Unknown Type"),
                                policy.optString("image", ""),
                                policy.optString("content", "")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            filteredList.clear();
                            filteredList.addAll(policyList);
                            policyAdapter.notifyDataSetChanged();
                            showLoading(false);
                            updateEmptyState();
                            Log.d(TAG, "Updated policy list with " + policyList.size() + " policies");
                            Toast.makeText(PolicyActivity.this, 
                                "Loaded " + policyList.size() + " policies", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(PolicyActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                            loadSampleData(); // Fallback to sample data
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(PolicyActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                        loadSampleData(); // Fallback to sample data
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading policy data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(PolicyActivity.this, "Error loading policies: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loadSampleData(); // Fallback to sample data
                });
            }
        }).start();
    }

    private void loadSampleData() {
        policyList.clear();
        
        // Add sample policies as fallback
        policyList.add(new PolicyItem(
            "HDFC Bank",
            "Personal Loan",
            "https://example.com/images/policy1.jpg",
            "Personal loan policy with competitive interest rates and flexible repayment options."
        ));

        policyList.add(new PolicyItem(
            "ICICI Bank",
            "Home Loan",
            "https://example.com/images/policy2.jpg",
            "Home loan policy offering attractive rates for first-time homebuyers."
        ));

        policyList.add(new PolicyItem(
            "SBI Bank",
            "Business Loan",
            "https://example.com/images/policy3.jpg",
            "Business loan policy designed for small and medium enterprises."
        ));

        policyList.add(new PolicyItem(
            "Axis Bank",
            "Vehicle Loan",
            "https://example.com/images/policy4.jpg",
            "Vehicle loan policy with quick approval and minimal documentation."
        ));

        policyList.add(new PolicyItem(
            "Kotak Bank",
            "Education Loan",
            "https://example.com/images/policy5.jpg",
            "Education loan policy supporting students pursuing higher education."
        ));

        // Initially show all policies
        filteredList.clear();
        filteredList.addAll(policyList);
        policyAdapter.notifyDataSetChanged();
    }

    private void filterPolicies() {
        String selectedVendorBank = filterVendorBankDropdown.getText().toString().trim();
        String selectedLoanType = filterLoanTypeDropdown.getText().toString().trim();

        if (selectedVendorBank.isEmpty() && selectedLoanType.isEmpty()) {
            Toast.makeText(this, "Please select at least one filter criteria", Toast.LENGTH_SHORT).show();
            return;
        }

        filteredList.clear();
        for (PolicyItem policy : policyList) {
            boolean matchesVendorBank = selectedVendorBank.isEmpty() || 
                policy.getVendorBank().equalsIgnoreCase(selectedVendorBank);
            boolean matchesLoanType = selectedLoanType.isEmpty() || 
                policy.getLoanType().equalsIgnoreCase(selectedLoanType);

            if (matchesVendorBank && matchesLoanType) {
                filteredList.add(policy);
            }
        }

        policyAdapter.notifyDataSetChanged();
        updateEmptyState();
        
        String filterText = "";
        if (!selectedVendorBank.isEmpty()) filterText += "Vendor Bank: " + selectedVendorBank;
        if (!selectedLoanType.isEmpty()) {
            if (!filterText.isEmpty()) filterText += ", ";
            filterText += "Loan Type: " + selectedLoanType;
        }
        
        Toast.makeText(this, "Filtered by: " + filterText, Toast.LENGTH_SHORT).show();
    }

    private void resetFilter() {
        filterVendorBankDropdown.setText("");
        filterLoanTypeDropdown.setText("");
        filteredList.clear();
        filteredList.addAll(policyList);
        policyAdapter.notifyDataSetChanged();
        updateEmptyState();
        Toast.makeText(this, "Filter reset", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            policyRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            policyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            policyRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            policyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Policy Item data class
    public static class PolicyItem {
        private String vendorBank;
        private String loanType;
        private String imageUrl;
        private String content;

        public PolicyItem(String vendorBank, String loanType, String imageUrl, String content) {
            this.vendorBank = vendorBank;
            this.loanType = loanType;
            this.imageUrl = imageUrl;
            this.content = content;
        }

        // Getters
        public String getVendorBank() { return vendorBank; }
        public String getLoanType() { return loanType; }
        public String getImageUrl() { return imageUrl; }
        public String getContent() { return content; }
    }
} 

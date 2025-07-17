package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPayoutActivity extends AppCompatActivity {

    private static final String TAG = "AddPayoutActivity";

    // Dropdowns
    private AutoCompleteTextView payoutTypeDropdown, loanTypeDropdown, vendorBankDropdown, categoryDropdown;
    private TextInputEditText payoutAmountInput;
    private MaterialButton submitButton;
    private ImageButton backButton;

    // RecyclerView for payout list
    private RecyclerView payoutRecyclerView;
    private PayoutAdapter payoutAdapter;
    private List<PayoutItem> payoutList;

    // Store dropdown data with IDs
    private Map<String, Integer> payoutTypeMap = new HashMap<>();
    private Map<String, Integer> loanTypeMap = new HashMap<>();
    private Map<String, Integer> vendorBankMap = new HashMap<>();
    private Map<String, Integer> categoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payout);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadDropdownData();
        loadPayoutList();
    }

    private void initializeViews() {
        payoutTypeDropdown = findViewById(R.id.payoutTypeDropdown);
        loanTypeDropdown = findViewById(R.id.loanTypeDropdown);
        vendorBankDropdown = findViewById(R.id.vendorBankDropdown);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        payoutAmountInput = findViewById(R.id.payoutAmountInput);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        payoutRecyclerView = findViewById(R.id.payoutRecyclerView);
    }

    private void setupRecyclerView() {
        payoutList = new ArrayList<>();
        payoutAdapter = new PayoutAdapter(payoutList);
        payoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payoutRecyclerView.setAdapter(payoutAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        submitButton.setOnClickListener(v -> submitPayout());
    }

    private void loadDropdownData() {
        // Load all dropdown data
        loadPayoutTypes();
        loadLoanTypes();
        loadVendorBanks();
        loadCategories();
    }

    private void loadPayoutTypes() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_payout_types.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

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
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> payoutTypes = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("payout_name");
                            payoutTypes.add(name);
                            payoutTypeMap.put(name, id);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AddPayoutActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                payoutTypes
                            );
                            payoutTypeDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading payout types: " + e.getMessage(), e);
            }
        }).start();
    }

    private void loadLoanTypes() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_loan_types.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

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
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> loanTypes = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("loan_type");
                            loanTypes.add(name);
                            loanTypeMap.put(name, id);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AddPayoutActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                loanTypes
                            );
                            loanTypeDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading loan types: " + e.getMessage(), e);
            }
        }).start();
    }

    private void loadVendorBanks() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_vendor_banks.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

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
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> vendorBanks = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("vendor_bank_name");
                            vendorBanks.add(name);
                            vendorBankMap.put(name, id);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AddPayoutActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                vendorBanks
                            );
                            vendorBankDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading vendor banks: " + e.getMessage(), e);
            }
        }).start();
    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_categories.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

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
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> categories = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("category_name");
                            categories.add(name);
                            categoryMap.put(name, id);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AddPayoutActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                categories
                            );
                            categoryDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading categories: " + e.getMessage(), e);
            }
        }).start();
    }

    private void loadPayoutList() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_payouts.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

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
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<PayoutItem> newPayoutList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.optInt("id", 0);
                            int userId = item.optInt("user_id", 0);
                            String payoutTypeName = item.optString("payout_type_name", "N/A");
                            String vendorBankName = item.optString("vendor_bank_name", "N/A");
                            String loanTypeName = item.optString("loan_type_name", "N/A");
                            String categoryName = item.optString("category_name", "N/A");
                            String payout = item.optString("payout", "N/A");
                            
                            PayoutItem payoutItem = new PayoutItem(id, userId, payoutTypeName, 
                                    vendorBankName, loanTypeName, categoryName, payout);
                            newPayoutList.add(payoutItem);
                        }
                        
                        runOnUiThread(() -> {
                            payoutList.clear();
                            payoutList.addAll(newPayoutList);
                            payoutAdapter.notifyDataSetChanged();
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading payout list: " + e.getMessage(), e);
            }
        }).start();
    }

    private void submitPayout() {
        String selectedPayoutType = payoutTypeDropdown.getText().toString().trim();
        String selectedLoanType = loanTypeDropdown.getText().toString().trim();
        String selectedVendorBank = vendorBankDropdown.getText().toString().trim();
        String selectedCategory = categoryDropdown.getText().toString().trim();
        String payoutAmount = payoutAmountInput.getText().toString().trim();

        // Validation
        if (selectedPayoutType.isEmpty()) {
            Toast.makeText(this, "Please select a payout type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLoanType.isEmpty()) {
            Toast.makeText(this, "Please select a loan type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedVendorBank.isEmpty()) {
            Toast.makeText(this, "Please select a vendor bank", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (payoutAmount.isEmpty()) {
            payoutAmountInput.setError("Please enter payout amount");
            return;
        }

        // Get IDs from maps
        Integer payoutTypeId = payoutTypeMap.get(selectedPayoutType);
        Integer loanTypeId = loanTypeMap.get(selectedLoanType);
        Integer vendorBankId = vendorBankMap.get(selectedVendorBank);
        Integer categoryId = categoryMap.get(selectedCategory);

        if (payoutTypeId == null || loanTypeId == null || vendorBankId == null || categoryId == null) {
            Toast.makeText(this, "Error: Invalid selection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        // Submit to server
        submitPayoutToServer(payoutTypeId, loanTypeId, vendorBankId, categoryId, payoutAmount);
    }

    private void submitPayoutToServer(int payoutTypeId, int loanTypeId, int vendorBankId, int categoryId, String payoutAmount) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/add_payout.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Create JSON request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("user_id", 1); // TODO: Get actual user ID
                requestBody.put("payout_type_id", payoutTypeId);
                requestBody.put("loan_type_id", loanTypeId);
                requestBody.put("vendor_bank_id", vendorBankId);
                requestBody.put("category_id", categoryId);
                requestBody.put("payout", payoutAmount);

                String jsonInputString = requestBody.toString();
                Log.d(TAG, "Request body: " + jsonInputString);

                // Send request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Submit payout response code: " + responseCode);

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
                Log.d(TAG, "Submit payout response: " + responseString);

                JSONObject json = new JSONObject(responseString);
                String status = json.getString("status");

                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");

                    if ("success".equals(status)) {
                        Toast.makeText(AddPayoutActivity.this, "Payout added successfully!", Toast.LENGTH_LONG).show();
                        
                        // Clear form
                        payoutTypeDropdown.setText("");
                        loanTypeDropdown.setText("");
                        vendorBankDropdown.setText("");
                        categoryDropdown.setText("");
                        payoutAmountInput.setText("");
                        
                        // Refresh the payout list
                        loadPayoutList();
                    } else {
                        String errorMsg = json.optString("message", "Unknown error occurred");
                        Toast.makeText(AddPayoutActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception submitting payout: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    Toast.makeText(AddPayoutActivity.this, "Error submitting payout: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
} 
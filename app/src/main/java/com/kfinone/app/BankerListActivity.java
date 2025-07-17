package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankerListActivity extends AppCompatActivity {

    private static final String TAG = "BankerListActivity";
    
    private AutoCompleteTextView vendorBankFilter;
    private AutoCompleteTextView loanTypeFilter;
    private AutoCompleteTextView stateFilter;
    private AutoCompleteTextView locationFilter;
    private MaterialButton filterButton;
    private MaterialButton resetButton;
    private LinearLayout tableContent;
    private ImageButton backButton;
    private ProgressBar loadingIndicator;
    private TextView loadingText;
    private LinearLayout loadingContainer;
    
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banker_list);

        // Initialize executor service for background tasks
        executorService = Executors.newFixedThreadPool(4);

        // Initialize views
        vendorBankFilter = findViewById(R.id.vendorBankFilter);
        loanTypeFilter = findViewById(R.id.loanTypeFilter);
        stateFilter = findViewById(R.id.stateFilter);
        locationFilter = findViewById(R.id.locationFilter);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        tableContent = findViewById(R.id.tableContent);
        backButton = findViewById(R.id.backButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingText = findViewById(R.id.loadingText);
        loadingContainer = findViewById(R.id.loadingContainer);

        // Set up click listeners
        backButton.setOnClickListener(v -> finish());

        filterButton.setOnClickListener(v -> {
            showLoading(true);
            loadBankersByFilters();
        });

        resetButton.setOnClickListener(v -> {
            vendorBankFilter.setText("");
            loanTypeFilter.setText("");
            stateFilter.setText("");
            locationFilter.setText("");
            tableContent.removeAllViews();
            showLoading(false);
        });

        // Load initial data with loading indicator
        showLoading(true);
        setupDropdowns();
    }

    private void showLoading(boolean show) {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (filterButton != null) {
            filterButton.setEnabled(!show);
        }
    }

    private void setupDropdowns() {
        // Load dropdowns in parallel with better error handling
        loadVendorBankOptions();
        loadLoanTypeOptions();
        loadStateOptions();
        loadLocationOptions();
        
        // Load initial banker data after a short delay
        tableContent.postDelayed(() -> {
            loadBankersByFilters();
        }, 1000);
    }

    private void loadVendorBankOptions() {
        executorService.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_vendor_banks.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
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
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> vendorBanks = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            vendorBanks.add(dataArray.getJSONObject(i).getString("vendor_bank_name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vendorBanks);
                            vendorBankFilter.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading vendor banks: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    // Add fallback data
                    String[] fallbackBanks = {"HDFC Bank", "ICICI Bank", "SBI Bank", "Axis Bank"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, fallbackBanks);
                    vendorBankFilter.setAdapter(adapter);
                });
            }
        });
    }

    private void loadLoanTypeOptions() {
        executorService.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_loan_types.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
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
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> loanTypes = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            loanTypes.add(dataArray.getJSONObject(i).getString("loan_type"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loanTypes);
                            loanTypeFilter.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading loan types: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    // Add fallback data
                    String[] fallbackTypes = {"Personal Loan", "Home Loan", "Business Loan", "Vehicle Loan"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, fallbackTypes);
                    loanTypeFilter.setAdapter(adapter);
                });
            }
        });
    }

    private void loadStateOptions() {
        executorService.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_branch_states.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
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
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> states = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            states.add(dataArray.getJSONObject(i).getString("branch_state_name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
                            stateFilter.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading states: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    // Add fallback data
                    String[] fallbackStates = {"Maharashtra", "Delhi", "Karnataka", "Tamil Nadu"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, fallbackStates);
                    stateFilter.setAdapter(adapter);
                });
            }
        });
    }

    private void loadLocationOptions() {
        executorService.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_branch_locations.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
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
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        java.util.List<String> locations = new java.util.ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            locations.add(dataArray.getJSONObject(i).getString("branch_location"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
                            locationFilter.setAdapter(adapter);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading locations: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    // Add fallback data
                    String[] fallbackLocations = {"Mumbai", "Pune", "Delhi", "Bangalore"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, fallbackLocations);
                    locationFilter.setAdapter(adapter);
                });
            }
        });
    }

    private void loadBankersByFilters() {
        executorService.execute(() -> {
            try {
                // Build URL with filter parameters
                StringBuilder urlBuilder = new StringBuilder("https://emp.kfinone.com/mobile/api/fetch_bankers_by_filters.php?");
                
                String vendorBank = vendorBankFilter.getText().toString();
                String loanType = loanTypeFilter.getText().toString();
                String state = stateFilter.getText().toString();
                String location = locationFilter.getText().toString();
                
                if (!vendorBank.isEmpty()) {
                    urlBuilder.append("vendor_bank=").append(java.net.URLEncoder.encode(vendorBank, "UTF-8")).append("&");
                }
                if (!loanType.isEmpty()) {
                    urlBuilder.append("loan_type=").append(java.net.URLEncoder.encode(loanType, "UTF-8")).append("&");
                }
                if (!state.isEmpty()) {
                    urlBuilder.append("state=").append(java.net.URLEncoder.encode(state, "UTF-8")).append("&");
                }
                if (!location.isEmpty()) {
                    urlBuilder.append("location=").append(java.net.URLEncoder.encode(location, "UTF-8")).append("&");
                }
                
                String urlString = urlBuilder.toString();
                if (urlString.endsWith("&")) {
                    urlString = urlString.substring(0, urlString.length() - 1);
                }
                
                Log.d(TAG, "Loading bankers from: " + urlString);
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
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
                        org.json.JSONArray dataArray = jsonResponse.getJSONArray("data");
                        runOnUiThread(() -> {
                            displayBankersInTable(dataArray);
                            showLoading(false);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(BankerListActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            tableContent.removeAllViews();
                            showLoading(false);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BankerListActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading bankers: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(BankerListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showLoading(false);
                });
            }
        });
    }

    private void displayBankersInTable(org.json.JSONArray bankersArray) {
        tableContent.removeAllViews();

        try {
            if (bankersArray.length() == 0) {
                TextView noDataText = new TextView(this);
                noDataText.setText("No bankers found with selected filters");
                noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                noDataText.setPadding(16, 32, 16, 32);
                tableContent.addView(noDataText);
                return;
            }
            
            // Limit the number of items to prevent UI freezing
            int maxItems = Math.min(bankersArray.length(), 50);
            
            for (int i = 0; i < maxItems; i++) {
                org.json.JSONObject banker = bankersArray.getJSONObject(i);
                
                // Create container box for each banker
                LinearLayout containerBox = new LinearLayout(this);
                containerBox.setOrientation(LinearLayout.VERTICAL);
                containerBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                containerBox.setBackgroundResource(R.drawable.edit_text_background);
                containerBox.setPadding(16, 16, 16, 16);
                
                // Add margin between boxes
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) containerBox.getLayoutParams();
                params.setMargins(0, 8, 0, 8);
                containerBox.setLayoutParams(params);
                
                // Add banker data
                String[][] bankerData = {
                    {"Vendor Bank", banker.optString("vendor_bank", "N/A")},
                    {"Banker Name", banker.optString("banker_name", "N/A")},
                    {"Designation", banker.optString("banker_designation", "N/A")},
                    {"Mobile No", banker.optString("Phone_number", "N/A")},
                    {"Email", banker.optString("email_id", "N/A")},
                    {"Loan Type", banker.optString("loan_type", "N/A")},
                    {"State", banker.optString("state", "N/A")},
                    {"Location", banker.optString("location", "N/A")},
                    {"Visiting Card", banker.optString("visiting_card", "N/A")},
                    {"Address", banker.optString("address", "N/A")}
                };
                
                for (String[] row : bankerData) {
                    LinearLayout dataRow = createVerticalDataRow(row[0], row[1]);
                    containerBox.addView(dataRow);
                }
                
                tableContent.addView(containerBox);
            }
            
            if (bankersArray.length() > maxItems) {
                TextView moreText = new TextView(this);
                moreText.setText("... and " + (bankersArray.length() - maxItems) + " more results");
                moreText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                moreText.setPadding(16, 16, 16, 16);
                tableContent.addView(moreText);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying data: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private LinearLayout createVerticalDataRow(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setPadding(0, 8, 0, 8);

        // Label (left side)
        TextView labelText = new TextView(this);
        labelText.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.4f
        ));
        labelText.setText(label + ":");
        labelText.setTypeface(labelText.getTypeface(), android.graphics.Typeface.BOLD);
        labelText.setTextColor(getResources().getColor(R.color.black));
        labelText.setPadding(0, 0, 16, 0);

        // Value (right side)
        TextView valueText = new TextView(this);
        valueText.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.6f
        ));
        valueText.setText(value);
        valueText.setTextColor(getResources().getColor(R.color.black));

        row.addView(labelText);
        row.addView(valueText);
        return row;
    }

    private View createSeparator() {
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        ));
        separator.setBackgroundColor(getResources().getColor(R.color.gray_light));
        separator.setPadding(0, 16, 0, 16);
        return separator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 

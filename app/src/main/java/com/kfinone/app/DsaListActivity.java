package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityDsaListBinding;
import org.json.JSONArray;
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
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.text.TextUtils;
import android.widget.Toast;
import java.net.URLEncoder;
import org.json.JSONException;

public class DsaListActivity extends AppCompatActivity {
    private static final String TAG = "DsaListActivity";
    private ActivityDsaListBinding binding;
    private AutoCompleteTextView vendorBankSpinner;
    private AutoCompleteTextView loanTypeSpinner;
    private AutoCompleteTextView stateSpinner;
    private AutoCompleteTextView locationSpinner;
    private TextInputEditText dsaCodeInput;
    private TextView backButton;
    private List<String> vendorBanks;
    private List<String> loanTypes;
    private List<String> states;
    private List<String> locations;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsaListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize lists and executor
        vendorBanks = new ArrayList<>();
        loanTypes = new ArrayList<>();
        states = new ArrayList<>();
        locations = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        initializeViews();
        loadDropdownOptionsFromDatabase();
        setupButtons();
        
        // Load all DSA records for showcase
        loadAllDsaRecords();
    }

    private void initializeViews() {
        vendorBankSpinner = binding.vendorBankSpinner;
        loanTypeSpinner = binding.loanTypeSpinner;
        stateSpinner = binding.stateSpinner;
        locationSpinner = binding.locationSpinner;
        dsaCodeInput = binding.dsaCodeInput;
        
        backButton = binding.backButton;
    }

    private void loadDropdownOptionsFromDatabase() {
        // Load vendor banks from database
        loadVendorBanksForDropdown();
        
        // Load loan types from database
        loadLoanTypesForDropdown();
        
        // Load branch states from database
        loadBranchStatesForDropdown();
        
        // Load branch locations from database
        loadBranchLocationsForDropdown();
    }

    private void loadVendorBanksForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/fetch_vendor_banks.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response from PHP: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "PHP returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: PHP returned HTML instead of JSON. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray vendorBanksArray = jsonResponse.getJSONArray("data");
                        List<String> newVendorBanks = new ArrayList<>();
                        newVendorBanks.add("Select Vendor Bank"); // Add default option
                        
                        for (int i = 0; i < vendorBanksArray.length(); i++) {
                            JSONObject vendorBank = vendorBanksArray.getJSONObject(i);
                            newVendorBanks.add(vendorBank.getString("vendor_bank_name"));
                        }
                        
                        runOnUiThread(() -> {
                            vendorBanks.clear();
                            vendorBanks.addAll(newVendorBanks);
                            
                            // Setup vendor bank dropdown
                            ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                                DsaListActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                vendorBanks
                            );
        vendorBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            vendorBankSpinner.setAdapter(vendorBankAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load vendor banks", e);
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

    private void loadLoanTypesForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/fetch_loan_types.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response from PHP: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "PHP returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: PHP returned HTML instead of JSON. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray loanTypesArray = jsonResponse.getJSONArray("data");
                        List<String> newLoanTypes = new ArrayList<>();
                        newLoanTypes.add("Select Loan Type"); // Add default option
                        
                        for (int i = 0; i < loanTypesArray.length(); i++) {
                            JSONObject loanType = loanTypesArray.getJSONObject(i);
                            newLoanTypes.add(loanType.getString("loan_type"));
                        }
                        
                        runOnUiThread(() -> {
                            loanTypes.clear();
                            loanTypes.addAll(newLoanTypes);
                            
                            // Setup loan types dropdown
                            ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                                DsaListActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                loanTypes
                            );
        loanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            loanTypeSpinner.setAdapter(loanTypeAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load loan types", e);
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

    private void loadBranchStatesForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/fetch_branch_states.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response from PHP: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "PHP returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: PHP returned HTML instead of JSON. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray statesArray = jsonResponse.getJSONArray("data");
                        List<String> newStates = new ArrayList<>();
                        newStates.add("Select State"); // Add default option
                        
                        for (int i = 0; i < statesArray.length(); i++) {
                            JSONObject state = statesArray.getJSONObject(i);
                            newStates.add(state.getString("branch_state_name"));
                        }
                        
                        runOnUiThread(() -> {
                            states.clear();
                            states.addAll(newStates);
                            
                            // Setup state dropdown
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                                DsaListActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                states
                            );
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            stateSpinner.setAdapter(stateAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load states", e);
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

    private void loadBranchLocationsForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/fetch_branch_locations.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response from PHP: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "PHP returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: PHP returned HTML instead of JSON. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray locationsArray = jsonResponse.getJSONArray("data");
                        List<String> newLocations = new ArrayList<>();
                        newLocations.add("Select Location"); // Add default option
                        
                        for (int i = 0; i < locationsArray.length(); i++) {
                            JSONObject location = locationsArray.getJSONObject(i);
                            newLocations.add(location.getString("branch_location"));
                        }
                        
                        runOnUiThread(() -> {
                            locations.clear();
                            locations.addAll(newLocations);
                            
                            // Setup location dropdown
                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                                DsaListActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                locations
                            );
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            locationSpinner.setAdapter(locationAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load locations", e);
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

    private void fetchDsaList(String vendorBank, String loanType, String state, String location, String dsaCode) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                // Build URL with parameters
                StringBuilder urlBuilder = new StringBuilder("https://emp.kfinone.com/mobile/api/test_dsa_connection.php");
                // Temporarily comment out the complex URL building to test basic connection
                /*
                if (!vendorBank.isEmpty() && !vendorBank.equals("Select Vendor Bank")) {
                    urlBuilder.append("vendor_bank=").append(URLEncoder.encode(vendorBank, "UTF-8")).append("&");
                }
                if (!loanType.isEmpty() && !loanType.equals("Select Loan Type")) {
                    urlBuilder.append("loan_type=").append(URLEncoder.encode(loanType, "UTF-8")).append("&");
                }
                if (!state.isEmpty() && !state.equals("Select State")) {
                    urlBuilder.append("state=").append(URLEncoder.encode(state, "UTF-8")).append("&");
                }
                if (!location.isEmpty() && !location.equals("Select Location")) {
                    urlBuilder.append("location=").append(URLEncoder.encode(location, "UTF-8")).append("&");
                }
                if (!dsaCode.isEmpty()) {
                    urlBuilder.append("dsa_code=").append(URLEncoder.encode(dsaCode, "UTF-8")).append("&");
                }
                
                String urlString = urlBuilder.toString();
                if (urlString.endsWith("&")) {
                    urlString = urlString.substring(0, urlString.length() - 1);
                }
                */
                
                String urlString = urlBuilder.toString();
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        // Check if this is a test response (has dsa_count) or actual data response (has data)
                        if (jsonResponse.has("dsa_count")) {
                            // This is a test response
                            int dsaCount = jsonResponse.getInt("dsa_count");
                            String message = jsonResponse.getString("message");
                            Log.d(TAG, "Test response: " + message + ", DSA count: " + dsaCount);
                            runOnUiThread(() -> showError("Test successful! Found " + dsaCount + " DSA records. Now testing actual data fetch..."));
                            
                            // Now try to fetch actual data
                            fetchActualDsaList(vendorBank, loanType, state, location, dsaCode);
                        } else if (jsonResponse.has("data")) {
                            // This is actual data response
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            List<DsaItem> dsaItems = new ArrayList<>();
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                DsaItem dsaItem = new DsaItem(
                                    item.getString("vendor_bank_name"),
                                    item.getString("dsa_code"),
                                    item.getString("bsa_name"),
                                    item.getString("loan_type"),
                                    item.getString("branch_state_name"),
                                    item.getString("branch_location")
                                );
                                dsaItems.add(dsaItem);
                            }
                            
                            if (dsaItems.isEmpty()) {
                                runOnUiThread(() -> {
                                    showError("No DSA records found matching your filter criteria. Try different filters or clear some filters.");
                                    populateTable(dsaItems); // Show empty table
                                });
                            } else {
                                runOnUiThread(() -> populateTable(dsaItems));
                            }
                        } else {
                            Log.e(TAG, "Unexpected response structure: " + jsonResponse.toString());
                            runOnUiThread(() -> showError("Unexpected response structure from server"));
                        }
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to fetch data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch DSA list", e);
                runOnUiThread(() -> showError("Failed to fetch data: " + e.getMessage()));
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

    private void fetchActualDsaList(String vendorBank, String loanType, String state, String location, String dsaCode) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                // Build URL with parameters for actual data fetch
                StringBuilder urlBuilder = new StringBuilder("https://emp.kfinone.com/mobile/api/get_dsa_list.php?");
                if (!vendorBank.isEmpty() && !vendorBank.equals("Select Vendor Bank")) {
                    urlBuilder.append("vendor_bank=").append(URLEncoder.encode(vendorBank, "UTF-8")).append("&");
                }
                if (!loanType.isEmpty() && !loanType.equals("Select Loan Type")) {
                    urlBuilder.append("loan_type=").append(URLEncoder.encode(loanType, "UTF-8")).append("&");
                }
                if (!state.isEmpty() && !state.equals("Select State")) {
                    urlBuilder.append("state=").append(URLEncoder.encode(state, "UTF-8")).append("&");
                }
                if (!location.isEmpty() && !location.equals("Select Location")) {
                    urlBuilder.append("location=").append(URLEncoder.encode(location, "UTF-8")).append("&");
                }
                if (!dsaCode.isEmpty()) {
                    urlBuilder.append("dsa_code=").append(URLEncoder.encode(dsaCode, "UTF-8")).append("&");
                }
                
                String urlString = urlBuilder.toString();
                if (urlString.endsWith("&")) {
                    urlString = urlString.substring(0, urlString.length() - 1);
                }
                
                Log.d(TAG, "Fetching actual data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response from get_dsa_list.php: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_dsa_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_dsa_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<DsaItem> dsaItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            DsaItem dsaItem = new DsaItem(
                                item.getString("vendor_bank_name"),
                                item.getString("dsa_code"),
                                item.getString("bsa_name"),
                                item.getString("loan_type"),
                                item.getString("branch_state_name"),
                                item.getString("branch_location")
                            );
                            dsaItems.add(dsaItem);
                        }
                        
                        if (dsaItems.isEmpty()) {
                            runOnUiThread(() -> {
                                showError("No DSA records found matching your filter criteria. Try different filters or clear some filters.");
                                populateTable(dsaItems); // Show empty table
                            });
                        } else {
                            runOnUiThread(() -> populateTable(dsaItems));
                        }
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_dsa_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to fetch DSA data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_dsa_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_dsa_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch actual DSA list", e);
                runOnUiThread(() -> showError("Failed to fetch actual DSA data: " + e.getMessage()));
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

    private void populateTable(List<DsaItem> dsaItems) {
        // Get the table container
        LinearLayout tableContainer = binding.tableLayout;
        
        // Remove existing data rows (keep header)
        int childCount = tableContainer.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            tableContainer.removeViewAt(i);
        }
        
        if (dsaItems.isEmpty()) {
            // Add a "No data found" row
            LinearLayout noDataRow = new LinearLayout(this);
            noDataRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            noDataRow.setOrientation(LinearLayout.HORIZONTAL);
            noDataRow.setPadding(48, 48, 48, 48);
            noDataRow.setBackgroundResource(R.drawable.edit_text_background);
            
            TextView noDataText = new TextView(this);
            noDataText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            noDataText.setText("No DSA records found matching your filter criteria");
            noDataText.setTextSize(14);
            noDataText.setTextColor(getResources().getColor(R.color.red));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(16, 16, 16, 16);
            
            noDataRow.addView(noDataText);
            tableContainer.addView(noDataRow);
            return;
        }
        
        // Add data rows
        for (DsaItem item : dsaItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Vendor Bank
            TextView vendorBankText = createTableCell(item.getVendorBank());
            row.addView(vendorBankText);
            
            // DSA Code
            TextView dsaCodeText = createTableCell(item.getDsaCode());
            row.addView(dsaCodeText);
            
            // DSA Name
            TextView dsaNameText = createTableCell(item.getDsaName());
            row.addView(dsaNameText);
            
            // Loan Type
            TextView loanTypeText = createTableCell(item.getLoanType());
            row.addView(loanTypeText);
            
            // State
            TextView stateText = createTableCell(item.getState());
            row.addView(stateText);
            
            // Location
            TextView locationText = createTableCell(item.getLocation());
            row.addView(locationText);
            
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
            
            tableContainer.addView(row);
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
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Data class for DSA items
    private static class DsaItem {
        private String vendorBank;
        private String dsaCode;
        private String dsaName;
        private String loanType;
        private String state;
        private String location;

        public DsaItem(String vendorBank, String dsaCode, String dsaName, String loanType, String state, String location) {
            this.vendorBank = vendorBank;
            this.dsaCode = dsaCode;
            this.dsaName = dsaName;
            this.loanType = loanType;
            this.state = state;
            this.location = location;
        }

        public String getVendorBank() { return vendorBank; }
        public String getDsaCode() { return dsaCode; }
        public String getDsaName() { return dsaName; }
        public String getLoanType() { return loanType; }
        public String getState() { return state; }
        public String getLocation() { return location; }
    }

    private void setupButtons() {
        backButton.setOnClickListener(v -> finish());

        binding.filterButton.setOnClickListener(v -> {
            // Get filter values
            String vendorBank = vendorBankSpinner.getText().toString();
            String loanType = loanTypeSpinner.getText().toString();
            String state = stateSpinner.getText().toString();
            String location = locationSpinner.getText().toString();
            String dsaCode = dsaCodeInput.getText().toString();

            // Check if any filters are applied
            boolean hasFilters = (!vendorBank.isEmpty() && !vendorBank.equals("Select Vendor Bank")) ||
                               (!loanType.isEmpty() && !loanType.equals("Select Loan Type")) ||
                               (!state.isEmpty() && !state.equals("Select State")) ||
                               (!location.isEmpty() && !location.equals("Select Location")) ||
                               (!dsaCode.isEmpty());

            if (!hasFilters) {
                // No filters applied, show all records
                showError("No filters applied. Please select at least one filter criteria.");
                return;
            }

            // Call API to fetch filtered data
            fetchDsaList(vendorBank, loanType, state, location, dsaCode);
        });

        binding.resetButton.setOnClickListener(v -> {
            vendorBankSpinner.setText("");
            loanTypeSpinner.setText("");
            stateSpinner.setText("");
            locationSpinner.setText("");
            // Clear the values in the table
            dsaCodeInput.setText("");
        });
    }

    private void loadAllDsaRecords() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                // Call the API without any filters to get all records
                String urlString = "https://emp.kfinone.com/mobile/api/get_dsa_list.php";
                
                Log.d(TAG, "Loading all DSA records from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response for all DSA records: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_dsa_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_dsa_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<DsaItem> dsaItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            DsaItem dsaItem = new DsaItem(
                                item.getString("vendor_bank_name"),
                                item.getString("dsa_code"),
                                item.getString("bsa_name"),
                                item.getString("loan_type"),
                                item.getString("branch_state_name"),
                                item.getString("branch_location")
                            );
                            dsaItems.add(dsaItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateTable(dsaItems);
                            showError("Loaded " + dsaItems.size() + " DSA records for showcase");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_dsa_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load DSA records: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_dsa_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_dsa_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load all DSA records", e);
                runOnUiThread(() -> showError("Failed to load DSA records: " + e.getMessage()));
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
} 

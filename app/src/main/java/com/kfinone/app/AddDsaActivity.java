package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityAddDsaBinding;
import com.google.android.material.textfield.TextInputEditText;
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

public class AddDsaActivity extends AppCompatActivity {
    private static final String TAG = "AddDsaActivity";
    private ActivityAddDsaBinding binding;
    private List<String> vendorBanks;
    private List<String> dsaNames;
    private List<String> loanTypes;
    private List<String> branchStates;
    private List<String> branchLocations;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDsaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize lists and executor
        vendorBanks = new ArrayList<>();
        dsaNames = new ArrayList<>();
        loanTypes = new ArrayList<>();
        branchStates = new ArrayList<>();
        branchLocations = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        // Initialize back button
        binding.backButton.setOnClickListener(v -> finish());

        // Load dropdown options from database
        loadVendorBanksForDropdown();
        loadDsaNamesForDropdown();
        loadLoanTypesForDropdown();
        loadBranchStatesForDropdown();
        loadBranchLocationsForDropdown();

        // Initialize submit button
        binding.submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // Add DSA code to database
                addDsaCodeToDatabase();
            }
        });
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray vendorBanksArray = jsonResponse.getJSONArray("data");
                        List<String> newVendorBanks = new ArrayList<>();
                        
                        for (int i = 0; i < vendorBanksArray.length(); i++) {
                            JSONObject vendorBank = vendorBanksArray.getJSONObject(i);
                            newVendorBanks.add(vendorBank.getString("vendor_bank_name"));
                        }
                        
                        runOnUiThread(() -> {
                            vendorBanks.clear();
                            vendorBanks.addAll(newVendorBanks);
                            
                            // Setup vendor bank dropdown
                            ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                                AddDsaActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                vendorBanks
                            );
        binding.vendorBankDropdown.setAdapter(vendorBankAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.optString("message", "Unknown error"));
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

    private void loadDsaNamesForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/fetch_dsa_names.php";
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dsaNamesArray = jsonResponse.getJSONArray("data");
                        List<String> newDsaNames = new ArrayList<>();
                        
                        for (int i = 0; i < dsaNamesArray.length(); i++) {
                            JSONObject dsaName = dsaNamesArray.getJSONObject(i);
                            newDsaNames.add(dsaName.getString("bsa_name"));
                        }
                        
                        runOnUiThread(() -> {
                            dsaNames.clear();
                            dsaNames.addAll(newDsaNames);
                            
                            // Setup DSA name dropdown
                            ArrayAdapter<String> dsaNameAdapter = new ArrayAdapter<>(
                                AddDsaActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                dsaNames
                            );
        binding.dsaNameDropdown.setAdapter(dsaNameAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.optString("message", "Unknown error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load DSA names", e);
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray loanTypesArray = jsonResponse.getJSONArray("data");
                        List<String> newLoanTypes = new ArrayList<>();
                        
                        for (int i = 0; i < loanTypesArray.length(); i++) {
                            JSONObject loanType = loanTypesArray.getJSONObject(i);
                            newLoanTypes.add(loanType.getString("loan_type"));
                        }
                        
                        runOnUiThread(() -> {
                            loanTypes.clear();
                            loanTypes.addAll(newLoanTypes);
                            
                            // Setup loan types dropdown
                            ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                                AddDsaActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                loanTypes
                            );
        binding.typeOfLoansDropdown.setAdapter(loanTypeAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.optString("message", "Unknown error"));
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray branchStatesArray = jsonResponse.getJSONArray("data");
                        List<String> newBranchStates = new ArrayList<>();
                        
                        for (int i = 0; i < branchStatesArray.length(); i++) {
                            JSONObject branchState = branchStatesArray.getJSONObject(i);
                            newBranchStates.add(branchState.getString("branch_state_name"));
                        }
                        
                        runOnUiThread(() -> {
                            branchStates.clear();
                            branchStates.addAll(newBranchStates);
                            
                            // Setup branch state dropdown
                            ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(
                                AddDsaActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                branchStates
                            );
                            binding.branchStateDropdown.setAdapter(branchStateAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.optString("message", "Unknown error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load branch states", e);
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray branchLocationsArray = jsonResponse.getJSONArray("data");
                        List<String> newBranchLocations = new ArrayList<>();
                        
                        for (int i = 0; i < branchLocationsArray.length(); i++) {
                            JSONObject branchLocation = branchLocationsArray.getJSONObject(i);
                            newBranchLocations.add(branchLocation.getString("branch_location"));
                        }
                        
                        runOnUiThread(() -> {
                            branchLocations.clear();
                            branchLocations.addAll(newBranchLocations);
                            
                            // Setup branch location dropdown
                            ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(
                                AddDsaActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                branchLocations
                            );
                            binding.branchLocationDropdown.setAdapter(branchLocationAdapter);
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.optString("message", "Unknown error"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load branch locations", e);
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

    private void addDsaCodeToDatabase() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_dsa_code.php";
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("vendor_bank", binding.vendorBankDropdown.getText().toString());
                jsonData.put("dsa_code", binding.dsaCodeInput.getText().toString());
                jsonData.put("bsa_name", binding.dsaNameDropdown.getText().toString());
                jsonData.put("loan_type", binding.typeOfLoansDropdown.getText().toString());
                jsonData.put("state", binding.branchStateDropdown.getText().toString());
                jsonData.put("location", binding.branchLocationDropdown.getText().toString());
                
                // Send data
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddDsaActivity.this, 
                                "DSA code added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("error");
                        runOnUiThread(() -> {
                            Toast.makeText(AddDsaActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AddDsaActivity.this, 
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add DSA code", e);
                runOnUiThread(() -> {
                    Toast.makeText(AddDsaActivity.this, 
                        "Failed to add DSA code: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Vendor Bank
        if (binding.vendorBankDropdown.getText().toString().isEmpty()) {
            binding.vendorBankDropdown.setError("Please select a vendor bank");
            isValid = false;
        }

        // Validate DSA Code
        if (binding.dsaCodeInput.getText().toString().isEmpty()) {
            binding.dsaCodeInput.setError("Please enter DSA code");
            isValid = false;
        }

        // Validate DSA Name
        if (binding.dsaNameDropdown.getText().toString().isEmpty()) {
            binding.dsaNameDropdown.setError("Please select a DSA name");
            isValid = false;
        }

        // Validate Type of Loans
        if (binding.typeOfLoansDropdown.getText().toString().isEmpty()) {
            binding.typeOfLoansDropdown.setError("Please select type of loans");
            isValid = false;
        }

        // Validate Branch State
        if (binding.branchStateDropdown.getText().toString().isEmpty()) {
            binding.branchStateDropdown.setError("Please select a state");
            isValid = false;
        }

        // Validate Branch Location
        if (binding.branchLocationDropdown.getText().toString().isEmpty()) {
            binding.branchLocationDropdown.setError("Please select a location");
            isValid = false;
        }

        return isValid;
    }
} 

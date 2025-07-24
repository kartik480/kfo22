package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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

public class DirectorAddBankerActivity extends AppCompatActivity {
    private static final String TAG = "DirectorAddBankerActivity";
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
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_add_banker);

        executor = Executors.newSingleThreadExecutor();

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

        loadAllDropdownData();

        backButton.setOnClickListener(v -> finish());
        uploadButton.setOnClickListener(v -> Toast.makeText(this, "File picker will be implemented", Toast.LENGTH_SHORT).show());
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitBankerData();
            }
        });
    }

    private void loadAllDropdownData() {
        loadVendorBankData();
        loadBankerDesignationData();
        loadLoanTypeData();
        loadBranchStateData();
        loadBranchLocationData();
    }

    private void loadVendorBankData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_vendor_bank_list.php";
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> vendorBankNames = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String vendorBankName = item.getString("vendor_bank_name");
                            vendorBankNames.add(vendorBankName);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                                DirectorAddBankerActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                vendorBankNames
                            );
                            vendorBankDropdown.setAdapter(vendorBankAdapter);
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to load vendor bank data"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from get_vendor_bank_list.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to load vendor bank data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
            }
        });
    }

    private void loadBankerDesignationData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_banker_designation_list.php";
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> designationNames = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String designationName = item.getString("designation_name");
                            designationNames.add(designationName);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(
                                DirectorAddBankerActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                designationNames
                            );
                            designationDropdown.setAdapter(designationAdapter);
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to load banker designation data"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from get_banker_designation_list.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to load banker designation data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
            }
        });
    }

    private void loadLoanTypeData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_loan_type_list.php";
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> loanTypes = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String loanType = item.getString("loan_type");
                            loanTypes.add(loanType);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                                DirectorAddBankerActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                loanTypes
                            );
                            loanTypeDropdown.setAdapter(loanTypeAdapter);
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to load loan type data"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from get_loan_type_list.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to load loan type data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
            }
        });
    }

    private void loadBranchStateData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_banker_branch_states.php";
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
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> branchStates = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            String branchStateName = dataArray.getString(i);
                            branchStates.add(branchStateName);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                                DirectorAddBankerActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                branchStates
                            );
                            stateDropdown.setAdapter(stateAdapter);
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to load branch state data"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from fetch_banker_branch_states.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to load branch state data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
            }
        });
    }

    private void loadBranchLocationData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_banker_branch_locations.php";
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
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> branchLocations = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            String branchLocation = dataArray.getString(i);
                            branchLocations.add(branchLocation);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                                DirectorAddBankerActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                branchLocations
                            );
                            locationDropdown.setAdapter(locationAdapter);
                            Toast.makeText(DirectorAddBankerActivity.this, "All dropdown data loaded successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to load branch location data"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from fetch_banker_branch_locations.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to load branch location data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void submitBankerData() {
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("vendor_bank", vendorBankDropdown.getText().toString().trim());
                jsonData.put("banker_name", bankerNameInput.getText().toString().trim());
                jsonData.put("phone_number", phoneInput.getText().toString().trim());
                jsonData.put("email_id", emailInput.getText().toString().trim());
                jsonData.put("banker_designation", designationDropdown.getText().toString().trim());
                jsonData.put("loan_type", loanTypeDropdown.getText().toString().trim());
                jsonData.put("state", stateDropdown.getText().toString().trim());
                jsonData.put("location", locationDropdown.getText().toString().trim());
                jsonData.put("visiting_card", "");
                jsonData.put("address", addressInput.getText().toString().trim());
                String urlString = "https://emp.kfinone.com/mobile/api/directorbankeradd.php";
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(DirectorAddBankerActivity.this, "Banker added successfully!", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to add banker"));
                    }
                } else {
                    runOnUiThread(() -> showError("HTTP error from directorbankeradd.php: " + responseCode));
                }
            } catch (Exception e) {
                runOnUiThread(() -> showError("Failed to submit banker data: " + e.getMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (connection != null) { connection.disconnect(); }
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                });
            }
        });
    }
} 
package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityAddEmpDetailsBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;

public class AddEmpDetailsActivity extends AppCompatActivity {
    private ActivityAddEmpDetailsBinding binding;
    private ExecutorService executorService;
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEmpDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService = Executors.newFixedThreadPool(2);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Load dropdown data
        loadBranchStateDropdown();
        loadBranchLocationDropdown();
        loadDepartmentDropdown();
        loadDesignationDropdown();
        loadBankDropdown();
        loadAccountTypeDropdown();
        loadReportingToDropdown();

        // Setup submit button
        binding.submitButton.setOnClickListener(v -> submitEmployeeDetails());
    }

    private void loadBranchStateDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_branch_state_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> branchStates = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject stateObj = data.getJSONObject(i);
                            branchStates.add(stateObj.getString("branch_state_name"));
                        }

                        // Update UI on main thread
                        runOnUiThread(() -> {
                            AutoCompleteTextView branchStateInput = findViewById(R.id.branchStateInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                branchStates
                            );
                            branchStateInput.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load branch states", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading branch states: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadBranchLocationDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_branch_location_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> branchLocations = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject locationObj = data.getJSONObject(i);
                            branchLocations.add(locationObj.getString("branch_location"));
                        }
                        runOnUiThread(() -> {
                            AutoCompleteTextView branchLocationInput = findViewById(R.id.branchLocationInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                branchLocations
                            );
                            branchLocationInput.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load branch locations", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading branch locations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadDepartmentDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_department_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> departments = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject deptObj = data.getJSONObject(i);
                            departments.add(deptObj.getString("department_name"));
                        }
                        runOnUiThread(() -> {
                            AutoCompleteTextView departmentInput = findViewById(R.id.departmentInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                departments
                            );
                            departmentInput.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load departments", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading departments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadDesignationDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_designation_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> designations = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject desigObj = data.getJSONObject(i);
                            designations.add(desigObj.getString("designation_name"));
                        }
                        runOnUiThread(() -> {
                            AutoCompleteTextView designationInput = findViewById(R.id.designationInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                designations
                            );
                            designationInput.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load designations", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading designations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadBankDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_bank_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> banks = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            banks.add(data.getString(i));
                        }
                        runOnUiThread(() -> {
                            AutoCompleteTextView bankNameInput = findViewById(R.id.bankNameInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                banks
                            );
                            bankNameInput.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load banks", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading banks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadAccountTypeDropdown() {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "get_account_type_list.php");
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> accountTypes = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject accountType = data.getJSONObject(i);
                            accountTypes.add(accountType.getString("account_type"));
                        }
                        runOnUiThread(() -> {
                            AutoCompleteTextView accountTypeInput = findViewById(R.id.accountTypeInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                accountTypes
                            );
                            accountTypeInput.setAdapter(adapter);
                        });
                    } else {
                        String errorMessage = jsonResponse.optString("error", "Unknown error");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to load account types: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading account types: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadReportingToDropdown() {
        executorService.execute(() -> {
            try {
                // Fetch reporting users from tbl_user table
                URL url = new URL(BASE_URL + "get_reporting_users_list.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000); // Increased timeout
                connection.setReadTimeout(10000);

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
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<String> users = new ArrayList<>();
                        
                        // Add a default "Select Reporting To" option
                        users.add("Select Reporting To");
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            String fullName = user.getString("full_name");
                            if (fullName != null && !fullName.trim().isEmpty()) {
                                users.add(fullName.trim());
                            }
                        }
                        
                        runOnUiThread(() -> {
                            AutoCompleteTextView reportingToInput = findViewById(R.id.reportingToInput);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                users
                            );
                            reportingToInput.setAdapter(adapter);
                            reportingToInput.setText("Select Reporting To", false);
                            
                            // Show success message if users were loaded
                            if (users.size() > 1) { // More than just the default option
                                Toast.makeText(this, "Loaded " + (users.size() - 1) + " reporting users", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No active users found for reporting", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        String errorMessage = jsonResponse.optString("message", "Unknown error");
                        runOnUiThread(() -> {
                            AutoCompleteTextView reportingToInput = findViewById(R.id.reportingToInput);
                            reportingToInput.setText("Error loading users");
                            reportingToInput.setEnabled(false);
                            Toast.makeText(this, "Failed to load reporting users: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        AutoCompleteTextView reportingToInput = findViewById(R.id.reportingToInput);
                        reportingToInput.setText("Network error");
                        reportingToInput.setEnabled(false);
                        Toast.makeText(this, "Network error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    AutoCompleteTextView reportingToInput = findViewById(R.id.reportingToInput);
                    reportingToInput.setText("Error loading users");
                    reportingToInput.setEnabled(false);
                    Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void submitEmployeeDetails() {
        // Get all form data
        String firstName = binding.firstNameInput.getText().toString().trim();
        String lastName = binding.lastNameInput.getText().toString().trim();
        String employeeId = binding.employeeIdInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String personalPhone = binding.personalPhoneInput.getText().toString().trim();
        String personalEmail = binding.personalEmailInput.getText().toString().trim();
        String officialPhone = binding.officialPhoneInput.getText().toString().trim();
        String officialEmail = binding.officialEmailInput.getText().toString().trim();
        String branchState = binding.branchStateInput.getText().toString().trim();
        String branchLocation = binding.branchLocationInput.getText().toString().trim();
        String department = binding.departmentInput.getText().toString().trim();
        String designation = binding.designationInput.getText().toString().trim();
        String aadhaarNumber = binding.aadhaarNumberInput.getText().toString().trim();
        String panNumber = binding.panNumberInput.getText().toString().trim();
        String accountNumber = binding.accountNumberInput.getText().toString().trim();
        String ifscCode = binding.ifscCodeInput.getText().toString().trim();
        String bankName = binding.bankNameInput.getText().toString().trim();
        String accountType = binding.accountTypeInput.getText().toString().trim();
        String presentAddress = binding.presentAddressInput.getText().toString().trim();
        String permanentAddress = binding.permanentAddressInput.getText().toString().trim();
        String reportingTo = binding.reportingToInput.getText().toString().trim();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || employeeId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate reporting to field
        if (reportingTo.isEmpty() || reportingTo.equals("Select Reporting To")) {
            Toast.makeText(this, "Please select a reporting user", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON data
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("firstName", firstName);
            jsonData.put("lastName", lastName);
            jsonData.put("employeeId", employeeId);
            jsonData.put("password", password);
            jsonData.put("personalPhone", personalPhone);
            jsonData.put("personalEmail", personalEmail);
            jsonData.put("officialPhone", officialPhone);
            jsonData.put("officialEmail", officialEmail);
            jsonData.put("branchState", branchState);
            jsonData.put("branchLocation", branchLocation);
            jsonData.put("department", department);
            jsonData.put("designation", designation);
            jsonData.put("aadhaarNumber", aadhaarNumber);
            jsonData.put("panNumber", panNumber);
            jsonData.put("accountNumber", accountNumber);
            jsonData.put("ifscCode", ifscCode);
            jsonData.put("bankName", bankName);
            jsonData.put("accountType", accountType);
            jsonData.put("presentAddress", presentAddress);
            jsonData.put("permanentAddress", permanentAddress);
            jsonData.put("reportingTo", reportingTo);

            // Send data to server
            Log.d("AddEmployee", "Sending data: " + jsonData.toString());
            sendEmployeeData(jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmployeeData(String jsonData) {
        executorService.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "test_add_employee_simple.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Send JSON data
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                BufferedReader reader;
                StringBuilder response = new StringBuilder();
                
                // Read response from either input stream or error stream
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseText = response.toString();
                Log.d("AddEmployee", "Response Code: " + responseCode);
                Log.d("AddEmployee", "Response: " + responseText);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseText);
                        if (jsonResponse.getString("status").equals("success")) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_LONG).show();
                                finish(); // Close the activity
                            });
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Failed to add employee: " + errorMessage, Toast.LENGTH_LONG).show();
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Invalid JSON response: " + responseText, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Server error " + responseCode + ": " + responseText, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error submitting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 

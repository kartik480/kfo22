package com.kfinone.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEmpDetailsActivity extends AppCompatActivity {
    
    // Form fields
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
    private TextInputEditText personalPhoneInput;
    private TextInputEditText officialPhoneInput;
    private TextInputEditText dateOfBirthInput;
    private AutoCompleteTextView branchStateDropdown;
    private TextInputEditText aadhaarNumberInput;
    private TextInputEditText presentAddressInput;
    private MaterialButton employeeImageUploadButton;
    private TextView employeeImageText;
    private TextInputEditText personalEmailInput;
    private TextInputEditText officialEmailInput;
    private TextInputEditText anniversaryDateInput;
    private AutoCompleteTextView branchLocationDropdown;
    private TextInputEditText panNumberInput;
    private TextInputEditText permanentAddressInput;
    private AutoCompleteTextView reportingToDropdown;
    private MaterialButton nextButton;

    private Uri employeeImageUri = null;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // User data from previous activity
    private String userId;
    private String userName;

    private final ActivityResultLauncher<String> employeeImageLauncher = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        uri -> {
            if (uri != null) {
                employeeImageUri = uri;
                employeeImageText.setText("File selected");
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emp_details);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            if (userId == null) {
                userId = intent.getStringExtra("userId");
            }
            userName = intent.getStringExtra("USERNAME");
            if (userName == null) {
                userName = intent.getStringExtra("userName");
            }
        }

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize views
        initializeViews();
        setupDatePickers();
        setupDropdowns();
        setupImageUpload();
        setupNextButton();
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        personalPhoneInput = findViewById(R.id.personalPhoneInput);
        officialPhoneInput = findViewById(R.id.officialPhoneInput);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        branchStateDropdown = findViewById(R.id.branchStateInput);
        aadhaarNumberInput = findViewById(R.id.aadhaarNumberInput);
        presentAddressInput = findViewById(R.id.presentAddressInput);
        employeeImageUploadButton = findViewById(R.id.employeeImageUploadButton);
        employeeImageText = findViewById(R.id.employeeImageText);
        personalEmailInput = findViewById(R.id.personalEmailInput);
        officialEmailInput = findViewById(R.id.officialEmailInput);
        anniversaryDateInput = findViewById(R.id.anniversaryDateInput);
        branchLocationDropdown = findViewById(R.id.branchLocationInput);
        panNumberInput = findViewById(R.id.panNumberInput);
        permanentAddressInput = findViewById(R.id.permanentAddressInput);
        reportingToDropdown = findViewById(R.id.reportingToInput);
        nextButton = findViewById(R.id.nextButton);
    }

    private void setupDatePickers() {
        DatePickerDialog.OnDateSetListener datePickerListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = dateFormatter.format(calendar.getTime());
            
            if (view.getId() == dateOfBirthInput.getId()) {
                dateOfBirthInput.setText(selectedDate);
            } else if (view.getId() == anniversaryDateInput.getId()) {
                anniversaryDateInput.setText(selectedDate);
            }
        };

        dateOfBirthInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        anniversaryDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupDropdowns() {
        fetchBranchStates();
        fetchBranchLocations();
        fetchReportingToUsers();
    }

    private void fetchBranchStates() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_branch_states_dropdown.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> states = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject stateObj = dataArray.getJSONObject(i);
                            states.add(stateObj.getString("name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, states
                            );
                            branchStateDropdown.setAdapter(adapter);
                            Log.d("AddEmpDetailsActivity", "Loaded " + states.size() + " branch states");
                        });
                    } else {
                        Log.e("AddEmpDetailsActivity", "API error: " + jsonResponse.optString("message", "Unknown error"));
                    }
                } else {
                    Log.e("AddEmpDetailsActivity", "HTTP error: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("AddEmpDetailsActivity", "Error fetching branch states", e);
            }
        });
    }

    private void fetchBranchLocations() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_branch_locations_dropdown.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> locations = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject locationObj = dataArray.getJSONObject(i);
                            locations.add(locationObj.getString("name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, locations
                            );
                            branchLocationDropdown.setAdapter(adapter);
                            Log.d("AddEmpDetailsActivity", "Loaded " + locations.size() + " branch locations");
                        });
                    } else {
                        Log.e("AddEmpDetailsActivity", "API error: " + jsonResponse.optString("message", "Unknown error"));
                    }
                } else {
                    Log.e("AddEmpDetailsActivity", "HTTP error: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("AddEmpDetailsActivity", "Error fetching branch locations", e);
            }
        });
    }

    private void fetchReportingToUsers() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_reporting_users_dropdown.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
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
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> users = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject userObj = dataArray.getJSONObject(i);
                            String userName = userObj.getString("name");
                            String userId = userObj.getString("id");
                            users.add(userName + " (ID: " + userId + ")");
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, users
                            );
                            reportingToDropdown.setAdapter(adapter);
                            Log.d("AddEmpDetailsActivity", "Loaded " + users.size() + " reporting users");
                        });
                    } else {
                        Log.e("AddEmpDetailsActivity", "Error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e("AddEmpDetailsActivity", "HTTP Error: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("AddEmpDetailsActivity", "Error fetching reporting users", e);
            }
        });
    }

    private void setupImageUpload() {
        employeeImageUploadButton.setOnClickListener(v -> {
            employeeImageLauncher.launch("image/*");
        });
    }

    private void setupNextButton() {
        nextButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitForm();
            }
        });
    }

    private boolean validateForm() {
        List<String> errors = new ArrayList<>();
        
        // Required field validations
        if (firstNameInput.getText().toString().trim().isEmpty()) {
            errors.add("First Name is required");
        }
        if (lastNameInput.getText().toString().trim().isEmpty()) {
            errors.add("Last Name is required");
        }
        if (personalPhoneInput.getText().toString().trim().isEmpty()) {
            errors.add("Personal Phone Number is required");
        } else if (personalPhoneInput.getText().toString().length() != 10) {
            errors.add("Personal Phone Number must be 10 digits");
        }
        if (officialPhoneInput.getText().toString().trim().isEmpty()) {
            errors.add("Official Phone Number is required");
        } else if (officialPhoneInput.getText().toString().length() != 10) {
            errors.add("Official Phone Number must be 10 digits");
        }
        if (personalEmailInput.getText().toString().trim().isEmpty()) {
            errors.add("Personal Email is required");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(personalEmailInput.getText().toString()).matches()) {
            errors.add("Personal Email format is invalid");
        }
        if (officialEmailInput.getText().toString().trim().isEmpty()) {
            errors.add("Official Email is required");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(officialEmailInput.getText().toString()).matches()) {
            errors.add("Official Email format is invalid");
        }
        if (branchStateDropdown.getText().toString().trim().isEmpty()) {
            errors.add("Branch State is required");
        }
        if (branchLocationDropdown.getText().toString().trim().isEmpty()) {
            errors.add("Branch Location is required");
        }
        if (aadhaarNumberInput.getText().toString().trim().isEmpty()) {
            errors.add("Aadhaar Number is required");
        } else if (aadhaarNumberInput.getText().toString().length() != 12) {
            errors.add("Aadhaar Number must be 12 digits");
        }
        if (panNumberInput.getText().toString().trim().isEmpty()) {
            errors.add("PAN Number is required");
        } else if (panNumberInput.getText().toString().length() != 10) {
            errors.add("PAN Number must be 10 characters");
        }
        if (presentAddressInput.getText().toString().trim().isEmpty()) {
            errors.add("Present Address is required");
        }
        if (permanentAddressInput.getText().toString().trim().isEmpty()) {
            errors.add("Permanent Address is required");
        }
        if (reportingToDropdown.getText().toString().trim().isEmpty()) {
            errors.add("Reporting To is required");
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join("\n", errors);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            return false;
        }
        
        return true;
    }

    private void submitForm() {
        // Show loading message
        Toast.makeText(this, "Saving employee details...", Toast.LENGTH_SHORT).show();
        
        executor.execute(() -> {
            try {
                // Prepare form data
                JSONObject formData = new JSONObject();
                formData.put("firstName", firstNameInput.getText().toString().trim());
                formData.put("lastName", lastNameInput.getText().toString().trim());
                formData.put("personalPhone", personalPhoneInput.getText().toString().trim());
                formData.put("officialPhone", officialPhoneInput.getText().toString().trim());
                formData.put("dateOfBirth", dateOfBirthInput.getText().toString().trim());
                formData.put("branchState", branchStateDropdown.getText().toString().trim());
                formData.put("aadhaarNumber", aadhaarNumberInput.getText().toString().trim());
                formData.put("presentAddress", presentAddressInput.getText().toString().trim());
                formData.put("personalEmail", personalEmailInput.getText().toString().trim());
                formData.put("officialEmail", officialEmailInput.getText().toString().trim());
                formData.put("anniversaryDate", anniversaryDateInput.getText().toString().trim());
                formData.put("branchLocation", branchLocationDropdown.getText().toString().trim());
                formData.put("panNumber", panNumberInput.getText().toString().trim());
                formData.put("permanentAddress", permanentAddressInput.getText().toString().trim());
                formData.put("reportingTo", reportingToDropdown.getText().toString().trim());
                
                // Send data to API
                URL url = new URL("https://emp.kfinone.com/mobile/api/add_employee_details.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                // Write data
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(formData.toString());
                writer.flush();
                writer.close();
                os.close();
                
                // Read response
                int responseCode = connection.getResponseCode();
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
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                
                if (jsonResponse.getString("status").equals("success")) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String username = data.getString("username");
                    String employeeNo = data.getString("employee_no");
                    String password = data.getString("password");
                    
                    runOnUiThread(() -> {
                        String successMessage = "Employee saved successfully!\n\n" +
                            "Username: " + username + "\n" +
                            "Employee No: " + employeeNo + "\n" +
                            "Password: " + password + "\n\n" +
                            "Redirecting to Active User List...";
                        
                        Toast.makeText(AddEmpDetailsActivity.this, successMessage, Toast.LENGTH_LONG).show();
                        
                        // Navigate to Active User List after a short delay
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(AddEmpDetailsActivity.this, ActiveEmpListActivity.class);
                            // Pass user data to maintain context
                            if (userId != null) intent.putExtra("USER_ID", userId);
                            if (userName != null) intent.putExtra("USERNAME", userName);
                            startActivity(intent);
                            finish(); // Close the current activity
                        }, 3000); // 3 second delay to show the success message
                    });
                } else {
                    String errorMessage = jsonResponse.optString("message", "Unknown error occurred");
                    runOnUiThread(() -> {
                        Toast.makeText(AddEmpDetailsActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e("AddEmpDetailsActivity", "Error submitting form", e);
                runOnUiThread(() -> {
                    Toast.makeText(AddEmpDetailsActivity.this, "Error saving employee: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
} 
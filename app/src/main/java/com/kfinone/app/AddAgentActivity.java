package com.kfinone.app;

import android.app.Activity;
import android.content.Intent;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAgentActivity extends AppCompatActivity {
    private static final String TAG = "AddAgentActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    private static final int PICK_FILE_REQUEST = 1;

    // UI Elements
    private EditText phoneNumberInput, fullNameInput, companyNameInput, alternativeNumberInput, emailInput, addressInput;
    private Spinner partnerTypeSpinner, branchStateSpinner, branchLocationSpinner;
    private Button chooseFileButton, submitButton;
    private TextView backButton, selectedFileText;
    private RequestQueue requestQueue;

    // File selection
    private Uri selectedFileUri;
    private String selectedFileName;

    // Activity result launcher for file picker
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);

        // Enable scrolling for the activity
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initializeViews();
        setupVolley();
        setupFilePicker();
        setupClickListeners();
        loadDropdownData();
    }

    private void initializeViews() {
        // Initialize EditText fields
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        fullNameInput = findViewById(R.id.fullNameInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        alternativeNumberInput = findViewById(R.id.alternativeNumberInput);
        emailInput = findViewById(R.id.emailInput);
        addressInput = findViewById(R.id.addressInput);

        // Initialize Spinners
        partnerTypeSpinner = findViewById(R.id.partnerTypeSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);

        // Initialize Buttons
        chooseFileButton = findViewById(R.id.chooseFileButton);
        submitButton = findViewById(R.id.submitButton);

        // Initialize TextViews
        backButton = findViewById(R.id.backButton);
        selectedFileText = findViewById(R.id.selectedFileText);
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedFileUri = data.getData();
                        selectedFileName = getFileName(selectedFileUri);
                        selectedFileText.setText(selectedFileName != null ? selectedFileName : "File selected");
                    }
                }
            }
        );
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        chooseFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });

        submitButton.setOnClickListener(v -> submitAgentData());
    }

    private void loadDropdownData() {
        loadPartnerTypes();
        loadBranchStates();
        loadBranchLocations();
    }

    private void loadPartnerTypes() {
        String url = BASE_URL + "get_partner_type_list.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> partnerTypes = new ArrayList<>();
                        partnerTypes.add("Select Partner Type");
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject typeObj = data.getJSONObject(i);
                            partnerTypes.add(typeObj.getString("name"));
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, partnerTypes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        partnerTypeSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackTypes = new ArrayList<>();
                        fallbackTypes.add("Select Partner Type");
                        fallbackTypes.add("Business");
                        fallbackTypes.add("Individual");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackTypes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        partnerTypeSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing partner types: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading partner types: " + error.getMessage());
                // Fallback data
                List<String> fallbackTypes = new ArrayList<>();
                fallbackTypes.add("Select Partner Type");
                fallbackTypes.add("Business");
                fallbackTypes.add("Individual");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                partnerTypeSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void loadBranchStates() {
        String url = BASE_URL + "get_branch_states_dropdown.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> branchStates = new ArrayList<>();
                        branchStates.add("Select Branch State");
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject stateObj = data.getJSONObject(i);
                            branchStates.add(stateObj.getString("name"));
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStates);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchStateSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackStates = new ArrayList<>();
                        fallbackStates.add("Select Branch State");
                        fallbackStates.add("Maharashtra");
                        fallbackStates.add("Delhi");
                        fallbackStates.add("Karnataka");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackStates);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchStateSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing branch states: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading branch states: " + error.getMessage());
                // Fallback data
                List<String> fallbackStates = new ArrayList<>();
                fallbackStates.add("Select Branch State");
                fallbackStates.add("Maharashtra");
                fallbackStates.add("Delhi");
                fallbackStates.add("Karnataka");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackStates);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchStateSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void loadBranchLocations() {
        String url = BASE_URL + "get_branch_locations_dropdown.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> branchLocations = new ArrayList<>();
                        branchLocations.add("Select Branch Location");
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject locationObj = data.getJSONObject(i);
                            branchLocations.add(locationObj.getString("name"));
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocations);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchLocationSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackLocations = new ArrayList<>();
                        fallbackLocations.add("Select Branch Location");
                        fallbackLocations.add("Mumbai Central");
                        fallbackLocations.add("Andheri West");
                        fallbackLocations.add("Bandra West");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackLocations);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchLocationSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing branch locations: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading branch locations: " + error.getMessage());
                // Fallback data
                List<String> fallbackLocations = new ArrayList<>();
                fallbackLocations.add("Select Branch Location");
                fallbackLocations.add("Mumbai Central");
                fallbackLocations.add("Andheri West");
                fallbackLocations.add("Bandra West");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackLocations);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchLocationSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void submitAgentData() {
        // Validate required fields
        if (!validateForm()) {
            return;
        }

        // Get form data
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String companyName = companyNameInput.getText().toString().trim();
        String alternativeNumber = alternativeNumberInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String partnerType = partnerTypeSpinner.getSelectedItem().toString();
        String branchState = branchStateSpinner.getSelectedItem().toString();
        String branchLocation = branchLocationSpinner.getSelectedItem().toString();
        String address = addressInput.getText().toString().trim();

        // Create JSON object for submission
        JSONObject agentData = new JSONObject();
        try {
            agentData.put("phone_number", phoneNumber);
            agentData.put("full_name", fullName);
            agentData.put("company_name", companyName);
            agentData.put("alternative_number", alternativeNumber);
            agentData.put("email", email);
            agentData.put("partner_type", partnerType);
            agentData.put("branch_state", branchState);
            agentData.put("branch_location", branchLocation);
            agentData.put("address", address);
            
            // Add file data if selected
            if (selectedFileUri != null) {
                byte[] fileBytes = getFileBytes(selectedFileUri);
                if (fileBytes != null) {
                    String base64File = android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                    agentData.put("visiting_card", base64File);
                    agentData.put("file_name", selectedFileName);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON: " + e.getMessage());
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Submit to API
        String url = BASE_URL + "director_add_agent.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, agentData,
            response -> {
                try {
                    String status = response.getString("status");
                    String message = response.getString("message");
                    
                    if ("success".equals(status)) {
                        Toast.makeText(this, "Agent added successfully!", Toast.LENGTH_LONG).show();
                        finish(); // Close activity
                    } else {
                        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing response: " + e.getMessage());
                    Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Error submitting agent data: " + error.getMessage());
                Toast.makeText(this, "Error submitting data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        
        requestQueue.add(request);
    }

    private boolean validateForm() {
        if (phoneNumberInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (fullNameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (companyNameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (emailInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (partnerTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select partner type", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (branchStateSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select branch state", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (branchLocationSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select branch location", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (addressInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME));
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

    private byte[] getFileBytes(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + e.getMessage());
            return null;
        }
    }
} 

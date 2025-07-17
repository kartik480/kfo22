package com.kfinone.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddBankerPanelActivity extends AppCompatActivity {
    private static final String TAG = "AddBankerPanelActivity";
    
    private Spinner vendorBankSpinner;
    private TextInputEditText bankerNameInput;
    private TextInputEditText phoneNumberInput;
    private TextInputEditText emailInput;
    private Spinner bankerDesignationSpinner;
    private Spinner loanTypeSpinner;
    private Spinner branchStateSpinner;
    private Spinner branchLocationSpinner;
    private TextInputEditText fileNameDisplay;
    private Button chooseFileButton;
    private TextInputEditText addressInput;
    private Button submitButton;
    private TextView backButton;
    
    private List<String> vendorBanks;
    private List<String> bankerDesignations;
    private List<String> loanTypes;
    private List<String> branchStates;
    private List<String> branchLocations;
    private ExecutorService executor;
    
    private Uri selectedFileUri;
    private String selectedFileName;
    
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    selectedFileUri = data.getData();
                    if (selectedFileUri != null) {
                        selectedFileName = getFileName(selectedFileUri);
                        fileNameDisplay.setText(selectedFileName);
                    }
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banker_panel);
        
        // Initialize lists and executor
        vendorBanks = new ArrayList<>();
        bankerDesignations = new ArrayList<>();
        loanTypes = new ArrayList<>();
        branchStates = new ArrayList<>();
        branchLocations = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
        
        initializeViews();
        loadDropdownOptions();
        setupClickListeners();
    }
    
    private void initializeViews() {
        vendorBankSpinner = findViewById(R.id.vendorBankSpinner);
        bankerNameInput = findViewById(R.id.bankerNameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        emailInput = findViewById(R.id.emailInput);
        bankerDesignationSpinner = findViewById(R.id.bankerDesignationSpinner);
        loanTypeSpinner = findViewById(R.id.loanTypeSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        fileNameDisplay = findViewById(R.id.fileNameDisplay);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        addressInput = findViewById(R.id.addressInput);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        chooseFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });
        
        fileNameDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });
        
        submitButton.setOnClickListener(v -> submitBankerData());
    }
    
    private void loadDropdownOptions() {
        loadVendorBanks();
        loadBankerDesignations();
        loadLoanTypes();
        loadBranchStates();
        loadBranchLocations();
    }
    
    private void loadVendorBanks() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_vendor_bank_list.php";
                String response = makeHttpRequest(url, "{}");
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        vendorBanks.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            vendorBanks.add(item.getString("vendor_bank_name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, vendorBanks);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            vendorBankSpinner.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading vendor banks", e);
                runOnUiThread(() -> {
                    vendorBanks.clear();
                    vendorBanks.add("HDFC Bank");
                    vendorBanks.add("ICICI Bank");
                    vendorBanks.add("SBI Bank");
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, vendorBanks);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vendorBankSpinner.setAdapter(adapter);
                });
            }
        });
    }
    
    private void loadBankerDesignations() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_banker_designation_list.php";
                String response = makeHttpRequest(url, "{}");
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        bankerDesignations.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            bankerDesignations.add(item.getString("designation_name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, bankerDesignations);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            bankerDesignationSpinner.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading banker designations", e);
                runOnUiThread(() -> {
                    bankerDesignations.clear();
                    bankerDesignations.add("Manager");
                    bankerDesignations.add("Assistant Manager");
                    bankerDesignations.add("Senior Executive");
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, bankerDesignations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bankerDesignationSpinner.setAdapter(adapter);
                });
            }
        });
    }
    
    private void loadLoanTypes() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_loan_type_list.php";
                String response = makeHttpRequest(url, "{}");
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        loanTypes.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            loanTypes.add(item.getString("loan_type_name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, loanTypes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            loanTypeSpinner.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading loan types", e);
                runOnUiThread(() -> {
                    loanTypes.clear();
                    loanTypes.add("Personal Loan");
                    loanTypes.add("Home Loan");
                    loanTypes.add("Business Loan");
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, loanTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    loanTypeSpinner.setAdapter(adapter);
                });
            }
        });
    }
    
    private void loadBranchStates() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_states_for_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        branchStates.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            branchStates.add(item.getString("state_name"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, branchStates);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            branchStateSpinner.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading branch states", e);
                runOnUiThread(() -> {
                    branchStates.clear();
                    branchStates.add("Maharashtra");
                    branchStates.add("Delhi");
                    branchStates.add("Karnataka");
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, branchStates);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchStateSpinner.setAdapter(adapter);
                });
            }
        });
    }
    
    private void loadBranchLocations() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_branch_locations_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        branchLocations.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            branchLocations.add(item.getString("branch_location"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, branchLocations);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            branchLocationSpinner.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading branch locations", e);
                runOnUiThread(() -> {
                    branchLocations.clear();
                    branchLocations.add("Mumbai");
                    branchLocations.add("Pune");
                    branchLocations.add("Delhi");
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, branchLocations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    branchLocationSpinner.setAdapter(adapter);
                });
            }
        });
    }
    
    private void submitBankerData() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }
        
        executor.execute(() -> {
            try {
                String url = BASE_URL + "add_banker.php";
                
                // Build multipart request
                MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("vendor_bank", vendorBankSpinner.getSelectedItem().toString())
                    .addFormDataPart("banker_name", bankerNameInput.getText().toString())
                    .addFormDataPart("phone_number", phoneNumberInput.getText().toString())
                    .addFormDataPart("email", emailInput.getText().toString())
                    .addFormDataPart("banker_designation", bankerDesignationSpinner.getSelectedItem().toString())
                    .addFormDataPart("loan_type", loanTypeSpinner.getSelectedItem().toString())
                    .addFormDataPart("branch_state", branchStateSpinner.getSelectedItem().toString())
                    .addFormDataPart("branch_location", branchLocationSpinner.getSelectedItem().toString())
                    .addFormDataPart("address", addressInput.getText().toString());
                
                // Add file if selected
                if (selectedFileUri != null) {
                    try {
                        byte[] fileBytes = getFileBytes(selectedFileUri);
                        builder.addFormDataPart("visiting_card", selectedFileName,
                            RequestBody.create(fileBytes, MediaType.parse("application/octet-stream")));
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading file", e);
                    }
                }
                
                RequestBody requestBody = builder.build();
                
                Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
                
                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    runOnUiThread(() -> {
                        try {
                            if (jsonResponse.getBoolean("success")) {
                                Toast.makeText(this, "Banker added successfully!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("error");
                                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response", e);
                            Toast.makeText(this, "Error parsing server response", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error submitting banker data", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error submitting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private boolean validateFields() {
        if (bankerNameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter banker name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (phoneNumberInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (emailInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
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
                    int columnIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex);
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
    
    private byte[] getFileBytes(Uri uri) throws IOException {
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[4096];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            }
        }
        return new byte[0];
    }
    
    private String makeHttpRequest(String url, String jsonBody) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "HTTP request failed", e);
            return null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 
package com.kfinone.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class DirectorAddPartnerActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, firstNameInput, lastNameInput, aliasNameInput, phoneInput, altPhoneInput, companyNameInput, officeAddressInput, residentialAddressInput, aadhaarInput, panInput, accountNumberInput, ifscInput;
    private Spinner partnerTypeDropdown, branchStateDropdown, branchLocationDropdown, bankNameDropdown, accountTypeDropdown;
    private Button panUploadButton, aadhaarUploadButton, photoUploadButton, bankProofUploadButton, submitButton;
    private Uri panUri, aadhaarUri, photoUri, bankProofUri;
    private static final int PICK_PAN = 1, PICK_AADHAAR = 2, PICK_PHOTO = 3, PICK_BANKPROOF = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_add_partner);
        initializeViews();
        fetchDropdownOptions();
        setupFilePickers();
        submitButton.setOnClickListener(v -> submitForm());
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        altPhoneInput = findViewById(R.id.altPhoneInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        aadhaarInput = findViewById(R.id.aadhaarInput);
        panInput = findViewById(R.id.panInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        ifscInput = findViewById(R.id.ifscInput);
        partnerTypeDropdown = findViewById(R.id.partnerTypeDropdown);
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        branchLocationDropdown = findViewById(R.id.branchLocationDropdown);
        bankNameDropdown = findViewById(R.id.bankNameDropdown);
        accountTypeDropdown = findViewById(R.id.accountTypeDropdown);
        panUploadButton = findViewById(R.id.panUploadButton);
        aadhaarUploadButton = findViewById(R.id.aadhaarUploadButton);
        photoUploadButton = findViewById(R.id.photoUploadButton);
        bankProofUploadButton = findViewById(R.id.bankProofUploadButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void fetchDropdownOptions() {
        fetchBankNames();
        // TODO: fetch other dropdowns (partner type, branch state, etc.)
    }

    private void fetchBankNames() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_bank_dropdown.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();
                android.util.Log.d("DirectorAddPartner", "Bank API HTTP response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    String responseStr = response.toString();
                    android.util.Log.d("DirectorAddPartner", "Bank API response: " + responseStr);
                    if (TextUtils.isEmpty(responseStr) || !responseStr.trim().startsWith("{")) {
                        android.util.Log.e("DirectorAddPartner", "Bank API response is not valid JSON: " + responseStr);
                        runOnUiThread(() -> Toast.makeText(DirectorAddPartnerActivity.this, "Bank API error: Invalid response", Toast.LENGTH_LONG).show());
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(responseStr);
                        JSONArray data = json.optJSONArray("data");
                        if (data != null) {
                            List<String> bankNames = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject bank = data.getJSONObject(i);
                                bankNames.add(bank.getString("bank_name"));
                            }
                            new Handler(Looper.getMainLooper()).post(() -> {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    DirectorAddPartnerActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    bankNames
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                bankNameDropdown.setAdapter(adapter);
                            });
                        } else {
                            android.util.Log.e("DirectorAddPartner", "Bank API JSON missing 'data': " + responseStr);
                            runOnUiThread(() -> Toast.makeText(DirectorAddPartnerActivity.this, "Bank API error: No data", Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        android.util.Log.e("DirectorAddPartner", "Exception parsing bank JSON: " + e.getMessage(), e);
                        runOnUiThread(() -> Toast.makeText(DirectorAddPartnerActivity.this, "Bank API error: Parse error", Toast.LENGTH_LONG).show());
                    }
                } else {
                    android.util.Log.e("DirectorAddPartner", "Bank API HTTP error: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(DirectorAddPartnerActivity.this, "Bank API error: HTTP " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                android.util.Log.e("DirectorAddPartner", "Exception fetching banks: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(DirectorAddPartnerActivity.this, "Bank API error: Exception", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void setupFilePickers() {
        panUploadButton.setOnClickListener(v -> pickFile(PICK_PAN));
        aadhaarUploadButton.setOnClickListener(v -> pickFile(PICK_AADHAAR));
        photoUploadButton.setOnClickListener(v -> pickFile(PICK_PHOTO));
        bankProofUploadButton.setOnClickListener(v -> pickFile(PICK_BANKPROOF));
    }

    private void pickFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            switch (requestCode) {
                case PICK_PAN: panUri = uri; break;
                case PICK_AADHAAR: aadhaarUri = uri; break;
                case PICK_PHOTO: photoUri = uri; break;
                case PICK_BANKPROOF: bankProofUri = uri; break;
            }
        }
    }

    private void submitForm() {
        // Collect all field values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String aliasName = aliasNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String altPhone = altPhoneInput.getText().toString().trim();
        String companyName = companyNameInput.getText().toString().trim();
        String officeAddress = officeAddressInput.getText().toString().trim();
        String residentialAddress = residentialAddressInput.getText().toString().trim();
        String aadhaarNumber = aadhaarInput.getText().toString().trim();
        String panNumber = panInput.getText().toString().trim();
        String accountNumber = accountNumberInput.getText().toString().trim();
        String ifscCode = ifscInput.getText().toString().trim();
        String partnerType = partnerTypeDropdown.getSelectedItem() != null ? partnerTypeDropdown.getSelectedItem().toString() : "";
        String branchState = branchStateDropdown.getSelectedItem() != null ? branchStateDropdown.getSelectedItem().toString() : "";
        String branchLocation = branchLocationDropdown.getSelectedItem() != null ? branchLocationDropdown.getSelectedItem().toString() : "";
        String bankName = bankNameDropdown.getSelectedItem() != null ? bankNameDropdown.getSelectedItem().toString() : "";
        String accountType = accountTypeDropdown.getSelectedItem() != null ? accountTypeDropdown.getSelectedItem().toString() : "";
        // Get user_id from Intent
        final String userId = getIntent().getStringExtra("USER_ID") != null ? getIntent().getStringExtra("USER_ID") : "";
        // Timestamps
        final String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);

        // Prepare multipart request
        new Thread(() -> {
            try {
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                String LINE_FEED = "\r\n";
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_add_partner.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setDoOutput(true);
                java.io.DataOutputStream request = new java.io.DataOutputStream(conn.getOutputStream());

                // Helper to write form fields
                java.util.Map<String, String> fields = new java.util.HashMap<>();
                fields.put("username", email); // using email as username
                fields.put("alias_name", aliasName);
                fields.put("first_name", firstName);
                fields.put("last_name", lastName);
                fields.put("password", password);
                fields.put("Phone_number", phone);
                fields.put("email_id", email);
                fields.put("alternative_mobile_number", altPhone);
                fields.put("company_name", companyName);
                fields.put("branch_state_name_id", branchState);
                fields.put("branch_location_id", branchLocation);
                fields.put("bank_id", bankName);
                fields.put("account_type_id", accountType);
                fields.put("office_address", officeAddress);
                fields.put("residential_address", residentialAddress);
                fields.put("aadhaar_number", aadhaarNumber);
                fields.put("pan_number", panNumber);
                fields.put("account_number", accountNumber);
                fields.put("ifsc_code", ifscCode);
                fields.put("rank", "");
                fields.put("status", "Active");
                fields.put("reportingTo", "");
                fields.put("employee_no", "");
                fields.put("department", "");
                fields.put("designation", "");
                fields.put("branchstate", branchState);
                fields.put("branchloaction", branchLocation);
                fields.put("bank_name", bankName);
                fields.put("account_type", accountType);
                fields.put("partner_type_id", partnerType);
                fields.put("user_id", userId);
                fields.put("created_at", timestamp);
                fields.put("createdBy", userId);
                fields.put("updated_at", timestamp);
                // Write all fields
                for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
                    request.writeBytes("--" + boundary + LINE_FEED);
                    request.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_FEED);
                    request.writeBytes(LINE_FEED);
                    request.writeBytes(entry.getValue() != null ? entry.getValue() : "");
                    request.writeBytes(LINE_FEED);
                }
                // Helper to write file
                writeFilePart(request, boundary, "pan_img", panUri);
                writeFilePart(request, boundary, "aadhaar_img", aadhaarUri);
                writeFilePart(request, boundary, "photo_img", photoUri);
                writeFilePart(request, boundary, "bankproof_img", bankProofUri);
                // End boundary
                request.writeBytes("--" + boundary + "--" + LINE_FEED);
                request.flush();
                request.close();
                int responseCode = conn.getResponseCode();
                java.io.InputStream is = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                org.json.JSONObject json = new org.json.JSONObject(response.toString());
                runOnUiThread(() -> {
                    if (json.optString("status").equals("success")) {
                        Toast.makeText(this, "Partner added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, DirectorMyPartnerActivity.class);
                        intent.putExtra("SHOW_LIST", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Helper method to write a file part to the multipart request
    private void writeFilePart(java.io.DataOutputStream request, String boundary, String fieldName, Uri fileUri) {
        try {
            if (fileUri == null) return;
            String LINE_FEED = "\r\n";
            String fileName = getFileNameFromUri(fileUri);
            request.writeBytes("--" + boundary + LINE_FEED);
            request.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + LINE_FEED);
            request.writeBytes("Content-Type: image/jpeg" + LINE_FEED);
            request.writeBytes(LINE_FEED);
            java.io.InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                request.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            request.writeBytes(LINE_FEED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to get file name from Uri
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result != null ? result : "file.jpg";
    }
} 
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
        // TODO: Collect all field values, validate, and send as multipart/form-data POST to director_add_partner.php
        // On success, show a message and finish or redirect to My Partner panel
        // --- BEGIN DEMO SUCCESS HANDLING ---
        // Replace this block with actual API call and success check
        boolean success = true; // Replace with real result
        if (success) {
            Toast.makeText(this, "Partner added successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DirectorMyPartnerActivity.class);
            intent.putExtra("SHOW_LIST", true);
            startActivity(intent);
            finish();
        }
        // --- END DEMO SUCCESS HANDLING ---
    }
} 
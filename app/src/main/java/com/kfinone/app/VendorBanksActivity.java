package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class VendorBanksActivity extends AppCompatActivity {
    private static final String TAG = "VendorBanksActivity";
    private TextInputEditText vendorBankNameInput;
    private MaterialButton submitButton;
    private LinearLayout vendorBankTableLayout;
    private List<VendorBankItem> vendorBanksList;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_banks);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        initializeViews();
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());

        // Load vendor bank data from database
        loadVendorBankData();
    }

    private void initializeViews() {
        vendorBankNameInput = findViewById(R.id.vendorBankNameInput);
        submitButton = findViewById(R.id.submitButton);
        vendorBankTableLayout = findViewById(R.id.vendorBankTableLayout);
        vendorBanksList = new ArrayList<>();
    }

    private void loadVendorBankData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_vendor_bank_list.php";
                Log.d(TAG, "Loading vendor bank data from: " + urlString);
                
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
                    Log.d(TAG, "Raw response for vendor bank data: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_vendor_bank_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_vendor_bank_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<VendorBankItem> vendorBankItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            VendorBankItem vendorBankItem = new VendorBankItem(
                                item.getInt("id"),
                                item.getString("vendor_bank_name")
                            );
                            vendorBankItems.add(vendorBankItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateVendorBankTable(vendorBankItems);
                            showError("Loaded " + vendorBankItems.size() + " vendor banks from database");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_vendor_bank_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load vendor bank data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_vendor_bank_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_vendor_bank_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load vendor bank data", e);
                runOnUiThread(() -> showError("Failed to load vendor bank data: " + e.getMessage()));
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

    private void populateVendorBankTable(List<VendorBankItem> vendorBankItems) {
        // Remove existing data rows (keep header)
        int childCount = vendorBankTableLayout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            vendorBankTableLayout.removeViewAt(i);
        }
        
        if (vendorBankItems.isEmpty()) {
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
            noDataText.setText("No vendor banks found");
            noDataText.setTextSize(14);
            noDataText.setTextColor(getResources().getColor(R.color.red));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(16, 16, 16, 16);
            
            noDataRow.addView(noDataText);
            vendorBankTableLayout.addView(noDataRow);
            return;
        }
        
        // Add data rows
        for (VendorBankItem item : vendorBankItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Vendor Bank Name
            TextView vendorBankNameText = createTableCell(item.getVendorBankName());
            row.addView(vendorBankNameText);
            
            // Status
            TextView statusText = createTableCell("Active");
            row.addView(statusText);
            
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
            
            vendorBankTableLayout.addView(row);
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
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        return textView;
    }

    private void handleSubmit() {
        String vendorBankName = vendorBankNameInput.getText().toString().trim();
        if (!vendorBankName.isEmpty()) {
            // Send vendor bank data to database
            addVendorBankToDatabase(vendorBankName);
        } else {
            showError("Please enter a vendor bank name");
        }
    }

    private void addVendorBankToDatabase(String vendorBankName) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/add_vendor_bank.php";
                Log.d(TAG, "Adding vendor bank to database: " + vendorBankName);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("vendor_bank_name", vendorBankName);
                
                // Send data
                String jsonString = jsonData.toString();
                connection.getOutputStream().write(jsonString.getBytes("UTF-8"));
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Raw response for adding vendor bank: " + responseString);
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
            vendorBankNameInput.setText("");
                            showError("Vendor bank added successfully: " + vendorBankName);
                            // Reload the vendor bank list
                            loadVendorBankData();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "add_vendor_bank.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to add vendor bank: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from add_vendor_bank.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from add_vendor_bank.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add vendor bank", e);
                runOnUiThread(() -> showError("Failed to add vendor bank: " + e.getMessage()));
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    // Data class for Vendor Bank items
    private static class VendorBankItem {
        private int id;
        private String vendorBankName;

        public VendorBankItem(int id, String vendorBankName) {
            this.id = id;
            this.vendorBankName = vendorBankName;
        }

        public int getId() { return id; }
        public String getVendorBankName() { return vendorBankName; }
    }
} 

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

public class BankersDesignationActivity extends AppCompatActivity {
    private static final String TAG = "BankersDesignationActivity";
    private TextInputEditText designationInput;
    private MaterialButton submitButton;
    private LinearLayout designationTableLayout;
    private List<BankersDesignationItem> designationsList;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankers_designation);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        initializeViews();
        
        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up submit button
        submitButton.setOnClickListener(v -> handleSubmit());

        // Load banker designation data from database
        loadDesignationData();
    }

    private void initializeViews() {
        designationInput = findViewById(R.id.designationInput);
        submitButton = findViewById(R.id.submitButton);
        designationTableLayout = findViewById(R.id.designationTableLayout);
        designationsList = new ArrayList<>();
    }

    private void loadDesignationData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_banker_designation_list.php";
                Log.d(TAG, "Loading banker designation data from: " + urlString);
                
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
                    Log.d(TAG, "Raw response for banker designation data: " + responseString);
                    
                    // Check if response starts with HTML (error)
                    if (responseString.trim().startsWith("<")) {
                        Log.e(TAG, "get_banker_designation_list.php returned HTML instead of JSON: " + responseString);
                        runOnUiThread(() -> showError("Server error: get_banker_designation_list.php returned HTML. Check server logs."));
                        return;
                    }
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<BankersDesignationItem> designationItems = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            BankersDesignationItem designationItem = new BankersDesignationItem(
                                item.getInt("id"),
                                item.getString("designation_name")
                            );
                            designationItems.add(designationItem);
                        }
                        
                        runOnUiThread(() -> {
                            populateDesignationTable(designationItems);
                            showError("Loaded " + designationItems.size() + " banker designations from database");
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "get_banker_designation_list.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to load banker designation data: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from get_banker_designation_list.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from get_banker_designation_list.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load banker designation data", e);
                runOnUiThread(() -> showError("Failed to load banker designation data: " + e.getMessage()));
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

    private void populateDesignationTable(List<BankersDesignationItem> designationItems) {
        // Remove existing data rows (keep header)
        int childCount = designationTableLayout.getChildCount();
        for (int i = childCount - 1; i > 0; i--) {
            designationTableLayout.removeViewAt(i);
        }
        
        if (designationItems.isEmpty()) {
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
            noDataText.setText("No banker designations found");
            noDataText.setTextSize(14);
            noDataText.setTextColor(getResources().getColor(R.color.red));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(16, 16, 16, 16);
            
            noDataRow.addView(noDataText);
            designationTableLayout.addView(noDataRow);
            return;
        }
        
        // Add data rows
        for (BankersDesignationItem item : designationItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(48, 48, 48, 48);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Designation Name
            TextView designationNameText = createTableCell(item.getDesignationName());
            row.addView(designationNameText);
            
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
            
            designationTableLayout.addView(row);
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
        String designation = designationInput.getText().toString().trim();
        if (!designation.isEmpty()) {
            // Send banker designation data to database
            addDesignationToDatabase(designation);
        } else {
            showError("Please enter a banker designation");
        }
    }

    private void addDesignationToDatabase(String designation) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/add_banker_designation.php";
                Log.d(TAG, "Adding banker designation to database: " + designation);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("designation_name", designation);
                
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
                    Log.d(TAG, "Raw response for adding banker designation: " + responseString);
                    
                    JSONObject jsonResponse = new JSONObject(responseString);
                    
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
            designationInput.setText("");
                            showError("Banker designation added successfully: " + designation);
                            // Reload the banker designation list
                            loadDesignationData();
                        });
                    } else {
                        String errorMessage = "Unknown error";
                        try {
                            errorMessage = jsonResponse.getString("error");
                        } catch (JSONException e) {
                            Log.e(TAG, "Error getting error message from JSON", e);
                        }
                        final String finalErrorMessage = errorMessage;
                        Log.e(TAG, "add_banker_designation.php API error: " + finalErrorMessage);
                        runOnUiThread(() -> showError("Failed to add banker designation: " + finalErrorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from add_banker_designation.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from add_banker_designation.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add banker designation", e);
                runOnUiThread(() -> showError("Failed to add banker designation: " + e.getMessage()));
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

    // Data class for Banker Designation items
    private static class BankersDesignationItem {
        private int id;
        private String designationName;

        public BankersDesignationItem(int id, String designationName) {
            this.id = id;
            this.designationName = designationName;
        }

        public int getId() { return id; }
        public String getDesignationName() { return designationName; }
    }
} 

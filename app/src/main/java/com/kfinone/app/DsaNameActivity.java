package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

public class DsaNameActivity extends AppCompatActivity {

    private TextInputEditText dsaNameInput;
    private MaterialButton submitButton;
    private LinearLayout tableContent;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsa_name);

        // Initialize views
        dsaNameInput = findViewById(R.id.dsaNameInput);
        submitButton = findViewById(R.id.submitButton);
        tableContent = findViewById(R.id.tableContent);
        backButton = findViewById(R.id.backButton);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsaName = dsaNameInput.getText().toString().trim();
                if (dsaName.isEmpty()) {
                    Toast.makeText(DsaNameActivity.this, "Please enter DSA Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitDsaName(dsaName);
            }
        });

        // Load existing DSA names
        loadDsaNames();
    }

    private void loadDsaNames() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_dsa_names.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        runOnUiThread(() -> {
                            displayDsaNames(dataArray);
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void submitDsaName(String dsaName) {
        new Thread(() -> {
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("bsa_name", dsaName);

                // Create request body
                String jsonString = jsonData.toString();
                byte[] postData = jsonString.getBytes("UTF-8");

                String urlString = "https://pznstudio.shop/kfinone/add_dsa_name.php";
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Send data
                java.io.OutputStream os = connection.getOutputStream();
                os.write(postData);
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(DsaNameActivity.this, "DSA name added successfully!", Toast.LENGTH_SHORT).show();
                            dsaNameInput.setText(""); // Clear input
                            loadDsaNames(); // Reload the list
                        });
                    } else {
                        String error = jsonResponse.optString("error", "Unknown error occurred");
                        runOnUiThread(() -> {
                            Toast.makeText(DsaNameActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(DsaNameActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(DsaNameActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void displayDsaNames(JSONArray dsaNamesArray) {
        tableContent.removeAllViews();
        
        try {
            if (dsaNamesArray.length() == 0) {
                TextView noDataText = new TextView(this);
                noDataText.setText("No DSA names found");
                noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                noDataText.setPadding(16, 32, 16, 32);
                tableContent.addView(noDataText);
                return;
            }
            
            for (int i = 0; i < dsaNamesArray.length(); i++) {
                JSONObject dsa = dsaNamesArray.getJSONObject(i);
                String dsaName = dsa.getString("bsa_name");
                String status = dsa.optString("status", "Active");
                addDsaToTable(dsaName, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addDsaToTable(String dsaName, String status) {
        // Create a new row layout
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(8, 8, 8, 8);

        // Add Name column
        TextView nameText = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.weight = 2;
        nameText.setLayoutParams(nameParams);
        nameText.setText(dsaName);
        rowLayout.addView(nameText);

        // Add Status column
        TextView statusText = new TextView(this);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT);
        statusParams.weight = 1;
        statusText.setLayoutParams(statusParams);
        statusText.setText(status);
        rowLayout.addView(statusText);

        // Add Actions column
        LinearLayout actionsLayout = new LinearLayout(this);
        LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT);
        actionsParams.weight = 1;
        actionsLayout.setLayoutParams(actionsParams);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Edit button
        MaterialButton editButton = new MaterialButton(this);
        editButton.setText("Edit");
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement edit functionality
                Toast.makeText(DsaNameActivity.this, "Edit clicked for " + dsaName, Toast.LENGTH_SHORT).show();
            }
        });
        actionsLayout.addView(editButton);

        // Delete button
        MaterialButton deleteButton = new MaterialButton(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement delete functionality
                tableContent.removeView(rowLayout);
                Toast.makeText(DsaNameActivity.this, "Deleted " + dsaName, Toast.LENGTH_SHORT).show();
            }
        });
        actionsLayout.addView(deleteButton);

        rowLayout.addView(actionsLayout);
        tableContent.addView(rowLayout);
    }
} 

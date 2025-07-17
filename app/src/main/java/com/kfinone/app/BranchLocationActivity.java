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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
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

public class BranchLocationActivity extends AppCompatActivity {
    private static final String TAG = "BranchLocationActivity";
    private AutoCompleteTextView branchStateDropdown;
    private TextInputEditText locationInput;
    private MaterialButton submitButton;
    private RecyclerView branchLocationsRecyclerView;
    private BranchLocationTableAdapter adapter;
    private List<BranchLocationItem> branchLocationList;
    private List<String> branchStates;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_location);

        // Initialize views
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        locationInput = findViewById(R.id.locationInput);
        submitButton = findViewById(R.id.submitButton);
        branchLocationsRecyclerView = findViewById(R.id.branchLocationsRecyclerView);
        ImageButton backButton = findViewById(R.id.backButton);

        // Initialize lists and executor
        branchLocationList = new ArrayList<>();
        branchStates = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        // Setup RecyclerView with new table adapter
        adapter = new BranchLocationTableAdapter(branchLocationList);
        branchLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        branchLocationsRecyclerView.setAdapter(adapter);

        // Load branch states from database
        loadBranchStatesForDropdown();

        // Fetch branch locations from new PHP endpoint
        fetchBranchLocationsTable();

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String selectedState = branchStateDropdown.getText().toString().trim();
            String locationName = locationInput.getText().toString().trim();

            if (selectedState.isEmpty()) {
                Toast.makeText(this, "Please select a branch state", Toast.LENGTH_SHORT).show();
                return;
            }

            if (locationName.isEmpty()) {
                Toast.makeText(this, "Please enter location name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add location to database with branch state
            addLocationToDatabase(locationName, selectedState);
        });
    }

    private void loadBranchStatesForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_branch_states_dropdown.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
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
                        JSONArray branchStatesArray = jsonResponse.getJSONArray("data");
                        List<String> newBranchStates = new ArrayList<>();
                        for (int i = 0; i < branchStatesArray.length(); i++) {
                            JSONObject stateObj = branchStatesArray.getJSONObject(i);
                            newBranchStates.add(stateObj.getString("name"));
                        }
                        runOnUiThread(() -> {
                            branchStates.clear();
                            branchStates.addAll(newBranchStates);
                            ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(
                                BranchLocationActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                branchStates
                            );
                            branchStateDropdown.setAdapter(branchStateAdapter);
                            Log.d(TAG, "Loaded " + branchStates.size() + " branch states from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load branch states", e);
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

    private void fetchBranchLocationsTable() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_branch_locations_with_states.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
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
                        JSONArray data = jsonResponse.getJSONArray("branch_locations");
                        List<BranchLocationItem> newBranchLocationList = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            int id = item.optInt("id");
                            String location = item.optString("branch_location", "");
                            int branchStateId = item.optInt("branch_state_id", 0);
                            String branchStateName = item.optString("branch_state_name", "Unknown");
                            String status = item.optString("status", "Active");
                            String createdAt = item.optString("created_at", "");
                            newBranchLocationList.add(new BranchLocationItem(id, location, branchStateId, branchStateName, status, createdAt));
                        }
                        runOnUiThread(() -> {
                            branchLocationList.clear();
                            branchLocationList.addAll(newBranchLocationList);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + branchLocationList.size() + " branch locations from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.optString("message", "Unknown error");
                        runOnUiThread(() -> {
                            Toast.makeText(BranchLocationActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BranchLocationActivity.this,
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch branch locations", e);
                runOnUiThread(() -> {
                    Toast.makeText(BranchLocationActivity.this,
                        "Failed to fetch branch locations: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
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

    private void addLocationToDatabase(String locationName, String branchState) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_branch_location_with_state.php";
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("branch_location", locationName);
                jsonData.put("branch_state", branchState);
                
                // Send data
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
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
                        runOnUiThread(() -> {
                            Toast.makeText(BranchLocationActivity.this,
                                "Branch location added successfully", Toast.LENGTH_SHORT).show();
                            
                            // Clear inputs
                            branchStateDropdown.setText("");
                            locationInput.setText("");
                            
                            // Refresh the list
                            fetchBranchLocationsTable();
                        });
                    } else {
                        String errorMessage = jsonResponse.optString("error", "Unknown error");
                        runOnUiThread(() -> {
                            Toast.makeText(BranchLocationActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BranchLocationActivity.this,
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add location", e);
                runOnUiThread(() -> {
                    Toast.makeText(BranchLocationActivity.this,
                        "Failed to add location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
} 

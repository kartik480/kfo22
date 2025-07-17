package com.kfinone.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class LocationActivity extends AppCompatActivity {
    private static final String TAG = "LocationActivity";
    private AutoCompleteTextView stateDropdown;
    private TextInputEditText locationInput;
    private MaterialButton submitButton;
    private RecyclerView locationsRecyclerView;
    private LocationAdapter locationAdapter;
    private List<Location> locations;
    private List<String> states;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize views
        stateDropdown = findViewById(R.id.stateDropdown);
        locationInput = findViewById(R.id.locationInput);
        submitButton = findViewById(R.id.submitButton);
        locationsRecyclerView = findViewById(R.id.locationsRecyclerView);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize lists and adapter
        locations = new ArrayList<>();
        states = new ArrayList<>();
        locationAdapter = new LocationAdapter(locations);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationsRecyclerView.setAdapter(locationAdapter);

        // Initialize executor for background tasks
        executor = Executors.newSingleThreadExecutor();

        // Load states for dropdown and locations from database
        loadStatesForDropdown();
        loadLocationsFromDatabase();

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            String selectedState = stateDropdown.getText().toString();
            String locationName = locationInput.getText().toString().trim();

            if (selectedState.isEmpty() || locationName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add location to database
            addLocationToDatabase(locationName, selectedState);
        });
    }

    private void loadStatesForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/simple_get_states.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
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
                    
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray statesArray = jsonResponse.getJSONArray("states");
                        List<String> newStates = new ArrayList<>();
                        
                        for (int i = 0; i < statesArray.length(); i++) {
                            newStates.add(statesArray.getString(i));
                        }
                        
                        runOnUiThread(() -> {
                            states.clear();
                            states.addAll(newStates);
                            
                            // Setup state dropdown
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                                LocationActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                states
                            );
                            stateDropdown.setAdapter(stateAdapter);
                            Log.d(TAG, "Loaded " + states.size() + " states from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load states", e);
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

    private void loadLocationsFromDatabase() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_locations_with_states.php";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
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
                    
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray locationsArray = jsonResponse.getJSONArray("locations");
                        List<Location> newLocations = new ArrayList<>();
                        
                        for (int i = 0; i < locationsArray.length(); i++) {
                            JSONObject locationObj = locationsArray.getJSONObject(i);
                            String locationName = locationObj.getString("name");
                            String stateName = locationObj.getString("state");
                            String status = locationObj.getString("status");
                            newLocations.add(new Location(locationName, stateName, status));
                        }
                        
                        runOnUiThread(() -> {
                            locations.clear();
                            locations.addAll(newLocations);
                            locationAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + locations.size() + " locations from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load locations", e);
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

    private void addLocationToDatabase(String locationName, String stateName) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_location.php";
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data with both location and state
                String jsonData = "{\"location\":\"" + locationName + "\",\"state\":\"" + stateName + "\"}";
                
                // Send data
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            // Clear inputs and show success message
                            stateDropdown.setText("");
                            locationInput.setText("");
                            Toast.makeText(LocationActivity.this, "Location added successfully", Toast.LENGTH_SHORT).show();
                            // Reload locations from database
                            loadLocationsFromDatabase();
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(LocationActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(LocationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Read error response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(LocationActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(LocationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(LocationActivity.this, "Server error: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add location", e);
                runOnUiThread(() -> {
                    Toast.makeText(LocationActivity.this, "Failed to add location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void deleteLocationFromDatabase(String locationName, String stateName, int position) {
        executor.execute(() -> {
            String urlString = "https://pznstudio.shop/kfinone/delete_location.php";
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data
                String jsonData = "{\"location\":\"" + locationName + "\",\"state_name\":\"" + stateName + "\"}";
                
                // Send data
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        runOnUiThread(() -> {
                            // Remove from list and update adapter
                            locations.remove(position);
                            locationAdapter.notifyItemRemoved(position);
                            locationAdapter.notifyItemRangeChanged(position, locations.size());
                            Toast.makeText(LocationActivity.this, "Location deleted successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(LocationActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(LocationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LocationActivity.this, "Server error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete location", e);
                runOnUiThread(() -> {
                    Toast.makeText(LocationActivity.this, "Failed to delete location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private static class Location {
        String name;
        String state;
        String status;

        Location(String name, String state, String status) {
            this.name = name;
            this.state = state;
            this.status = status;
        }
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private List<Location> locations;

        LocationAdapter(List<Location> locations) {
            this.locations = locations;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_location, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Location location = locations.get(position);
            holder.locationName.setText(location.name);
            holder.stateName.setText(location.state);
            
            // Set status with proper styling
            holder.status.setText(location.status);
            if ("Active".equals(location.status)) {
                holder.status.setBackgroundResource(R.drawable.circle_background);
                holder.status.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.status.setBackgroundResource(android.R.color.holo_red_light);
                holder.status.setTextColor(getResources().getColor(R.color.white));
            }

            // Set up edit button click listener
            holder.editButton.setOnClickListener(v -> {
                // Pre-fill the input fields for editing
                stateDropdown.setText(location.state);
                locationInput.setText(location.name);
                locationInput.requestFocus();
                
                // Show edit mode message
                Toast.makeText(LocationActivity.this, 
                    "Edit mode: Update the fields and click Submit", 
                    Toast.LENGTH_LONG).show();
            });

            // Set up delete button click listener
            holder.deleteButton.setOnClickListener(v -> {
                // Show confirmation dialog
                new AlertDialog.Builder(LocationActivity.this)
                    .setTitle("Delete Location")
                    .setMessage("Are you sure you want to delete '" + location.name + "'?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteLocationFromDatabase(location.name, location.state, position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView locationName;
            TextView stateName;
            TextView status;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View view) {
                super(view);
                locationName = view.findViewById(R.id.locationName);
                stateName = view.findViewById(R.id.stateName);
                status = view.findViewById(R.id.status);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 

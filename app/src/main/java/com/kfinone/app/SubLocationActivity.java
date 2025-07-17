package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class SubLocationActivity extends AppCompatActivity {
    private static final String TAG = "SubLocationActivity";
    private AutoCompleteTextView stateDropdown;
    private AutoCompleteTextView locationDropdown;
    private TextInputEditText subLocationInput;
    private MaterialButton submitButton;
    private RecyclerView subLocationsRecyclerView;
    private List<SubLocation> subLocations;
    private List<String> states;
    private List<String> locations;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_location);

        // Initialize views
        stateDropdown = findViewById(R.id.stateDropdown);
        locationDropdown = findViewById(R.id.locationDropdown);
        subLocationInput = findViewById(R.id.subLocationInput);
        submitButton = findViewById(R.id.submitButton);
        subLocationsRecyclerView = findViewById(R.id.subLocationsRecyclerView);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize lists and executor
        states = new ArrayList<>();
        locations = new ArrayList<>();
        subLocations = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        // Load states and locations from database
        loadStatesForDropdown();
        loadLocationsForDropdown();
        loadSubLocationsFromDatabase();

        // Setup RecyclerView
        SubLocationAdapter adapter = new SubLocationAdapter(subLocations);
        subLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subLocationsRecyclerView.setAdapter(adapter);

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            String selectedState = stateDropdown.getText().toString();
            String selectedLocation = locationDropdown.getText().toString();
            String subLocationName = subLocationInput.getText().toString().trim();

            if (selectedState.isEmpty() || selectedLocation.isEmpty() || subLocationName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add sub location to database with all parameters
            addSubLocationToDatabase(subLocationName, selectedState, selectedLocation);
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
                                SubLocationActivity.this, 
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

    private void loadLocationsForDropdown() {
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray locationsArray = jsonResponse.getJSONArray("locations");
                        List<String> newLocations = new ArrayList<>();
                        
                        for (int i = 0; i < locationsArray.length(); i++) {
                            JSONObject locationObj = locationsArray.getJSONObject(i);
                            newLocations.add(locationObj.getString("name"));
                        }
                        
                        runOnUiThread(() -> {
                            locations.clear();
                            locations.addAll(newLocations);
                            
                            // Setup location dropdown
                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                                SubLocationActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                locations
                            );
                            locationDropdown.setAdapter(locationAdapter);
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

    private void loadSubLocationsFromDatabase() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_sub_locations_with_relations.php";
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
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray subLocationsArray = jsonResponse.getJSONArray("sub_locations");
                        List<SubLocation> newSubLocations = new ArrayList<>();
                        
                        for (int i = 0; i < subLocationsArray.length(); i++) {
                            JSONObject subLocationObj = subLocationsArray.getJSONObject(i);
                            String subLocationName = subLocationObj.getString("sub_location");
                            String stateName = subLocationObj.getString("state_name");
                            String locationName = subLocationObj.getString("location_name");
                            String status = subLocationObj.getString("status");
                            newSubLocations.add(new SubLocation(subLocationName, stateName, locationName, status));
                        }
                        
                        runOnUiThread(() -> {
                            subLocations.clear();
                            subLocations.addAll(newSubLocations);
                            // Notify adapter of data change
                            if (subLocationsRecyclerView.getAdapter() != null) {
                                subLocationsRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                            Log.d(TAG, "Loaded " + subLocations.size() + " sub locations from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load sub locations", e);
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

    private void addSubLocationToDatabase(String subLocationName, String stateName, String locationName) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_sub_location_with_relations.php";
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Prepare JSON data with all required parameters
                JSONObject jsonData = new JSONObject();
                jsonData.put("sub_location", subLocationName);
                jsonData.put("state", stateName);
                jsonData.put("location", locationName);
                
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
                            Toast.makeText(SubLocationActivity.this, 
                                "Sub location added successfully", Toast.LENGTH_SHORT).show();
                            
                            // Clear inputs
                            stateDropdown.setText("");
                            locationDropdown.setText("");
                            subLocationInput.setText("");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("error");
                        runOnUiThread(() -> {
                            Toast.makeText(SubLocationActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SubLocationActivity.this, 
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add sub location", e);
                runOnUiThread(() -> {
                    Toast.makeText(SubLocationActivity.this, 
                        "Failed to add sub location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static class SubLocation {
        String name;
        String state;
        String location;
        String status;

        SubLocation(String name, String state, String location, String status) {
            this.name = name;
            this.state = state;
            this.location = location;
            this.status = status;
        }
    }

    private class SubLocationAdapter extends RecyclerView.Adapter<SubLocationAdapter.ViewHolder> {
        private List<SubLocation> subLocations;

        SubLocationAdapter(List<SubLocation> subLocations) {
            this.subLocations = subLocations;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_sub_location, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SubLocation subLocation = subLocations.get(position);
            holder.subLocationName.setText(subLocation.name);
            holder.stateName.setText(subLocation.state);
            holder.locationName.setText(subLocation.location);
            holder.status.setText(subLocation.status);
        }

        @Override
        public int getItemCount() {
            return subLocations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView subLocationName;
            TextView stateName;
            TextView locationName;
            TextView status;

            ViewHolder(View view) {
                super(view);
                subLocationName = view.findViewById(R.id.subLocationName);
                stateName = view.findViewById(R.id.stateName);
                locationName = view.findViewById(R.id.locationName);
                status = view.findViewById(R.id.status);
            }
        }
    }
} 

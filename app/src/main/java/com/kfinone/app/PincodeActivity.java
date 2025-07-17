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

public class PincodeActivity extends AppCompatActivity {
    private static final String TAG = "PincodeActivity";
    private AutoCompleteTextView stateDropdown;
    private AutoCompleteTextView locationDropdown;
    private AutoCompleteTextView subLocationDropdown;
    private TextInputEditText pincodeInput;
    private MaterialButton submitButton;
    private RecyclerView pincodesRecyclerView;
    private PincodeAdapter pincodeAdapter;
    private List<Pincode> pincodes;
    private List<String> states;
    private List<String> locations;
    private List<String> subLocations;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);

        // Initialize views
        stateDropdown = findViewById(R.id.stateDropdown);
        locationDropdown = findViewById(R.id.locationDropdown);
        subLocationDropdown = findViewById(R.id.subLocationDropdown);
        pincodeInput = findViewById(R.id.pincodeInput);
        submitButton = findViewById(R.id.submitButton);
        pincodesRecyclerView = findViewById(R.id.pincodesRecyclerView);

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Initialize lists and executor
        states = new ArrayList<>();
        locations = new ArrayList<>();
        subLocations = new ArrayList<>();
        pincodes = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();

        // Load dropdown options from database
        loadStatesForDropdown();
        loadLocationsForDropdown();
        loadSubLocationsForDropdown();
        loadPincodesFromDatabase();

        // Setup RecyclerView
        pincodeAdapter = new PincodeAdapter(pincodes);
        pincodesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pincodesRecyclerView.setAdapter(pincodeAdapter);

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            String selectedState = stateDropdown.getText().toString();
            String selectedLocation = locationDropdown.getText().toString();
            String selectedSubLocation = subLocationDropdown.getText().toString();
            String pincode = pincodeInput.getText().toString();

            if (selectedState.isEmpty() || selectedLocation.isEmpty() || selectedSubLocation.isEmpty() || pincode.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pincode.length() != 6) {
                Toast.makeText(this, "PIN Code must be 6 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add pincode to database
            addPincodeToDatabase(pincode, selectedState, selectedLocation, selectedSubLocation);
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
                        JSONArray statesArray = jsonResponse.getJSONArray("states");
                        List<String> newStates = new ArrayList<>();
                        
                        for (int i = 0; i < statesArray.length(); i++) {
                            JSONObject stateObj = statesArray.getJSONObject(i);
                            newStates.add(stateObj.getString("state_name"));
                        }
                        
                        runOnUiThread(() -> {
                            states.clear();
                            states.addAll(newStates);
                            
                            // Setup state dropdown
                            ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                                PincodeActivity.this, 
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
                        JSONArray locationsArray = jsonResponse.getJSONArray("locations");
                        List<String> newLocations = new ArrayList<>();
                        
                        for (int i = 0; i < locationsArray.length(); i++) {
                            JSONObject locationObj = locationsArray.getJSONObject(i);
                            newLocations.add(locationObj.getString("location"));
                        }
                        
                        runOnUiThread(() -> {
                            locations.clear();
                            locations.addAll(newLocations);
                            
                            // Setup location dropdown
                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                                PincodeActivity.this, 
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

    private void loadSubLocationsForDropdown() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_sub_locations_with_relations.php";
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
                        JSONArray subLocationsArray = jsonResponse.getJSONArray("sub_locations");
                        List<String> newSubLocations = new ArrayList<>();
                        
                        for (int i = 0; i < subLocationsArray.length(); i++) {
                            JSONObject subLocationObj = subLocationsArray.getJSONObject(i);
                            newSubLocations.add(subLocationObj.getString("sub_location"));
                        }
                        
                        runOnUiThread(() -> {
                            subLocations.clear();
                            subLocations.addAll(newSubLocations);
                            
                            // Setup sub location dropdown
                            ArrayAdapter<String> subLocationAdapter = new ArrayAdapter<>(
                                PincodeActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                subLocations
                            );
                            subLocationDropdown.setAdapter(subLocationAdapter);
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

    private void loadPincodesFromDatabase() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_pincodes_with_relations.php";
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
                        JSONArray pincodesArray = jsonResponse.getJSONArray("pincodes");
                        List<Pincode> newPincodes = new ArrayList<>();
                        
                        for (int i = 0; i < pincodesArray.length(); i++) {
                            JSONObject pincodeObj = pincodesArray.getJSONObject(i);
                            newPincodes.add(new Pincode(
                                pincodeObj.getString("pincode"),
                                pincodeObj.getString("state_name"),
                                pincodeObj.getString("location_name"),
                                pincodeObj.getString("sub_location_name"),
                                pincodeObj.getString("status")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            pincodes.clear();
                            pincodes.addAll(newPincodes);
                            if (pincodeAdapter != null) {
                                pincodeAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, "Loaded " + pincodes.size() + " pincodes from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load pincodes", e);
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

    private void addPincodeToDatabase(String pincode, String state, String location, String subLocation) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_pincode_with_relations.php";
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
                jsonData.put("pincode", pincode);
                jsonData.put("state", state);
                jsonData.put("location", location);
                jsonData.put("sub_location", subLocation);
                
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
                            Toast.makeText(PincodeActivity.this, 
                                "PIN code added successfully", Toast.LENGTH_SHORT).show();

                            // Clear inputs
                            stateDropdown.setText("");
                            locationDropdown.setText("");
                            subLocationDropdown.setText("");
                            pincodeInput.setText("");
                            
                            // Refresh the pincodes list
                            loadPincodesFromDatabase();
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("error");
                        runOnUiThread(() -> {
                            Toast.makeText(PincodeActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(PincodeActivity.this, 
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add pincode", e);
                runOnUiThread(() -> {
                    Toast.makeText(PincodeActivity.this, 
                        "Failed to add PIN code: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // Pincode data class
    private static class Pincode {
        String code;
        String state;
        String location;
        String subLocation;
        String status;

        Pincode(String code, String state, String location, String subLocation, String status) {
            this.code = code;
            this.state = state;
            this.location = location;
            this.subLocation = subLocation;
            this.status = status;
        }
    }

    // RecyclerView Adapter
    private class PincodeAdapter extends RecyclerView.Adapter<PincodeAdapter.PincodeViewHolder> {
        private List<Pincode> pincodes;

        PincodeAdapter(List<Pincode> pincodes) {
            this.pincodes = pincodes;
        }

        @Override
        public PincodeViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_pincode, parent, false);
            return new PincodeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PincodeViewHolder holder, int position) {
            Pincode pincode = pincodes.get(position);
            holder.pincode.setText(pincode.code);
            holder.stateName.setText(pincode.state);
            holder.locationName.setText(pincode.location);
            holder.subLocationName.setText(pincode.subLocation);
            holder.status.setText(pincode.status);

            holder.editButton.setOnClickListener(v -> {
                // TODO: Implement edit functionality
                Toast.makeText(PincodeActivity.this, "Edit clicked for " + pincode.code, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                pincodes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, pincodes.size());
            });
        }

        @Override
        public int getItemCount() {
            return pincodes.size();
        }

        class PincodeViewHolder extends RecyclerView.ViewHolder {
            TextView pincode;
            TextView stateName;
            TextView locationName;
            TextView subLocationName;
            TextView status;
            ImageButton editButton;
            ImageButton deleteButton;

            PincodeViewHolder(View itemView) {
                super(itemView);
                pincode = itemView.findViewById(R.id.pincode);
                stateName = itemView.findViewById(R.id.stateName);
                locationName = itemView.findViewById(R.id.locationName);
                subLocationName = itemView.findViewById(R.id.subLocationName);
                status = itemView.findViewById(R.id.status);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 

package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class StateActivity extends AppCompatActivity {
    private static final String TAG = "StateActivity";
    private TextInputEditText stateInput;
    private MaterialButton submitButton;
    private RecyclerView statesRecyclerView;
    private StateAdapter stateAdapter;
    private List<StateItem> states;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        // Initialize views
        stateInput = findViewById(R.id.stateInput);
        submitButton = findViewById(R.id.submitButton);
        statesRecyclerView = findViewById(R.id.statesRecyclerView);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize states list and adapter
        states = new ArrayList<>();
        stateAdapter = new StateAdapter(states);
        statesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statesRecyclerView.setAdapter(stateAdapter);

        // Initialize executor for background tasks
        executor = Executors.newSingleThreadExecutor();

        // Load states from database
        loadStatesFromDatabase();
        
        // Setup submit button click listener
        submitButton.setOnClickListener(v -> {
            String stateName = stateInput.getText().toString().trim();
            if (!stateName.isEmpty()) {
                // Add state to database
                addStateToDatabase(stateName);
            } else {
                Toast.makeText(this, "Please enter state name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStatesFromDatabase() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/simple_get_states.php"; // Using simple API
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
                        List<StateItem> newStates = new ArrayList<>();
                        
                        for (int i = 0; i < statesArray.length(); i++) {
                            String stateName = statesArray.getString(i);
                            newStates.add(new StateItem(stateName, "Active"));
                        }
                        
                        runOnUiThread(() -> {
                            states.clear();
                            states.addAll(newStates);
                            stateAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + states.size() + " states from database");
                        });
                    } else {
                        Log.e(TAG, "API error: " + jsonResponse.getString("error"));
                        runOnUiThread(() -> {
                            Toast.makeText(StateActivity.this, "Error loading states", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP error: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(StateActivity.this, "Server error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Network request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(StateActivity.this, "Failed to load states: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void addStateToDatabase(String stateName) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_state.php";
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
                String jsonData = "{\"state_name\":\"" + stateName + "\"}";
                
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
                            // Clear input and show success message
                            stateInput.setText("");
                            Toast.makeText(StateActivity.this, "State added successfully", Toast.LENGTH_SHORT).show();
                            // Reload states from database
                            loadStatesFromDatabase();
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(StateActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(StateActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(StateActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(StateActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(StateActivity.this, "Server error: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add state", e);
                runOnUiThread(() -> {
                    Toast.makeText(StateActivity.this, "Failed to add state: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    // State Item class
    private static class StateItem {
        String name;
        String status;

        StateItem(String name, String status) {
            this.name = name;
            this.status = status;
        }
    }

    // State Adapter
    private class StateAdapter extends RecyclerView.Adapter<StateAdapter.ViewHolder> {
        private List<StateItem> states;

        StateAdapter(List<StateItem> states) {
            this.states = states;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_state, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StateItem state = states.get(position);
            holder.stateName.setText(state.name);
            holder.status.setText("Active"); // Always show Active for now
            
            // Set up edit button click listener
            holder.editButton.setOnClickListener(v -> {
                // TODO: Implement edit functionality
                Toast.makeText(StateActivity.this, "Edit clicked for " + state.name, Toast.LENGTH_SHORT).show();
            });
            
            // Set up delete button click listener
            holder.deleteButton.setOnClickListener(v -> {
                // Remove from list and update UI
                states.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, states.size());
                Toast.makeText(StateActivity.this, "Deleted " + state.name, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return states.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView stateName;
            TextView status;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View view) {
                super(view);
                stateName = view.findViewById(R.id.stateName);
                status = view.findViewById(R.id.status);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 

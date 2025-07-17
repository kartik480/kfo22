package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class BranchStateActivity extends AppCompatActivity {
    private static final String TAG = "BranchStateActivity";
    private TextInputEditText branchStateInput;
    private MaterialButton submitButton;
    private RecyclerView branchStatesRecyclerView;
    private BranchStateAdapter adapter;
    private List<BranchState> branchStates;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_state);

        // Initialize views
        branchStateInput = findViewById(R.id.branchStateInput);
        submitButton = findViewById(R.id.submitButton);
        branchStatesRecyclerView = findViewById(R.id.branchStatesRecyclerView);
        ImageButton backButton = findViewById(R.id.backButton);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Setup RecyclerView
        branchStates = new ArrayList<>();
        adapter = new BranchStateAdapter(branchStates);
        branchStatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        branchStatesRecyclerView.setAdapter(adapter);

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String branchStateName = branchStateInput.getText().toString().trim();
            if (!branchStateName.isEmpty()) {
                // Add branch state to database
                addBranchStateToDatabase(branchStateName);
            } else {
                Toast.makeText(this, "Please enter branch state name", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch branch states from database
        fetchBranchStates();
    }

    private void fetchBranchStates() {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/get_branch_state_list.php";
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
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
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        List<BranchState> newBranchStates = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            String name = item.getString("branch_state_name");
                            String status = item.getString("status");
                            newBranchStates.add(new BranchState(name, status));
                        }
                        
                        runOnUiThread(() -> {
                            branchStates.clear();
                            branchStates.addAll(newBranchStates);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + branchStates.size() + " branch states from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        runOnUiThread(() -> {
                            Toast.makeText(BranchStateActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BranchStateActivity.this, 
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch branch states", e);
                runOnUiThread(() -> {
                    Toast.makeText(BranchStateActivity.this, 
                        "Failed to fetch branch states: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void addBranchStateToDatabase(String branchStateName) {
        executor.execute(() -> {
            String urlString = "https://emp.kfinone.com/mobile/api/add_branch_state.php";
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
                JSONObject jsonData = new JSONObject();
                jsonData.put("branch_state_name", branchStateName);
                
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
                            Toast.makeText(BranchStateActivity.this, 
                                "Branch state added successfully", Toast.LENGTH_SHORT).show();
                            
                            // Clear input
                            branchStateInput.setText("");
                            
                            // Refresh the list
                            fetchBranchStates();
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("error");
                        runOnUiThread(() -> {
                            Toast.makeText(BranchStateActivity.this, 
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BranchStateActivity.this, 
                            "HTTP Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to add branch state", e);
                runOnUiThread(() -> {
                    Toast.makeText(BranchStateActivity.this, 
                        "Failed to add branch state: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // Branch State data class
    private static class BranchState {
        private String name;
        private String status;

        public BranchState(String name, String status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }
    }

    // RecyclerView Adapter
    private class BranchStateAdapter extends RecyclerView.Adapter<BranchStateAdapter.ViewHolder> {
        private List<BranchState> branchStates;

        public BranchStateAdapter(List<BranchState> branchStates) {
            this.branchStates = branchStates;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_branch_state, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BranchState branchState = branchStates.get(position);
            holder.branchStateName.setText(branchState.getName());
            holder.branchStateStatus.setText(branchState.getStatus());

            holder.editButton.setOnClickListener(v -> {
                // TODO: Implement edit functionality
                Toast.makeText(BranchStateActivity.this, "Edit clicked for " + branchState.getName(), Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                branchStates.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(BranchStateActivity.this, "Branch State deleted", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return branchStates.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView branchStateName;
            TextView branchStateStatus;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                branchStateName = itemView.findViewById(R.id.branchStateName);
                branchStateStatus = itemView.findViewById(R.id.branchStateStatus);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 

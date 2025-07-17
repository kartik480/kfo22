package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeamLinksActivity extends AppCompatActivity {

    private static final String TAG = "TeamLinksActivity";
    private AutoCompleteTextView userDropdown;
    private MaterialButton showIconsButton;
    private MaterialButton resetButton;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_links);

        // Initialize executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        userDropdown = findViewById(R.id.userDropdown);
        showIconsButton = findViewById(R.id.showIconsButton);
        resetButton = findViewById(R.id.resetButton);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Load user data from database
        loadUserData();

        // Setup show icons button
        showIconsButton.setOnClickListener(v -> {
            String selectedUser = userDropdown.getText().toString();
            if (selectedUser.isEmpty()) {
                Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Implement show icons functionality
            Toast.makeText(this, "Showing icons for: " + selectedUser, Toast.LENGTH_SHORT).show();
        });

        // Setup reset button
        resetButton.setOnClickListener(v -> {
            userDropdown.setText("");
            // TODO: Reset any other fields or states
            Toast.makeText(this, "Reset completed", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_team_links_users.php";
                
                Log.d(TAG, "Loading user data from: " + urlString);
                
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> users = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            String userName = dataArray.getString(i);
                            users.add(userName);
                        }
                        
                        runOnUiThread(() -> {
                            // Setup user dropdown with data from database
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                TeamLinksActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                users
                            );
                            userDropdown.setAdapter(adapter);

                            Log.d(TAG, "Loaded " + users.size() + " users from database");
                        });
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Log.e(TAG, "fetch_team_links_users.php API error: " + errorMessage);
                        runOnUiThread(() -> showError("Failed to load user data: " + errorMessage));
                    }
                } else {
                    Log.e(TAG, "HTTP error from fetch_team_links_users.php: " + responseCode);
                    runOnUiThread(() -> showError("HTTP error from fetch_team_links_users.php: " + responseCode));
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to load user data", e);
                runOnUiThread(() -> showError("Failed to load user data: " + e.getMessage()));
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
} 

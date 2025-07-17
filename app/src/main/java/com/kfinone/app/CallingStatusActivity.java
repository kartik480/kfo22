package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CallingStatusActivity extends AppCompatActivity {
    private static final String TAG = "CallingStatusActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/"; // Change this to your server URL
    
    private TextInputEditText callingStatusInput;
    private MaterialButton submitButton;
    private RecyclerView callingStatusRecyclerView;
    private CallingStatusAdapter callingStatusAdapter;
    private List<CallingStatusItem> callingStatuses;
    private OkHttpClient client;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_status);

        // Initialize HTTP client and executor
        client = new OkHttpClient();
        executor = Executors.newSingleThreadExecutor();

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize views
        callingStatusInput = findViewById(R.id.callingStatusInput);
        submitButton = findViewById(R.id.submitButton);
        callingStatusRecyclerView = findViewById(R.id.callingStatusRecyclerView);

        // Initialize RecyclerView
        callingStatuses = new ArrayList<>();
        callingStatusAdapter = new CallingStatusAdapter(callingStatuses);
        callingStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        callingStatusRecyclerView.setAdapter(callingStatusAdapter);

        // Set up submit button click listener
        submitButton.setOnClickListener(v -> submitCallingStatus());

        // Load existing calling statuses
        loadCallingStatuses();
    }

    private void submitCallingStatus() {
        String status = callingStatusInput.getText().toString().trim();
        
        if (status.isEmpty()) {
            Toast.makeText(this, "Please enter a calling status", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable submit button to prevent multiple submissions
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        executor.execute(() -> {
            try {
                // Prepare JSON data
                JSONObject jsonData = new JSONObject();
                jsonData.put("calling_status", status);

                // Create request body
                RequestBody body = RequestBody.create(
                    jsonData.toString(),
                    MediaType.parse("application/json; charset=utf-8")
                );

                // Create request
                Request request = new Request.Builder()
                    .url(BASE_URL + "add_calling_status.php")
                    .post(body)
                    .build();

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    runOnUiThread(() -> {
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit");

                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (jsonResponse.getBoolean("success")) {
                                    Toast.makeText(CallingStatusActivity.this, 
                                        "Calling status added successfully!", Toast.LENGTH_SHORT).show();
                                    
                                    // Clear input
                                    callingStatusInput.setText("");
                                    
                                    // Reload the list to show the new item
                                    loadCallingStatuses();
                                } else {
                                    String error = jsonResponse.optString("error", "Unknown error occurred");
                                    Toast.makeText(CallingStatusActivity.this, 
                                        "Error: " + error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing response", e);
                                Toast.makeText(CallingStatusActivity.this, 
                                    "Error parsing server response", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(CallingStatusActivity.this, 
                                "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error submitting calling status", e);
                runOnUiThread(() -> {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                    Toast.makeText(CallingStatusActivity.this, 
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadCallingStatuses() {
        Log.d(TAG, "Starting to load calling statuses...");
        
        executor.execute(() -> {
            try {
                Log.d(TAG, "Creating network request...");
                
                // Create request
                Request request = new Request.Builder()
                    .url(BASE_URL + "get_calling_status_options.php")
                    .get()
                    .build();

                Log.d(TAG, "Executing network request to: " + BASE_URL + "get_calling_status_options.php");

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Load Response Code: " + response.code());
                    Log.d(TAG, "Load Response: " + responseBody);

                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                Log.d(TAG, "Parsed JSON response: " + jsonResponse.toString());
                                
                                if (jsonResponse.getBoolean("success")) {
                                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                                    Log.d(TAG, "Found " + dataArray.length() + " calling statuses");
                                    
                                    List<CallingStatusItem> newCallingStatuses = new ArrayList<>();
                                    
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject item = dataArray.getJSONObject(i);
                                        int id = item.getInt("id");
                                        String callingStatus = item.getString("calling_status");
                                        newCallingStatuses.add(new CallingStatusItem(id, callingStatus));
                                        Log.d(TAG, "Added calling status: " + callingStatus + " (ID: " + id + ")");
                                    }
                                    
                                    Log.d(TAG, "Updating adapter with " + newCallingStatuses.size() + " items");
                                    callingStatusAdapter.updateCallingStatuses(newCallingStatuses);
                                    
                                    if (newCallingStatuses.isEmpty()) {
                                        Toast.makeText(CallingStatusActivity.this, 
                                            "No calling statuses found in database", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CallingStatusActivity.this, 
                                            "Loaded " + newCallingStatuses.size() + " calling statuses", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String error = jsonResponse.optString("error", "Unknown error occurred");
                                    Log.e(TAG, "Server returned error: " + error);
                                    Toast.makeText(CallingStatusActivity.this, 
                                        "Error loading calling statuses: " + error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing response", e);
                                Toast.makeText(CallingStatusActivity.this, 
                                    "Error parsing server response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(TAG, "HTTP Error: " + response.code());
                            Toast.makeText(CallingStatusActivity.this, 
                                "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading calling statuses", e);
                runOnUiThread(() -> {
                    Toast.makeText(CallingStatusActivity.this, 
                        "Error loading calling statuses: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 

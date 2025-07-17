package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CallingSubStatusActivity extends AppCompatActivity {
    private static final String TAG = "CallingSubStatusActivity";
    private AutoCompleteTextView callingStatusDropdown;
    private TextInputEditText callingSubStatusInput;
    private MaterialButton submitButton;
    private RecyclerView subStatusRecyclerView;
    private CallingSubStatusAdapter subStatusAdapter;
    private List<CallingSubStatusItem> subStatusList = new ArrayList<>();
    private List<Integer> callingStatusIds = new ArrayList<>();
    private List<String> callingStatusNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_sub_status);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize views
        callingStatusDropdown = findViewById(R.id.callingStatusDropdown);
        callingSubStatusInput = findViewById(R.id.callingSubStatusInput);
        submitButton = findViewById(R.id.submitButton);
        subStatusRecyclerView = findViewById(R.id.subStatusRecyclerView);

        // Setup RecyclerView
        subStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subStatusAdapter = new CallingSubStatusAdapter(subStatusList);
        subStatusRecyclerView.setAdapter(subStatusAdapter);

        // Fetch calling status options from API for the dropdown
        fetchCallingStatusOptions();

        // Fetch all existing sub-statuses to populate the table
        fetchAllSubStatuses();

        // Set up submit button click listener
        submitButton.setOnClickListener(v -> {
            String selectedStatus = callingStatusDropdown.getText().toString();
            String subStatus = callingSubStatusInput.getText().toString().trim();

            if (selectedStatus.isEmpty() || subStatus.isEmpty()) {
                Toast.makeText(this, "Please select a status and enter a sub-status.", Toast.LENGTH_SHORT).show();
            } else {
                // Find the calling status ID for the selected status
                int callingStatusId = -1;
                for (int i = 0; i < callingStatusNames.size(); i++) {
                    if (callingStatusNames.get(i).equals(selectedStatus)) {
                        callingStatusId = callingStatusIds.get(i);
                        break;
                    }
                }
                
                if (callingStatusId == -1) {
                    Toast.makeText(this, "Invalid calling status selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Disable button to prevent multiple submissions
                submitButton.setEnabled(false);
                submitButton.setText("Submitting...");
                // Call the method to add the sub-status to the database
                submitSubStatus(callingStatusId, subStatus);
            }
        });
    }

    private void fetchCallingStatusOptions() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_calling_status_dropdown.php";
                Log.d(TAG, "Fetching calling status options from: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseBody = response.toString();
                    Log.d(TAG, "Response Body: " + responseBody);

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<String> callingStatuses = new ArrayList<>();
                        List<Integer> callingStatusIds = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject statusObj = dataArray.getJSONObject(i);
                            String callingStatus = statusObj.getString("calling_status");
                            int statusId = statusObj.getInt("id");
                            callingStatuses.add(callingStatus);
                            callingStatusIds.add(statusId);
                        }

                        Log.d(TAG, "Fetched " + callingStatuses.size() + " calling statuses");

                        // Update UI on main thread
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                CallingSubStatusActivity.this, 
                                android.R.layout.simple_dropdown_item_1line, 
                                callingStatuses
                            );
                            callingStatusDropdown.setAdapter(adapter);
                            
                            // Store the IDs for later use
                            CallingSubStatusActivity.this.callingStatusIds = callingStatusIds;
                            CallingSubStatusActivity.this.callingStatusNames = callingStatuses;
                        });
                    } else {
                        String error = jsonResponse.optString("error", "Failed to fetch calling statuses");
                        Log.e(TAG, "API Error: " + error);
                        runOnUiThread(() -> {
                            Toast.makeText(CallingSubStatusActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(CallingSubStatusActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Exception while fetching calling status options", e);
                runOnUiThread(() -> {
                    Toast.makeText(CallingSubStatusActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void submitSubStatus(final int callingStatusId, final String callingSubStatus) {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/add_calling_sub_status.php";
                Log.d(TAG, "Submitting to URL: " + urlString);

                // Create JSON object for the request body
                JSONObject postData = new JSONObject();
                postData.put("calling_status_id", callingStatusId);
                postData.put("calling_sub_status", callingSubStatus);
                String jsonInputString = postData.toString();
                Log.d(TAG, "Request JSON: " + jsonInputString);

                // Set up the connection
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                // Write data to the connection
                connection.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

                // Read the response
                int responseCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream()
                ));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseBody = response.toString();
                Log.d(TAG, "Response Body: " + responseBody);
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                // Extract values from JSON within the try-catch block
                final String message = jsonResponse.getString("message");
                final boolean success = jsonResponse.getBoolean("success");

                // Update UI on the main thread
                runOnUiThread(() -> {
                    Toast.makeText(CallingSubStatusActivity.this, message, Toast.LENGTH_LONG).show();
                    if (success) {
                        // Clear the input fields on success
                        callingStatusDropdown.setText("", false);
                        callingSubStatusInput.setText("");
                        // Refresh the table with the new data
                        fetchAllSubStatuses();
                    }
                    // Re-enable the button
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception during submitSubStatus", e);
                runOnUiThread(() -> {
                    Toast.makeText(CallingSubStatusActivity.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Re-enable the button
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit");
                });
            }
        }).start();
    }

    private void fetchAllSubStatuses() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/fetch_all_calling_sub_statuses.php";
                Log.d(TAG, "Fetching all sub-statuses from: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Fetch sub statuses response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseBody = response.toString();
                    Log.d(TAG, "Fetch sub statuses response: " + responseBody);

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<CallingSubStatusItem> fetchedList = new ArrayList<>();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject itemObj = dataArray.getJSONObject(i);
                            int id = itemObj.getInt("id");
                            String callingSubStatus = itemObj.getString("calling_sub_status");
                            String parentCallingStatus = itemObj.optString("parent_calling_status", "Unknown");
                            String status = itemObj.optString("status", "1");
                            
                            fetchedList.add(new CallingSubStatusItem(
                                    id,
                                    parentCallingStatus,
                                    callingSubStatus,
                                    status
                            ));
                        }
                        
                        Log.d(TAG, "Fetched " + fetchedList.size() + " calling sub statuses");
                        runOnUiThread(() -> {
                            subStatusAdapter.updateData(fetchedList);
                            if (fetchedList.isEmpty()) {
                                Toast.makeText(CallingSubStatusActivity.this, 
                                    "No calling sub statuses found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String error = jsonResponse.optString("error", "Unknown error occurred");
                        Log.e(TAG, "API Error: " + error);
                        runOnUiThread(() -> {
                            Toast.makeText(CallingSubStatusActivity.this, 
                                "Error loading sub statuses: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(CallingSubStatusActivity.this, 
                            "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchAllSubStatuses", e);
                runOnUiThread(() -> {
                    Toast.makeText(CallingSubStatusActivity.this, 
                        "Error loading sub statuses: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
} 

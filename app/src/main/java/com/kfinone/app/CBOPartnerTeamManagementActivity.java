package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CBOPartnerTeamManagementActivity extends AppCompatActivity {

    private static final String TAG = "CBOPartnerTeamManagement";

    // UI elements
    private Spinner userDropdown;
    private Button showDataButton;
    private Button resetButton;
    private LinearLayout dataContainer;
    private LinearLayout dataContent;

    // User data
    private String userName;
    private String userId;
    
    // Executor for API calls
    private ExecutorService executor;
    
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_partner_team_management);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        // Initialize executor service
        executor = Executors.newSingleThreadExecutor();

        initializeViews();
        setupToolbar();
        loadUserDropdownData();
    }

    private void initializeViews() {
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        dataContainer = findViewById(R.id.dataContainer);
        dataContent = findViewById(R.id.dataContent);

        // Setup click listeners
        showDataButton.setOnClickListener(v -> showPartnerData());
        resetButton.setOnClickListener(v -> resetData());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Partner Team Management");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void loadUserDropdownData() {
        executor.execute(() -> {
            try {
                // Call API to get RBH users for dropdown
                String url = BASE_URL + "get_rbh_users.php";
                JSONObject request = new JSONObject();
                request.put("user_id", userId);
                
                String response = makeHttpRequest(url, request.toString());
                Log.d(TAG, "RBH Users API response: " + response);
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray users = jsonResponse.getJSONArray("data");
                        List<String> userNames = new ArrayList<>();
                        List<String> userIds = new ArrayList<>();
                        
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            userNames.add(user.getString("name"));
                            userIds.add(user.getString("id"));
                        }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, 
                                android.R.layout.simple_spinner_item, 
                                userNames
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            userDropdown.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(this, "Error loading RBH users: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "Error loading RBH users: Unknown error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading RBH users", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading RBH users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showPartnerData() {
        if (userDropdown.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String selectedUserName = userDropdown.getSelectedItem().toString();
        Log.d(TAG, "Showing data for user: " + selectedUserName);
        
        // Show loading state
        showDataButton.setEnabled(false);
        showDataButton.setText("Loading...");
        
        executor.execute(() -> {
            try {
                // Call API to get partner data for selected user
                String url = BASE_URL + "get_cbo_partner_team_data.php";
                JSONObject request = new JSONObject();
                request.put("user_id", userId);
                request.put("selected_user_name", selectedUserName);
                
                String response = makeHttpRequest(url, request.toString());
                Log.d(TAG, "Partner data API response: " + response);
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray partnerData = jsonResponse.getJSONArray("data");
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            displayPartnerData(partnerData);
                            showDataButton.setEnabled(true);
                            showDataButton.setText("Show Data");
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(this, "Error loading data: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "Error loading data: Unknown error", Toast.LENGTH_SHORT).show();
                            }
                            showDataButton.setEnabled(true);
                            showDataButton.setText("Show Data");
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
                        showDataButton.setEnabled(true);
                        showDataButton.setText("Show Data");
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading partner data", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showDataButton.setEnabled(true);
                    showDataButton.setText("Show Data");
                });
            }
        });
    }

    private void displayPartnerData(JSONArray partnerData) {
        dataContent.removeAllViews();
        
        if (partnerData.length() == 0) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No partner data found for this user");
            noDataText.setTextSize(16);
            noDataText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noDataText.setGravity(android.view.Gravity.CENTER);
            noDataText.setPadding(32, 64, 32, 64);
            dataContent.addView(noDataText);
        } else {
            for (int i = 0; i < partnerData.length(); i++) {
                try {
                    JSONObject partner = partnerData.getJSONObject(i);
                    
                    // Create partner data card
                    LinearLayout partnerCard = new LinearLayout(this);
                    partnerCard.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    partnerCard.setOrientation(LinearLayout.VERTICAL);
                    partnerCard.setBackgroundResource(R.drawable.edit_text_background);
                    partnerCard.setPadding(16, 16, 16, 16);
                    partnerCard.setElevation(2);
                    
                    // Partner name
                    TextView nameText = new TextView(this);
                    nameText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    nameText.setText("Partner: " + partner.getString("partner_name"));
                    nameText.setTextSize(16);
                    nameText.setTypeface(null, android.graphics.Typeface.BOLD);
                    nameText.setTextColor(getResources().getColor(android.R.color.black));
                    nameText.setPadding(0, 0, 0, 8);
                    partnerCard.addView(nameText);
                    
                    // Partner details
                    if (partner.has("partner_details")) {
                        TextView detailsText = new TextView(this);
                        detailsText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        detailsText.setText("Details: " + partner.getString("partner_details"));
                        detailsText.setTextSize(14);
                        detailsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        partnerCard.addView(detailsText);
                    }
                    
                    dataContent.addView(partnerCard);
                    
                    // Add separator
                    if (i < partnerData.length() - 1) {
                        View separator = new View(this);
                        separator.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                        ));
                        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        separator.setPadding(0, 8, 0, 8);
                        dataContent.addView(separator);
                    }
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing partner data", e);
                }
            }
        }
        
        // Show the data container
        dataContainer.setVisibility(View.VISIBLE);
    }

    private void resetData() {
        // Reset dropdown selection
        userDropdown.setSelection(0);
        
        // Hide data container
        dataContainer.setVisibility(View.GONE);
        
        // Clear data content
        dataContent.removeAllViews();
        
        Toast.makeText(this, "Data reset successfully", Toast.LENGTH_SHORT).show();
    }

    private String makeHttpRequest(String url, String jsonBody) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "HTTP request failed", e);
            return null;
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to CBO Partner panel
        Intent intent = new Intent(this, CBOPartnerActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}

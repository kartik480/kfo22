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
    
    // RBH Users data
    private List<RbhUser> rbhUsersList;
    private RbhUser selectedRbhUser;
    
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
        
        // Initialize RBH users list
        rbhUsersList = new ArrayList<>();

        initializeViews();
        setupToolbar();
        loadRbhUsersForDropdown();
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

    private void loadRbhUsersForDropdown() {
        executor.execute(() -> {
            try {
                // Call API to get RBH users for dropdown
                String url = BASE_URL + "get_rbh_users_for_dropdown.php";
                JSONObject request = new JSONObject();
                request.put("user_id", userId);
                
                String response = makeHttpRequest(url, request.toString());
                Log.d(TAG, "RBH Users API response: " + response);
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray users = jsonResponse.getJSONArray("data");
                        rbhUsersList.clear();
                        
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            RbhUser rbhUser = new RbhUser(
                                user.optString("id", ""),
                                user.optString("username", ""),
                                user.optString("firstName", ""),
                                user.optString("lastName", ""),
                                user.optString("designation_id", ""),
                                user.optString("designation_name", ""),
                                user.optString("fullName", ""),
                                user.optString("displayName", "")
                            );
                            rbhUsersList.add(rbhUser);
                        }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            setupRbhUserDropdown();
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
    
    private void setupRbhUserDropdown() {
        Log.d(TAG, "Setting up RBH user dropdown. Users list size: " + rbhUsersList.size());
        
        if (rbhUsersList.isEmpty()) {
            Log.e(TAG, "RBH users list is empty, cannot setup dropdown");
            Toast.makeText(this, "No RBH users found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ArrayAdapter<RbhUser> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            rbhUsersList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userDropdown.setAdapter(adapter);
        
        // Add item selection listener
        userDropdown.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedRbhUser = (RbhUser) parent.getItemAtPosition(position);
                Log.d(TAG, "Selected RBH user: " + selectedRbhUser.getDisplayName() + " (ID: " + selectedRbhUser.getId() + ")");
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedRbhUser = null;
            }
        });
        
        Log.d(TAG, "RBH user dropdown setup completed");
    }

    private void showPartnerData() {
        if (selectedRbhUser == null) {
            Toast.makeText(this, "Please select an RBH user first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Showing partner data for RBH user: " + selectedRbhUser.getDisplayName() + " (ID: " + selectedRbhUser.getId() + ")");
        
        // Show loading state
        showDataButton.setEnabled(false);
        showDataButton.setText("Loading...");
        
        executor.execute(() -> {
            try {
                // Call API to get partner data for selected RBH user
                String url = BASE_URL + "get_cbo_partner_team_data.php";
                JSONObject request = new JSONObject();
                request.put("user_id", userId);
                request.put("selected_rbh_user_id", selectedRbhUser.getId());
                request.put("selected_rbh_user_name", selectedRbhUser.getFullName());
                
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
            noDataText.setText("No partner users found created by this RBH user");
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
                    
                    // Partner username/name
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
                    
                    // Company name
                    if (partner.has("company_name") && !partner.getString("company_name").equals("N/A")) {
                        TextView companyText = new TextView(this);
                        companyText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        companyText.setText("Company: " + partner.getString("company_name"));
                        companyText.setTextSize(14);
                        companyText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        companyText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(companyText);
                    }
                    
                    // Partner type
                    if (partner.has("partner_type") && !partner.getString("partner_type").equals("N/A")) {
                        TextView typeText = new TextView(this);
                        typeText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        typeText.setText("Type: " + partner.getString("partner_type"));
                        typeText.setTextSize(14);
                        typeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        typeText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(typeText);
                    }
                    
                    // Contact information
                    if (partner.has("phone_number") && !partner.getString("phone_number").equals("N/A")) {
                        TextView phoneText = new TextView(this);
                        phoneText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        phoneText.setText("Phone: " + partner.getString("phone_number"));
                        phoneText.setTextSize(14);
                        phoneText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        phoneText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(phoneText);
                    }
                    
                    if (partner.has("email_id") && !partner.getString("email_id").equals("N/A")) {
                        TextView emailText = new TextView(this);
                        emailText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        emailText.setText("Email: " + partner.getString("email_id"));
                        emailText.setTextSize(14);
                        emailText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        emailText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(emailText);
                    }
                    
                    // Location information
                    if (partner.has("state") && !partner.getString("state").equals("N/A")) {
                        TextView stateText = new TextView(this);
                        stateText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        stateText.setText("State: " + partner.getString("state"));
                        stateText.setTextSize(14);
                        stateText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        stateText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(stateText);
                    }
                    
                    if (partner.has("location") && !partner.getString("location").equals("N/A")) {
                        TextView locationText = new TextView(this);
                        locationText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        locationText.setText("Location: " + partner.getString("location"));
                        locationText.setTextSize(14);
                        locationText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        locationText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(locationText);
                    }
                    
                    // Status and creation info
                    TextView statusText = new TextView(this);
                    statusText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    statusText.setText("Status: " + partner.getString("status"));
                    statusText.setTextSize(14);
                    statusText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    statusText.setPadding(0, 0, 0, 4);
                    partnerCard.addView(statusText);
                    
                    if (partner.has("created_date") && !partner.getString("created_date").equals("N/A")) {
                        TextView dateText = new TextView(this);
                        dateText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        dateText.setText("Created: " + partner.getString("created_date"));
                        dateText.setTextSize(14);
                        dateText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        dateText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(dateText);
                    }
                    
                    // Created by info
                    if (partner.has("created_by")) {
                        TextView createdByText = new TextView(this);
                        createdByText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        createdByText.setText("Created by RBH: " + partner.getString("created_by"));
                        createdByText.setTextSize(14);
                        createdByText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                        createdByText.setPadding(0, 0, 0, 4);
                        partnerCard.addView(createdByText);
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
        selectedRbhUser = null;
        
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

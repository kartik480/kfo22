package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegionalBusinessHeadActiveEmpListActivity extends AppCompatActivity {
    private TextView titleText, welcomeText;
    private View backButton, refreshButton, addButton;
    private String userName, userId;
    private BottomNavigationView rbhBottomNav;
    private RecyclerView userRecyclerView;
    private RegionalBusinessHeadUserAdapter userAdapter;
    private List<RegionalBusinessHeadUser> userList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_regional_business_head_active_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging for user data
        android.util.Log.d("RBHActiveEmpList", "Intent extras received:");
        android.util.Log.d("RBHActiveEmpList", "USERNAME: " + userName);
        android.util.Log.d("RBHActiveEmpList", "USER_ID: " + userId);
        
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        if (userId == null || userId.isEmpty()) {
            android.util.Log.e("RBHActiveEmpList", "ERROR: USER_ID is null or empty!");
            
            // Try to get user ID from username if available
            if (userName != null && !userName.isEmpty()) {
                android.util.Log.d("RBHActiveEmpList", "Attempting to get user ID from username: " + userName);
                // For now, show error message
                Toast.makeText(this, "Error: User ID not found. Please go back and try again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: User ID not found. Please go back and try again.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        setupRecyclerView();
        fetchActiveUsers();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        userRecyclerView = findViewById(R.id.userRecyclerView);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
        
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Refresh button
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing active employee list...", Toast.LENGTH_SHORT).show();
            fetchActiveUsers();
        });

        // Add button
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add New Active Employee - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add active employee activity
        });

        // Bottom Navigation
        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_team) {
                Intent intent = new Intent(this, RBHTeamActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                Intent intent = new Intent(this, RBHPortfolioActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_reports) {
                Intent intent = new Intent(this, RBHReportsActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Active User Management");
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new RegionalBusinessHeadUserAdapter(userList, this);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);
    }

    private void fetchActiveUsers() {
        executorService.execute(() -> {
            try {
                // Debug: Check if userId is available
                if (userId == null || userId.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: User ID is null. Cannot fetch users.", Toast.LENGTH_LONG).show();
                        android.util.Log.e("RBHActiveEmpList", "Cannot fetch users: userId is null");
                    });
                    return;
                }
                
                // Create API request to fetch users reporting to the logged-in user
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_active_users.php?reportingTo=" + userId + "&status=active";
                
                android.util.Log.d("RBHActiveEmpList", "Fetching users from: " + apiUrl);
                android.util.Log.d("RBHActiveEmpList", "Using userId: " + userId);
                
                String response = makeGetRequest(apiUrl);
                
                runOnUiThread(() -> {
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                userList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    
                                    // Create user object with all fields from tbl_rbh_users (including password)
                                    RegionalBusinessHeadUser user = new RegionalBusinessHeadUser(
                                        getStringOrNull(userObj, "id"),
                                        getStringOrNull(userObj, "username"),
                                        getStringOrNull(userObj, "alias_name"),
                                        getStringOrNull(userObj, "first_name"),
                                        getStringOrNull(userObj, "last_name"),
                                        getStringOrNull(userObj, "Phone_number"),
                                        getStringOrNull(userObj, "email_id"),
                                        getStringOrNull(userObj, "alternative_mobile_number"),
                                        getStringOrNull(userObj, "company_name"),
                                        getStringOrNull(userObj, "branch_state_name_id"),
                                        getStringOrNull(userObj, "branch_location_id"),
                                        getStringOrNull(userObj, "bank_id"),
                                        getStringOrNull(userObj, "account_type_id"),
                                        getStringOrNull(userObj, "office_address"),
                                        getStringOrNull(userObj, "residential_address"),
                                        getStringOrNull(userObj, "aadhaar_number"),
                                        getStringOrNull(userObj, "pan_number"),
                                        getStringOrNull(userObj, "account_number"),
                                        getStringOrNull(userObj, "ifsc_code"),
                                        getStringOrNull(userObj, "rank"),
                                        getStringOrNull(userObj, "status"),
                                        getStringOrNull(userObj, "reportingTo"),
                                        getStringOrNull(userObj, "pan_img"),
                                        getStringOrNull(userObj, "aadhaar_img"),
                                        getStringOrNull(userObj, "photo_img"),
                                        getStringOrNull(userObj, "bankproof_img"),
                                        getStringOrNull(userObj, "resume_file"),
                                        getStringOrNull(userObj, "data_icons"),
                                        getStringOrNull(userObj, "work_icons"),
                                        getStringOrNull(userObj, "user_id"),
                                        getStringOrNull(userObj, "createdBy"),
                                        getStringOrNull(userObj, "created_at"),
                                        getStringOrNull(userObj, "updated_at"),
                                        // No resolved fields from JOINs - set to null
                                        null, // reporting_to_username
                                        null, // reporting_to_first_name
                                        null, // reporting_to_last_name
                                        null, // state_name
                                        null, // location_name
                                        null, // bank_name
                                        null  // account_type_name
                                    );
                                    userList.add(user);
                                }
                                
                                userAdapter.notifyDataSetChanged();
                                
                                if (userList.isEmpty()) {
                                    Toast.makeText(this, "No active users found reporting to you", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Found " + userList.size() + " active users", Toast.LENGTH_SHORT).show();
                                }
                                
                                // Log debug information if available
                                if (jsonResponse.has("debug_info")) {
                                    JSONObject debugInfo = jsonResponse.getJSONObject("debug_info");
                                    android.util.Log.d("RBHActiveEmpList", "Debug Info: " + debugInfo.toString());
                                }
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(this, "API Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                android.util.Log.e("RBHActiveEmpList", "API Error: " + errorMessage);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            android.util.Log.e("RBHActiveEmpList", "JSON Parse Error: " + e.getMessage(), e);
                        }
                    } else {
                        Toast.makeText(this, "No response from server. Please check your connection.", Toast.LENGTH_LONG).show();
                        android.util.Log.e("RBHActiveEmpList", "No response from server");
                    }
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    android.util.Log.e("RBHActiveEmpList", "Fetch Error: " + e.getMessage(), e);
                });
            }
        });
    }

    // Helper method to safely get string values from JSON
    private String getStringOrNull(JSONObject json, String key) {
        try {
            if (json.has(key) && !json.isNull(key)) {
                return json.getString(key);
            }
        } catch (JSONException e) {
            android.util.Log.w("RBHActiveEmpList", "Error getting " + key + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String makeGetRequest(String url) {
        try {
            java.net.URL apiUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                // Handle different HTTP response codes
                java.io.BufferedReader errorReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getErrorStream())
                );
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                
                String errorMessage = "HTTP Error " + responseCode + ": " + errorResponse.toString();
                android.util.Log.e("RBHActiveEmpList", errorMessage);
                return null;
            }
        } catch (Exception e) {
            android.util.Log.e("RBHActiveEmpList", "Network error: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 
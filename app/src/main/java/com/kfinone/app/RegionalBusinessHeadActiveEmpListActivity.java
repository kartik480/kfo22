package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionalBusinessHeadActiveEmpListActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // User data
    private String userName;
    private String userId;
    private String sourcePanel;
    
    // UI elements
    private ListView employeeListView;
    private ProgressBar loadingProgress;
    private TextView errorMessage;
    private TextView employeeCount;
    
    // Data
    private RBHEmployeeAdapter employeeAdapter;
    private List<Map<String, Object>> employeeList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_active_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        // Debug: Log the received values
        Log.d("RBHActiveEmpList", "Received userName: " + userName);
        Log.d("RBHActiveEmpList", "Received userId: " + userId);
        Log.d("RBHActiveEmpList", "Received sourcePanel: " + sourcePanel);
        
        // Check for other possible username fields
        String username = intent.getStringExtra("username");
        String userUsername = intent.getStringExtra("user_username");
        Log.d("RBHActiveEmpList", "Received username: " + username);
        Log.d("RBHActiveEmpList", "Received user_username: " + userUsername);
        
        // Use the correct username field - prioritize username over userName
        if (username != null && !username.isEmpty()) {
            userName = username;
        } else if (userUsername != null && !userUsername.isEmpty()) {
            userName = userUsername;
        }
        
        // If we still don't have the correct username, use the known value
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Use default RBH username
            Log.d("RBHActiveEmpList", "Using default username: " + userName);
        }
        
        Log.d("RBHActiveEmpList", "Final userName to use: " + userName);
        
        initializeViews();
        setupClickListeners();
        initializeData();
        loadActiveEmployeeList();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        
        // Employee list views
        employeeListView = findViewById(R.id.employeeListView);
        loadingProgress = findViewById(R.id.loadingProgress);
        errorMessage = findViewById(R.id.errorMessage);
        employeeCount = findViewById(R.id.employeeCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmployee());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
                startActivity(intent);
                finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHEmpLinksActivity.class);
            passUserDataToIntent(intent);
                startActivity(intent);
                finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });
    }

    private void goBack() {
        Intent intent;
        
        // Navigate back based on source panel
        if ("RBH_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        } else if ("RBH_EMP_MASTER".equals(sourcePanel)) {
            intent = new Intent(this, RBHEmpMasterActivity.class);
        } else {
            // Default fallback
            intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        }
        
        passUserDataToIntent(intent);
                startActivity(intent);
                finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing active employee list...", Toast.LENGTH_SHORT).show();
        loadActiveEmployeeList();
    }

    private void addNewEmployee() {
        Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to add employee activity
    }

    private void initializeData() {
        employeeList = new ArrayList<>();
        employeeAdapter = new RBHEmployeeAdapter(this, employeeList);
        employeeListView.setAdapter(employeeAdapter);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadActiveEmployeeList() {
        Log.d("RBHActiveEmpList", "Loading employee list for username: " + userName);
        
        if (userName == null || userName.isEmpty()) {
            Log.e("RBHActiveEmpList", "Username is null or empty");
            showError("Username not available");
                    return;
                }
                
        showLoading(true);
        hideError();

        // API URL with reportingTo parameter - use username instead of userId
        String url = "https://emp.kfinone.com/mobile/api/get_rbh_active_emp_list.php?reportingTo=" + userName;
        Log.d("RBHActiveEmpList", "API URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showLoading(false);
                    try {
                        // Debug: Log the full response
                        Log.d("RBHActiveEmpList", "API Response: " + response.toString());
                        
                        String status = response.getString("status");
                        Log.d("RBHActiveEmpList", "Status: " + status);
                        
                        if ("success".equals(status)) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray teamMembers = data.getJSONArray("team_members");
                            
                            Log.d("RBHActiveEmpList", "Team members count: " + teamMembers.length());
                            
                            List<Map<String, Object>> newEmployeeList = new ArrayList<>();
                            
                            for (int i = 0; i < teamMembers.length(); i++) {
                                JSONObject employee = teamMembers.getJSONObject(i);
                                Map<String, Object> employeeMap = new HashMap<>();
                                
                                // Convert JSONObject to Map
                                JSONArray keys = employee.names();
                                if (keys != null) {
                                    for (int j = 0; j < keys.length(); j++) {
                                        String key = keys.getString(j);
                                        Object value = employee.get(key);
                                        employeeMap.put(key, value);
                                    }
                                }
                                
                                newEmployeeList.add(employeeMap);
                                Log.d("RBHActiveEmpList", "Added employee: " + employeeMap.get("full_name"));
                            }
                            
                            Log.d("RBHActiveEmpList", "Final employee list size: " + newEmployeeList.size());
                            
                            // Update adapter
                            employeeAdapter.updateData(newEmployeeList);
                            
                            // Update count
                            int count = newEmployeeList.size();
                            employeeCount.setText("Total Employees: " + count);
                            
                            if (count == 0) {
                                showError("No employees found reporting to you");
                            } else {
                                // Show success message
                                Toast.makeText(RegionalBusinessHeadActiveEmpListActivity.this, 
                                    "Found " + count + " employee(s)", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                            String message = response.optString("message", "Unknown error occurred");
                            Log.e("RBHActiveEmpList", "API Error: " + message);
                            showError(message);
                            }
                        } catch (JSONException e) {
                        Log.e("RBHActiveEmpList", "JSON Parse Error: " + e.getMessage());
                        showError("Error parsing response: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showLoading(false);
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage = "Server error: " + error.networkResponse.statusCode;
                    }
                    Log.e("RBHActiveEmpList", "Volley Error: " + errorMessage);
                    showError(errorMessage);
                }
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        employeeListView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        employeeListView.setVisibility(View.GONE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
        employeeListView.setVisibility(View.VISIBLE);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
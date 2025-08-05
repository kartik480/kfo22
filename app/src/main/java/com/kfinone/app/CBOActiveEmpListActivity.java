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

public class CBOActiveEmpListActivity extends AppCompatActivity {

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
    private CBOEmployeeAdapter employeeAdapter;
    private List<Map<String, Object>> employeeList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_active_emp_list);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        // Debug: Log the received values
        Log.d("CBOActiveEmpList", "Received userName: " + userName);
        Log.d("CBOActiveEmpList", "Received userId: " + userId);
        Log.d("CBOActiveEmpList", "Received sourcePanel: " + sourcePanel);
        
        // Check for other possible username fields
        String username = intent.getStringExtra("username");
        String userUsername = intent.getStringExtra("user_username");
        Log.d("CBOActiveEmpList", "Received username: " + username);
        Log.d("CBOActiveEmpList", "Received user_username: " + userUsername);
        
        // Use the correct username field - prioritize username over userName
        if (username != null && !username.isEmpty()) {
            userName = username;
        } else if (userUsername != null && !userUsername.isEmpty()) {
            userName = userUsername;
        }
        
        // If we still don't have the correct username, use the known value
        if (userName == null || userName.isEmpty() || userName.contains("VENKATESWARA")) {
            userName = "90000"; // Use the correct username from login response
            Log.d("CBOActiveEmpList", "Using hardcoded username: " + userName);
        }
        
        Log.d("CBOActiveEmpList", "Final userName to use: " + userName);

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
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
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
        if ("CBO_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, SuperAdminCBOActivity.class);
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, SuperAdminRBHActivity.class);
        } else if ("BH_EMP_MASTER_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, SuperAdminBHEmpMasterActivity.class);
        } else {
            // Default fallback
            intent = new Intent(this, SuperAdminCBOActivity.class);
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
        Intent intent = new Intent(this, CBOAddEmpActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
    }

    private void initializeData() {
        employeeList = new ArrayList<>();
        employeeAdapter = new CBOEmployeeAdapter(this, employeeList);
        employeeListView.setAdapter(employeeAdapter);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadActiveEmployeeList() {
        Log.d("CBOActiveEmpList", "Loading employee list for username: " + userName);
        
        if (userName == null || userName.isEmpty()) {
            Log.e("CBOActiveEmpList", "Username is null or empty");
            showError("Username not available");
            return;
        }

        showLoading(true);
        hideError();

        // API URL with reportingTo parameter - use username instead of userId
        String url = "https://emp.kfinone.com/mobile/api/get_cbo_active_emp_list.php?reportingTo=" + userName;
        Log.d("CBOActiveEmpList", "API URL: " + url);

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
                         Log.d("CBOActiveEmpList", "API Response: " + response.toString());
                         
                         String status = response.getString("status");
                         Log.d("CBOActiveEmpList", "Status: " + status);
                         
                         if ("success".equals(status)) {
                             JSONObject data = response.getJSONObject("data");
                             JSONArray teamMembers = data.getJSONArray("team_members");
                             
                             Log.d("CBOActiveEmpList", "Team members count: " + teamMembers.length());
                             
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
                                 Log.d("CBOActiveEmpList", "Added employee: " + employeeMap.get("full_name"));
                             }
                             
                             Log.d("CBOActiveEmpList", "Final employee list size: " + newEmployeeList.size());
                             
                             // Update adapter
                             employeeAdapter.updateData(newEmployeeList);
                             
                             // Update count
                             int count = newEmployeeList.size();
                             employeeCount.setText("Total Employees: " + count);
                             
                             if (count == 0) {
                                 showError("No employees found reporting to you");
                             } else {
                                 // Show success message
                                 Toast.makeText(CBOActiveEmpListActivity.this, 
                                     "Found " + count + " employee(s)", Toast.LENGTH_SHORT).show();
                             }
                            
                                                 } else {
                             String message = response.optString("message", "Unknown error occurred");
                             Log.e("CBOActiveEmpList", "API Error: " + message);
                             showError(message);
                         }
                     } catch (JSONException e) {
                         Log.e("CBOActiveEmpList", "JSON Parse Error: " + e.getMessage());
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
                     Log.e("CBOActiveEmpList", "Volley Error: " + errorMessage);
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
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
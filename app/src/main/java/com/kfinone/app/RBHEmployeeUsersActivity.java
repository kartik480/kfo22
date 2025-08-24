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

public class RBHEmployeeUsersActivity extends AppCompatActivity {

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
    private String firstName;
    private String lastName;
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
        setContentView(R.layout.activity_rbh_employee_users);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        // Debug: Log the received values
        Log.d("RBHEmployeeUsers", "Received userName: " + userName);
        Log.d("RBHEmployeeUsers", "Received userId: " + userId);
        Log.d("RBHEmployeeUsers", "Received firstName: " + firstName);
        Log.d("RBHEmployeeUsers", "Received lastName: " + lastName);
        Log.d("RBHEmployeeUsers", "Received sourcePanel: " + sourcePanel);

        initializeViews();
        setupClickListeners();
        initializeData();
        loadRBHEmployeeList();
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
        Toast.makeText(this, "Refreshing RBH employee list...", Toast.LENGTH_SHORT).show();
        loadRBHEmployeeList();
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

    private void loadRBHEmployeeList() {
        Log.d("RBHEmployeeUsers", "Loading employee list for username: " + userName);
        
        if (userName == null || userName.isEmpty()) {
            Log.e("RBHEmployeeUsers", "Username is null or empty");
            showError("Username not available");
            return;
        }

        showLoading(true);
        hideError();

        // API URL for RBH employee list - using API that works with tbl_user table
        String url = "https://emp.kfinone.com/mobile/api/get_rbh_reporting_users.php?user_id=" + userId;
        Log.d("RBHEmployeeUsers", "API URL: " + url);

        // Create GET request instead of POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null, // No request body for GET
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showLoading(false);
                    try {
                        // Debug: Log the full response
                        Log.d("RBHEmployeeUsers", "API Response: " + response.toString());
                        
                        String status = response.getString("status");
                        Log.d("RBHEmployeeUsers", "Status: " + status);
                        
                        if ("success".equals(status)) {
                            JSONArray teamMembers = response.getJSONArray("data");
                            
                            Log.d("RBHEmployeeUsers", "Team members count: " + teamMembers.length());
                            
                            List<Map<String, Object>> newEmployeeList = new ArrayList<>();
                            
                            for (int i = 0; i < teamMembers.length(); i++) {
                                JSONObject member = teamMembers.getJSONObject(i);
                                
                                Map<String, Object> employee = new HashMap<>();
                                employee.put("id", member.optString("id", "N/A"));
                                employee.put("username", member.optString("username", "N/A"));
                                employee.put("first_name", member.optString("first_name", "N/A"));
                                employee.put("last_name", member.optString("last_name", "N/A"));
                                employee.put("email_id", member.optString("email_id", "N/A"));
                                employee.put("Phone_number", member.optString("Phone_number", "N/A"));
                                employee.put("company_name", "N/A"); // Not provided by this API
                                employee.put("status", member.optString("status", "N/A"));
                                employee.put("rank", "N/A"); // Not provided by this API
                                employee.put("reportingTo", member.optString("reportingTo", "N/A"));
                                
                                newEmployeeList.add(employee);
                            }
                            
                            employeeList.clear();
                            employeeList.addAll(newEmployeeList);
                            employeeAdapter.notifyDataSetChanged();
                            
                            // Update employee count
                            employeeCount.setText("Total Employees: " + employeeList.size());
                            
                            if (employeeList.isEmpty()) {
                                showEmptyState();
                            } else {
                                hideEmptyState();
                                Toast.makeText(RBHEmployeeUsersActivity.this, 
                                    "Found " + employeeList.size() + " employees", 
                                    Toast.LENGTH_SHORT).show();
                            }
                            
                        } else {
                            String message = response.optString("message", "Unknown error");
                            showError("API Error: " + message);
                        }
                        
                    } catch (JSONException e) {
                        Log.e("RBHEmployeeUsers", "JSON Parse Error: " + e.getMessage());
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
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    showError(errorMessage);
                    Log.e("RBHEmployeeUsers", "Volley Error: " + error.getMessage());
                }
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        employeeListView.setVisibility(show ? View.GONE : View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
    }

    private void showError(String message) {
        showLoading(false);
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        employeeListView.setVisibility(View.GONE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        showLoading(false);
        errorMessage.setText("No employees found for this RBH user");
        errorMessage.setVisibility(View.VISIBLE);
        employeeListView.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        errorMessage.setVisibility(View.GONE);
        employeeListView.setVisibility(View.VISIBLE);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("SOURCE_PANEL", "RBH_EMPLOYEE_USERS");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
}

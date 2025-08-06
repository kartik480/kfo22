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

public class CBOMyActiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "CBOMyActiveEmpList";

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
    private String userIdNumber;
    
    // UI elements
    private ListView employeeListView;
    private ProgressBar loadingProgress;
    private TextView errorMessage;
    private TextView employeeCount;
    private LinearLayout emptyStateLayout;
    
    // Data
    private CBOMyEmployeeAdapter employeeAdapter;
    private List<Map<String, Object>> employeeList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_my_active_emp_list);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        userIdNumber = intent.getStringExtra("USER_ID_NUMBER");
        
        // Debug: Log ALL intent extras to understand what's being passed
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "=== All Intent Extras ===");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, key + " = " + value + " (type: " + (value != null ? value.getClass().getSimpleName() : "null") + ")");
            }
            Log.d(TAG, "========================");
        }
        
        // Debug: Log the received values
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);
        Log.d(TAG, "Received userIdNumber: " + userIdNumber);
        
        // Check for other possible user ID fields
        String username = intent.getStringExtra("username");
        String userUsername = intent.getStringExtra("user_username");
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");
        Log.d(TAG, "Received username: " + username);
        Log.d(TAG, "Received user_username: " + userUsername);
        Log.d(TAG, "Received FIRST_NAME: " + firstName);
        Log.d(TAG, "Received LAST_NAME: " + lastName);
        
        // Determine the correct user ID to use - should work for ANY designated user
        // Check if userId is numeric (valid database ID)
        boolean isNumericUserId = userId != null && userId.matches("\\d+");
        
        if (isNumericUserId) {
            // Use the USER_ID if it's a valid numeric ID (this is the correct approach)
            Log.d(TAG, "Using numeric USER_ID: " + userId);
        } else if (userIdNumber != null && !userIdNumber.isEmpty() && userIdNumber.matches("\\d+")) {
            userId = userIdNumber;
            Log.d(TAG, "Using numeric USER_ID_NUMBER: " + userId);
        } else {
            // Critical issue: We're not getting the numeric user ID properly
            // This suggests the login data isn't being passed correctly through the intent chain
            Log.e(TAG, "CRITICAL: No numeric user ID found! This indicates a data flow issue.");
            Log.e(TAG, "Expected: USER_ID should contain the numeric ID from login (e.g., '21')");
            Log.e(TAG, "Received: userName='" + userName + "', userId='" + userId + "'");
            
            // Data flow issue: USER_ID should be passed from login through the intent chain
            // This indicates a problem in the earlier activities not passing USER_ID properly
            Log.e(TAG, "No numeric user ID found in intent chain!");
            Log.e(TAG, "This means the login USER_ID is not being passed properly through:");
            Log.e(TAG, "Login → ChiefBusinessOfficerPanelActivity → CBOEmployeeActivity → CBOMyActiveEmpListActivity");
            
            // Initialize views first to avoid null pointer exceptions
            initializeViews();
            setupClickListeners();
            initializeData();
            
            // Show error to the user since we can't proceed without any identifier
            showError("Unable to load employee list. User ID not found. Please check the data flow from login.");
            return; // Don't proceed with the API call
        }
        
        Log.d(TAG, "Final userId to use: " + userId);

        initializeViews();
        setupClickListeners();
        initializeData();
        loadMyEmployeeList();
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

        // UI elements
        employeeListView = findViewById(R.id.employeeListView);
        loadingProgress = findViewById(R.id.loadingProgress);
        errorMessage = findViewById(R.id.errorMessage);
        employeeCount = findViewById(R.id.employeeCount);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
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
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeData() {
        employeeList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        employeeAdapter = new CBOMyEmployeeAdapter(this, employeeList);
        employeeListView.setAdapter(employeeAdapter);
    }

    private void loadMyEmployeeList() {
        if (userId == null || userId.isEmpty()) {
            showError("User ID not available. Please login again.");
            return;
        }

        showLoading();
        
        String url = "https://emp.kfinone.com/mobile/api/cbo_my_emp_list.php?user_id=" + userId;
        Log.d(TAG, "Loading employee list from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoading();
                    processEmployeeListResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoading();
                    String errorMsg = "Network error occurred";
                    if (error.networkResponse != null) {
                        errorMsg += " (Code: " + error.networkResponse.statusCode + ")";
                    }
                    Log.e(TAG, "Error loading employee list: " + error.getMessage());
                    showError(errorMsg);
                }
            }
        );

        requestQueue.add(request);
    }

    private void processEmployeeListResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            
            if ("success".equals(status)) {
                JSONObject data = response.getJSONObject("data");
                JSONArray employeesArray = data.getJSONArray("employees");
                JSONObject statistics = data.getJSONObject("statistics");
                
                // Clear existing employee list
                employeeList.clear();
                
                // Process each employee
                for (int i = 0; i < employeesArray.length(); i++) {
                    JSONObject employeeJson = employeesArray.getJSONObject(i);
                    Map<String, Object> employee = new HashMap<>();
                    
                    employee.put("id", employeeJson.optString("id", ""));
                    employee.put("full_name", employeeJson.optString("full_name", ""));
                    employee.put("firstName", employeeJson.optString("firstName", ""));
                    employee.put("lastName", employeeJson.optString("lastName", ""));
                    employee.put("employee_id", employeeJson.optString("employee_id", ""));
                    employee.put("mobile", employeeJson.optString("mobile", ""));
                    employee.put("email", employeeJson.optString("email", ""));
                    employee.put("designation_id", employeeJson.optString("designation_id", ""));
                    employee.put("department_id", employeeJson.optString("department_id", ""));
                    employee.put("status", employeeJson.optString("status", ""));
                    employee.put("reportingTo", employeeJson.optString("reportingTo", ""));
                    employee.put("manage_icons_string", employeeJson.optString("manage_icons_string", ""));
                    
                    // Handle manage_icons array
                    JSONArray manageIconsArray = employeeJson.optJSONArray("manage_icons");
                    List<String> manageIcons = new ArrayList<>();
                    if (manageIconsArray != null) {
                        for (int j = 0; j < manageIconsArray.length(); j++) {
                            manageIcons.add(manageIconsArray.getString(j));
                        }
                    }
                    employee.put("manage_icons", manageIcons);
                    
                    employeeList.add(employee);
                }
                
                // Update UI
                updateEmployeeCountDisplay(statistics.getInt("employee_count"));
                employeeAdapter.updateEmployees(employeeList);
                
                // Show/hide empty state
                if (employeeList.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                }
                
                Log.d(TAG, "Successfully loaded " + employeeList.size() + " employees");
                
            } else {
                String errorMsg = response.optString("message", "Failed to load employee list");
                showError(errorMsg);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response: " + e.getMessage());
            showError("Error processing response data");
        }
    }

    private void updateEmployeeCountDisplay(int count) {
        String countText = "Total Employees: " + count;
        employeeCount.setText(countText);
    }

    private void showLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        employeeListView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingProgress.setVisibility(View.GONE);
    }

    private void showError(String message) {
        hideLoading();
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        employeeListView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        hideLoading();
        errorMessage.setVisibility(View.GONE);
        employeeListView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        employeeListView.setVisibility(View.VISIBLE);
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOEmployeeActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing employee data...", Toast.LENGTH_SHORT).show();
        loadMyEmployeeList();
    }

    private void addNewEmployee() {
        Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to add employee activity
    }

    private void passUserDataToIntent(Intent intent) {
        if (userName != null) {
            intent.putExtra("USERNAME", userName);
        }
        if (userId != null) {
            intent.putExtra("USER_ID", userId);
        }
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }
}
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class MarketingHeadInactiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadInactiveEmp";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView totalCountText;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    
    private MarketingHeadInactiveEmpListAdapter adapter;
    private List<MarketingHeadUser> employees;
    private RequestQueue requestQueue;
    
    // API URL for inactive employees (status = 0)
    private static final String API_URL = "https://emp.kfinone.com/mobile/api/get_inactive_employees.php";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_inactive_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadInactiveEmpListActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadInactiveEmpListActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupRecyclerView();

        
        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // Setup refresh button
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadInactiveEmployees());
        
        // Update welcome text
        updateWelcomeText();
        
        // Load inactive employees
        loadInactiveEmployees();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalCountText = findViewById(R.id.totalCountText);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
    }
    
    private void setupRecyclerView() {
        employees = new ArrayList<>();
        adapter = new MarketingHeadInactiveEmpListAdapter(employees, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - Inactive Employee List");
        } else {
            welcomeText.setText("Welcome, Marketing Head - Inactive Employee List");
        }
    }
    
    private void loadInactiveEmployees() {
        showProgress(true);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            API_URL,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showProgress(false);
                    
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        
                        if (success) {
                            JSONArray employeesArray = response.getJSONArray("employees");
                            int count = response.getInt("count");
                            
                            // Clear existing list
                            employees.clear();
                            
                                                         // Parse employees
                             for (int i = 0; i < employeesArray.length(); i++) {
                                 JSONObject empObj = employeesArray.getJSONObject(i);
                                 Log.d(TAG, "Employee " + i + " data: " + empObj.toString());
                                 MarketingHeadUser employee = parseEmployee(empObj);
                                 employees.add(employee);
                             }
                            
                            // Update adapter
                            adapter.notifyDataSetChanged();
                            
                            // Update count
                            totalCountText.setText("Total Inactive Employees: " + count);
                            
                            if (count == 0) {
                                showEmptyState();
                            } else {
                                hideEmptyState();
                            }
                            
                                                         Log.d(TAG, "Successfully loaded " + count + " inactive employees");
                             Log.d(TAG, "First employee data: " + (count > 0 ? employees.get(0).getFullName() : "No employees"));
                            
                                                 } else {
                             // Check if there's debug info
                             if (response.has("debug_info")) {
                                 try {
                                     JSONObject debugInfo = response.getJSONObject("debug_info");
                                     Log.d(TAG, "Debug info: " + debugInfo.toString());
                                 } catch (JSONException e) {
                                     Log.e(TAG, "Error parsing debug info: " + e.getMessage());
                                 }
                             }
                             showError("Failed to load inactive employees: " + message);
                         }
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        showError("Error parsing response data");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    
                    String errorMessage = "Network error occurred";
                    if (error.networkResponse != null) {
                        errorMessage = "Server error: " + error.networkResponse.statusCode;
                    }
                    
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    showError(errorMessage);
                }
            }
        );
        
        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }
    
    private MarketingHeadUser parseEmployee(JSONObject empObj) throws JSONException {
        // Log the employee data for debugging
        Log.d(TAG, "Parsing employee: " + empObj.optString("firstName", "") + " " + empObj.optString("lastName", ""));
        
        return new MarketingHeadUser(
            empObj.optString("id", ""),
            empObj.optString("username", ""),
            empObj.optString("", ""), // alias_name not in basic columns
            empObj.optString("firstName", ""),
            empObj.optString("lastName", ""),
            empObj.optString("", ""), // password not in basic columns
            empObj.optString("mobile", ""),
            empObj.optString("email_id", ""),
            empObj.optString("", ""), // dob not in basic columns
            empObj.optString("", ""), // branch_state_name_id not in basic columns
            empObj.optString("", ""), // branch_location_id not in basic columns
            empObj.optString("", ""), // acc_holder_name not in basic columns
            empObj.optString("", ""), // bank_name not in basic columns
            empObj.optString("", ""), // account_type not in basic columns
            empObj.optString("", ""), // branch_name not in basic columns
            empObj.optString("", ""), // account_number not in basic columns
            empObj.optString("", ""), // ifsc_code not in basic columns
            empObj.optString("", ""), // rank not in basic columns
            empObj.optString("status", ""),
            empObj.optString("", ""), // reportingTo not in basic columns
            empObj.optString("", ""), // designation not in basic columns
            empObj.optString("", ""), // department not in basic columns
            empObj.optString("employee_no", ""),
            empObj.optString("", ""), // work_state not in basic columns
            empObj.optString("", ""), // work_location not in basic columns
            empObj.optString("", ""), // residential_address not in basic columns
            empObj.optString("", ""), // office_address not in basic columns
            empObj.optString("", ""), // pan_number not in basic columns
            empObj.optString("", ""), // aadhaar_number not in basic columns
            empObj.optString("", ""), // manage_icons not in basic columns
            empObj.optString("", ""), // data_icons not in basic columns
            empObj.optString("", ""), // reference_name not in basic columns
            empObj.optString("", ""), // reference_relation not in basic columns
            empObj.optString("", ""), // reference_mobile not in basic columns
            empObj.optString("", ""), // reference_address not in basic columns
            empObj.optString("", ""), // reference_name2 not in basic columns
            empObj.optString("", ""), // reference_relation2 not in basic columns
            empObj.optString("", ""), // reference_mobile2 not in basic columns
            empObj.optString("", ""), // reference_address2 not in basic columns
            empObj.optString("", ""), // official_phone not in basic columns
            empObj.optString("", ""), // official_email not in basic columns
            empObj.optString("", ""), // createdBy not in basic columns
            empObj.optString("created_at", ""),
            empObj.optString("updated_at", "")
        );
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyState() {
        TextView emptyText = findViewById(R.id.emptyText);
        if (emptyText != null) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No inactive employees found in the system");
        }
    }
    
    private void hideEmptyState() {
        TextView emptyText = findViewById(R.id.emptyText);
        if (emptyText != null) {
            emptyText.setVisibility(View.GONE);
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Show error state
        TextView emptyText = findViewById(R.id.emptyText);
        if (emptyText != null) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Error: " + message + "\n\nClick Refresh button to retry");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            showToast("Settings - Coming Soon!");
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help - Inactive Employee List");
        builder.setMessage("This panel displays all inactive employees in the system.\n\n" +
                "Features:\n" +
                "• View all inactive employees with their details\n" +
                "• Click Refresh button to reload the list\n" +
                "• Click 'View' button to see complete employee information\n" +
                "• Search and filter options coming soon\n\n" +
                "The list shows:\n" +
                "• Employee names and IDs\n" +
                "• Contact information\n" +
                "• Department and designation\n" +
                "• Branch and location\n" +
                "• Status and joining date\n\n" +
                "Note: Inactive employees have status = 0");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Inactive Employee List");
        builder.setMessage("Inactive Employee List Panel v1.0\n\n" +
                "This panel provides comprehensive access to all inactive employees including:\n" +
                "• Complete employee profiles\n" +
                "• Contact and work information\n" +
                "• Location and status details\n" +
                "• Real-time data from the database\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}

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
                            
                        } else {
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
        return new MarketingHeadUser(
            empObj.optString("id", ""),
            empObj.optString("username", ""),
            empObj.optString("alias_name", ""),
            empObj.optString("firstName", ""),
            empObj.optString("lastName", ""),
            empObj.optString("password", ""),
            empObj.optString("mobile", ""),
            empObj.optString("email_id", ""),
            empObj.optString("dob", ""), // Now using dob from tbl_user
            empObj.optString("branch_state_name_id", ""),
            empObj.optString("branch_location_id", ""),
            empObj.optString("acc_holder_name", ""),
            empObj.optString("bank_name", ""),
            empObj.optString("", ""), // account_type not in tbl_user
            empObj.optString("branch_name", ""),
            empObj.optString("account_number", ""),
            empObj.optString("ifsc_code", ""),
            empObj.optString("rank", ""),
            empObj.optString("status", ""),
            empObj.optString("reportingTo", ""),
            empObj.optString("", ""), // designation not in tbl_user, using designation_id
            empObj.optString("", ""), // department not in tbl_user, using department_id
            empObj.optString("employee_no", ""),
            empObj.optString("work_state", ""),
            empObj.optString("work_location", ""),
            empObj.optString("residential_address", ""),
            empObj.optString("office_address", ""),
            empObj.optString("pan_number", ""),
            empObj.optString("aadhaar_number", ""),
            empObj.optString("manage_icons", ""),
            empObj.optString("data_icons", ""),
            empObj.optString("reference_name", ""),
            empObj.optString("reference_relation", ""),
            empObj.optString("reference_mobile", ""),
            empObj.optString("reference_address", ""),
            empObj.optString("reference_name2", ""),
            empObj.optString("reference_relation2", ""),
            empObj.optString("reference_mobile2", ""),
            empObj.optString("reference_address2", ""),
            empObj.optString("official_phone", ""),
            empObj.optString("official_email", ""),
            empObj.optString("createdBy", ""),
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

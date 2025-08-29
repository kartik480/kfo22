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

public class MarketingHeadInactiveSDSAListActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadInactiveSDSA";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView totalCountText;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    
    private SdsaUserAdapter adapter;
    private List<SdsaUser> sdsaUsers;
    private RequestQueue requestQueue;
    
    // API URL for inactive users (status = 0)
    private static final String API_URL = "https://emp.kfinone.com/mobile/api/get_inactive_sdsa_users.php";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_inactive_sdsa_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadInactiveSDSAListActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadInactiveSDSAListActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupRecyclerView();

        
        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // Setup refresh button
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadInactiveSDSUsers());
        
        // Update welcome text
        updateWelcomeText();
        
        // Load inactive SDSA users
        loadInactiveSDSUsers();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalCountText = findViewById(R.id.totalCountText);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
    }
    
    private void setupRecyclerView() {
        sdsaUsers = new ArrayList<>();
        adapter = new SdsaUserAdapter(sdsaUsers, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - Inactive SDSA List");
        } else {
            welcomeText.setText("Welcome, Marketing Head - Inactive SDSA List");
        }
    }
    
    private void loadInactiveSDSUsers() {
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
                            JSONArray usersArray = response.getJSONArray("users");
                            int count = response.getInt("count");
                            
                            // Clear existing list
                            sdsaUsers.clear();
                            
                            // Parse users
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObj = usersArray.getJSONObject(i);
                                SdsaUser user = parseSdsaUser(userObj);
                                sdsaUsers.add(user);
                            }
                            
                            // Update adapter
                            adapter.notifyDataSetChanged();
                            
                            // Update count
                            totalCountText.setText("Total Inactive SDSA Users: " + count);
                            
                            if (count == 0) {
                                showEmptyState();
                            } else {
                                hideEmptyState();
                            }
                            
                            Log.d(TAG, "Successfully loaded " + count + " inactive SDSA users");
                            
                        } else {
                            showError("Failed to load inactive SDSA users: " + message);
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
    
    private SdsaUser parseSdsaUser(JSONObject userObj) throws JSONException {
        return new SdsaUser(
            userObj.optString("id", ""),
            userObj.optString("username", ""),
            userObj.optString("alias_name", ""),
            userObj.optString("first_name", ""),
            userObj.optString("last_name", ""),
            userObj.optString("Phone_number", ""),
            userObj.optString("email_id", ""),
            userObj.optString("alternative_mobile_number", ""),
            userObj.optString("company_name", ""),
            userObj.optString("branch_state_name_id", ""),
            userObj.optString("branch_location_id", ""),
            userObj.optString("bank_id", ""),
            userObj.optString("account_type_id", ""),
            userObj.optString("office_address", ""),
            userObj.optString("residential_address", ""),
            userObj.optString("aadhaar_number", ""),
            userObj.optString("pan_number", ""),
            userObj.optString("account_number", ""),
            userObj.optString("ifsc_code", ""),
            userObj.optString("rank", ""),
            userObj.optString("status", ""),
            userObj.optString("reportingTo", ""),
            userObj.optString("employee_no", ""),
            userObj.optString("department", ""),
            userObj.optString("designation", ""),
            // Use joined fields when available, fallback to original fields
            userObj.optString("branch_state_name", userObj.optString("branchstate", "")),
            userObj.optString("branch_location", userObj.optString("branchloaction", "")),
            userObj.optString("actual_bank_name", userObj.optString("bank_name", "")),
            userObj.optString("actual_account_type", userObj.optString("account_type", "")),
            userObj.optString("user_id", ""),
            userObj.optString("createdBy", ""),
            userObj.optString("created_at", ""),
            userObj.optString("updated_at", ""),
            userObj.optString("fullName", ""),
            userObj.optString("displayName", ""),
            userObj.optString("password", ""),
            userObj.optString("pan_img", ""),
            userObj.optString("aadhaar_img", ""),
            userObj.optString("photo_img", ""),
            userObj.optString("bankproof_img", "")
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
            emptyText.setText("No inactive SDSA users found in the system");
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
        builder.setTitle("Help - Inactive SDSA List");
        builder.setMessage("This panel displays all inactive SDSA users in the system.\n\n" +
                "Features:\n" +
                "• View all inactive SDSA users with their details\n" +
                "• Click Refresh button to reload the list\n" +
                "• Click 'View' button to see complete user information\n" +
                "• Search and filter options coming soon\n\n" +
                "The list shows:\n" +
                "• User names and aliases\n" +
                "• Contact information\n" +
                "• Bank details\n" +
                "• Location information\n" +
                "• Status and rank\n\n" +
                "Note: Inactive users have status = 0");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Inactive SDSA List");
        builder.setMessage("Inactive SDSA List Panel v1.0\n\n" +
                "This panel provides comprehensive access to all inactive SDSA users including:\n" +
                "• Complete user profiles\n" +
                "• Contact and bank information\n" +
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

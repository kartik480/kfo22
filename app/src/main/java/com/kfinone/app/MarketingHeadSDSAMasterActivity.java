package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarketingHeadSDSAMasterActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadSDSAMaster";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView totalSDSACount;
    private TextView activeSDSACount;
    private TextView inactiveSDSACount;
    
    // Card Views
    private CardView cardActiveSDSAList;
    private CardView cardInactiveSDSAList;
    
    // API URLs
    private static final String TOTAL_SDSA_API_URL = "https://emp.kfinone.com/mobile/api/get_total_sdsa_count.php";
    
    // Volley request queue
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_sdsa_master);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadSDSAMasterActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadSDSAMasterActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupClickListeners();
        updateWelcomeText();
        
        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // Load SDSA counts
        loadSDSACounts();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalSDSACount = findViewById(R.id.totalSDSACount);
        activeSDSACount = findViewById(R.id.activeSDSACount);
        inactiveSDSACount = findViewById(R.id.inactiveSDSACount);
        
        cardActiveSDSAList = findViewById(R.id.cardActiveSDSAList);
        cardInactiveSDSAList = findViewById(R.id.cardInactiveSDSAList);
    }
    
    private void setupClickListeners() {
        // Active SDSA List
        cardActiveSDSAList.setOnClickListener(v -> {
            Intent intent = new Intent(MarketingHeadSDSAMasterActivity.this, MarketingHeadActiveSDSAListActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
        
        // Inactive SDSA List
        cardInactiveSDSAList.setOnClickListener(v -> {
            Intent intent = new Intent(MarketingHeadSDSAMasterActivity.this, MarketingHeadInactiveSDSAListActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }
    
    private void loadSDSACounts() {
        // Set initial values
        totalSDSACount.setText("0");
        activeSDSACount.setText("0");
        inactiveSDSACount.setText("0");
        
        Log.d(TAG, "Setting initial values - Total: 0, Active: 0, Inactive: 0");
        
        // Load all SDSA counts from single API
        loadTotalSDSACount();
    }
    
    private void loadTotalSDSACount() {
        Log.d(TAG, "Calling API URL: " + TOTAL_SDSA_API_URL);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            TOTAL_SDSA_API_URL,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Raw API Response: " + response.toString());
                        
                        boolean success = response.getBoolean("success");
                        Log.d(TAG, "API Success: " + success);
                        
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            Log.d(TAG, "Data object: " + data.toString());
                            
                            int totalCount = data.getInt("total_count");
                            int activeCount = data.getInt("active_count");
                            int inactiveCount = data.getInt("inactive_count");
                            
                            Log.d(TAG, "Parsed counts - Total: " + totalCount + 
                                      ", Active: " + activeCount + ", Inactive: " + inactiveCount);
                            
                            // Update all counts
                            totalSDSACount.setText(String.valueOf(totalCount));
                            activeSDSACount.setText(String.valueOf(activeCount));
                            inactiveSDSACount.setText(String.valueOf(inactiveCount));
                            
                            Log.d(TAG, "SDSA Counts loaded - Total: " + totalCount + 
                                      ", Active: " + activeCount + ", Inactive: " + inactiveCount);
                            Log.d(TAG, "Updated TextView totalSDSACount with: " + totalCount);
                            Log.d(TAG, "Updated TextView activeSDSACount with: " + activeCount);
                            Log.d(TAG, "Updated TextView inactiveSDSACount with: " + inactiveCount);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing total SDSA response: " + e.getMessage());
                        Log.e(TAG, "Full response that caused error: " + response.toString());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading total SDSA count: " + error.getMessage());
                }
            }
        );
        
        requestQueue.add(jsonObjectRequest);
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " - SDSA Master");
        } else {
            welcomeText.setText("Welcome, Marketing Head - SDSA Master");
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
            showToast("Help - Coming Soon!");
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About SDSA Master");
        builder.setMessage("SDSA Master Panel v1.0\n\n" +
                "This panel provides comprehensive SDSA management tools including:\n" +
                "• Active SDSA List\n" +
                "• Inactive SDSA List\n" +
                "• SDSA Status Management\n\n" +
                "© 2025 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}

package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class RegionalBusinessHeadDataLinksActivity extends AppCompatActivity {
    
    // UI Elements
    private View backButton, refreshButton, addButton;
    private ProgressBar loadingIndicator;
    private TextView errorMessage;
    private RecyclerView iconsRecyclerView;
    
    // Bottom Navigation
    private View dashboardButton, empLinksButton, reportsButton, settingsButton;
    
    // Data
    private String userName, userId, firstName, lastName;
    private List<DataIcon> dataIcons;
    private RegionalBusinessHeadDataLinksAdapter adapter;
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regional_business_head_data_links);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        
        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        
        // Load data
        loadDataIcons();
    }
    
    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        // Content views
        loadingIndicator = findViewById(R.id.loadingIndicator);
        errorMessage = findViewById(R.id.errorMessage);
        iconsRecyclerView = findViewById(R.id.iconsRecyclerView);
        
        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        
        // Debug logging
        Log.d("RBHDataLinks", "View initialization - backButton: " + (backButton != null ? "OK" : "NULL"));
        Log.d("RBHDataLinks", "View initialization - refreshButton: " + (refreshButton != null ? "OK" : "NULL"));
        Log.d("RBHDataLinks", "View initialization - addButton: " + (addButton != null ? "OK" : "NULL"));
        Log.d("RBHDataLinks", "View initialization - loadingIndicator: " + (loadingIndicator != null ? "OK" : "NULL"));
        Log.d("RBHDataLinks", "View initialization - errorMessage: " + (errorMessage != null ? "OK" : "NULL"));
        Log.d("RBHDataLinks", "View initialization - iconsRecyclerView: " + (iconsRecyclerView != null ? "OK" : "NULL"));
    }
    
    private void setupClickListeners() {
        // Top navigation
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBack());
        }
        if (refreshButton != null) {
            refreshButton.setOnClickListener(v -> refreshData());
        }
        if (addButton != null) {
            addButton.setOnClickListener(v -> addNewIcon());
        }
        
        // Bottom navigation
        if (dashboardButton != null) {
            dashboardButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
                finish();
            });
        }
        
        if (empLinksButton != null) {
            empLinksButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHEmpLinksActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
                finish();
            });
        }
        
        if (reportsButton != null) {
            reportsButton.setOnClickListener(v -> {
                Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void setupRecyclerView() {
        dataIcons = new ArrayList<>();
        adapter = new RegionalBusinessHeadDataLinksAdapter(this, dataIcons);
        
        if (iconsRecyclerView != null) {
            iconsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            iconsRecyclerView.setAdapter(adapter);
        }
    }
    
    private void loadDataIcons() {
        if (userId == null) {
            showError("User ID not found. Please login again.");
            return;
        }
        
        showLoading(true);
        hideError();
        
        // API URL for data icons
        String url = "https://emp.kfinone.com/mobile/api/get_rbh_data_icons.php";
        
        // Create request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
            requestBody.put("username", userName);
        } catch (JSONException e) {
            Log.e("RBHDataLinks", "Error creating request body", e);
            showError("Error preparing request");
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RBHDataLinks", "API Response: " + response.toString());
                        parseDataIconsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RBHDataLinks", "API Error: " + error.toString());
                        showError("Network error: " + error.getMessage());
                        showLoading(false);
                    }
                });
        
        requestQueue.add(request);
    }
    
    private void parseDataIconsResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            
            if ("success".equals(status)) {
                JSONArray iconsArray = response.getJSONArray("data");
                List<DataIcon> newIcons = new ArrayList<>();
                
                for (int i = 0; i < iconsArray.length(); i++) {
                    JSONObject iconObj = iconsArray.getJSONObject(i);
                    
                    DataIcon icon = new DataIcon(
                        iconObj.getInt("id"),
                        iconObj.getString("icon_name"),
                        iconObj.getString("icon_url"),
                        iconObj.getString("icon_image"),
                        iconObj.getString("icon_description")
                    );
                    
                    newIcons.add(icon);
                }
                
                updateIconsList(newIcons);
                showLoading(false);
                
            } else {
                String message = response.optString("message", "Unknown error occurred");
                showError(message);
                showLoading(false);
            }
            
        } catch (JSONException e) {
            Log.e("RBHDataLinks", "Error parsing response", e);
            showError("Error parsing server response");
            showLoading(false);
        }
    }
    
    private void updateIconsList(List<DataIcon> newIcons) {
        if (adapter != null) {
            adapter.updateData(newIcons);
            if (iconsRecyclerView != null) {
                iconsRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (iconsRecyclerView != null) {
            iconsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showError(String message) {
        if (errorMessage != null) {
            errorMessage.setText(message);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideError() {
        if (errorMessage != null) {
            errorMessage.setVisibility(View.GONE);
        }
    }
    
    private void goBack() {
        Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
    
    private void refreshData() {
        Toast.makeText(this, "Refreshing data icons...", Toast.LENGTH_SHORT).show();
        loadDataIcons();
    }
    
    private void addNewIcon() {
        Toast.makeText(this, "Add New Data Icon - Coming Soon", Toast.LENGTH_SHORT).show();
    }
    
    private void passUserDataToIntent(Intent intent) {
        intent.putExtra("USERNAME", userName);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("FIRST_NAME", firstName);
        intent.putExtra("LAST_NAME", lastName);
    }
}

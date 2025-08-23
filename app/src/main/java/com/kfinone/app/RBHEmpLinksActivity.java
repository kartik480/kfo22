package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class RBHEmpLinksActivity extends AppCompatActivity {

    // UI Elements
    private View backButton;
    private View refreshButton;
    private View addButton;
    private ProgressBar loadingIndicator;
    private TextView errorMessage;
    private RecyclerView iconsRecyclerView;
    
    // Bottom Navigation
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Data
    private List<ManageIcon> manageIcons;
    private RBHEmpLinksAdapter adapter;
    private RequestQueue requestQueue;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadManageIcons();
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
        Log.d("RBHEmpLinksActivity", "View initialization - backButton: " + (backButton != null ? "OK" : "NULL"));
        Log.d("RBHEmpLinksActivity", "View initialization - refreshButton: " + (refreshButton != null ? "OK" : "NULL"));
        Log.d("RBHEmpLinksActivity", "View initialization - addButton: " + (addButton != null ? "OK" : "NULL"));
        Log.d("RBHEmpLinksActivity", "View initialization - loadingIndicator: " + (loadingIndicator != null ? "OK" : "NULL"));
        Log.d("RBHEmpLinksActivity", "View initialization - errorMessage: " + (errorMessage != null ? "OK" : "NULL"));
        Log.d("RBHEmpLinksActivity", "View initialization - iconsRecyclerView: " + (iconsRecyclerView != null ? "OK" : "NULL"));
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
                // Already in Emp Links, do nothing
                Toast.makeText(this, "You are already in Emp Links", Toast.LENGTH_SHORT).show();
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
        manageIcons = new ArrayList<>();
        adapter = new RBHEmpLinksAdapter(this, manageIcons);
        
        if (iconsRecyclerView != null) {
            iconsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            iconsRecyclerView.setAdapter(adapter);
        }
    }

    private void loadManageIcons() {
        if (userId == null) {
            showError("User ID not found. Please login again.");
            return;
        }

        showLoading(true);
        hideError();

        // API URL - using the local API endpoint
        String url = "https://emp.kfinone.com/mobile/api/get_rbh_manage_icons.php";
        
        // Create request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
            requestBody.put("username", userName);
        } catch (JSONException e) {
            Log.e("RBHEmpLinksActivity", "Error creating request body", e);
            showError("Error preparing request");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RBHEmpLinksActivity", "API Response: " + response.toString());
                        parseManageIconsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RBHEmpLinksActivity", "API Error: " + error.toString());
                        showError("Network error: " + error.getMessage());
                        showLoading(false);
                    }
                });

        requestQueue.add(request);
    }

    private void parseManageIconsResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            
            if ("success".equals(status)) {
                JSONArray iconsArray = response.getJSONArray("data");
                List<ManageIcon> newIcons = new ArrayList<>();

                for (int i = 0; i < iconsArray.length(); i++) {
                    JSONObject iconObj = iconsArray.getJSONObject(i);
                    
                    ManageIcon icon = new ManageIcon(
                        iconObj.getInt("id"),
                        iconObj.getString("icon_name"),
                        iconObj.getString("icon_url"),
                        iconObj.getString("icon_image"),
                        iconObj.getString("icon_description"),
                        iconObj.getString("status")
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
            Log.e("RBHEmpLinksActivity", "Error parsing response", e);
            showError("Error parsing server response");
            showLoading(false);
        }
    }

    private void updateIconsList(List<ManageIcon> newIcons) {
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
        Toast.makeText(this, "Refreshing manage icons...", Toast.LENGTH_SHORT).show();
        loadManageIcons();
    }

    private void addNewIcon() {
        Toast.makeText(this, "Add New Icon - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
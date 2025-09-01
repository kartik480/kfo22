package com.kfinone.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.DefaultRetryPolicy;

public class MarketingHeadPanelActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadPanel";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView totalEmpCount;
    private TextView totalSDSACount;
    
    // Stat Card Views
    private CardView cardTotalEmp;
    private CardView cardTotalSDSA;
    
    // Header Icons
    private View menuButton;
    private View notificationIcon;
    private View profileIcon;
    
    // Action Card Views
    private LinearLayout cardEmpMaster;
    private LinearLayout cardLocationMaster;
    private LinearLayout cardSDSAMaster;
    private LinearLayout cardEmpLinks;
    private LinearLayout cardDataLinks;
    private LinearLayout cardWorkLinks;
    private LinearLayout cardDSACodes;
    private LinearLayout cardBanker;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_marketing_head_panel);
        
        // Initialize Volley queue early for better performance
        requestQueue = Volley.newRequestQueue(this);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "MarketingHeadPanelActivity received USER_ID: " + userId);
        Log.d(TAG, "MarketingHeadPanelActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupHeaderClickListeners();
        setupCardClickListeners();
        
        // Set initial values to 0
        setInitialStats();
        
        // Load data asynchronously to prevent ANR
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadMarketingHeadData();
        }, 100); // Small delay to ensure UI is ready
        
        updateWelcomeText();
        
        // Setup stat card click listeners
        setupStatCardClickListeners();
        
        // Check for app updates with delay to prevent blocking UI
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkForAppUpdates();
        }, 500);
    }
    
    private void initializeViews() {
        // Header Views
        welcomeText = findViewById(R.id.welcomeText);
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSDSACount = findViewById(R.id.totalSDSACount);
        
        // Stat Card Views
        cardTotalEmp = findViewById(R.id.cardTotalEmp);
        cardTotalSDSA = findViewById(R.id.cardTotalSDSA);
        
        // Header Icons
        menuButton = findViewById(R.id.menuButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        
        // Action Card Views
        cardEmpMaster = findViewById(R.id.cardEmpMaster);
        cardLocationMaster = findViewById(R.id.cardLocationMaster);
        cardSDSAMaster = findViewById(R.id.cardSDSAMaster);
        cardEmpLinks = findViewById(R.id.cardEmpLinks);
        cardDataLinks = findViewById(R.id.cardDataLinks);
        cardWorkLinks = findViewById(R.id.cardWorkLinks);
        cardDSACodes = findViewById(R.id.cardDSACodes);
        cardBanker = findViewById(R.id.cardBanker);
    }
    
    private void setupHeaderClickListeners() {
        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        notificationIcon.setOnClickListener(v -> {
            showToast("Notifications - Coming Soon!");
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            showProfileMenu();
        });
    }
    
    private void showMenuOptions() {
        String[] options = {"About", "Help", "Settings", "Logout"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Menu Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAboutDialog();
                    break;
                case 1:
                    showToast("Help - Coming Soon!");
                    break;
                case 2:
                    showToast("Settings - Coming Soon!");
                    break;
                case 3:
                    logout();
                    break;
            }
        });
        builder.show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Marketing Head Panel");
        builder.setMessage("Marketing Head Dashboard v1.0\n\n" +
                "This panel provides comprehensive marketing management tools including:\n" +
                "• Campaign Management\n" +
                "• Brand Strategy\n" +
                "• Market Research\n" +
                "• Lead Management\n" +
                "• Performance Analytics\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showProfileMenu() {
        String[] options = {"My Account", "Profile Settings", "Change Password"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openMyAccount();
                    break;
                case 1:
                    showToast("Profile Settings - Coming Soon!");
                    break;
                case 2:
                    showToast("Change Password - Coming Soon!");
                    break;
            }
        });
        builder.show();
    }
    
    private void openMyAccount() {
        Intent accountIntent = new Intent(MarketingHeadPanelActivity.this, MyAccountPanelActivity.class);
        accountIntent.putExtra("USERNAME", username);
        accountIntent.putExtra("FIRST_NAME", firstName);
        accountIntent.putExtra("LAST_NAME", lastName);
        accountIntent.putExtra("USER_ID", userId);
        startActivity(accountIntent);
    }
    
    private void setupCardClickListeners() {
        // Emp Master
        cardEmpMaster.setOnClickListener(v -> {
            Intent empMasterIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadEmpMasterActivity.class);
            empMasterIntent.putExtra("USERNAME", username);
            empMasterIntent.putExtra("FIRST_NAME", firstName);
            empMasterIntent.putExtra("LAST_NAME", lastName);
            empMasterIntent.putExtra("USER_ID", userId);
            startActivity(empMasterIntent);
        });
        
        // Location Master
        cardLocationMaster.setOnClickListener(v -> {
            Intent locationMasterIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadLocationMasterActivity.class);
            locationMasterIntent.putExtra("USERNAME", username);
            locationMasterIntent.putExtra("FIRST_NAME", firstName);
            locationMasterIntent.putExtra("LAST_NAME", lastName);
            locationMasterIntent.putExtra("USER_ID", userId);
            startActivity(locationMasterIntent);
        });
        
        // SDSA Master
        cardSDSAMaster.setOnClickListener(v -> {
            Intent sdsaMasterIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadSDSAMasterActivity.class);
            sdsaMasterIntent.putExtra("USERNAME", username);
            sdsaMasterIntent.putExtra("FIRST_NAME", firstName);
            sdsaMasterIntent.putExtra("LAST_NAME", lastName);
            sdsaMasterIntent.putExtra("USER_ID", userId);
            startActivity(sdsaMasterIntent);
        });
        
        // Emp Links
        cardEmpLinks.setOnClickListener(v -> {
            Intent empLinksIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadEmpLinksActivity.class);
            empLinksIntent.putExtra("USERNAME", username);
            empLinksIntent.putExtra("FIRST_NAME", firstName);
            empLinksIntent.putExtra("LAST_NAME", lastName);
            empLinksIntent.putExtra("USER_ID", userId);
            startActivity(empLinksIntent);
        });
        
        // Data Links
        cardDataLinks.setOnClickListener(v -> {
            Intent dataLinksIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadDataLinksActivity.class);
            dataLinksIntent.putExtra("USERNAME", username);
            dataLinksIntent.putExtra("FIRST_NAME", firstName);
            dataLinksIntent.putExtra("LAST_NAME", lastName);
            dataLinksIntent.putExtra("USER_ID", userId);
            startActivity(dataLinksIntent);
        });
        
        // Work Links
        cardWorkLinks.setOnClickListener(v -> {
            Intent workLinksIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadWorkLinksActivity.class);
            workLinksIntent.putExtra("USERNAME", username);
            workLinksIntent.putExtra("FIRST_NAME", firstName);
            workLinksIntent.putExtra("LAST_NAME", lastName);
            workLinksIntent.putExtra("USER_ID", userId);
            startActivity(workLinksIntent);
        });
        
        // DSA Codes
        cardDSACodes.setOnClickListener(v -> {
            Intent dsaCodeIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadDsaCodeMasterActivity.class);
            dsaCodeIntent.putExtra("USERNAME", username);
            dsaCodeIntent.putExtra("FIRST_NAME", firstName);
            dsaCodeIntent.putExtra("LAST_NAME", lastName);
            dsaCodeIntent.putExtra("USER_ID", userId);
            startActivity(dsaCodeIntent);
        });
        
        // Banker
        cardBanker.setOnClickListener(v -> {
            Intent bankerIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadBankerPanelActivity.class);
            bankerIntent.putExtra("USERNAME", username);
            bankerIntent.putExtra("FIRST_NAME", firstName);
            bankerIntent.putExtra("LAST_NAME", lastName);
            bankerIntent.putExtra("USER_ID", userId);
            startActivity(bankerIntent);
        });
    }
    
    private void setupStatCardClickListeners() {
        // Total Emp
        cardTotalEmp.setOnClickListener(v -> {
            Intent activeEmpListIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadActiveEmpListActivity.class);
            activeEmpListIntent.putExtra("USERNAME", username);
            activeEmpListIntent.putExtra("FIRST_NAME", firstName);
            activeEmpListIntent.putExtra("LAST_NAME", lastName);
            activeEmpListIntent.putExtra("USER_ID", userId);
            startActivity(activeEmpListIntent);
        });
        
        // Add long press listener to refresh employee count
        cardTotalEmp.setOnLongClickListener(v -> {
            refreshEmployeeCount();
            showToast("Refreshing employee count...");
            return true;
        });
        
        // Total SDSA
        cardTotalSDSA.setOnClickListener(v -> {
            Intent activeSDSAListIntent = new Intent(MarketingHeadPanelActivity.this, MarketingHeadActiveSDSAListActivity.class);
            activeSDSAListIntent.putExtra("USERNAME", username);
            activeSDSAListIntent.putExtra("FIRST_NAME", firstName);
            activeSDSAListIntent.putExtra("LAST_NAME", lastName);
            activeSDSAListIntent.putExtra("USER_ID", userId);
            startActivity(activeSDSAListIntent);
        });
        
        // Add long press listener to refresh SDSA count
        cardTotalSDSA.setOnLongClickListener(v -> {
            refreshSDSACount();
            showToast("Refreshing SDSA count...");
            return true;
        });
    }
    
    private void setInitialStats() {
        totalEmpCount.setText("0");
        totalSDSACount.setText("0");
    }
    
    private void loadMarketingHeadData() {
        // Load all the counts from their respective APIs
        Log.d(TAG, "Loading marketing head data - fetching all counts");
        
        // Fetch employee count
        fetchEmployeeCount();
        
        // Fetch SDSA count
        fetchSDSACount();
        
        // TODO: Add other marketing data when APIs are available
    }
    
    private void updateWelcomeText() {
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName);
        } else {
            welcomeText.setText("Welcome back, Marketing Head");
        }
    }
    
    private void checkForAppUpdates() {
        // TODO: Implement app update check
        Log.d(TAG, "App update check - Coming Soon!");
    }
    
    /**
     * Fetch the total number of employees for the current Marketing Head user
     * This method calls the same API endpoint used in other panels for consistency
     */
    private void fetchEmployeeCount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch employee count");
            totalEmpCount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching employee count for username: " + username);
        // Use the working employee API endpoint
        String url = "https://emp.kfinone.com/mobile/api/get_marketing_head_active_emp_list.php";
        Log.d(TAG, "Employee count API URL: " + url);
        
        // Create request body with username and user_id for better compatibility
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            if (userId != null && !userId.isEmpty()) {
                requestBody.put("user_id", userId);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body: " + e.getMessage());
            return;
        }
        
        Log.d(TAG, "Employee count request body: " + requestBody.toString());
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "Employee count API response: " + jsonResponse.toString());
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            // Use the working API response structure
                            if (jsonResponse.has("total_count")) {
                                // Direct total_count field from our working API
                                int totalCount = jsonResponse.optInt("total_count", 0);
                                totalEmpCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Employee count updated from total_count field: " + totalCount);
                            } else if (jsonResponse.has("active_employees")) {
                                // Count the active_employees array length
                                JSONArray activeEmployees = jsonResponse.getJSONArray("active_employees");
                                int totalCount = activeEmployees.length();
                                totalEmpCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Employee count updated from active_employees array length: " + totalCount);
                            } else {
                                Log.w(TAG, "No count data found in working API response");
                                totalEmpCount.setText("0");
                            }
                        } else {
                            Log.w(TAG, "Failed to fetch employee count: " + jsonResponse.optString("message", "Unknown error"));
                            totalEmpCount.setText("0");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing employee count response", e);
                        totalEmpCount.setText("0");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching employee count", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Network response status: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Network response data: " + new String(error.networkResponse.data));
                    }
                    totalEmpCount.setText("0");
                }
            }
        );

        // Add aggressive timeout and retry policy to prevent ANR
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout
            0,      // No retries (prevents hanging)
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
        
        Log.d(TAG, "Employee count request added to queue");
    }
    
    /**
     * Fetch the total number of SDSA users for the current Marketing Head user
     * This method calls the same API endpoint used in the Active SDSA List for consistency
     */
    private void fetchSDSACount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch SDSA count");
            totalSDSACount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching SDSA count for username: " + username);
        // Use the same API endpoint as Active SDSA List
        String url = "https://emp.kfinone.com/mobile/api/get_all_sdsa_users.php";
        Log.d(TAG, "SDSA count API URL: " + url);
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "SDSA count API response: " + jsonResponse.toString());
                    try {
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            // Get count from the same field as Active SDSA List
                            int totalCount = jsonResponse.optInt("count", 0);
                            totalSDSACount.setText(String.valueOf(totalCount));
                            Log.d(TAG, "SDSA count updated: " + totalCount);
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error");
                            Log.w(TAG, "Failed to fetch SDSA count: " + message);
                            totalSDSACount.setText("0");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing SDSA count response", e);
                        totalSDSACount.setText("0");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching SDSA count", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Network response status: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Network response data: " + new String(error.networkResponse.data));
                    }
                    totalSDSACount.setText("0");
                }
            }
        );

        // Add aggressive timeout and retry policy to prevent ANR
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout
            0,      // No retries (prevents hanging)
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
        
        Log.d(TAG, "SDSA count request added to queue");
    }
    

    
    /**
     * Manually refresh the employee count
     * This can be called from UI or other parts of the app
     */
    public void refreshEmployeeCount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of employee count requested");
            fetchEmployeeCount();
        } else {
            Log.w(TAG, "Cannot refresh employee count - username not available");
        }
    }
    
    /**
     * Manually refresh the SDSA count
     * This can be called from UI or other parts of the app
     */
    public void refreshSDSACount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of SDSA count requested");
            fetchSDSACount();
        } else {
            Log.w(TAG, "Cannot refresh SDSA count - username not available");
        }
    }
    

    
    /**
     * Refresh all counts at once
     * This can be called from UI or other parts of the app
     */
    public void refreshAllCounts() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of all counts requested");
            fetchEmployeeCount();
            fetchSDSACount();
            // TODO: Add other marketing data refresh methods when APIs are available
        } else {
            Log.w(TAG, "Cannot refresh counts - username not available");
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reinitialize Volley queue if needed
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        
        // Restore user data and welcome message when returning to this activity
        updateWelcomeText();
        
        // Refresh all counts when returning to the home page
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Refreshing all counts on resume");
            refreshAllCounts();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (requestQueue != null) {
            requestQueue.cancelAll("MarketingHeadPanelActivity");
        }
        
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        
        // Clear references to prevent memory leaks
        requestQueue = null;
        executor = null;
    }
    
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Clear any stored data
            // Navigate back to login
            Intent intent = new Intent(MarketingHeadPanelActivity.this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
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
    

}

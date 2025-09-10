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

// Removed unused executor imports

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.DefaultRetryPolicy;

public class BusinessHeadPanelActivity extends AppCompatActivity {
    private static final String TAG = "BusinessHeadPanel";
    private static final String BASE_URL = "https://p3plzcpnl508816.prod.phx3.secureserver.net/mobile/api/";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private TextView totalEmpCount;
    private TextView totalSDSACount;
    private TextView totalPartnerCount;
    private TextView totalPortfolioCount;
    private TextView totalAgentCount;
    
    // Stat Card Views
    private CardView cardTotalEmp;
    private CardView cardTotalSDSA;
    private CardView cardTotalPartner;
    private CardView cardTotalPortfolio;
    private CardView cardTotalAgent;
    
    // Header Icons
    private View menuButton;
    private View notificationIcon;
    private View profileIcon;
    
    // Action Card Views
    private LinearLayout cardTeamManagement;
    private LinearLayout cardBusinessAnalytics;
    private LinearLayout cardReportsInsights;
    private LinearLayout cardPerformanceTracking;
    private LinearLayout cardStrategicPlanning;
    private LinearLayout cardResourceManagement;
    private LinearLayout cardBusinessGrowth;
    private LinearLayout cardInnovationHub;
    private LinearLayout cardPartnerships;
    private LinearLayout cardMarketAnalysis;
    private LinearLayout cardRiskManagement;
    private LinearLayout cardCompliance;
    private LinearLayout cardBudgetManagement;
    
    // Executor removed since we're not doing heavy background operations
    private RequestQueue requestQueue;
    
    // Handler for UI operations
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_business_head_panel);
        
        // Initialize Volley queue early for better performance
        requestQueue = Volley.newRequestQueue(this);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "BusinessHeadPanelActivity received USER_ID: " + userId);
        Log.d(TAG, "BusinessHeadPanelActivity received USERNAME: " + username);
        
        // Initialize views
        initializeViews();
        setupHeaderClickListeners();
        setupCardClickListeners();
        
        // Set initial values to 0
        setInitialStats();
        
        // Load data asynchronously to prevent ANR
        uiHandler.postDelayed(() -> {
            loadBusinessHeadData();
        }, 100); // Small delay to ensure UI is ready
        
        // Set initial values and focus on partner count
        Log.d(TAG, "Setting up partner count fetching");
        
        updateWelcomeText();
        
        // Setup stat card click listeners
        setupStatCardClickListeners();
        
        // Check for app updates with delay to prevent blocking UI
        uiHandler.postDelayed(() -> {
            checkForAppUpdates();
        }, 500);
    }
    
    private void initializeViews() {
        // Header Views
        welcomeText = findViewById(R.id.welcomeText);
        totalEmpCount = findViewById(R.id.totalTeamCount);
        totalSDSACount = findViewById(R.id.activeProjectsCount);
        totalPartnerCount = findViewById(R.id.revenueCount);
        totalPortfolioCount = findViewById(R.id.performanceScore);
        totalAgentCount = findViewById(R.id.growthRate);
        
        // Stat Card Views
        cardTotalEmp = findViewById(R.id.cardTotalEmp);
        cardTotalSDSA = findViewById(R.id.cardTotalSDSA);
        cardTotalPartner = findViewById(R.id.cardTotalPartner);
        cardTotalPortfolio = findViewById(R.id.cardTotalPortfolio);
        cardTotalAgent = findViewById(R.id.cardTotalAgent);
        
        // Header Icons
        menuButton = findViewById(R.id.menuButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        
        // Action Card Views
        cardTeamManagement = findViewById(R.id.cardTeamManagement);
        cardBusinessAnalytics = findViewById(R.id.cardBusinessAnalytics);
        cardReportsInsights = findViewById(R.id.cardReportsInsights);
        cardPerformanceTracking = findViewById(R.id.cardPerformanceTracking);
        cardStrategicPlanning = findViewById(R.id.cardStrategicPlanning);
        cardResourceManagement = findViewById(R.id.cardResourceManagement);
        cardBusinessGrowth = findViewById(R.id.cardBusinessGrowth);
        cardInnovationHub = findViewById(R.id.cardInnovationHub);
        cardPartnerships = findViewById(R.id.cardPartnerships);
        cardMarketAnalysis = findViewById(R.id.cardMarketAnalysis);
        cardRiskManagement = findViewById(R.id.cardRiskManagement);
        cardCompliance = findViewById(R.id.cardCompliance);
        cardBudgetManagement = findViewById(R.id.cardBudgetManagement);
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
                    showLogoutConfirmation();
                    break;
            }
        });
        builder.show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Business Head Panel");
        builder.setMessage("Business Head Panel v1.0\n\n" +
                "This panel provides comprehensive management tools for Business Heads to oversee their business operations, team performance, and strategic planning.\n\n" +
                "Key Features:\n" +
                "• Team Management\n" +
                "• Business Analytics\n" +
                "• Performance Tracking\n" +
                "• Strategic Planning\n" +
                "• Resource Management");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showProfileMenu() {
        String[] options = {"Profile", "My Account", "Settings", "Help", "About"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Open User Profile
                    Intent intent = new Intent(this, UserProfileActivity.class);
                    if (userId != null) intent.putExtra("USER_ID", userId);
                    if (username != null) intent.putExtra("USERNAME", username);
                    if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                    if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("USER_DESIGNATION", "BH");
                    intent.putExtra("SOURCE_PANEL", "BH_PANEL");
                    startActivity(intent);
                    break;
                case 1:
                    // Open My Account (existing functionality)
                    Intent accountIntent = new Intent(BusinessHeadPanelActivity.this, MyAccountPanelActivity.class);
                    if (userId != null) accountIntent.putExtra("USER_ID", userId);
                    if (username != null) accountIntent.putExtra("USERNAME", username);
                    if (firstName != null) accountIntent.putExtra("FIRST_NAME", firstName);
                    if (lastName != null) accountIntent.putExtra("LAST_NAME", lastName);
                    startActivity(accountIntent);
                    break;
                case 2:
                    showToast("Settings - Coming Soon!");
                    break;
                case 3:
                    showToast("Help - Coming Soon!");
                    break;
                case 4:
                    showAboutDialog();
                    break;
            }
        });
        builder.show();
    }
    
    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    
    private void setupCardClickListeners() {
        // Emp Links
        cardTeamManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Data Links
        cardBusinessAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadDataLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Work Links
        cardReportsInsights.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Performance Tracking (Active Employee List)
        cardPerformanceTracking.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Strategic Planning (My SDSA Users)
        cardStrategicPlanning.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadMySdsaUsersActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Resource Management (Partner)
        cardResourceManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Business Growth (Agent)
        cardBusinessGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Innovation Hub (Payout)
        cardInnovationHub.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BHPayoutPanelActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "BUSINESS_HEAD_PANEL");
            startActivity(intent);
        });
        
        // Partnerships (Bankers List)
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadBankersListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Market Analysis (Portfolio)
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Risk Management (Vehicle Insurance)
        cardRiskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadVehicleInsuranceActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Compliance (Document Check List)
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadDocumentCheckListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        // Budget Management (Policy)
        cardBudgetManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPolicyActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // SDSA Card - Navigate to Business Head SDSA Panel
        cardTotalSDSA.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadSdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }
    
    private void setInitialStats() {
        // Set all stat values to 0
        totalEmpCount.setText("0");
        totalSDSACount.setText("0");
        totalPartnerCount.setText("0");
        totalPortfolioCount.setText("0");
        totalAgentCount.setText("0");
    }
    
    private void updateWelcomeText() {
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName + " " + lastName);
        } else if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName);
        } else {
            welcomeText.setText("Welcome back, Business Head");
        }
    }
    
    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }
    
    private void loadBusinessHeadData() {
        // Fetch all the counts from their respective working APIs
        Log.d(TAG, "Loading business head data - fetching all counts");
        
        // Fetch partner count
        fetchPartnerCount();
        
        // Fetch SDSA count
        fetchSDSACount();
        
        // Set portfolio count to 0 for now since the API endpoint doesn't exist
        // TODO: Implement portfolio count when the API is created
        totalPortfolioCount.setText("0");
        
        // Fetch agent count
        fetchAgentCount();
        
        // Set employee count to 0 for now since its API is not working
        // TODO: Uncomment the line below when the employee API is fixed
        // fetchEmployeeCount();
        totalEmpCount.setText("0");
        
        Log.d(TAG, "Business head data loading completed. Username: " + username + ", UserID: " + userId);
        Log.d(TAG, "Partner, SDSA, and Agent count APIs are being fetched. Portfolio and Employee counts set to 0.");
    }
    
    /**
     * Fetch the total number of partners for the current Business Head user
     * This method calls the same API endpoint used in BusinessHeadMyPartnerActivity
     * to ensure consistency in partner counting
     */
    private void fetchPartnerCount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch partner count");
            totalPartnerCount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching partner count for username: " + username);
        // Use the same API endpoint as BusinessHeadMyPartnerActivity for consistency
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_my_partners.php";
        Log.d(TAG, "Partner count API URL: " + url);
        
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
        
        Log.d(TAG, "Partner count request body: " + requestBody.toString());
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "Partner count API response: " + jsonResponse.toString());
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            // Try to get count from statistics
                            if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                                JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                int totalCount = statistics.optInt("total_partners", 0);
                                totalPartnerCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Partner count updated from statistics: " + totalCount);
                            } else if (jsonResponse.has("counts")) {
                                // Alternative location for counts
                                int totalCount = jsonResponse.getJSONObject("counts").optInt("total_partners", 0);
                                totalPartnerCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Partner count updated from counts: " + totalCount);
                            } else if (jsonResponse.has("data")) {
                                // Check if data is an array (partner list) or object
                                if (jsonResponse.getJSONObject("data").has("partner_users")) {
                                    // This is the working structure from BusinessHeadMyPartnerActivity
                                    JSONArray partnerUsers = jsonResponse.getJSONObject("data").getJSONArray("partner_users");
                                    int totalCount = partnerUsers.length();
                                    totalPartnerCount.setText(String.valueOf(totalCount));
                                    Log.d(TAG, "Partner count updated from partner_users array: " + totalCount);
                            } else {
                                    // Fallback: try to count the data array directly
                                    try {
                                        int totalCount = jsonResponse.getJSONArray("data").length();
                                        totalPartnerCount.setText(String.valueOf(totalCount));
                                        Log.d(TAG, "Partner count updated from data array length: " + totalCount);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Data is not an array, trying object approach");
                                        // If data is an object, try to get count from it
                                        JSONObject dataObj = jsonResponse.getJSONObject("data");
                                        if (dataObj.has("total_count")) {
                                            int totalCount = dataObj.optInt("total_count", 0);
                                            totalPartnerCount.setText(String.valueOf(totalCount));
                                            Log.d(TAG, "Partner count updated from total_count: " + totalCount);
                                        } else {
                                            totalPartnerCount.setText("0");
                                            Log.w(TAG, "No partner count found in response");
                                        }
                                    }
                            }
                        } else {
                                Log.w(TAG, "No data field found in partner count response");
                                totalPartnerCount.setText("0");
                            }
                        } else {
                            Log.w(TAG, "Failed to fetch partner count: " + jsonResponse.optString("message", "Unknown error"));
                            totalPartnerCount.setText("0");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing partner count response", e);
                        totalPartnerCount.setText("0");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching partner count", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Network response status: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Network response data: " + new String(error.networkResponse.data));
                    }
                    totalPartnerCount.setText("0");
                }
            }
        );

        // Add aggressive timeout and retry policy to prevent ANR
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout (reduced from 10s)
            0,      // No retries (prevents hanging)
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
        
        Log.d(TAG, "Partner count request added to queue");
    }
    
    /**
     * Fetch the total number of SDSA users for the current Business Head user
     * This method calls the same API endpoint used in BusinessHeadMySdsaUsersActivity
     * to ensure consistency in SDSA counting
     */
    private void fetchSDSACount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch SDSA count");
            totalSDSACount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching SDSA count for username: " + username);
        // Use the same API endpoint as BusinessHeadMySdsaUsersActivity for consistency
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_my_sdsa_users.php";
        Log.d(TAG, "SDSA count API URL: " + url);
        
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
        
        Log.d(TAG, "SDSA count request body: " + requestBody.toString());
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "SDSA count API response: " + jsonResponse.toString());
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            // Based on the actual API response, try to get count from different locations
                            if (jsonResponse.has("count")) {
                                // Direct count field from the API response
                                int totalCount = jsonResponse.optInt("count", 0);
                                totalSDSACount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "SDSA count updated from count field: " + totalCount);
                            } else if (jsonResponse.has("users")) {
                                // Count the users array length
                                JSONArray users = jsonResponse.getJSONArray("users");
                                int totalCount = users.length();
                                totalSDSACount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "SDSA count updated from users array length: " + totalCount);
                            } else if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                            // Try to get count from statistics
                                JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                int totalCount = statistics.optInt("total_sdsa_users", 0);
                                totalSDSACount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "SDSA count updated from statistics: " + totalCount);
                            } else if (jsonResponse.has("counts")) {
                                // Alternative location for counts
                                int totalCount = jsonResponse.getJSONObject("counts").optInt("total_sdsa_users", 0);
                                totalSDSACount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "SDSA count updated from counts: " + totalCount);
                            } else if (jsonResponse.has("data")) {
                                // Check if data is an array (SDSA list) or object
                                if (jsonResponse.getJSONObject("data").has("sdsa_users")) {
                                    // This is the working structure from BusinessHeadMySdsaUsersActivity
                                    JSONArray sdsaUsers = jsonResponse.getJSONObject("data").getJSONArray("sdsa_users");
                                    int totalCount = sdsaUsers.length();
                                    totalSDSACount.setText(String.valueOf(totalCount));
                                    Log.d(TAG, "SDSA count updated from sdsa_users array: " + totalCount);
                            } else {
                                    // Fallback: try to count the data array directly
                                    try {
                                int totalCount = jsonResponse.getJSONArray("data").length();
                                        totalSDSACount.setText(String.valueOf(totalCount));
                                        Log.d(TAG, "SDSA count updated from data array length: " + totalCount);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Data is not an array, trying object approach");
                                        // If data is an object, try to get count from it
                                        JSONObject dataObj = jsonResponse.getJSONObject("data");
                                        if (dataObj.has("total_count")) {
                                            int totalCount = dataObj.optInt("total_count", 0);
                                            totalSDSACount.setText(String.valueOf(totalCount));
                                            Log.d(TAG, "SDSA count updated from total_count: " + totalCount);
                                        } else {
                                            totalSDSACount.setText("0");
                                            Log.w(TAG, "No SDSA count found in response");
                                        }
                                    }
                            }
                        } else {
                                Log.w(TAG, "No data field found in SDSA count response");
                                totalSDSACount.setText("0");
                            }
                        } else {
                            Log.w(TAG, "Failed to fetch SDSA count: " + jsonResponse.optString("message", "Unknown error"));
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
            5000,  // 5 seconds timeout (reduced from 10s)
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
     * Fetch the total number of portfolios for the current Business Head user
     * This method calls the same API endpoint used in BusinessHeadPortfolioActivity
     * to ensure consistency in portfolio counting
     * 
     * NOTE: This method is currently not being called since the portfolio API endpoint
     * returns 404 errors. The portfolio count is set to 0 until the API is fixed.
     */
    private void fetchPortfolioCount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch portfolio count");
            totalPortfolioCount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching portfolio count for username: " + username);
        // Use the same API endpoint as BusinessHeadPortfolioActivity for consistency
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_portfolios.php";
        Log.d(TAG, "Portfolio count API URL: " + url);
        
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
        
        Log.d(TAG, "Portfolio count request body: " + requestBody.toString());
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "Portfolio count API response: " + jsonResponse.toString());
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            // Try to get count from statistics
                            if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                                JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                int totalCount = statistics.optInt("total_portfolios", 0);
                                totalPortfolioCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Portfolio count updated from statistics: " + totalCount);
                            } else if (jsonResponse.has("counts")) {
                                // Alternative location for counts
                                int totalCount = jsonResponse.getJSONObject("counts").optInt("total_portfolios", 0);
                                totalPortfolioCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Portfolio count updated from counts: " + totalCount);
                            } else if (jsonResponse.has("data")) {
                                // Check if data is an array (portfolio list) or object
                                if (jsonResponse.getJSONObject("data").has("portfolios")) {
                                    // This is the working structure from BusinessHeadPortfolioActivity
                                    JSONArray portfolios = jsonResponse.getJSONObject("data").getJSONArray("portfolios");
                                    int totalCount = portfolios.length();
                                    totalPortfolioCount.setText(String.valueOf(totalCount));
                                    Log.d(TAG, "Portfolio count updated from portfolios array: " + totalCount);
                                } else {
                                    // Fallback: try to count the data array directly
                                    try {
                                        int totalCount = jsonResponse.getJSONArray("data").length();
                                        totalPortfolioCount.setText(String.valueOf(totalCount));
                                        Log.d(TAG, "Portfolio count updated from data array length: " + totalCount);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Data is not an array, trying object approach");
                                        // If data is an object, try to get count from it
                                        JSONObject dataObj = jsonResponse.getJSONObject("data");
                                        if (dataObj.has("total_count")) {
                                            int totalCount = dataObj.optInt("total_count", 0);
                                            totalPortfolioCount.setText(String.valueOf(totalCount));
                                            Log.d(TAG, "Portfolio count updated from total_count: " + totalCount);
                                        } else {
                                            totalPortfolioCount.setText("0");
                                            Log.w(TAG, "No portfolio count found in response");
                                        }
                                    }
                                }
                            } else {
                                Log.w(TAG, "No data field found in portfolio count response");
                                totalPortfolioCount.setText("0");
                            }
                        } else {
                            Log.w(TAG, "Failed to fetch portfolio count: " + jsonResponse.optString("message", "Unknown error"));
                            totalPortfolioCount.setText("0");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing portfolio count response", e);
                        totalPortfolioCount.setText("0");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching portfolio count", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Network response status: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Network response data: " + new String(error.networkResponse.data));
                    }
                    totalPortfolioCount.setText("0");
                }
            }
        );

        // Add aggressive timeout and retry policy to prevent ANR
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout (reduced from 10s)
            0,      // No retries (prevents hanging)
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
        
        Log.d(TAG, "Portfolio count request added to queue");
    }
    
    /**
     * Fetch the total number of agents for the current Business Head user
     * This method calls the same API endpoint used in BusinessHeadMyAgentActivity
     * to ensure consistency in agent counting
     */
    private void fetchAgentCount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch agent count");
            totalAgentCount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching agent count for username: " + username);
        // Use the same API endpoint as BusinessHeadMyAgentActivity for consistency
        // This API expects GET parameters, not POST body
        String url = "https://emp.kfinone.com/mobile/api/business_head_my_agents.php?username=" + username;
        if (userId != null && !userId.isEmpty()) {
            url += "&user_id=" + userId;
        }
        Log.d(TAG, "Agent count API URL: " + url);
        
        // This API uses GET method, not POST
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    Log.d(TAG, "Agent count API response: " + jsonResponse.toString());
                    try {
                        if (jsonResponse.getString("success").equals("true")) {
                            // Try to get count from stats object first (most reliable)
                            if (jsonResponse.has("stats")) {
                                JSONObject stats = jsonResponse.getJSONObject("stats");
                                int totalCount = stats.optInt("total_agents", 0);
                                totalAgentCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Agent count updated from stats.total_agents: " + totalCount);
                            } else if (jsonResponse.has("data")) {
                                // Check if data is an array (agent list) - count the array length
                                try {
                                    JSONArray agents = jsonResponse.getJSONArray("data");
                                    int totalCount = agents.length();
                                    totalAgentCount.setText(String.valueOf(totalCount));
                                    Log.d(TAG, "Agent count updated from data array length: " + totalCount);
                                } catch (Exception e) {
                                    Log.w(TAG, "Data is not an array, trying object approach");
                                    // If data is an object, try to get count from it
                                    JSONObject dataObj = jsonResponse.getJSONObject("data");
                                    if (dataObj.has("total_count")) {
                                        int totalCount = dataObj.optInt("total_count", 0);
                                        totalAgentCount.setText(String.valueOf(totalCount));
                                        Log.d(TAG, "Agent count updated from data.total_count: " + totalCount);
                            } else {
                                        totalAgentCount.setText("0");
                                        Log.w(TAG, "No agent count found in response");
                                    }
                                }
                            } else {
                                Log.w(TAG, "No stats or data field found in agent count response");
                                totalAgentCount.setText("0");
                            }
                        } else {
                            Log.w(TAG, "Failed to fetch agent count: " + jsonResponse.optString("message", "Unknown error"));
                            totalAgentCount.setText("0");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing agent count response", e);
                        totalAgentCount.setText("0");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching agent count", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Network response status: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Network response data: " + new String(error.networkResponse.data));
                    }
                    totalAgentCount.setText("0");
                }
            }
        );

        // Add aggressive timeout and retry policy to prevent ANR
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout (reduced from 10s)
            0,      // No retries (prevents hanging)
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
        
        Log.d(TAG, "Agent count request added to queue");
    }
    
    /**
     * Fetch the total number of employees for the current Business Head user
     * This method is prepared for when the employee API endpoint is fixed
     * Currently not called since the API returns 404 errors
     */
    private void fetchEmployeeCount() {
        if (username == null || username.isEmpty()) {
            Log.w(TAG, "Username is null or empty, cannot fetch employee count");
            totalEmpCount.setText("0");
            return;
        }
        
        Log.d(TAG, "Fetching employee count for username: " + username);
        // Use the employee API endpoint when it's fixed
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_employees.php";
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
                            // Try to get count from statistics
                            if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                                JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                int totalCount = statistics.optInt("total_employees", 0);
                                totalEmpCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Employee count updated from statistics: " + totalCount);
                            } else if (jsonResponse.has("counts")) {
                                // Alternative location for counts
                                int totalCount = jsonResponse.getJSONObject("counts").optInt("total_employees", 0);
                                totalEmpCount.setText(String.valueOf(totalCount));
                                Log.d(TAG, "Employee count updated from counts: " + totalCount);
                            } else if (jsonResponse.has("data")) {
                                // Check if data is an array (employee list) or object
                                if (jsonResponse.getJSONObject("data").has("employees")) {
                                    // This is the expected structure
                                    JSONArray employees = jsonResponse.getJSONObject("data").getJSONArray("employees");
                                    int totalCount = employees.length();
                                    totalEmpCount.setText(String.valueOf(totalCount));
                                    Log.d(TAG, "Employee count updated from employees array: " + totalCount);
                                } else {
                                    // Fallback: try to count the data array directly
                                    try {
                                        int totalCount = jsonResponse.getJSONArray("data").length();
                                        totalEmpCount.setText(String.valueOf(totalCount));
                                        Log.d(TAG, "Employee count updated from data array length: " + totalCount);
                                    } catch (Exception e) {
                                        Log.w(TAG, "Data is not an array, trying object approach");
                                        // If data is an object, try to get count from it
                                        JSONObject dataObj = jsonResponse.getJSONObject("data");
                                        if (dataObj.has("total_count")) {
                                            int totalCount = dataObj.optInt("total_count", 0);
                                            totalEmpCount.setText(String.valueOf(totalCount));
                                            Log.d(TAG, "Employee count updated from total_count: " + totalCount);
                                        } else {
                                            totalEmpCount.setText("0");
                                            Log.w(TAG, "No employee count found in response");
                                        }
                                    }
                                }
                            } else {
                                Log.w(TAG, "No data field found in employee count response");
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
            5000,  // 5 seconds timeout (reduced from 10s)
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
    
    private void setupStatCardClickListeners() {
        // Total Emp Card - Navigate to Business Head My Emp Panel
        cardTotalEmp.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadEmpMasterActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Add long press listener to refresh all counts
        cardTotalEmp.setOnLongClickListener(v -> {
            refreshAllCounts();
            showToast("Refreshing all counts...");
            return true;
        });
        
        // Total SDSA Card - Navigate to Business Head SDSA Panel
        cardTotalSDSA.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadSdsaActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Add long press listener to refresh SDSA count
        cardTotalSDSA.setOnLongClickListener(v -> {
            refreshSDSACount();
            showToast("Refreshing SDSA count...");
            return true;
        });
        
        // Total Partner Card - Navigate directly to Business Head My Partner Panel
        cardTotalPartner.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadMyPartnerActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("SOURCE_PANEL", "BH_PANEL");
            startActivity(intent);
        });
        
        // Add long press listener to refresh partner count
        cardTotalPartner.setOnLongClickListener(v -> {
            refreshPartnerCount();
            showToast("Refreshing partner count...");
            return true;
        });
        
        // Total Portfolio Card - Navigate to Business Head Portfolio Panel
        cardTotalPortfolio.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPortfolioActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Add long press listener to refresh portfolio count
        cardTotalPortfolio.setOnLongClickListener(v -> {
            refreshPortfolioCount();
            showToast("Refreshing portfolio count...");
            return true;
        });
        
        // Total Agent Card - Navigate to Business Head Agent Panel
        cardTotalAgent.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadAgentActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Add long press listener to refresh agent count
        cardTotalAgent.setOnLongClickListener(v -> {
            refreshAgentCount();
            showToast("Refreshing agent count...");
            return true;
        });
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // ANR Prevention: Set fallback values on network errors
        if (message.contains("Network error") || message.contains("timeout")) {
            Log.w(TAG, "Network error detected - setting fallback values");
            setInitialStats();
        }
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Manually refresh the partner count
     * This can be called from UI or other parts of the app
     */
    public void refreshPartnerCount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of partner count requested");
            fetchPartnerCount();
        } else {
            Log.w(TAG, "Cannot refresh partner count - username not available");
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
     * Manually refresh the portfolio count
     * This can be called from UI or other parts of the app
     * Note: Currently set to 0 since the portfolio API endpoint doesn't exist
     */
    public void refreshPortfolioCount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of portfolio count requested");
            // For now, set to 0 since the API endpoint doesn't exist
            // TODO: Implement fetchPortfolioCount() when the API is created
            totalPortfolioCount.setText("0");
            showToast("Portfolio count API not available yet");
        } else {
            Log.w(TAG, "Cannot refresh portfolio count - username not available");
        }
    }
    
    /**
     * Manually refresh the agent count
     * This can be called from UI or other parts of the app
     */
    public void refreshAgentCount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of agent count requested");
            fetchAgentCount();
        } else {
            Log.w(TAG, "Cannot refresh agent count - username not available");
        }
    }
    
    /**
     * Refresh all counts at once
     * This can be called from UI or other parts of the app
     */
    public void refreshAllCounts() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of all counts requested");
            fetchPartnerCount();
            fetchSDSACount();
            // Portfolio count is set to 0 since the API doesn't exist
            totalPortfolioCount.setText("0");
            fetchAgentCount();
            // TODO: Uncomment the line below when the employee API is fixed
            // fetchEmployeeCount();
        } else {
            Log.w(TAG, "Cannot refresh counts - username not available");
        }
    }
    
    /**
     * Manually refresh the employee count
     * This can be called from UI or other parts of the app
     * Note: Currently set to 0 since the employee API is not working
     */
    public void refreshEmployeeCount() {
        if (username != null && !username.isEmpty()) {
            Log.d(TAG, "Manual refresh of employee count requested");
            // For now, set to 0 since the API is not working
            // TODO: Uncomment the line below when the employee API is fixed
            // fetchEmployeeCount();
            totalEmpCount.setText("0");
            showToast("Employee count API not available yet");
        } else {
            Log.w(TAG, "Cannot refresh employee count - username not available");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_head_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (requestQueue != null) {
            requestQueue.cancelAll("BusinessHeadPanelActivity");
        }
        
        // Clear references to prevent memory leaks
        requestQueue = null;
        uiHandler = null;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Cancel ongoing network requests when activity is paused
        if (requestQueue != null) {
            requestQueue.cancelAll("BusinessHeadPanelActivity");
        }
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
    public void onBackPressed() {
        showLogoutConfirmation();
    }
    
    /**
     * Check for app updates and show dialog if update is available
     */
    private void checkForAppUpdates() {
        try {
            AppUpdateChecker updateChecker = new AppUpdateChecker(this);
            updateChecker.checkForUpdates();
        } catch (Exception e) {
            Log.e(TAG, "Error checking for updates", e);
        }
    }
} 
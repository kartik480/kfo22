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

public class BusinessHeadPanelActivity extends AppCompatActivity {
    private static final String TAG = "BusinessHeadPanel";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
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
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private RequestQueue requestQueue;
    
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadBusinessHeadData();
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
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Innovation Hub (Payout)
        cardInnovationHub.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BHPayoutPanelActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("SOURCE_PANEL", "BUSINESS_HEAD_PANEL");
            startActivity(intent);
        });
        
        // Partnerships (Bankers List)
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadBankersListActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Market Analysis (Portfolio)
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPortfolioActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Risk Management
        cardRiskManagement.setOnClickListener(v -> {
            showToast("Risk Management - Coming Soon!");
        });
        
        // Compliance (Document Check List)
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadDocumentCheckListActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Budget Management (Policy)
        cardBudgetManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPolicyActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        


        // SDSA
        cardStrategicPlanning.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadSdsaActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
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
        // Use Volley for better performance instead of HttpURLConnection
        String url = BASE_URL + "get_business_head_users.php";
        
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        if (jsonResponse.getString("status").equals("success")) {
                            // Check if statistics field exists
                            if (jsonResponse.has("statistics")) {
                                JSONObject statistics = jsonResponse.getJSONObject("statistics");
                                
                                final int totalBusinessHeads = statistics.optInt("total_business_head_users", 0);
                                final int activeBusinessHeads = statistics.optInt("active_business_head_users", 0);
                                final int totalTeamMembers = statistics.optInt("total_team_members", 0);
                                final int activeTeamMembers = statistics.optInt("active_team_members", 0);
                                final int totalSdsaUsers = statistics.optInt("total_sdsa_users", 0);
                                final int totalPartnerUsers = statistics.optInt("total_partner_users", 0);
                                final int totalAgentUsers = statistics.optInt("total_agent_users", 0);
                                
                                updateStatsWithRealData(totalTeamMembers, totalSdsaUsers, totalPartnerUsers, 0, totalAgentUsers);
                            } else {
                                // If no statistics, use default values
                                updateStats(0, 0, 0, 0);
                                Log.w(TAG, "No statistics field found in API response");
                            }
                        } else {
                            showError("Failed to load data: " + jsonResponse.optString("message", "Unknown error"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON response", e);
                        showError("Error parsing response: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley error loading business head data", error);
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    showError(errorMessage);
                }
            }
        );

        // Add timeout and retry policy for better performance
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            10000, // 10 seconds timeout
            1,      // 1 retry
            1.0f    // No backoff multiplier
        ));

        // Add to Volley queue
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(jsonRequest);
    }
    
    private void updateStats(int totalBusinessHeads, int activeBusinessHeads, int totalTeamMembers, int activeTeamMembers) {
        // Update stats with real data from API
        totalEmpCount.setText(String.valueOf(totalTeamMembers));
        totalSDSACount.setText(String.valueOf(totalTeamMembers)); // SDSA count from team members
        totalPartnerCount.setText(String.valueOf(totalTeamMembers)); // Partner count from team members
        totalPortfolioCount.setText(String.valueOf(totalTeamMembers)); // Portfolio count from team members
        totalAgentCount.setText(String.valueOf(totalTeamMembers)); // Agent count from team members
        
        Log.d(TAG, "Updated stats - Total Team Members: " + totalTeamMembers + ", Active Team Members: " + activeTeamMembers);
    }
    
    private void updateStatsWithRealData(int totalEmp, int totalSdsa, int totalPartner, int totalPortfolio, int totalAgent) {
        // Update each stat card with real data from API
        totalEmpCount.setText(String.valueOf(totalEmp));
        totalSDSACount.setText(String.valueOf(totalSdsa));
        totalPartnerCount.setText(String.valueOf(totalPartner));
        totalPortfolioCount.setText(String.valueOf(totalPortfolio));
        totalAgentCount.setText(String.valueOf(totalAgent));
        
        Log.d(TAG, "Updated stats with real data - Emp: " + totalEmp + ", SDSA: " + totalSdsa + 
              ", Partner: " + totalPartner + ", Portfolio: " + totalPortfolio + ", Agent: " + totalAgent);
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
        
        // Total Partner Card - Navigate to Business Head Partner Panel
        cardTotalPartner.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPartnerActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
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
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        
        // Clean up resources to prevent memory leaks
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
        
        if (requestQueue != null) {
            requestQueue.cancelAll("BusinessHeadPanelActivity");
        }
        
        // Clear references to prevent memory leaks
        requestQueue = null;
        executor = null;
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
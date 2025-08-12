package com.kfinone.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_panel);
        
        // Check for app updates
        checkForAppUpdates();
        
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
        loadBusinessHeadData();
        updateWelcomeText();
        
        // Setup stat card click listeners
        setupStatCardClickListeners();
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
            Intent intent = new Intent(BusinessHeadPanelActivity.this, MyAccountPanelActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
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
            Toast.makeText(this, "Employee Management - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Employee Management functionality
        });
        
        // Data Links
        cardBusinessAnalytics.setOnClickListener(v -> {
            Toast.makeText(this, "Data Analytics - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Data Analytics functionality
        });
        
        // Work Links
        cardReportsInsights.setOnClickListener(v -> {
            Toast.makeText(this, "Work Links - Coming Soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement Work Links functionality
        });
        
        // Performance Tracking (Emp Master)
        cardPerformanceTracking.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadEmpMasterActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Strategic Planning
        cardStrategicPlanning.setOnClickListener(v -> {
            showToast("Strategic Planning - Coming Soon!");
        });
        
        // Resource Management (Partner)
        cardResourceManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadPartnerActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
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
        
        // Innovation Hub
        cardInnovationHub.setOnClickListener(v -> {
            showToast("Innovation Hub - Coming Soon!");
        });
        
        // Partnerships (Bankers List)
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BankerListActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("SOURCE_PANEL", "BUSINESS_HEAD_PANEL");
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
        if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName);
        } else {
            welcomeText.setText("Welcome back, Business Head");
        }
    }
    
    private void loadBusinessHeadData() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_business_head_users.php";
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    
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
                            
                            runOnUiThread(() -> {
                                updateStatsWithRealData(totalTeamMembers, totalSdsaUsers, totalPartnerUsers, 0, totalAgentUsers);
                            });
                        } else {
                            // If no statistics, use default values
                            runOnUiThread(() -> {
                                updateStats(0, 0, 0, 0);
                                Log.w(TAG, "No statistics field found in API response");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            showError("Failed to load data: " + jsonResponse.optString("message", "Unknown error"));
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        showError("Server error: " + responseCode);
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading business head data", e);
                runOnUiThread(() -> {
                    showError("Network error: " + e.getMessage());
                });
            }
        });
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
        executor.shutdown();
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
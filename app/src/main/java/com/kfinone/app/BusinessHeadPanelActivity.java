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

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    
    // Header Views
    private TextView welcomeText;
    private TextView totalTeamCount;
    private TextView activeProjectsCount;
    private TextView revenueCount;
    private TextView performanceScore;
    private TextView growthRate;
    
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
    private LinearLayout cardGoalsKPIs;
    private LinearLayout cardSettings;
    
    // Bottom Navigation
    private BottomNavigationView businessHeadBottomNav;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_panel);
        
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
        setupBottomNavigation();
        setupCardClickListeners();
        loadBusinessHeadData();
        updateWelcomeText();
    }
    
    private void initializeViews() {
        // Header Views
        welcomeText = findViewById(R.id.welcomeText);
        totalTeamCount = findViewById(R.id.totalTeamCount);
        activeProjectsCount = findViewById(R.id.activeProjectsCount);
        revenueCount = findViewById(R.id.revenueCount);
        performanceScore = findViewById(R.id.performanceScore);
        growthRate = findViewById(R.id.growthRate);
        
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
        cardGoalsKPIs = findViewById(R.id.cardGoalsKPIs);
        cardSettings = findViewById(R.id.cardSettings);
        
        // Bottom Navigation
        businessHeadBottomNav = findViewById(R.id.businessHeadBottomNav);
    }
    
    private void setupBottomNavigation() {
        businessHeadBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_dashboard) {
                // Already on dashboard
                return true;
            } else if (itemId == R.id.nav_team) {
                showToast("Team Management - Coming Soon!");
                return true;
            } else if (itemId == R.id.nav_reports) {
                showToast("Reports & Analytics - Coming Soon!");
                return true;
            } else if (itemId == R.id.nav_settings) {
                showToast("Settings - Coming Soon!");
                return true;
            }
            
            return false;
        });
    }
    
    private void setupCardClickListeners() {
        // Emp Links
        cardTeamManagement.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, EmpLinksActivity.class);
            // Pass user data
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (username != null) intent.putExtra("USERNAME", username);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            startActivity(intent);
        });
        
        // Data Links
        cardBusinessAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadDataLinksActivity.class);
            startActivity(intent);
        });
        
        // Work Links
        cardReportsInsights.setOnClickListener(v -> {
            Intent intent = new Intent(BusinessHeadPanelActivity.this, BusinessHeadWorkLinksActivity.class);
            startActivity(intent);
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
        
        // Goals & KPIs
        cardGoalsKPIs.setOnClickListener(v -> {
            showToast("Goals & KPIs - Coming Soon!");
        });
        
        // Settings
        cardSettings.setOnClickListener(v -> {
            showToast("Settings - Coming Soon!");
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
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONObject statistics = data.getJSONObject("statistics");
                        
                        final int totalBusinessHeads = statistics.getInt("total_business_head_users");
                        final int activeBusinessHeads = statistics.getInt("active_business_head_users");
                        final int totalTeamMembers = statistics.getInt("total_team_members");
                        final int activeTeamMembers = statistics.getInt("active_team_members");
                        
                        runOnUiThread(() -> {
                            updateStats(totalBusinessHeads, activeBusinessHeads, totalTeamMembers, activeTeamMembers);
                        });
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
        // Update team count
        totalTeamCount.setText(String.valueOf(activeTeamMembers));
        
        // Update other stats with sample data for now
        activeProjectsCount.setText("12");
        revenueCount.setText("₹2.5M");
        performanceScore.setText("92%");
        growthRate.setText("+18%");
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
} 
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
    private TextView totalMarketingTeamCount;
    private TextView activeCampaignsCount;
    private TextView leadConversionRate;
    private TextView marketSharePercentage;
    private TextView brandAwarenessScore;
    
    // Stat Card Views
    private CardView cardTotalMarketingTeam;
    private CardView cardActiveCampaigns;
    private CardView cardLeadConversion;
    private CardView cardMarketShare;
    private CardView cardBrandAwareness;
    
    // Header Icons
    private View menuButton;
    private View notificationIcon;
    private View profileIcon;
    
    // Action Card Views
    private LinearLayout cardCampaignManagement;
    private LinearLayout cardBrandStrategy;
    private LinearLayout cardMarketResearch;
    private LinearLayout cardLeadManagement;
    private LinearLayout cardDigitalMarketing;
    private LinearLayout cardContentStrategy;
    private LinearLayout cardPerformanceAnalytics;
    private LinearLayout cardCustomerInsights;
    private LinearLayout cardSocialMedia;
    private LinearLayout cardROITracking;
    private LinearLayout cardCompetitorAnalysis;
    private LinearLayout cardMarketingBudget;
    
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
        totalMarketingTeamCount = findViewById(R.id.totalMarketingTeamCount);
        activeCampaignsCount = findViewById(R.id.activeCampaignsCount);
        leadConversionRate = findViewById(R.id.leadConversionRate);
        marketSharePercentage = findViewById(R.id.marketSharePercentage);
        brandAwarenessScore = findViewById(R.id.brandAwarenessScore);
        
        // Stat Card Views
        cardTotalMarketingTeam = findViewById(R.id.cardTotalMarketingTeam);
        cardActiveCampaigns = findViewById(R.id.cardActiveCampaigns);
        cardLeadConversion = findViewById(R.id.cardLeadConversion);
        cardMarketShare = findViewById(R.id.cardMarketShare);
        cardBrandAwareness = findViewById(R.id.cardBrandAwareness);
        
        // Header Icons
        menuButton = findViewById(R.id.menuButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        
        // Action Card Views
        cardCampaignManagement = findViewById(R.id.cardCampaignManagement);
        cardBrandStrategy = findViewById(R.id.cardBrandStrategy);
        cardMarketResearch = findViewById(R.id.cardMarketResearch);
        cardLeadManagement = findViewById(R.id.cardLeadManagement);
        cardDigitalMarketing = findViewById(R.id.cardDigitalMarketing);
        cardContentStrategy = findViewById(R.id.cardContentStrategy);
        cardPerformanceAnalytics = findViewById(R.id.cardPerformanceAnalytics);
        cardCustomerInsights = findViewById(R.id.cardCustomerInsights);
        cardSocialMedia = findViewById(R.id.cardSocialMedia);
        cardROITracking = findViewById(R.id.cardROITracking);
        cardCompetitorAnalysis = findViewById(R.id.cardCompetitorAnalysis);
        cardMarketingBudget = findViewById(R.id.cardMarketingBudget);
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
        // Campaign Management
        cardCampaignManagement.setOnClickListener(v -> {
            showToast("Campaign Management - Coming Soon!");
            // TODO: Launch Campaign Management Activity
        });
        
        // Brand Strategy
        cardBrandStrategy.setOnClickListener(v -> {
            showToast("Brand Strategy - Coming Soon!");
            // TODO: Launch Brand Strategy Activity
        });
        
        // Market Research
        cardMarketResearch.setOnClickListener(v -> {
            showToast("Market Research - Coming Soon!");
            // TODO: Launch Market Research Activity
        });
        
        // Lead Management
        cardLeadManagement.setOnClickListener(v -> {
            showToast("Lead Management - Coming Soon!");
            // TODO: Launch Lead Management Activity
        });
        
        // Digital Marketing
        cardDigitalMarketing.setOnClickListener(v -> {
            showToast("Digital Marketing - Coming Soon!");
            // TODO: Launch Digital Marketing Activity
        });
        
        // Content Strategy
        cardContentStrategy.setOnClickListener(v -> {
            showToast("Content Strategy - Coming Soon!");
            // TODO: Launch Content Strategy Activity
        });
        
        // Performance Analytics
        cardPerformanceAnalytics.setOnClickListener(v -> {
            showToast("Performance Analytics - Coming Soon!");
            // TODO: Launch Performance Analytics Activity
        });
        
        // Customer Insights
        cardCustomerInsights.setOnClickListener(v -> {
            showToast("Customer Insights - Coming Soon!");
            // TODO: Launch Customer Insights Activity
        });
        
        // Social Media
        cardSocialMedia.setOnClickListener(v -> {
            showToast("Social Media - Coming Soon!");
            // TODO: Launch Social Media Activity
        });
        
        // ROI Tracking
        cardROITracking.setOnClickListener(v -> {
            showToast("ROI Tracking - Coming Soon!");
            // TODO: Launch ROI Tracking Activity
        });
        
        // Competitor Analysis
        cardCompetitorAnalysis.setOnClickListener(v -> {
            showToast("Competitor Analysis - Coming Soon!");
            // TODO: Launch Competitor Analysis Activity
        });
        
        // Marketing Budget
        cardMarketingBudget.setOnClickListener(v -> {
            showToast("Marketing Budget - Coming Soon!");
            // TODO: Launch Marketing Budget Activity
        });
    }
    
    private void setupStatCardClickListeners() {
        // Total Marketing Team
        cardTotalMarketingTeam.setOnClickListener(v -> {
            showToast("Marketing Team Overview - Coming Soon!");
            // TODO: Launch Marketing Team Activity
        });
        
        // Active Campaigns
        cardActiveCampaigns.setOnClickListener(v -> {
            showToast("Active Campaigns - Coming Soon!");
            // TODO: Launch Active Campaigns Activity
        });
        
        // Lead Conversion
        cardLeadConversion.setOnClickListener(v -> {
            showToast("Lead Conversion Analytics - Coming Soon!");
            // TODO: Launch Lead Conversion Activity
        });
        
        // Market Share
        cardMarketShare.setOnClickListener(v -> {
            showToast("Market Share Analysis - Coming Soon!");
            // TODO: Launch Market Share Activity
        });
        
        // Brand Awareness
        cardBrandAwareness.setOnClickListener(v -> {
            showToast("Brand Awareness Metrics - Coming Soon!");
            // TODO: Launch Brand Awareness Activity
        });
    }
    
    private void setInitialStats() {
        totalMarketingTeamCount.setText("0");
        activeCampaignsCount.setText("0");
        leadConversionRate.setText("0%");
        marketSharePercentage.setText("0%");
        brandAwarenessScore.setText("0");
    }
    
    private void loadMarketingHeadData() {
        // TODO: Implement API calls to load marketing data
        // For now, set some sample data
        executor.execute(() -> {
            // Simulate API call delay
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Update UI on main thread
            runOnUiThread(() -> {
                // Sample data - replace with actual API calls
                totalMarketingTeamCount.setText("12");
                activeCampaignsCount.setText("8");
                leadConversionRate.setText("24%");
                marketSharePercentage.setText("18%");
                brandAwarenessScore.setText("85");
            });
        });
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
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
    
    @Override
    protected void onResume() {
        super.onResume();
        updateWelcomeText();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            requestQueue.cancelAll("MarketingHeadPanelActivity");
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (requestQueue != null) {
            requestQueue.cancelAll("MarketingHeadPanelActivity");
        }
    }
}

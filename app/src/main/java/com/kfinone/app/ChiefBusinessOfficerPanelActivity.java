package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChiefBusinessOfficerPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalPortfolioCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    
    // Stat card views
    private View cardTotalEmp, cardTotalSdsa, cardTotalPartner, cardTotalPortfolio, cardTotalAgent;
    
    // Action card views
    private View cardPortfolio, cardTeam, cardReports, cardAnalytics, cardStrategy, 
                 cardPerformance, cardGrowth, cardInnovation, cardPartnerships, 
                 cardMarketAnalysis, cardRiskManagement, cardCompliance, cardBudget, 
                 cardGoals, cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_business_officer_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
        }
        
        // Debug: Log received user data
        Log.d("CBOPanel", "Received userName: " + userName);
        Log.d("CBOPanel", "Received userId: " + userId);
        
        // Critical check: Ensure we have a valid userId
        if (userId == null || userId.isEmpty()) {
            Log.e("CBOPanel", "CRITICAL ERROR: No valid userId received!");
            Log.e("CBOPanel", "This will cause navigation issues in downstream activities");
            Log.e("CBOPanel", "Expected: numeric ID like '21', Got: '" + userId + "'");
            
            // Try to get userId from SharedPreferences as fallback
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String savedUserId = prefs.getString("USER_ID", null);
                if (savedUserId != null && !savedUserId.isEmpty()) {
                    userId = savedUserId;
                    Log.d("CBOPanel", "Using userId from SharedPreferences: " + userId);
                } else {
                    Log.e("CBOPanel", "No userId found in SharedPreferences either");
                }
            } catch (Exception e) {
                Log.e("CBOPanel", "Error reading from SharedPreferences: " + e.getMessage());
            }
        } else {
            Log.d("CBOPanel", "✓ Valid userId received: " + userId);
        }
        
        // Additional debug info
        Log.d("CBOPanel", "Final userId before updateStats: " + userId);
        Log.d("CBOPanel", "userId type: " + (userId != null ? userId.getClass().getSimpleName() : "null"));
        Log.d("CBOPanel", "userId length: " + (userId != null ? userId.length() : "N/A"));
        
        initializeViews();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        totalPortfolioCount = findViewById(R.id.totalPortfolioCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize stat cards
        cardTotalEmp = findViewById(R.id.cardTotalEmp);
        cardTotalSdsa = findViewById(R.id.cardTotalSdsa);
        cardTotalPartner = findViewById(R.id.cardTotalPartner);
        cardTotalPortfolio = findViewById(R.id.cardTotalPortfolio);
        cardTotalAgent = findViewById(R.id.cardTotalAgent);
        
        // Initialize action cards
        cardPortfolio = findViewById(R.id.cardPortfolio);
        cardTeam = findViewById(R.id.cardTeam);
        cardReports = findViewById(R.id.cardReports);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        cardStrategy = findViewById(R.id.cardStrategy);
        cardPerformance = findViewById(R.id.cardPerformance);
        cardGrowth = findViewById(R.id.cardGrowth);
        cardInnovation = findViewById(R.id.cardInnovation);
        cardPartnerships = findViewById(R.id.cardPartnerships);
        cardMarketAnalysis = findViewById(R.id.cardMarketAnalysis);
        cardRiskManagement = findViewById(R.id.cardRiskManagement);
        cardCompliance = findViewById(R.id.cardCompliance);
        cardBudget = findViewById(R.id.cardBudget);
        cardGoals = findViewById(R.id.cardGoals);
        cardSettings = findViewById(R.id.cardSettings);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
    }

    private void setupClickListeners() {
        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            showProfileMenu();
        });

        // Stat Cards Click Listeners - Make them clickable for navigation
        // Total Emp Card - Navigate to CBO Active Emp List
        cardTotalEmp.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOMyActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Total SDSA Card - Navigate to CBO SDSA Panel
        cardTotalSdsa.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOSdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Total Partner Card - Navigate to CBO Partner Panel
        cardTotalPartner.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Total Portfolio Card - Navigate to CBO Portfolio Panel
        cardTotalPortfolio.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPortfolioPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Total Agent Card - Navigate to CBO Agent Panel
        cardTotalAgent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Action Card Click Listeners
        cardPortfolio.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        cardTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerDataLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        cardAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpMasterActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardStrategy.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmployeeActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardPerformance.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOSdsaActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPartnerActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardInnovation.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOAgentActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardPartnerships.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPayoutPanelActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardMarketAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardRiskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOBankersPanelActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardCompliance.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPortfolioPanelActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardGoals.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocCheckListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
        
        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, PolicyActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        // Update welcome message with full name (firstName + lastName)
        String welcomeMessage;
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeMessage = "Welcome back, " + firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            welcomeMessage = "Welcome back, " + firstName;
        } else if (userName != null && !userName.isEmpty()) {
            welcomeMessage = "Welcome back, " + userName;
        } else {
            welcomeMessage = "Welcome back, CBO";
        }
        welcomeText.setText(welcomeMessage);
    }
    
    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (userName != null) intent.putExtra("USERNAME", userName);
    }

    private void showMenuOptions() {
        String[] menuOptions = {
            "Dashboard",
            "Team Management", 
            "Portfolio Management",
            "Reports & Analytics",
            "Profile",
            "Notifications",
            "Help & Support",
            "About",
            "Logout"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(menuOptions, (dialog, which) -> {
                switch (which) {
                    case 0: // Dashboard
                        // Already on dashboard
                        break;
                    case 1: // Team Management
                        Intent teamIntent = new Intent(this, CBOTeamActivity.class);
                        passUserDataToIntent(teamIntent);
                        teamIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(teamIntent);
                        break;
                    case 2: // Portfolio Management
                        Intent portfolioIntent = new Intent(this, CBOPortfolioActivity.class);
                        passUserDataToIntent(portfolioIntent);
                        portfolioIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(portfolioIntent);
                        break;
                    case 3: // Reports & Analytics
                        Intent reportsIntent = new Intent(this, CBOReportsActivity.class);
                        passUserDataToIntent(reportsIntent);
                        reportsIntent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(reportsIntent);
                        break;
                    case 4: // Profile
                        Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case 5: // Notifications
                        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case 6: // Help & Support
                        Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case 7: // About
                        showAboutDialog();
                        break;
                    case 8: // Logout
                        showLogoutConfirmation();
                        break;
                }
            })
            .show();
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About KfinOne CBO Panel")
            .setMessage("Chief Business Officer Dashboard\n\n" +
                       "Version: 1.0\n" +
                       "Designed for executive management\n" +
                       "© 2024 KfinOne Technologies")
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void showProfileMenu() {
        String[] options = {"Profile", "Settings", "Help", "About"};
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Account Options")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // Open User Profile
                        Intent intent = new Intent(this, UserProfileActivity.class);
                        passUserDataToIntent(intent);
                        intent.putExtra("USER_DESIGNATION", "CBO");
                        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
                        startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(this, "Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "Help - Coming Soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        showAboutDialog();
                        break;
                }
            })
            .show();
    }

    // API Methods to fetch counts
    private void fetchEmployeeCount() {
        Log.d("CBOPanel", "=== fetchEmployeeCount() started ===");
        Log.d("CBOPanel", "userId: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            Log.e("CBOPanel", "userId is null or empty, setting count to 0");
            totalEmpCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                                 String urlString = "https://emp.kfinone.com/mobile/api/cbo_my_emp_list.php?user_id=" + userId;
                 Log.d("CBOPanel", "Calling API: " + urlString);
                 
                 java.net.URL url = new java.net.URL(urlString);
                 java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                 connection.setRequestMethod("GET");
                 connection.setConnectTimeout(15000);
                 connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d("CBOPanel", "Employee API Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d("CBOPanel", "Employee API Response: " + response.toString());
                    
                                         org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                     Log.d("CBOPanel", "Employee API Response: " + response.toString());
                     
                     // Check for different success indicators
                     boolean isSuccess = jsonResponse.optBoolean("success", false) || 
                                       "success".equals(jsonResponse.optString("status", ""));
                     
                     if (isSuccess) {
                         // The working API returns: {"status": "success", "data": {"employees": [...], "statistics": {"employee_count": X}}}
                         org.json.JSONArray employees = null;
                         
                         // First try to get the count from statistics (most reliable)
                         if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                             try {
                                 org.json.JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                 if (statistics.has("employee_count")) {
                                     final int count = statistics.getInt("employee_count");
                                     Log.d("CBOPanel", "Employee count from statistics: " + count);
                                     Log.d("CBOPanel", "✓ SUCCESS: Setting Employee count to: " + count);
                                     runOnUiThread(() -> totalEmpCount.setText(String.valueOf(count)));
                                     return;
                                 }
                             } catch (Exception e) {
                                 Log.e("CBOPanel", "Error getting count from statistics: " + e.getMessage());
                             }
                         }
                         
                         // Fallback: Try different possible array names
                         if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("employees")) {
                             employees = jsonResponse.getJSONObject("data").getJSONArray("employees");
                         } else if (jsonResponse.has("employees")) {
                             employees = jsonResponse.getJSONArray("employees");
                         } else if (jsonResponse.has("data")) {
                             employees = jsonResponse.getJSONArray("data");
                         } else if (jsonResponse.has("users")) {
                             employees = jsonResponse.getJSONArray("users");
                         }
                         
                         if (employees != null) {
                             final int count = employees.length();
                             Log.d("CBOPanel", "Employee count from array length: " + count);
                             Log.d("CBOPanel", "✓ SUCCESS: Setting Employee count to: " + count);
                             runOnUiThread(() -> totalEmpCount.setText(String.valueOf(count)));
                         } else {
                             Log.e("CBOPanel", "No employees array found in response");
                             runOnUiThread(() -> totalEmpCount.setText("0"));
                         }
                     } else {
                         Log.e("CBOPanel", "Employee API returned success=false");
                         runOnUiThread(() -> totalEmpCount.setText("0"));
                     }
                } else {
                    Log.e("CBOPanel", "Employee API HTTP error: " + responseCode);
                    runOnUiThread(() -> totalEmpCount.setText("0"));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("CBOPanel", "Error fetching employee count: " + e.getMessage());
                runOnUiThread(() -> totalEmpCount.setText("0"));
            }
        }).start();
    }
    
    private void fetchSdsaCount() {
        if (userId == null || userId.isEmpty()) {
            totalSdsaCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                // Use the correct API endpoint that exists
                String urlString = "https://emp.kfinone.com/mobile/api/get_cbo_my_sdsa_users.php?reportingTo=" + userId;
                Log.d("CBOPanel", "Calling SDSA API: " + urlString);
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d("CBOPanel", "SDSA API Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d("CBOPanel", "SDSA API Response: " + response.toString());
                    
                                         org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                     Log.d("CBOPanel", "SDSA API Response: " + response.toString());
                     
                     // Check for different success indicators
                     boolean isSuccess = jsonResponse.optBoolean("success", false) || 
                                       "success".equals(jsonResponse.optString("status", ""));
                     
                     if (isSuccess) {
                         // Try different possible array names
                         org.json.JSONArray sdsaList = null;
                         if (jsonResponse.has("sdsa_users")) {
                             sdsaList = jsonResponse.getJSONArray("sdsa_users");
                         } else if (jsonResponse.has("users")) {
                             sdsaList = jsonResponse.getJSONArray("users");
                         } else if (jsonResponse.has("data")) {
                             sdsaList = jsonResponse.getJSONArray("data");
                         }
                         
                         if (sdsaList != null) {
                             final int count = sdsaList.length();
                             Log.d("CBOPanel", "SDSA count: " + count);
                             Log.d("CBOPanel", "✓ SUCCESS: Setting SDSA count to: " + count);
                             runOnUiThread(() -> totalSdsaCount.setText(String.valueOf(count)));
                         } else {
                             Log.e("CBOPanel", "No SDSA array found in response");
                             runOnUiThread(() -> totalSdsaCount.setText("0"));
                         }
                     } else {
                         Log.e("CBOPanel", "SDSA API returned success=false");
                         runOnUiThread(() -> totalSdsaCount.setText("0"));
                     }
                } else {
                    Log.e("CBOPanel", "SDSA API HTTP error: " + responseCode);
                    runOnUiThread(() -> totalSdsaCount.setText("0"));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("CBOPanel", "Error fetching SDSA count: " + e.getMessage());
                runOnUiThread(() -> totalSdsaCount.setText("0"));
            }
        }).start();
    }
    
    private void fetchPartnerCount() {
        if (userId == null || userId.isEmpty()) {
            totalPartnerCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                // Use the correct API endpoint that exists
                String urlString = "https://emp.kfinone.com/mobile/api/cbo_my_partner_users.php?user_id=" + userId;
                Log.d("CBOPanel", "Calling Partner API: " + urlString);
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d("CBOPanel", "Partner API Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d("CBOPanel", "Partner API Response: " + response.toString());
                    
                                         org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                     Log.d("CBOPanel", "Partner API Response: " + response.toString());
                     
                     // Check for different success indicators
                     boolean isSuccess = jsonResponse.optBoolean("success", false) || 
                                       "success".equals(jsonResponse.optString("status", ""));
                     
                     if (isSuccess) {
                         // Try different possible array names
                         org.json.JSONArray partnerList = null;
                         if (jsonResponse.has("partner_users")) {
                             partnerList = jsonResponse.getJSONArray("partner_users");
                         } else if (jsonResponse.has("data")) {
                             partnerList = jsonResponse.getJSONArray("data");
                         } else if (jsonResponse.has("users")) {
                             partnerList = jsonResponse.getJSONArray("users");
                         }
                         
                         if (partnerList != null) {
                             final int count = partnerList.length();
                             Log.d("CBOPanel", "Partner count: " + count);
                             Log.d("CBOPanel", "✓ SUCCESS: Setting Partner count to: " + count);
                             runOnUiThread(() -> totalPartnerCount.setText(String.valueOf(count)));
                         } else {
                             Log.e("CBOPanel", "No partner array found in response");
                             runOnUiThread(() -> totalPartnerCount.setText("0"));
                         }
                     } else {
                         Log.e("CBOPanel", "Partner API returned success=false");
                         runOnUiThread(() -> totalPartnerCount.setText("0"));
                     }
                } else {
                    Log.e("CBOPanel", "Partner API HTTP error: " + responseCode);
                    runOnUiThread(() -> totalPartnerCount.setText("0"));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("CBOPanel", "Error fetching partner count: " + e.getMessage());
                runOnUiThread(() -> totalPartnerCount.setText("0"));
            }
        }).start();
    }
    
    private void fetchPortfolioCount() {
        // Since there's no portfolio API, we'll set a placeholder value
        Log.d("CBOPanel", "Portfolio API not available, setting placeholder value");
        runOnUiThread(() -> totalPortfolioCount.setText("N/A"));
    }
    
    private void fetchAgentCount() {
        if (userId == null || userId.isEmpty()) {
            totalAgentCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                // Use the correct API endpoint that exists
                String urlString = "https://emp.kfinone.com/mobile/api/cbo_my_agent_data.php?user_id=" + userId;
                Log.d("CBOPanel", "Calling Agent API: " + urlString);
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d("CBOPanel", "Agent API Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d("CBOPanel", "Agent API Response: " + response.toString());
                    
                                         org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                     Log.d("CBOPanel", "Agent API Response: " + response.toString());
                     
                     // Check for different success indicators
                     boolean isSuccess = jsonResponse.optBoolean("success", false) || 
                                       "success".equals(jsonResponse.optString("status", ""));
                     
                     if (isSuccess) {
                         // Try different possible array names
                         org.json.JSONArray agentList = null;
                         if (jsonResponse.has("data")) {
                             agentList = jsonResponse.getJSONArray("data");
                         } else if (jsonResponse.has("agents")) {
                             agentList = jsonResponse.getJSONArray("agents");
                         } else if (jsonResponse.has("users")) {
                             agentList = jsonResponse.getJSONArray("users");
                         }
                         
                         if (agentList != null) {
                             final int count = agentList.length();
                             Log.d("CBOPanel", "Agent count: " + count);
                             Log.d("CBOPanel", "✓ SUCCESS: Setting Agent count to: " + count);
                             runOnUiThread(() -> totalAgentCount.setText(String.valueOf(count)));
                         } else {
                             Log.e("CBOPanel", "No agent array found in response");
                             runOnUiThread(() -> totalAgentCount.setText("0"));
                         }
                     } else {
                         Log.e("CBOPanel", "Agent API returned success=false");
                         runOnUiThread(() -> totalAgentCount.setText("0"));
                     }
                } else {
                    Log.e("CBOPanel", "Agent API HTTP error: " + responseCode);
                    runOnUiThread(() -> totalAgentCount.setText("0"));
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("CBOPanel", "Error fetching agent count: " + e.getMessage());
                runOnUiThread(() -> totalAgentCount.setText("0"));
            }
        }).start();
    }

    private void updateStats() {
        Log.d("CBOPanel", "=== Starting updateStats() ===");
        Log.d("CBOPanel", "Current userId: " + userId);
        Log.d("CBOPanel", "userId type: " + (userId != null ? userId.getClass().getSimpleName() : "null"));
        Log.d("CBOPanel", "userId length: " + (userId != null ? userId.length() : "N/A"));
        
        // Show loading state initially
        totalEmpCount.setText("...");
        totalSdsaCount.setText("...");
        totalPortfolioCount.setText("...");
        totalPartnerCount.setText("...");
        totalAgentCount.setText("...");
        
        // Check if userId is available
        if (userId == null || userId.isEmpty()) {
            Log.e("CBOPanel", "userId is null, cannot fetch real data");
            Log.e("CBOPanel", "Setting test values to verify UI is working");
            
            // Set test values to verify the UI is working
            totalEmpCount.setText("TEST");
            totalSdsaCount.setText("TEST");
            totalPortfolioCount.setText("TEST");
            totalPartnerCount.setText("TEST");
            totalAgentCount.setText("TEST");
            
            // Try to get userId from SharedPreferences again
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String savedUserId = prefs.getString("USER_ID", null);
                Log.d("CBOPanel", "SharedPreferences USER_ID: " + savedUserId);
                
                if (savedUserId != null && !savedUserId.isEmpty()) {
                    Log.d("CBOPanel", "Retrying with userId from SharedPreferences: " + savedUserId);
                    userId = savedUserId;
                    
                    // Now try to fetch real data
                    fetchEmployeeCount();
                    fetchSdsaCount();
                    fetchPartnerCount();
                    fetchPortfolioCount();
                    fetchAgentCount();
                }
            } catch (Exception e) {
                Log.e("CBOPanel", "Error reading from SharedPreferences: " + e.getMessage());
            }
            return;
        }
        
                 // Fetch real data from APIs
         fetchEmployeeCount();
         fetchSdsaCount();
         fetchPartnerCount();
         fetchPortfolioCount();
         fetchAgentCount();
         
         Log.d("CBOPanel", "=== updateStats() completed ===");
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Navigate back to login
                Intent intent = new Intent(this, EnhancedLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }



    @Override
    public void onBackPressed() {
        // Show logout confirmation when back button is pressed
        showLogoutConfirmation();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Restore user data and welcome message when returning to this activity
        updateWelcomeMessage();
    }
    
    // Test method to debug API calls
    private void testApiWithHardcodedUserId() {
        Log.d("CBOPanel", "=== Testing API with hardcoded userId ===");
        
        // Test with a common CBO userId (like 90000)
        String testUserId = "90000";
        Log.d("CBOPanel", "Testing with hardcoded userId: " + testUserId);
        
        new Thread(() -> {
            try {
                                 // Test the employee API
                 String urlString = "https://emp.kfinone.com/mobile/api/cbo_my_emp_list.php?user_id=" + testUserId;
                 Log.d("CBOPanel", "TEST: Calling Employee API with hardcoded userId: " + urlString);
                 
                 java.net.URL url = new java.net.URL(urlString);
                 java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                 connection.setRequestMethod("GET");
                 connection.setConnectTimeout(15000);
                 connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d("CBOPanel", "TEST: Employee API Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d("CBOPanel", "TEST: Employee API Response: " + response.toString());
                    
                                         org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                     Log.d("CBOPanel", "TEST: Employee API Response: " + response.toString());
                     
                     // Check for different success indicators
                     boolean isSuccess = jsonResponse.optBoolean("success", false) || 
                                       "success".equals(jsonResponse.optString("status", ""));
                     
                     if (isSuccess) {
                         // The working API returns: {"status": "success", "data": {"employees": [...], "statistics": {"employee_count": X}}}
                         org.json.JSONArray employees = null;
                         
                         // First try to get the count from statistics (most reliable)
                         if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("statistics")) {
                             try {
                                 org.json.JSONObject statistics = jsonResponse.getJSONObject("data").getJSONObject("statistics");
                                 if (statistics.has("employee_count")) {
                                     final int count = statistics.getInt("employee_count");
                                     Log.d("CBOPanel", "TEST: Employee count from statistics: " + count);
                                     
                                     // Update the UI to show this test result
                                     runOnUiThread(() -> {
                                         totalEmpCount.setText("TEST:" + count);
                                         Log.d("CBOPanel", "TEST: Updated UI with test result: " + count);
                                     });
                                     return;
                                 }
                             } catch (Exception e) {
                                 Log.e("CBOPanel", "TEST: Error getting count from statistics: " + e.getMessage());
                             }
                         }
                         
                         // Fallback: Try different possible array names
                         if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("employees")) {
                             employees = jsonResponse.getJSONObject("data").getJSONArray("employees");
                         } else if (jsonResponse.has("employees")) {
                             employees = jsonResponse.getJSONArray("employees");
                         } else if (jsonResponse.has("data")) {
                             employees = jsonResponse.getJSONArray("data");
                         } else if (jsonResponse.has("users")) {
                             employees = jsonResponse.getJSONArray("users");
                         }
                         
                         if (employees != null) {
                             final int count = employees.length();
                             Log.d("CBOPanel", "TEST: Employee count from array length: " + count);
                             
                             // Update the UI to show this test result
                             runOnUiThread(() -> {
                                 totalEmpCount.setText("TEST:" + count);
                                 Log.d("CBOPanel", "TEST: Updated UI with test result: " + count);
                             });
                         } else {
                             Log.e("CBOPanel", "TEST: No employees array found in response");
                         }
                     } else {
                         Log.e("CBOPanel", "TEST: Employee API returned success=false");
                     }
                } else {
                    Log.e("CBOPanel", "TEST: Employee API HTTP error: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("CBOPanel", "TEST: Error testing employee API: " + e.getMessage());
            }
        }).start();
    }
} 
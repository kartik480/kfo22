package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class RegionalBusinessHeadPanelActivity extends AppCompatActivity {
    private TextView totalEmpCount, totalSdsaCount, totalPartnerCount, totalAgentCount, welcomeText;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    
    // Action card views
    private View cardTotalEmp, cardTotalSdsa, cardTotalPartner, cardTotalAgent, cardTeam, cardReports, cardAnalytics, cardStrategy, 
                 cardPerformance, cardGrowth, cardInnovation, cardPartnerships, 
                 cardMarketAnalysis, cardCompliance, cardDocumentCheckList, cardInsurance, cardPortfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_regional_business_head_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        
        // Debug logging for user data received
        android.util.Log.d("RBHPanel", "=== RBH Panel User Data Debug ===");
        android.util.Log.d("RBHPanel", "Intent extras received:");
        android.util.Log.d("RBHPanel", "USERNAME: " + userName);
        android.util.Log.d("RBHPanel", "USER_ID: " + userId);
        android.util.Log.d("RBHPanel", "FIRST_NAME: " + firstName);
        android.util.Log.d("RBHPanel", "LAST_NAME: " + lastName);
        
        // Check if we have all required data
        if (userName == null || userName.isEmpty()) {
            android.util.Log.w("RBHPanel", "USERNAME is null or empty, using fallback");
            userName = "RBH"; // Default fallback
        }
        
        if (userId == null || userId.isEmpty()) {
            android.util.Log.e("RBHPanel", "CRITICAL ERROR: USER_ID is null or empty!");
            android.util.Log.e("RBHPanel", "This will cause all API calls to fail!");
        } else if ("1".equals(userId)) {
            android.util.Log.e("RBHPanel", "CRITICAL ERROR: USER_ID is 1 (KRAJESHK - SuperAdmin)!");
            android.util.Log.e("RBHPanel", "This is wrong - should be a Regional Business Head user ID!");
            
            // Try to get correct user ID from username
            if (userName != null && !userName.isEmpty()) {
                if ("93000".equals(userName)) {
                    userId = "40"; // SHAIK JEELANI BASHA - Regional Business Head
                    android.util.Log.d("RBHPanel", "Fixed: Mapped username 93000 to userId 40");
                } else if ("chiranjeevi".equals(userName)) {
                    userId = "32"; // CHIRANJEEVI NARLAGIRI - Regional Business Head
                    android.util.Log.d("RBHPanel", "Fixed: Mapped username chiranjeevi to userId 32");
                } else {
                    android.util.Log.w("RBHPanel", "Unknown username, cannot map to correct userId");
                }
            }
        } else {
            android.util.Log.d("RBHPanel", "✓ USER_ID is valid: " + userId);
        }
        
        android.util.Log.d("RBHPanel", "=== End RBH Panel User Data Debug ===");
        
        initializeViews();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Initialize action cards
        cardTotalEmp = findViewById(R.id.cardTotalEmp);
        cardTotalSdsa = findViewById(R.id.cardTotalSdsa);
        cardTotalPartner = findViewById(R.id.cardTotalPartner);
        cardTotalAgent = findViewById(R.id.cardTotalAgent);
        cardTeam = findViewById(R.id.cardTeam);
        cardReports = findViewById(R.id.cardReports);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        cardStrategy = findViewById(R.id.cardStrategy);
        cardPerformance = findViewById(R.id.cardPerformance);
        cardGrowth = findViewById(R.id.cardGrowth);
        cardInnovation = findViewById(R.id.cardInnovation);
        cardPartnerships = findViewById(R.id.cardPartnerships);
        cardMarketAnalysis = findViewById(R.id.cardMarketAnalysis);
        cardCompliance = findViewById(R.id.cardCompliance);
        cardDocumentCheckList = findViewById(R.id.cardDocumentCheckList);
        cardInsurance = findViewById(R.id.cardInsurance);
        cardPortfolio = findViewById(R.id.cardPortfolio);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        
        // Log card initialization
        android.util.Log.d("RBHPanel", "Card initialization:");
        android.util.Log.d("RBHPanel", "cardTotalEmp: " + (cardTotalEmp != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalSdsa: " + (cardTotalSdsa != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalPartner: " + (cardTotalPartner != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTotalAgent: " + (cardTotalAgent != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardTeam: " + (cardTeam != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardReports: " + (cardReports != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardAnalytics: " + (cardAnalytics != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardStrategy: " + (cardStrategy != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardPerformance: " + (cardPerformance != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardGrowth: " + (cardGrowth != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardInnovation: " + (cardInnovation != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardPartnerships: " + (cardPartnerships != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardMarketAnalysis: " + (cardMarketAnalysis != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardCompliance: " + (cardCompliance != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardDocumentCheckList: " + (cardDocumentCheckList != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardInsurance: " + (cardInsurance != null ? "FOUND" : "NULL"));
        android.util.Log.d("RBHPanel", "cardPortfolio: " + (cardPortfolio != null ? "FOUND" : "NULL"));
    }

    private void setupClickListeners() {
        // Menu button click listener
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                showMenuOptions();
            });
        }

        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                // TODO: Open notifications panel
            });
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                // TODO: Open profile settings
            });
        }

        // Action Card Click Listeners
        if (cardTotalEmp != null) {
            cardTotalEmp.setOnClickListener(v -> {
                // Navigate to RBH Active Emp List
                android.util.Log.d("RBHPanel", "Total Emp Card clicked! Navigating to RegionalBusinessHeadActiveEmpListActivity");
                Intent intent = new Intent(this, RegionalBusinessHeadActiveEmpListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardTotalSdsa != null) {
            cardTotalSdsa.setOnClickListener(v -> {
                // Navigate to RBH My SDSA Panel
                android.util.Log.d("RBHPanel", "Total SDSA Card clicked! Navigating to RBHMySdsaPanelActivity");
                android.util.Log.d("RBHPanel", "Passing user data to SDSA panel:");
                android.util.Log.d("RBHPanel", "USERNAME: " + userName);
                android.util.Log.d("RBHPanel", "USER_ID: " + userId);
                
                Intent intent = new Intent(this, RBHMySdsaPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardTotalPartner != null) {
            cardTotalPartner.setOnClickListener(v -> {
                // Navigate to RBH My Partner Panel
                android.util.Log.d("RBHPanel", "Total Partner Card clicked! Navigating to RegionalBusinessHeadMyPartnerPanelActivity");
                Intent intent = new Intent(this, RegionalBusinessHeadMyPartnerPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardTotalAgent != null) {
            cardTotalAgent.setOnClickListener(v -> {
                // Navigate to RBH My Agent List
                android.util.Log.d("RBHPanel", "Total Agent Card clicked! Navigating to RBHMyAgentListActivity");
                Intent intent = new Intent(this, RBHMyAgentListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardPortfolio != null) {
            cardPortfolio.setOnClickListener(v -> {
                // Navigate to RBH Emp Links Panel
                android.util.Log.d("RBHPanel", "Emp Links Card clicked! Navigating to RBHEmpLinksActivity");
                Intent intent = new Intent(this, RBHEmpLinksActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            });
        }
        
        if (cardTeam != null) {
            cardTeam.setOnClickListener(v -> {
                // Navigate to RBH Data Links Panel
                android.util.Log.d("RBHPanel", "Data Links Card clicked! Navigating to RegionalBusinessHeadDataLinksActivity");
                Intent intent = new Intent(this, RegionalBusinessHeadDataLinksActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            });
        }
        
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> {
                // Navigate to RBH Work Links Panel
                android.util.Log.d("RBHPanel", "Work Links Card clicked! Navigating to RegionalBusinessHeadWorkLinksActivity");
                Intent intent = new Intent(this, RegionalBusinessHeadWorkLinksActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            });
        }
        
        if (cardAnalytics != null) {
            cardAnalytics.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHEmpMasterActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardStrategy != null) {
            cardStrategy.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegionalBusinessHeadActiveEmpListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardPerformance != null) {
            cardPerformance.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHMySdsaPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardGrowth != null) {
            cardGrowth.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegionalBusinessHeadMyPartnerPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardInnovation != null) {
            cardInnovation.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHMyAgentPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId); // Pass the actual user ID
                startActivity(intent);
            });
        }
        
        if (cardPartnerships != null) {
            cardPartnerships.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHPayoutPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });
        }
        
        if (cardMarketAnalysis != null) {
            cardMarketAnalysis.setOnClickListener(v -> {
                Intent intent = new Intent(this, DsaCodeListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
        
        if (cardCompliance != null) {
            cardCompliance.setOnClickListener(v -> {
                Intent intent = new Intent(this, RBHPortfolioPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
            });
        }
        
        if (cardDocumentCheckList != null) {
            cardDocumentCheckList.setOnClickListener(v -> {
                android.util.Log.d("RBHPanel", "Document Check List Card clicked! Navigating to RBHDocumentCheckListActivity");
                Intent intent = new Intent(this, RBHDocumentCheckListActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            });
        }
        
        if (cardInsurance != null) {
            cardInsurance.setOnClickListener(v -> {
                android.util.Log.d("RBHPanel", "Insurance Card clicked! Navigating to RBHInsurancePanelActivity");
                Intent intent = new Intent(this, RBHInsurancePanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
                startActivity(intent);
            });
        }
    }

    private void updateWelcomeMessage() {
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName + " " + lastName);
        } else if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome back, " + firstName);
        } else if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Welcome back, " + userName);
        } else {
            welcomeText.setText("Welcome back, RBH");
        }
    }

    private void showMenuOptions() {
        String[] options = {"About", "Help", "Logout"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Menu Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAboutDialog();
                    break;
                case 1:
                    Toast.makeText(this, "Help - Coming Soon", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showLogoutConfirmation();
                    break;
            }
        });
        builder.show();
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About Regional Business Head Panel");
        builder.setMessage("Regional Business Head Panel v1.0\n\n" +
                "This panel provides comprehensive management tools for Regional Business Heads to oversee their regional operations, team performance, and business metrics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void updateStats() {
        // Fetch real statistics from APIs
        fetchEmployeeCount();
        fetchSdsaCount();
        fetchPartnerCount();
        fetchAgentCount();
    }
    
    private void fetchEmployeeCount() {
        if (userId == null || userId.isEmpty()) {
            totalEmpCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_rbh_active_emp_list.php?user_id=" + userId;
                URL url = new URL(urlString);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    String responseString = response.toString();
                    android.util.Log.d("RBHPanel", "Employee API Response: " + responseString);
                    
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        int totalCount = json.getInt("total_employees");
                        runOnUiThread(() -> {
                            totalEmpCount.setText(String.valueOf(totalCount));
                            android.util.Log.d("RBHPanel", "Employee count updated: " + totalCount);
                        });
                    } else {
                        runOnUiThread(() -> totalEmpCount.setText("0"));
                    }
                } else {
                    runOnUiThread(() -> totalEmpCount.setText("0"));
                }
            } catch (Exception e) {
                android.util.Log.e("RBHPanel", "Error fetching employee count: " + e.getMessage());
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
                String urlString = "https://emp.kfinone.com/mobile/api/get_rbh_my_sdsa_users.php?reportingTo=" + userId + "&status=active";
                URL url = new URL(urlString);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    String responseString = response.toString();
                    android.util.Log.d("RBHPanel", "SDSA API Response: " + responseString);
                    
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONObject stats = json.optJSONObject("statistics");
                        if (stats != null) {
                            int totalCount = stats.optInt("total_users", 0);
                            runOnUiThread(() -> {
                                totalSdsaCount.setText(String.valueOf(totalCount));
                                android.util.Log.d("RBHPanel", "SDSA count updated: " + totalCount);
                            });
                        } else {
                            runOnUiThread(() -> totalSdsaCount.setText("0"));
                        }
                    } else {
                        runOnUiThread(() -> totalSdsaCount.setText("0"));
                    }
                } else {
                    runOnUiThread(() -> totalSdsaCount.setText("0"));
                }
            } catch (Exception e) {
                android.util.Log.e("RBHPanel", "Error fetching SDSA count: " + e.getMessage());
                runOnUiThread(() -> totalSdsaCount.setText("0"));
            }
        }).start();
    }
    
    private void fetchPartnerCount() {
        if (userName == null || userName.isEmpty()) {
            totalPartnerCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/rbh_my_partner_users.php?username=" + userName;
                URL url = new URL(urlString);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    String responseString = response.toString();
                    android.util.Log.d("RBHPanel", "Partner API Response: " + responseString);
                    
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONObject stats = json.optJSONObject("statistics");
                        if (stats != null) {
                            int totalCount = stats.optInt("total_partners", 0);
                            runOnUiThread(() -> {
                                totalPartnerCount.setText(String.valueOf(totalCount));
                                android.util.Log.d("RBHPanel", "Partner count updated: " + totalCount);
                            });
                        } else {
                            runOnUiThread(() -> totalPartnerCount.setText("0"));
                        }
                    } else {
                        runOnUiThread(() -> totalPartnerCount.setText("0"));
                    }
                } else {
                    runOnUiThread(() -> totalPartnerCount.setText("0"));
                }
            } catch (Exception e) {
                android.util.Log.e("RBHPanel", "Error fetching partner count: " + e.getMessage());
                runOnUiThread(() -> totalPartnerCount.setText("0"));
            }
        }).start();
    }
    
    private void fetchAgentCount() {
        if (userId == null || userId.isEmpty()) {
            totalAgentCount.setText("0");
            return;
        }
        
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/rbh_my_agent_data.php?user_id=" + userId;
                URL url = new URL(urlString);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    String responseString = response.toString();
                    android.util.Log.d("RBHPanel", "Agent API Response: " + responseString);
                    
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONObject stats = json.optJSONObject("statistics");
                        if (stats != null) {
                            int totalCount = stats.optInt("total_agents", 0);
                            runOnUiThread(() -> {
                                totalAgentCount.setText(String.valueOf(totalCount));
                                android.util.Log.d("RBHPanel", "Agent count updated: " + totalCount);
                            });
                        } else {
                            runOnUiThread(() -> totalAgentCount.setText("0"));
                        }
                    } else {
                        runOnUiThread(() -> totalAgentCount.setText("0"));
                    }
                } else {
                    runOnUiThread(() -> totalAgentCount.setText("0"));
                }
            } catch (Exception e) {
                android.util.Log.e("RBHPanel", "Error fetching agent count: " + e.getMessage());
                runOnUiThread(() -> totalAgentCount.setText("0"));
            }
        }).start();
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed() {
        showLogoutConfirmation();
    }
    
    private String getUserIdFromUsername(String username) {
        // This is a simplified implementation
        // In a real app, you would fetch this from SharedPreferences or a local database
        // For now, we'll return a placeholder that the API can handle
        return "1"; // Default user ID for testing
    }
} 
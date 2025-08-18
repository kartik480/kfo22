package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kfinone.app.ManagingDirectorPayoutPanelActivity;
import com.kfinone.app.ManagingDirectorEmpLinksActivity;
import com.kfinone.app.ManagingDirectorAgentPanelActivity;
import com.kfinone.app.ManagingDirectorDataLinksActivity;
import com.kfinone.app.ManagingDirectorWorkLinksActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpecialPanelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Top navigation elements
    private View menuButton;

    // Card elements
    private TextView totalEmpCount;
    private TextView totalSdsaCount;
    private TextView totalPartnerCount;
    private TextView totalPortfolioCount;
    private TextView totalAgentCount;
    
    // Welcome elements
    private TextView welcomeText;
    private TextView userInfoText;

    // Change indicators
    private TextView totalEmpChange;
    private TextView totalSdsaChange;
    private TextView totalPartnerChange;
    private TextView totalPortfolioChange;
    private TextView totalAgentChange;
    
    // Coming Soon Overlay
    private View comingSoonOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Aggressive fullscreen approach
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        
        // Set status bar to transparent
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        setContentView(R.layout.activity_special_panel);

        initializeViews();
        setupClickListeners();
        loadDashboardData();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        // Drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        
        // Top navigation
        menuButton = findViewById(R.id.menuButton);

        // Card counts
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        totalPortfolioCount = findViewById(R.id.totalPortfolioCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);

        // Change indicators
        totalEmpChange = findViewById(R.id.totalEmpChange);
        totalSdsaChange = findViewById(R.id.totalSdsaChange);
        totalPartnerChange = findViewById(R.id.totalPartnerChange);
        totalPortfolioChange = findViewById(R.id.totalPortfolioChange);
        totalAgentChange = findViewById(R.id.totalAgentChange);
        
        // Welcome elements
        welcomeText = findViewById(R.id.welcomeText);
        userInfoText = findViewById(R.id.userInfoText);
        
        // Coming Soon Overlay
        comingSoonOverlay = findViewById(R.id.comingSoonOverlay);
        
        // Set up overlay click listener to dismiss it
        comingSoonOverlay.setOnClickListener(v -> {
            comingSoonOverlay.setVisibility(View.GONE);
        });
        
        // Set up navigation view
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupClickListeners() {
        // Top navigation
        menuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Card click listeners
        findViewById(R.id.totalEmpCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.totalSdsaCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, MySdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.totalPartnerCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.totalPortfolioCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.totalAgentCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Quick Access Boxes - Row 1

        // New Managing Director Quick Access Boxes
        findViewById(R.id.empLinksBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.dataLinksBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorDataLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.workLinkBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.employeeBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.sdsaBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, MySdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.partnerBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.agentBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorAgentPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Quick Access Boxes - Row 2
        findViewById(R.id.payoutBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorPayoutPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.dsaCodesBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });

        findViewById(R.id.bankersBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, BankersPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.portfolioBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        findViewById(R.id.addInsuranceBox).setOnClickListener(v -> {
            // Show coming soon overlay
            comingSoonOverlay.setVisibility(View.VISIBLE);
            
            // Hide overlay after 2 seconds
            comingSoonOverlay.postDelayed(() -> {
                comingSoonOverlay.setVisibility(View.GONE);
            }, 2000);
        });

        findViewById(R.id.documentCheckListBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, DocCheckListActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });

        findViewById(R.id.policyBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, PolicyActivity.class);
            passUserDataToIntent(intent);
            intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
            startActivity(intent);
        });
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
        }
    }

    private void loadDashboardData() {
        // Initialize all cards with "--" instead of "Loading..."
        totalEmpCount.setText("--");
        totalSdsaCount.setText("--");
        totalPartnerCount.setText("--");
        totalPortfolioCount.setText("--");
        totalAgentCount.setText("--");
        
        // Fetch active employee count for Total Employee card
        fetchActiveEmployeeCount();
        
        DataService.fetchDashboardData(new DataService.DataCallback() {
            @Override
            public void onSuccess(int employeeCount, int sdsaCount, int partnerCount, int portfolioCount, int agentCount) {
                runOnUiThread(() -> {
                    // Don't update totalEmpCount here as it will be updated by fetchActiveEmployeeCount
                    totalSdsaCount.setText(String.valueOf(sdsaCount));
                    totalPartnerCount.setText(String.valueOf(partnerCount));
                    totalPortfolioCount.setText(String.valueOf(portfolioCount));
                    totalAgentCount.setText(String.valueOf(agentCount));
                    Log.d("SpecialPanelActivity", "Updated counts - SDSA: " + sdsaCount + ", Partners: " + partnerCount + ", Portfolio: " + portfolioCount + ", Agents: " + agentCount);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Don't update totalEmpCount here as it will be updated by fetchActiveEmployeeCount
                    totalSdsaCount.setText("--");
                    totalPartnerCount.setText("--");
                    totalPortfolioCount.setText("--");
                    totalAgentCount.setText("--");
                    Toast.makeText(SpecialPanelActivity.this, "Error loading data: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void fetchActiveEmployeeCount() {
        new Thread(() -> {
            try {
                Log.d("SpecialPanelActivity", "Fetching active employee count...");
                
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_managing_director_active_emp_list.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d("SpecialPanelActivity", "Active employee API response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d("SpecialPanelActivity", "Active employee API response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONObject statistics = json.optJSONObject("statistics");
                        final int activeEmpCount;
                        
                        if (statistics != null) {
                            activeEmpCount = statistics.optInt("active_members", 0);
                        } else {
                            // Fallback: count the data array
                            JSONArray data = json.optJSONArray("data");
                            if (data != null) {
                                activeEmpCount = data.length();
                            } else {
                                activeEmpCount = 0;
                            }
                        }
                        
                        Log.d("SpecialPanelActivity", "Found " + activeEmpCount + " active employees");
                        
                        runOnUiThread(() -> {
                            totalEmpCount.setText(String.valueOf(activeEmpCount));
                            Log.d("SpecialPanelActivity", "Updated Total Employee card with " + activeEmpCount + " active employees");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e("SpecialPanelActivity", "Server returned error for active employees: " + errorMsg);
                        runOnUiThread(() -> {
                            totalEmpCount.setText("0");
                        });
                    }
                } else {
                    Log.e("SpecialPanelActivity", "Server error with response code for active employees: " + responseCode);
                    runOnUiThread(() -> {
                        totalEmpCount.setText("0");
                    });
                }
            } catch (Exception e) {
                Log.e("SpecialPanelActivity", "Exception in fetchActiveEmployeeCount: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    totalEmpCount.setText("0");
                });
            }
        }).start();
    }

    private void updateWelcomeMessage() {
        // Get user info from intent
        Intent intent = getIntent();
        String firstName = null;
        String lastName = null;
        String username = null;
        
        if (intent != null) {
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
            username = intent.getStringExtra("USERNAME");
        }
        
        // If not in intent, try to get from SharedPreferences
        if (firstName == null || firstName.isEmpty()) {
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                firstName = prefs.getString("FIRST_NAME", "");
                lastName = prefs.getString("LAST_NAME", "");
                username = prefs.getString("USERNAME", "");
            } catch (Exception e) {
                Log.e("SpecialPanel", "Error getting user data from SharedPreferences: " + e.getMessage());
            }
        }
        
        // Debug logging
        Log.d("SpecialPanel", "FIRST_NAME: " + firstName);
        Log.d("SpecialPanel", "LAST_NAME: " + lastName);
        Log.d("SpecialPanel", "USERNAME: " + username);
        
        String welcomeMessage;
        if (firstName != null && !firstName.isEmpty() && firstName.length() > 1) {
            welcomeMessage = "Welcome, " + firstName + "!";
            Log.d("SpecialPanel", "Using firstName: " + welcomeMessage);
        } else if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeMessage = "Welcome, " + firstName + " " + lastName + "!";
            Log.d("SpecialPanel", "Using firstName + lastName: " + welcomeMessage);
        } else if (username != null && !username.isEmpty()) {
            String[] nameParts = username.split("\\s+");
            if (nameParts.length > 0 && nameParts[0].length() > 1) {
                welcomeMessage = "Welcome, " + nameParts[0] + "!";
                Log.d("SpecialPanel", "Using first part of username: " + welcomeMessage);
            } else {
                welcomeMessage = "Welcome, " + username + "!";
                Log.d("SpecialPanel", "Using full username: " + welcomeMessage);
            }
        } else {
            welcomeMessage = "Welcome, Admin!";
            Log.d("SpecialPanel", "Using default Admin message: " + welcomeMessage);
        }
        
        welcomeText.setText(welcomeMessage);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_dashboard) {
            // Already on dashboard
            Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_employee_management) {
            Intent intent = new Intent(this, EmployeePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_sdsa_management) {
            Intent intent = new Intent(this, SdsaPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_partner_management) {
            Intent intent = new Intent(this, ManagingDirectorPartnerMasterActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_agent_management) {
            Intent intent = new Intent(this, AgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_portfolio_management) {
            Intent intent = new Intent(this, PortfolioPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_insurance_management) {
            Intent intent = new Intent(this, InsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_payout_management) {
            Intent intent = new Intent(this, PayoutActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_document_management) {
            Intent intent = new Intent(this, DocumentUploadActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        } else if (id == R.id.nav_reports) {
            Toast.makeText(this, "Reports & Analytics - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Re-apply fullscreen when window gains focus
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CBOReportingUsersActivity extends AppCompatActivity {
    private static final String TAG = "CBOReportingUsers";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText, statsText, titleText;
    private BottomNavigationView bottomNav;
    
    private String userName;
    private String userId;
    private List<ReportingUser> reportingUsers;
    private ReportingUsersAdapter adapter;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_reporting_users);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "CBO";
        }
        
        if (userId == null || userId.isEmpty()) {
            // Try to get from SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            userId = prefs.getString("USER_ID", "");
            userName = prefs.getString("USERNAME", "");
            Log.d(TAG, "Retrieved from SharedPreferences - userId: " + userId);
            Log.d(TAG, "Retrieved from SharedPreferences - userName: " + userName);
            
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        
        initializeViews();
        setupBottomNavigation();
        fetchReportingUsers();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        statsText = findViewById(R.id.statsText);
        titleText = findViewById(R.id.titleText);
        bottomNav = findViewById(R.id.bottomNavigationView);
        
        // Set title
        titleText.setText("My Team Members");
        
        // Setup RecyclerView
        reportingUsers = new ArrayList<>();
        adapter = new ReportingUsersAdapter(reportingUsers, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_team) {
                // Already on team page
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                Intent intent = new Intent(this, CBOPortfolioActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_reports) {
                Intent intent = new Intent(this, CBOReportsActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            }
            return false;
        });
        
        // Set current selection
        bottomNav.setSelectedItemId(R.id.nav_team);
    }

    private void fetchReportingUsers() {
        showLoading(true);
        executor.execute(() -> {
            try {
                String url = "https://emp.kfinone.com/mobile/api/get_designated_user_reporting_team.php?user_id=" + userId + "&designation=Chief Business Officer";
                String response = makeGetRequest(url);
                runOnUiThread(() -> {
                    showLoading(false);
                    parseResponse(response);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching reporting users", e);
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Failed to fetch team members: " + e.getMessage());
                });
            }
        });
    }

    private String makeGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                throw new IOException("HTTP error code: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            
            if ("success".equals(status)) {
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                JSONObject stats = jsonResponse.getJSONObject("statistics");
                JSONObject loggedInUser = jsonResponse.getJSONObject("logged_in_user");
                
                // Clear existing data
                reportingUsers.clear();
                
                // Parse reporting users
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject userObj = dataArray.getJSONObject(i);
                    ReportingUser user = new ReportingUser();
                    
                    user.setId(userObj.getString("id"));
                    user.setUsername(userObj.getString("username"));
                    user.setFirstName(userObj.getString("firstName"));
                    user.setLastName(userObj.getString("lastName"));
                    user.setFullName(userObj.getString("fullName"));
                    user.setDisplayName(userObj.getString("displayName"));
                    user.setEmployeeNo(userObj.optString("employee_no", ""));
                    user.setMobile(userObj.optString("mobile", ""));
                    user.setEmail(userObj.optString("email_id", ""));
                    user.setDesignationName(userObj.getString("designation_name"));
                    user.setDepartmentName(userObj.optString("department_name", ""));
                    user.setStatus(userObj.optString("status", ""));
                    
                    // Parse manage icons
                    List<String> manageIcons = new ArrayList<>();
                    if (userObj.has("manage_icons")) {
                        try {
                            JSONArray iconsArray = userObj.getJSONArray("manage_icons");
                            for (int j = 0; j < iconsArray.length(); j++) {
                                manageIcons.add(iconsArray.getString(j));
                            }
                        } catch (JSONException e) {
                            // If manage_icons is a string, split by comma
                            String iconsString = userObj.optString("manage_icons", "");
                            if (!iconsString.isEmpty()) {
                                String[] icons = iconsString.split(",");
                                for (String icon : icons) {
                                    manageIcons.add(icon.trim());
                                }
                            }
                        }
                    }
                    user.setManageIcons(manageIcons);
                    
                    reportingUsers.add(user);
                }
                
                // Update UI
                updateStats(stats, loggedInUser);
                adapter.notifyDataSetChanged();
                
                if (reportingUsers.isEmpty()) {
                    showEmptyState("No team members found reporting to you.");
                } else {
                    hideEmptyState();
                }
                
            } else {
                String message = jsonResponse.optString("message", "Unknown error occurred");
                showError(message);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response", e);
            showError("Error parsing server response");
        }
    }

    private void updateStats(JSONObject stats, JSONObject loggedInUser) {
        try {
            int totalUsers = stats.getInt("total_reporting_users");
            int uniqueDesignations = stats.getInt("unique_designations");
            int uniqueDepartments = stats.getInt("unique_departments");
            
            String managerName = loggedInUser.getString("fullName");
            String designation = loggedInUser.getString("designation_name");
            
            String statsMessage = String.format(
                "Manager: %s (%s)\nTotal Team Members: %d\nDesignations: %d\nDepartments: %d",
                managerName, designation, totalUsers, uniqueDesignations, uniqueDepartments
            );
            
            statsText.setText(statsMessage);
            statsText.setVisibility(View.VISIBLE);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing stats", e);
            statsText.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.GONE);
            statsText.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        emptyStateText.setText(message);
        emptyStateText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        statsText.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyStateText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        statsText.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showEmptyState("Failed to load team members. Please try again.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    // Data class for reporting users
    public static class ReportingUser {
        private String id, username, firstName, lastName, fullName, displayName;
        private String employeeNo, mobile, email, designationName, departmentName, status;
        private List<String> manageIcons;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getEmployeeNo() { return employeeNo; }
        public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
        
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDesignationName() { return designationName; }
        public void setDesignationName(String designationName) { this.designationName = designationName; }
        
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<String> getManageIcons() { return manageIcons; }
        public void setManageIcons(List<String> manageIcons) { this.manageIcons = manageIcons; }
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import com.kfinone.app.ReportingUser;
import com.kfinone.app.ReportingUserAdapter;

public class CBOTeamSdsaActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // RBH User Dropdown elements
    private Spinner rbhUserSpinner;
    private LinearLayout selectedUserInfo;
    private TextView selectedUserName;
    private TextView selectedUserDetails;
    
    // Action buttons
    private Button showDataButton;
    private Button resetButton;

    // Reporting Users List
    private LinearLayout reportingUsersSection;
    private ListView reportingUsersListView;
    private TextView totalUsersText;
    private TextView activeUsersText;
    private TextView inactiveUsersText;

    // User data
    private String userName;
    private String userId;

    // RBH Users data
    private List<RbhUser> rbhUserList;
    private RbhUser selectedRbhUser;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_team_sdsa);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadTeamSdsaData();
        fetchRbhUsers();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // RBH User Dropdown elements
        rbhUserSpinner = findViewById(R.id.rbhUserSpinner);
        selectedUserInfo = findViewById(R.id.selectedUserInfo);
        selectedUserName = findViewById(R.id.selectedUserName);
        selectedUserDetails = findViewById(R.id.selectedUserDetails);
        
        // Action buttons
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);

        // Reporting Users List
        reportingUsersSection = findViewById(R.id.reportingUsersSection);
        reportingUsersListView = findViewById(R.id.reportingUsersListView);
        totalUsersText = findViewById(R.id.totalUsersText);
        activeUsersText = findViewById(R.id.activeUsersText);
        inactiveUsersText = findViewById(R.id.inactiveUsersText);

        // Note: Individual user clicks are now handled by View Details buttons in the adapter

        executorService = Executors.newSingleThreadExecutor();
        rbhUserList = new ArrayList<>();
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewSdsa());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // RBH User Spinner
        rbhUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= rbhUserList.size()) {
                    selectedRbhUser = rbhUserList.get(position - 1);
                    showSelectedUserInfo();
                    showDataButton.setEnabled(true);
                } else {
                    selectedRbhUser = null;
                    hideSelectedUserInfo();
                    showDataButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRbhUser = null;
                hideSelectedUserInfo();
                showDataButton.setEnabled(false);
            }
        });

        // Show Data Button
        showDataButton.setOnClickListener(v -> {
            if (selectedRbhUser != null) {
                showUserData(selectedRbhUser);
            } else {
                Toast.makeText(this, "Please select a Regional Business Head first", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Reset Button
        resetButton.setOnClickListener(v -> {
            resetSelection();
        });
    }

    private void showSelectedUserInfo() {
        if (selectedRbhUser != null) {
            selectedUserName.setText("Selected User: " + selectedRbhUser.getFullName());
            selectedUserDetails.setText("Designation: " + selectedRbhUser.getDesignationName());
            selectedUserInfo.setVisibility(View.VISIBLE);
        }
    }

    private void hideSelectedUserInfo() {
        selectedUserInfo.setVisibility(View.GONE);
    }



    private void fetchRbhUsers() {
        android.util.Log.d("CBOTeamSdsa", "Starting to fetch RBH users...");
        executorService.execute(() -> {
            try {
                // Use main database API
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_users_for_dropdown.php";
                android.util.Log.d("CBOTeamSdsa", "Making API call to: " + apiUrl);
                String response = makeGetRequest(apiUrl);
                
                runOnUiThread(() -> {
                    android.util.Log.d("CBOTeamSdsa", "Response received: " + (response != null ? response.length() : 0) + " characters");
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            android.util.Log.d("CBOTeamSdsa", "JSON parsed successfully");
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                rbhUserList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    RbhUser user = new RbhUser(
                                        userObj.optString("id"),
                                        userObj.optString("username"),
                                        userObj.optString("firstName"),
                                        userObj.optString("lastName"),
                                        userObj.optString("designation_id"),
                                        userObj.optString("designation_name"),
                                        userObj.optString("fullName"),
                                        userObj.optString("displayName")
                                    );
                                    rbhUserList.add(user);
                                }
                                
                                android.util.Log.d("CBOTeamSdsa", "Setting up spinner with " + rbhUserList.size() + " users");
                                setupRbhUserSpinner();
                                Toast.makeText(this, "Loaded " + rbhUserList.size() + " RBH users", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                android.util.Log.e("CBOTeamSdsa", "API Error: " + errorMessage);
                                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            android.util.Log.e("CBOTeamSdsa", "JSON Parse Error: " + e.getMessage());
                            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        android.util.Log.e("CBOTeamSdsa", "No response from server");
                        Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (Exception e) {
                android.util.Log.e("CBOTeamSdsa", "Exception fetching RBH users: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching RBH users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupRbhUserSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Select Regional Business Head"); // Default option
        
        for (RbhUser user : rbhUserList) {
            spinnerItems.add(user.getDisplayName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rbhUserSpinner.setAdapter(adapter);
    }

    private String makeGetRequest(String urlString) throws IOException {
        android.util.Log.d("CBOTeamSdsa", "Making GET request to: " + urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try {
            int responseCode = connection.getResponseCode();
            android.util.Log.d("CBOTeamSdsa", "Response code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                android.util.Log.d("CBOTeamSdsa", "Success response length: " + response.length());
                return response.toString();
            } else {
                android.util.Log.e("CBOTeamSdsa", "HTTP Error: " + responseCode);
                
                // Try to read error stream
                try {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorReader.close();
                    android.util.Log.e("CBOTeamSdsa", "Error response: " + errorResponse.toString());
                } catch (Exception e) {
                    android.util.Log.e("CBOTeamSdsa", "Could not read error stream: " + e.getMessage());
                }
                
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    private void showUserData(RbhUser rbhUser) {
        // Show user data in a dialog or navigate to user details
        Toast.makeText(this, "Showing data for: " + rbhUser.getFullName(), Toast.LENGTH_SHORT).show();
        
        // Fetch and display reporting users
        fetchReportingUsers(rbhUser.getId());
    }
    
    private void resetSelection() {
        // Reset the spinner to default selection
        rbhUserSpinner.setSelection(0);
        
        // Clear selected user
        selectedRbhUser = null;
        
        // Hide user info
        hideSelectedUserInfo();
        
        // Disable action buttons
        showDataButton.setEnabled(false);
        
        // Load all users again
        fetchAllSdsaUsers();
        
        Toast.makeText(this, "Selection reset successfully, showing all users", Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        // Simply finish this activity to return to the previous one
        // This preserves the user data in the CBOSdsaActivity
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing team SDSA data...", Toast.LENGTH_SHORT).show();
        loadTeamSdsaData(); // This will load all SDSA users
        fetchRbhUsers();
    }

    private void addNewSdsa() {
        Toast.makeText(this, "Add New SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add SDSA activity
    }

    private void loadTeamSdsaData() {
        // First test the connection, then load all SDSA users
        testConnection();
    }

    private void testConnection() {
        executorService.execute(() -> {
            try {
                String testUrl = "https://emp.kfinone.com/mobile/api/test_connection.php";
                android.util.Log.d("CBOTeamSdsa", "Testing connection to: " + testUrl);
                
                String response = makeGetRequest(testUrl);
                android.util.Log.d("CBOTeamSdsa", "Test response: " + (response != null ? response.length() : 0) + " characters");
                
                if (response != null && !response.isEmpty()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        
                        if (success) {
                            android.util.Log.d("CBOTeamSdsa", "Connection test successful");
                            // Now fetch all SDSA users
                            fetchAllSdsaUsers();
                        } else {
                            String message = jsonResponse.getString("message");
                            android.util.Log.e("CBOTeamSdsa", "Connection test failed: " + message);
                            runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                                "Connection test failed: " + message, Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        android.util.Log.e("CBOTeamSdsa", "JSON Parse Error in test: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                            "Error parsing test response: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    android.util.Log.e("CBOTeamSdsa", "No test response from server");
                    runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                        "No test response from server", Toast.LENGTH_SHORT).show());
                }
                
            } catch (Exception e) {
                android.util.Log.e("CBOTeamSdsa", "Exception in connection test: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                    "Connection test error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchAllSdsaUsers() {
        executorService.execute(() -> {
            try {
                // Use the simple API first to test basic functionality
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_all_sdsa_users_simple.php";
                android.util.Log.d("CBOTeamSdsa", "Fetching all SDSA users from: " + apiUrl);
                
                String response = makeGetRequest(apiUrl);
                android.util.Log.d("CBOTeamSdsa", "Response received: " + (response != null ? response.length() : 0) + " characters");
                
                if (response != null && !response.isEmpty()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        android.util.Log.d("CBOTeamSdsa", "JSON parsed successfully");
                        
                        boolean success = jsonResponse.getBoolean("success");
                        android.util.Log.d("CBOTeamSdsa", "Success: " + success);
                        
                        if (success) {
                            JSONArray usersArray = jsonResponse.getJSONArray("users");
                            android.util.Log.d("CBOTeamSdsa", "Users array length: " + usersArray.length());
                            
                            List<ReportingUser> allUsers = new ArrayList<>();
                            
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObj = usersArray.getJSONObject(i);
                                ReportingUser user = new ReportingUser(
                                    userObj.getString("id"),
                                    userObj.getString("username"),
                                    userObj.getString("first_name"),
                                    userObj.getString("last_name"),
                                    userObj.getString("email_id"),
                                    userObj.getString("Phone_number"),
                                    userObj.getString("designation"),
                                    userObj.getString("department"),
                                    userObj.getString("status")
                                );
                                
                                // Set additional fields from joined tables
                                user.setEmployeeNo(userObj.optString("employee_no"));
                                user.setRank(userObj.optString("rank"));
                                user.setCompanyName(userObj.optString("company_name"));
                                user.setAlternativeMobileNumber(userObj.optString("alternative_mobile_number"));
                                user.setOfficeAddress(userObj.optString("office_address"));
                                user.setResidentialAddress(userObj.optString("residential_address"));
                                user.setAadhaarNumber(userObj.optString("aadhaar_number"));
                                user.setPanNumber(userObj.optString("pan_number"));
                                user.setAccountNumber(userObj.optString("account_number"));
                                user.setIfscCode(userObj.optString("ifsc_code"));
                                user.setUserId(userObj.optString("user_id"));
                                user.setCreatedBy(userObj.optString("createdBy"));
                                user.setCreatedAt(userObj.optString("created_at"));
                                user.setUpdatedAt(userObj.optString("updated_at"));
                                user.setReportingTo(userObj.optString("reportingTo"));
                                
                                // Set fields from joined tables (actual names instead of IDs)
                                // For simple API, these will be the direct values from tbl_sdsa_users
                                user.setBranchState(userObj.optString("branchstate"));
                                user.setBranchLocation(userObj.optString("branchloaction"));
                                user.setBankName(userObj.optString("bank_name"));
                                user.setAccountType(userObj.optString("account_type"));
                                
                                allUsers.add(user);
                            }
                            
                            android.util.Log.d("CBOTeamSdsa", "Created " + allUsers.size() + " ReportingUser objects");
                            
                            // Update UI on main thread
                            runOnUiThread(() -> displayAllSdsaUsers(allUsers));
                            
                        } else {
                            String message = jsonResponse.getString("message");
                            android.util.Log.e("CBOTeamSdsa", "API Error: " + message);
                            runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                                "Error: " + message, Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        android.util.Log.e("CBOTeamSdsa", "JSON Parse Error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                            "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    android.util.Log.e("CBOTeamSdsa", "No response from server");
                    runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                        "No response from server", Toast.LENGTH_SHORT).show());
                }
                
            } catch (Exception e) {
                android.util.Log.e("CBOTeamSdsa", "Exception fetching all SDSA users: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                    "Error fetching all SDSA users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void displayAllSdsaUsers(List<ReportingUser> users) {
        if (users.isEmpty()) {
            Toast.makeText(this, "No SDSA users found in the system", Toast.LENGTH_SHORT).show();
            reportingUsersSection.setVisibility(View.GONE);
            return;
        }
        
        // Update summary information
        updateReportingUsersSummary(users);
        
        // Create adapter for the ListView
        ReportingUserAdapter adapter = new ReportingUserAdapter(this, users);
        reportingUsersListView.setAdapter(adapter);
        
        // Show the reporting users section
        reportingUsersSection.setVisibility(View.VISIBLE);
        
        Toast.makeText(this, "Loaded " + users.size() + " SDSA users", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void fetchReportingUsers(String rbhUserId) {
        executorService.execute(() -> {
            try {
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_reporting_users.php?rbh_user_id=" + rbhUserId;
                String response = makeGetRequest(apiUrl);
                
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    
                    if (success) {
                        JSONArray usersArray = jsonResponse.getJSONArray("users");
                        List<ReportingUser> reportingUsers = new ArrayList<>();
                        
                                                 for (int i = 0; i < usersArray.length(); i++) {
                             JSONObject userObj = usersArray.getJSONObject(i);
                             ReportingUser user = new ReportingUser(
                                 userObj.getString("id"),
                                 userObj.getString("username"),
                                 userObj.getString("first_name"),
                                 userObj.getString("last_name"),
                                 userObj.getString("email_id"),
                                 userObj.getString("Phone_number"),
                                 userObj.getString("designation"),
                                 userObj.getString("department"),
                                 userObj.getString("status")
                             );
                             
                             // Set additional fields from joined tables
                             user.setEmployeeNo(userObj.optString("employee_no"));
                             user.setRank(userObj.optString("rank"));
                             user.setCompanyName(userObj.optString("company_name"));
                             user.setAlternativeMobileNumber(userObj.optString("alternative_mobile_number"));
                             user.setOfficeAddress(userObj.optString("office_address"));
                             user.setResidentialAddress(userObj.optString("residential_address"));
                             user.setAadhaarNumber(userObj.optString("aadhaar_number"));
                             user.setPanNumber(userObj.optString("pan_number"));
                             user.setAccountNumber(userObj.optString("account_number"));
                             user.setIfscCode(userObj.optString("ifsc_code"));
                             user.setUserId(userObj.optString("user_id"));
                             user.setCreatedBy(userObj.optString("createdBy"));
                             user.setCreatedAt(userObj.optString("created_at"));
                             user.setUpdatedAt(userObj.optString("updated_at"));
                             user.setReportingTo(userObj.optString("reportingTo"));
                             
                             // Set fields from joined tables (actual names instead of IDs)
                             // For simple API, these will be the direct values from tbl_sdsa_users
                             user.setBranchState(userObj.optString("branchstate"));
                             user.setBranchLocation(userObj.optString("branchloaction"));
                             user.setBankName(userObj.optString("bank_name"));
                             user.setAccountType(userObj.optString("account_type"));
                             
                             reportingUsers.add(user);
                         }
                        
                        // Update UI on main thread
                        runOnUiThread(() -> displayReportingUsers(reportingUsers));
                        
                    } else {
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                            "Error: " + message, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                        "No response from server", Toast.LENGTH_SHORT).show());
                }
                
            } catch (Exception e) {
                android.util.Log.e("CBOTeamSdsa", "Error fetching reporting users: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(CBOTeamSdsaActivity.this, 
                    "Error fetching reporting users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void displayReportingUsers(List<ReportingUser> users) {
        if (users.isEmpty()) {
            Toast.makeText(this, "No users found reporting to this RBH", Toast.LENGTH_SHORT).show();
            reportingUsersSection.setVisibility(View.GONE);
            return;
        }
        
        // Update section title to show filtered users
        TextView sectionTitle = findViewById(R.id.reportingUsersTitle);
        if (sectionTitle != null) {
            sectionTitle.setText("Users Reporting to Selected RBH:");
        }
        
        // Update summary information
        updateReportingUsersSummary(users);
        
        // Create adapter for the ListView
        ReportingUserAdapter adapter = new ReportingUserAdapter(this, users);
        reportingUsersListView.setAdapter(adapter);
        
        // Show the reporting users section
        reportingUsersSection.setVisibility(View.VISIBLE);
    }
    
    private void updateReportingUsersSummary(List<ReportingUser> users) {
        int totalUsers = users.size();
        int activeUsers = 0;
        int inactiveUsers = 0;
        
        for (ReportingUser user : users) {
            if (user.getStatus() != null && "active".equalsIgnoreCase(user.getStatus())) {
                activeUsers++;
            } else {
                inactiveUsers++;
            }
        }
        
        totalUsersText.setText("Total Users: " + totalUsers);
        activeUsersText.setText("Active: " + activeUsers);
        inactiveUsersText.setText("Inactive: " + inactiveUsers);
    }
    
    private void showReportingUserDetails(ReportingUser user) {
        // Show comprehensive user information about the selected reporting user
        StringBuilder details = new StringBuilder();
        details.append("=== COMPLETE USER DETAILS ===\n\n");
        
        // Basic Information
        details.append("📋 BASIC INFORMATION:\n");
        details.append("• Full Name: ").append(user.getFullName()).append("\n");
        details.append("• Username: ").append(user.getUsername()).append("\n");
        details.append("• Employee No: ").append(user.getEmployeeNo()).append("\n");
        details.append("• Status: ").append(user.getStatus()).append("\n");
        details.append("• Rank: ").append(user.getRank()).append("\n\n");
        
        // Contact Information
        details.append("📞 CONTACT INFORMATION:\n");
        details.append("• Email: ").append(user.getEmailId()).append("\n");
        details.append("• Phone: ").append(user.getPhoneNumber()).append("\n");
        details.append("• Alternative Mobile: ").append(user.getAlternativeMobileNumber()).append("\n\n");
        
        // Professional Information
        details.append("💼 PROFESSIONAL DETAILS:\n");
        details.append("• Department: ").append(user.getDepartment()).append("\n");
        details.append("• Designation: ").append(user.getDesignation()).append("\n");
        details.append("• Company: ").append(user.getCompanyName()).append("\n");
        details.append("• Reporting To: ").append(user.getReportingTo()).append("\n\n");
        
        // Location Information
        details.append("📍 LOCATION DETAILS:\n");
        details.append("• Branch State: ").append(user.getBranchState()).append("\n");
        details.append("• Branch Location: ").append(user.getBranchLocation()).append("\n");
        details.append("• Office Address: ").append(user.getOfficeAddress()).append("\n");
        details.append("• Residential Address: ").append(user.getResidentialAddress()).append("\n\n");
        
        // Banking Information
        details.append("🏦 BANKING DETAILS:\n");
        details.append("• Bank Name: ").append(user.getBankName()).append("\n");
        details.append("• Account Type: ").append(user.getAccountType()).append("\n");
        details.append("• Account Number: ").append(user.getAccountNumber()).append("\n");
        details.append("• IFSC Code: ").append(user.getIfscCode()).append("\n\n");
        
        // Identity Information
        details.append("🆔 IDENTITY DETAILS:\n");
        details.append("• Aadhaar Number: ").append(user.getAadhaarNumber()).append("\n");
        details.append("• PAN Number: ").append(user.getPanNumber()).append("\n");
        details.append("• User ID: ").append(user.getUserId()).append("\n\n");
        
        // System Information
        details.append("⚙️ SYSTEM DETAILS:\n");
        details.append("• Created By: ").append(user.getCreatedBy()).append("\n");
        details.append("• Created At: ").append(user.getCreatedAt()).append("\n");
        details.append("• Updated At: ").append(user.getUpdatedAt()).append("\n");
        
        // Show in a Toast (you might want to show a dialog for better readability)
        Toast.makeText(this, details.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 
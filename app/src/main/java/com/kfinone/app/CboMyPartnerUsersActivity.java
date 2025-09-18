package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;

public class CboMyPartnerUsersActivity extends AppCompatActivity {
    
    private static final String TAG = "CboMyPartnerUsers";
    private TextView totalUsersText, activeUsersText, inactiveUsersText, welcomeText;
    private ListView partnerUsersListView;
    private View loadingView, noDataView;
    private FloatingActionButton refreshFab;
    
    private String userId;
    private String username;
    private String userFullName;
    private List<PartnerUser> partnerUsersList;
    private PartnerUsersAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_my_partner_users);
        
        // Get user data from intent first, then fall back to SharedPreferences
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        userFullName = intent.getStringExtra("USER_FULL_NAME");
        
        // If not in intent, try SharedPreferences
        if (userId == null || userId.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            userId = prefs.getString("USER_ID", "");
            username = prefs.getString("USERNAME", "");
            String firstName = prefs.getString("FIRST_NAME", "");
            String lastName = prefs.getString("LAST_NAME", "");
            userFullName = (firstName + " " + lastName).trim();
            Log.d("CboMyPartnerUsers", "Retrieved from SharedPreferences - userId: " + userId);
            Log.d("CboMyPartnerUsers", "Retrieved from SharedPreferences - username: " + username);
            Log.d("CboMyPartnerUsers", "Retrieved from SharedPreferences - userFullName: " + userFullName);
        }
        
        if (userId == null || userId.isEmpty() || userId.equals("0")) {
            Toast.makeText(this, "User ID is required. Please login again.", Toast.LENGTH_LONG).show();
            Log.e("CboMyPartnerUsers", "User ID is missing or invalid: " + userId);
            Log.e("CboMyPartnerUsers", "Username: " + username);
            Log.e("CboMyPartnerUsers", "User Full Name: " + userFullName);
            finish();
            return;
        }
        
        initializeViews();
        setupToolbar();
        setupClickListeners();
        fetchPartnerUsers();
    }
    
    private void initializeViews() {
        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Partner Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize text views
        welcomeText = findViewById(R.id.welcomeText);
        totalUsersText = findViewById(R.id.totalUsersText);
        activeUsersText = findViewById(R.id.activeUsersText);
        inactiveUsersText = findViewById(R.id.inactiveUsersText);
        
        // Initialize list view
        partnerUsersListView = findViewById(R.id.partnerUsersListView);
        
        // Initialize loading and no data views
        loadingView = findViewById(R.id.loadingView);
        noDataView = findViewById(R.id.noDataView);
        
        // Initialize refresh button
        refreshFab = findViewById(R.id.refreshFab);
        
        // Initialize adapter
        partnerUsersList = new ArrayList<>();
        adapter = new PartnerUsersAdapter(this, partnerUsersList);
        partnerUsersListView.setAdapter(adapter);
        
        // Set up View Details button click listener
        adapter.setOnViewDetailsClickListener(new PartnerUsersAdapter.OnViewDetailsClickListener() {
            @Override
            public void onViewDetailsClick(PartnerUser partnerUser) {
                showPartnerUserDetails(partnerUser);
            }
        });
        
        // Set list item click listener (optional - for clicking anywhere on the item)
        partnerUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PartnerUser partnerUser = partnerUsersList.get(position);
                showPartnerUserDetails(partnerUser);
            }
        });
    }
    
    private void setupToolbar() {
        if (userFullName != null && !userFullName.isEmpty()) {
            welcomeText.setText("Welcome, " + userFullName);
        } else {
            welcomeText.setText("Welcome, Chief Business Officer");
        }
    }
    
    private void setupClickListeners() {
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPartnerUsers();
            }
        });
    }
    
    private void fetchPartnerUsers() {
        showLoading(true);
        
        // Debug logging
        Log.d("CBOMyPartnerUsers", "Starting fetchPartnerUsers");
        Log.d("CBOMyPartnerUsers", "userId: " + userId);
        Log.d("CBOMyPartnerUsers", "username: " + username);
        
        // Build URL with both user_id and username for better compatibility
        String url = "https://emp.kfinone.com/mobile/api/cbo_my_partner_users.php?user_id=" + userId;
        if (username != null && !username.isEmpty()) {
            url += "&username=" + username;
        }
        
        Log.d("CBOMyPartnerUsers", "API URL: " + url);
        Log.d("CBOMyPartnerUsers", "Fetching partner users for user_id: " + userId + ", username: " + username);
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    showLoading(false);
                    Log.d("CBOMyPartnerUsers", "Response: " + response.toString());
                    
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            List<PartnerUser> partnerUsers = parsePartnerUsersResponse(dataArray);
                            
                            // Update statistics
                            JSONObject stats = response.getJSONObject("statistics");
                            updateStatistics(stats);
                            
                            // Update user list
                            updateUserList(partnerUsers);
                            
                            // Log success information
                            JSONObject loggedInUser = response.getJSONObject("logged_in_user");
                            String cboUsername = response.optString("cbo_username", "");
                            Log.d("CBOMyPartnerUsers", "CBO Username used for filtering: " + cboUsername);
                            Log.d("CBOMyPartnerUsers", "Total partner users found: " + partnerUsers.size());
                            
                        } else {
                            String errorMessage = response.getString("message");
                            showError("Error: " + errorMessage);
                        }
                    } catch (JSONException e) {
                        Log.e("CBOMyPartnerUsers", "JSON parsing error", e);
                        showError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    showLoading(false);
                    Log.e("CBOMyPartnerUsers", "Network error", error);
                    showError("Network error: " + error.getMessage());
                }
        );
        
        // Add request to queue
        Volley.newRequestQueue(this).add(request);
    }
    
    private List<PartnerUser> parsePartnerUsersResponse(JSONArray dataArray) throws JSONException {
        List<PartnerUser> partnerUsers = new ArrayList<>();
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject userObj = dataArray.getJSONObject(i);
            
            PartnerUser partnerUser = new PartnerUser(
                userObj.optInt("id", 0),
                userObj.optString("username", ""),
                userObj.optString("full_name", ""),
                userObj.optString("phone_number", ""),
                userObj.optString("email_id", ""),
                userObj.optString("company_name", ""),
                userObj.optString("status", ""),
                userObj.optString("created_at", ""),
                userObj.optString("createdBy", "")
            );
            
            // Set additional fields
            partnerUser.setAliasName(userObj.optString("alias_name", ""));
            partnerUser.setFirstName(userObj.optString("first_name", ""));
            partnerUser.setLastName(userObj.optString("last_name", ""));
            partnerUser.setPassword(userObj.optString("password", ""));
            partnerUser.setAlternativeMobileNumber(userObj.optString("alternative_mobile_number", ""));
            partnerUser.setBranchStateNameId(userObj.optString("branch_state_name_id", ""));
            partnerUser.setBranchLocationId(userObj.optString("branch_location_id", ""));
            partnerUser.setBankId(userObj.optString("bank_id", ""));
            partnerUser.setAccountTypeId(userObj.optString("account_type_id", ""));
            partnerUser.setBranchState(userObj.optString("branchstate", ""));
            partnerUser.setBranchLocation(userObj.optString("branchloaction", ""));
            partnerUser.setBankName(userObj.optString("bank_name", ""));
            partnerUser.setAccountType(userObj.optString("account_type", ""));
            partnerUser.setOfficeAddress(userObj.optString("office_address", ""));
            partnerUser.setResidentialAddress(userObj.optString("residential_address", ""));
            partnerUser.setAadhaarNumber(userObj.optString("aadhaar_number", ""));
            partnerUser.setPanNumber(userObj.optString("pan_number", ""));
            partnerUser.setAccountNumber(userObj.optString("account_number", ""));
            partnerUser.setIfscCode(userObj.optString("ifsc_code", ""));
            partnerUser.setRank(userObj.optString("rank", ""));
            partnerUser.setReportingTo(userObj.optString("reportingTo", ""));
            partnerUser.setEmployeeNo(userObj.optString("employee_no", ""));
            partnerUser.setDepartment(userObj.optString("department", ""));
            partnerUser.setDesignation(userObj.optString("designation", ""));
            partnerUser.setPartnerTypeId(userObj.optString("partner_type_id", ""));
            partnerUser.setPanImg(userObj.optString("pan_img", ""));
            partnerUser.setAadhaarImg(userObj.optString("aadhaar_img", ""));
            partnerUser.setPhotoImg(userObj.optString("photo_img", ""));
            partnerUser.setBankproofImg(userObj.optString("bankproof_img", ""));
            partnerUser.setUserId(userObj.optString("user_id", ""));
            partnerUser.setUpdatedAt(userObj.optString("updated_at", ""));
            
            partnerUsers.add(partnerUser);
        }
        
        return partnerUsers;
    }
    
    private void showPartnerUserDetails(PartnerUser partnerUser) {
        // Create a detailed message with all available information
        StringBuilder details = new StringBuilder();
        details.append("👤 Partner User Details\n\n");
        details.append("📋 Basic Information:\n");
        details.append("ID: ").append(partnerUser.getId()).append("\n");
        details.append("Username: ").append(partnerUser.getUsername()).append("\n");
        details.append("Alias Name: ").append(partnerUser.getAliasName()).append("\n");
        details.append("First Name: ").append(partnerUser.getFirstName()).append("\n");
        details.append("Last Name: ").append(partnerUser.getLastName()).append("\n");
        details.append("Full Name: ").append(partnerUser.getFullName()).append("\n");
        details.append("Password: ").append(partnerUser.getPassword()).append("\n");
        details.append("Employee No: ").append(partnerUser.getEmployeeNo()).append("\n");
        details.append("Department: ").append(partnerUser.getDepartment()).append("\n");
        details.append("Designation: ").append(partnerUser.getDesignation()).append("\n");
        details.append("Status: ").append(partnerUser.getStatus()).append("\n");
        details.append("Rank: ").append(partnerUser.getRank()).append("\n");
        details.append("Reporting To: ").append(partnerUser.getReportingTo()).append("\n");
        details.append("Partner Type ID: ").append(partnerUser.getPartnerTypeId()).append("\n");
        details.append("User ID: ").append(partnerUser.getUserId()).append("\n\n");
        
        details.append("📞 Contact Information:\n");
        details.append("Phone: ").append(partnerUser.getPhoneNumber()).append("\n");
        details.append("Alternative Phone: ").append(partnerUser.getAlternativeMobileNumber()).append("\n");
        details.append("Email: ").append(partnerUser.getEmail()).append("\n\n");
        
        details.append("🏢 Company Information:\n");
        details.append("Company: ").append(partnerUser.getCompanyName()).append("\n");
        details.append("Branch State Name ID: ").append(partnerUser.getBranchStateNameId()).append("\n");
        details.append("Branch Location ID: ").append(partnerUser.getBranchLocationId()).append("\n");
        details.append("Branch State: ").append(partnerUser.getBranchState()).append("\n");
        details.append("Branch Location: ").append(partnerUser.getBranchLocation()).append("\n\n");
        
        details.append("🏦 Banking Information:\n");
        details.append("Bank ID: ").append(partnerUser.getBankId()).append("\n");
        details.append("Account Type ID: ").append(partnerUser.getAccountTypeId()).append("\n");
        details.append("Bank Name: ").append(partnerUser.getBankName()).append("\n");
        details.append("Account Type: ").append(partnerUser.getAccountType()).append("\n");
        details.append("Account Number: ").append(partnerUser.getAccountNumber()).append("\n");
        details.append("IFSC Code: ").append(partnerUser.getIfscCode()).append("\n\n");
        
        details.append("📍 Address Information:\n");
        details.append("Office Address: ").append(partnerUser.getOfficeAddress()).append("\n");
        details.append("Residential Address: ").append(partnerUser.getResidentialAddress()).append("\n\n");
        
        details.append("🆔 Identity Information:\n");
        details.append("Aadhaar Number: ").append(partnerUser.getAadhaarNumber()).append("\n");
        details.append("PAN Number: ").append(partnerUser.getPanNumber()).append("\n\n");
        
        details.append("📷 Image Files:\n");
        details.append("PAN Image: ").append(partnerUser.getPanImg()).append("\n");
        details.append("Aadhaar Image: ").append(partnerUser.getAadhaarImg()).append("\n");
        details.append("Photo Image: ").append(partnerUser.getPhotoImg()).append("\n");
        details.append("Bank Proof Image: ").append(partnerUser.getBankproofImg()).append("\n\n");
        
        details.append("📅 System Information:\n");
        details.append("Created By: ").append(partnerUser.getCreatedBy()).append("\n");
        details.append("Created At: ").append(partnerUser.getCreatedAt()).append("\n");
        details.append("Updated At: ").append(partnerUser.getUpdatedAt()).append("\n");
        
        // Show in a dialog instead of toast for better readability
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Partner User Details");
        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Close", null);
        builder.show();
        
        // TODO: Create PartnerUserDetailsActivity for detailed view with better UI
        // Intent intent = new Intent(this, PartnerUserDetailsActivity.class);
        // intent.putExtra("PARTNER_USER_ID", partnerUser.getId());
        // intent.putExtra("PARTNER_USER_NAME", partnerUser.getFullName());
        // intent.putExtra("PARTNER_USER_PHONE", partnerUser.getPhoneNumber());
        // intent.putExtra("PARTNER_USER_EMAIL", partnerUser.getEmail());
        // intent.putExtra("PARTNER_USER_COMPANY", partnerUser.getCompanyName());
        // intent.putExtra("PARTNER_USER_STATUS", partnerUser.getStatus());
        // startActivity(intent);
    }
    
    private void showLoading(boolean show) {
        if (show) {
            loadingView.setVisibility(View.VISIBLE);
            partnerUsersListView.setVisibility(View.GONE);
            noDataView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            partnerUsersListView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showNoData() {
        noDataView.setVisibility(View.VISIBLE);
        partnerUsersListView.setVisibility(View.GONE);
    }
    
    private void hideNoData() {
        noDataView.setVisibility(View.GONE);
        partnerUsersListView.setVisibility(View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showNoData();
    }

    private void updateStatistics(JSONObject stats) throws JSONException {
        int totalUsers = stats.optInt("total_users", 0);
        int activeUsers = stats.optInt("active_users", 0);
        int inactiveUsers = stats.optInt("inactive_users", 0);

        totalUsersText.setText("Total: " + totalUsers);
        activeUsersText.setText("Active: " + activeUsers);
        inactiveUsersText.setText("Inactive: " + inactiveUsers);
    }

    private void updateUserList(List<PartnerUser> partnerUsers) {
        partnerUsersList.clear();
        partnerUsersList.addAll(partnerUsers);
        adapter.notifyDataSetChanged();
        if (partnerUsersList.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    // Partner User data class
    public static class PartnerUser {
        private int id;
        private String username;
        private String aliasName;
        private String firstName;
        private String lastName;
        private String fullName;
        private String password;
        private String phoneNumber;
        private String email;
        private String alternativeMobileNumber;
        private String companyName;
        private String branchStateNameId;
        private String branchLocationId;
        private String bankId;
        private String accountTypeId;
        private String branchState;
        private String branchLocation;
        private String bankName;
        private String accountType;
        private String officeAddress;
        private String residentialAddress;
        private String aadhaarNumber;
        private String panNumber;
        private String accountNumber;
        private String ifscCode;
        private String rank;
        private String status;
        private String reportingTo;
        private String employeeNo;
        private String department;
        private String designation;
        private String partnerTypeId;
        private String panImg;
        private String aadhaarImg;
        private String photoImg;
        private String bankproofImg;
        private String userId;
        private String createdAt;
        private String createdBy;
        private String updatedAt;

        // Constructor
        public PartnerUser(int id, String username, String fullName, String phoneNumber, 
                          String email, String companyName, String status, String createdAt, 
                          String createdBy) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.companyName = companyName;
            this.status = status;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
        }

        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getAliasName() { return aliasName; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getFullName() { return fullName; }
        public String getPassword() { return password; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getAlternativeMobileNumber() { return alternativeMobileNumber; }
        public String getCompanyName() { return companyName; }
        public String getBranchStateNameId() { return branchStateNameId; }
        public String getBranchLocationId() { return branchLocationId; }
        public String getBankId() { return bankId; }
        public String getAccountTypeId() { return accountTypeId; }
        public String getBranchState() { return branchState; }
        public String getBranchLocation() { return branchLocation; }
        public String getBankName() { return bankName; }
        public String getAccountType() { return accountType; }
        public String getOfficeAddress() { return officeAddress; }
        public String getResidentialAddress() { return residentialAddress; }
        public String getAadhaarNumber() { return aadhaarNumber; }
        public String getPanNumber() { return panNumber; }
        public String getAccountNumber() { return accountNumber; }
        public String getIfscCode() { return ifscCode; }
        public String getRank() { return rank; }
        public String getStatus() { return status; }
        public String getReportingTo() { return reportingTo; }
        public String getEmployeeNo() { return employeeNo; }
        public String getDepartment() { return department; }
        public String getDesignation() { return designation; }
        public String getPartnerTypeId() { return partnerTypeId; }
        public String getPanImg() { return panImg; }
        public String getAadhaarImg() { return aadhaarImg; }
        public String getPhotoImg() { return photoImg; }
        public String getBankproofImg() { return bankproofImg; }
        public String getUserId() { return userId; }
        public String getCreatedAt() { return createdAt; }
        public String getCreatedBy() { return createdBy; }
        public String getUpdatedAt() { return updatedAt; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setUsername(String username) { this.username = username; }
        public void setAliasName(String aliasName) { this.aliasName = aliasName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setEmail(String email) { this.email = email; }
        public void setAlternativeMobileNumber(String alternativeMobileNumber) { this.alternativeMobileNumber = alternativeMobileNumber; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public void setBranchState(String branchState) { this.branchState = branchState; }
        public void setBranchLocation(String branchLocation) { this.branchLocation = branchLocation; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
        public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }
        public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
        public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
        public void setRank(String rank) { this.rank = rank; }
        public void setStatus(String status) { this.status = status; }
        public void setReportingTo(String reportingTo) { this.reportingTo = reportingTo; }
        public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
        public void setDepartment(String department) { this.department = department; }
        public void setDesignation(String designation) { this.designation = designation; }
        public void setPassword(String password) { this.password = password; }
        public void setBranchStateNameId(String branchStateNameId) { this.branchStateNameId = branchStateNameId; }
        public void setBranchLocationId(String branchLocationId) { this.branchLocationId = branchLocationId; }
        public void setBankId(String bankId) { this.bankId = bankId; }
        public void setAccountTypeId(String accountTypeId) { this.accountTypeId = accountTypeId; }
        public void setPartnerTypeId(String partnerTypeId) { this.partnerTypeId = partnerTypeId; }
        public void setPanImg(String panImg) { this.panImg = panImg; }
        public void setAadhaarImg(String aadhaarImg) { this.aadhaarImg = aadhaarImg; }
        public void setPhotoImg(String photoImg) { this.photoImg = photoImg; }
        public void setBankproofImg(String bankproofImg) { this.bankproofImg = bankproofImg; }
        public void setUserId(String userId) { this.userId = userId; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
} 
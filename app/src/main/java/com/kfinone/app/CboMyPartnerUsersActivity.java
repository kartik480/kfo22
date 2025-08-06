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
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userId = String.valueOf(prefs.getInt("user_id", 0));
            username = prefs.getString("username", "");
            userFullName = prefs.getString("user_full_name", "");
        }
        
        if (userId == null || userId.isEmpty() || userId.equals("0")) {
            Toast.makeText(this, "User ID is required", Toast.LENGTH_SHORT).show();
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
        
        // Set list item click listener
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
        
        // Use the user ID from the class variable (set in onCreate)
        String url = "https://emp.kfinone.com/mobile/api/cbo_my_partner_users.php?user_id=" + userId;
        
        Log.d("CBOMyPartnerUsers", "Fetching partner users for user_id: " + userId);
        
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
            
            partnerUsers.add(partnerUser);
        }
        
        return partnerUsers;
    }
    
    private void showPartnerUserDetails(PartnerUser partnerUser) {
        // Show partner user details in a toast for now
        String details = "Name: " + partnerUser.getFullName() + 
                        "\nPhone: " + partnerUser.getPhoneNumber() + 
                        "\nEmail: " + partnerUser.getEmail() + 
                        "\nCompany: " + partnerUser.getCompanyName() + 
                        "\nStatus: " + partnerUser.getStatus();
        
        Toast.makeText(this, details, Toast.LENGTH_LONG).show();
        
        // TODO: Create PartnerUserDetailsActivity for detailed view
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
        private String phoneNumber;
        private String email;
        private String alternativeMobileNumber;
        private String companyName;
        private String status;
        private String createdAt;
        private String createdBy;

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
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getAlternativeMobileNumber() { return alternativeMobileNumber; }
        public String getCompanyName() { return companyName; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public String getCreatedBy() { return createdBy; }

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
        public void setStatus(String status) { this.status = status; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
} 
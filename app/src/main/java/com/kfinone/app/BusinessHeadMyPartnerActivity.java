package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusinessHeadMyPartnerActivity extends AppCompatActivity {
    
    private static final String TAG = "BHMyPartner";
    private static final String API_URL = "https://emp.kfinone.com/mobile/api/business_head_my_partner_users.php";
    
    // UI Components
    private Toolbar toolbar;
    private TextView welcomeText, userInfoText;
    private TextView totalPartnersText, activePartnersText, inactivePartnersText;
    private AutoCompleteTextView agentTypeDropdown, branchStateDropdown, branchLocationDropdown;
    private AutoCompleteTextView searchEditText;
    private ListView partnersListView;
    private ProgressBar loadingProgressBar;
    
    // Data
    private List<PartnerUser> partnerList;
    private List<PartnerUser> filteredPartnerList;
    private PartnerAdapter partnerAdapter;
    private RequestQueue requestQueue;
    
    // Filter options
    private String selectedAgentType = "All Types";
    private String selectedBranchState = "All States";
    private String selectedBranchLocation = "All Locations";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_partner);
        
        initializeViews();
        setupToolbar();
        setupDropdowns();
        setupSearch();
        initializeData();
        loadPartnerData();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        welcomeText = findViewById(R.id.welcomeText);
        userInfoText = findViewById(R.id.userInfoText);
        totalPartnersText = findViewById(R.id.totalPartnersText);
        activePartnersText = findViewById(R.id.activePartnersText);
        inactivePartnersText = findViewById(R.id.inactivePartnersText);
        agentTypeDropdown = findViewById(R.id.agentTypeDropdown);
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        branchLocationDropdown = findViewById(R.id.branchLocationDropdown);
        searchEditText = findViewById(R.id.searchEditText);
        partnersListView = findViewById(R.id.partnersListView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        
        requestQueue = Volley.newRequestQueue(this);
        partnerList = new ArrayList<>();
        filteredPartnerList = new ArrayList<>();
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("BH My Partner");
        }
        
        // Set welcome message based on logged-in user
        welcomeText.setText("Welcome, Business Head");
        userInfoText.setText("Manage your partner network");
    }
    
    private void setupDropdowns() {
        // Agent Type Dropdown
        String[] agentTypes = {"All Types", "Individual", "Corporate", "Institutional", "Retail"};
        ArrayAdapter<String> agentTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, agentTypes);
        agentTypeDropdown.setAdapter(agentTypeAdapter);
        agentTypeDropdown.setText("All Types", false);
        
        // Branch State Dropdown
        String[] branchStates = {"All States", "Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat", "Uttar Pradesh"};
        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, branchStates);
        branchStateDropdown.setAdapter(branchStateAdapter);
        branchStateDropdown.setText("All States", false);
        
        // Branch Location Dropdown
        String[] branchLocations = {"All Locations", "Mumbai", "Delhi", "Bangalore", "Chennai", "Ahmedabad", "Lucknow"};
        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, branchLocations);
        branchLocationDropdown.setAdapter(branchLocationAdapter);
        branchLocationDropdown.setText("All Locations", false);
        
        // Set dropdown change listeners
        agentTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedAgentType = agentTypes[position];
            applyFilters();
        });
        
        branchStateDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedBranchState = branchStates[position];
            applyFilters();
        });
        
        branchLocationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedBranchLocation = branchLocations[position];
            applyFilters();
        });
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });
    }
    
    private void initializeData() {
        partnerAdapter = new PartnerAdapter(this, filteredPartnerList);
        partnersListView.setAdapter(partnerAdapter);
    }
    
    private void loadPartnerData() {
        showLoading(true);
        
        // Get logged-in user info (you'll need to implement this based on your login system)
        String username = getLoggedInUsername(); // Implement this method
        
        if (username == null || username.isEmpty()) {
            showLoading(false);
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String url = API_URL + "?username=" + username;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "API Response: " + response.toString());
                    parseApiResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "API Error: " + error.toString());
                    showLoading(false);
                    Toast.makeText(BusinessHeadMyPartnerActivity.this, 
                        "Error loading partner data", Toast.LENGTH_SHORT).show();
                }
            });
        
        requestQueue.add(request);
    }
    
    private String getLoggedInUsername() {
        // TODO: Implement this method to get the logged-in user's username
        // This should return the username from your login session/SharedPreferences
        return "94000"; // Temporary hardcoded value for testing
    }
    
    private void parseApiResponse(JSONObject response) {
        try {
            boolean success = response.getBoolean("success");
            if (success) {
                JSONArray data = response.getJSONArray("data");
                JSONObject stats = response.getJSONObject("stats");
                JSONObject creatorInfo = response.getJSONObject("creator_info");
                
                List<PartnerUser> newPartnerList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject partner = data.getJSONObject(i);
                    PartnerUser partnerUser = new PartnerUser();
                    
                    // Set basic fields
                    partnerUser.setId(partner.optString("id", ""));
                    partnerUser.setUsername(partner.optString("username", ""));
                    partnerUser.setFirstName(partner.optString("first_name", ""));
                    partnerUser.setLastName(partner.optString("last_name", ""));
                    partnerUser.setPhoneNumber(partner.optString("Phone_number", ""));
                    partnerUser.setEmailId(partner.optString("email_id", ""));
                    partnerUser.setCompanyName(partner.optString("company_name", ""));
                    partnerUser.setStatus(partner.optString("status", ""));
                    partnerUser.setCreatedBy(partner.optString("createdBy", ""));
                    partnerUser.setCreatedAt(partner.optString("created_at", ""));
                    
                    // Set creator info
                    partnerUser.setCreatorName(creatorInfo.optString("username", ""));
                    partnerUser.setCreatorDesignationName(creatorInfo.optString("firstName", "") + " " + creatorInfo.optString("lastName", ""));
                    
                    newPartnerList.add(partnerUser);
                }
                
                runOnUiThread(() -> {
                    updatePartnerList(newPartnerList);
                    updateStatistics(stats);
                    showLoading(false);
                });
            } else {
                String message = response.optString("message", "Unknown error");
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(BusinessHeadMyPartnerActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing API response", e);
            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(BusinessHeadMyPartnerActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void updatePartnerList(List<PartnerUser> newPartnerList) {
        partnerList.clear();
        partnerList.addAll(newPartnerList);
        applyFilters();
    }
    
    private void updateStatistics(JSONObject stats) {
        try {
            int total = stats.optInt("total_partners", 0);
            int active = stats.optInt("active_partners", 0);
            int inactive = stats.optInt("inactive_partners", 0);
            
            totalPartnersText.setText(String.valueOf(total));
            activePartnersText.setText(String.valueOf(active));
            inactivePartnersText.setText(String.valueOf(inactive));
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
        }
    }
    
    private void applyFilters() {
        String searchQuery = searchEditText.getText().toString().toLowerCase().trim();
        
        List<PartnerUser> filtered = new ArrayList<>();
        
        for (PartnerUser partner : partnerList) {
            boolean matchesSearch = searchQuery.isEmpty() || 
                partner.getDisplayName().toLowerCase().contains(searchQuery) ||
                partner.getDisplayCompany().toLowerCase().contains(searchQuery) ||
                partner.getDisplayPhone().toLowerCase().contains(searchQuery) ||
                partner.getDisplayEmail().toLowerCase().contains(searchQuery);
            
            boolean matchesAgentType = selectedAgentType.equals("All Types") || 
                (partner.getPartnerTypeId() != null && partner.getPartnerTypeId().equals(selectedAgentType));
            
            boolean matchesBranchState = selectedBranchState.equals("All States") || 
                (partner.getState() != null && partner.getState().equals(selectedBranchState));
            
            boolean matchesBranchLocation = selectedBranchLocation.equals("All Locations") || 
                (partner.getLocation() != null && partner.getLocation().equals(selectedBranchLocation));
            
            if (matchesSearch && matchesAgentType && matchesBranchState && matchesBranchLocation) {
                filtered.add(partner);
            }
        }
        
        filteredPartnerList.clear();
        filteredPartnerList.addAll(filtered);
        partnerAdapter.notifyDataSetChanged();
        
        // Update statistics for filtered results
        updateFilteredStatistics();
    }
    
    private void updateFilteredStatistics() {
        int total = filteredPartnerList.size();
        int active = 0;
        int inactive = 0;
        
        for (PartnerUser partner : filteredPartnerList) {
            if (partner.isActive()) {
                active++;
            } else {
                inactive++;
            }
        }
        
        totalPartnersText.setText(String.valueOf(total));
        activePartnersText.setText(String.valueOf(active));
        inactivePartnersText.setText(String.valueOf(inactive));
    }
    
    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        partnersListView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import java.util.List;

public class BusinessHeadMyAgentActivity extends AppCompatActivity {
    
    private static final String TAG = "BHMyAgent";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    private static final String AGENT_API_URL = "https://emp.kfinone.com/mobile/api/business_head_my_agents.php";

    private View backButton;
    private LinearLayout agentListContainer;
    private AutoCompleteTextView agentTypeDropdown, branchStateDropdown, branchLocationDropdown;
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    
    // Filter options
    private String selectedAgentType = "All Types";
    private String selectedBranchState = "All States";
    private String selectedBranchLocation = "All Locations";
    
    // Dropdown data
    private List<String> partnerTypeNames = new ArrayList<>();
    private List<String> partnerTypeIds = new ArrayList<>();
    private List<String> branchStateNames = new ArrayList<>();
    private List<String> branchStateIds = new ArrayList<>();
    private List<String> branchLocationNames = new ArrayList<>();
    private List<String> branchLocationIds = new ArrayList<>();
    
    // Agent data
    private List<AgentData> agentList = new ArrayList<>();
    private List<AgentData> filteredAgentList = new ArrayList<>();
    
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_agent);
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        
        requestQueue = Volley.newRequestQueue(this);
        
        initializeViews();
        loadDropdownOptions();
        setupClickListeners();
        loadAgentData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        agentListContainer = findViewById(R.id.agentListContainer);
        agentTypeDropdown = findViewById(R.id.agentTypeDropdown);
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        branchLocationDropdown = findViewById(R.id.branchLocationDropdown);
    }
    
    private void loadDropdownOptions() {
        String url = BASE_URL + "director_agent_dropdowns.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    // Partner Types (Agent Types)
                    JSONArray partnerTypes = response.getJSONArray("partner_types");
                    partnerTypeNames.clear(); 
                    partnerTypeIds.clear();
                    partnerTypeNames.add("All Types"); 
                    partnerTypeIds.add("0");
                    for (int i = 0; i < partnerTypes.length(); i++) {
                        JSONObject obj = partnerTypes.getJSONObject(i);
                        partnerTypeNames.add(obj.getString("name"));
                        partnerTypeIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> partnerTypeAdapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_dropdown_item_1line, partnerTypeNames);
                    agentTypeDropdown.setAdapter(partnerTypeAdapter);
                    agentTypeDropdown.setText("All Types", false);

                    // Branch States
                    JSONArray branchStates = response.getJSONArray("branch_states");
                    branchStateNames.clear(); 
                    branchStateIds.clear();
                    branchStateNames.add("All States"); 
                    branchStateIds.add("0");
                    for (int i = 0; i < branchStates.length(); i++) {
                        JSONObject obj = branchStates.getJSONObject(i);
                        branchStateNames.add(obj.getString("name"));
                        branchStateIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_dropdown_item_1line, branchStateNames);
                    branchStateDropdown.setAdapter(branchStateAdapter);
                    branchStateDropdown.setText("All States", false);

                    // Branch Locations
                    JSONArray branchLocations = response.getJSONArray("branch_locations");
                    branchLocationNames.clear(); 
                    branchLocationIds.clear();
                    branchLocationNames.add("All Locations"); 
                    branchLocationIds.add("0");
                    for (int i = 0; i < branchLocations.length(); i++) {
                        JSONObject obj = branchLocations.getJSONObject(i);
                        branchLocationNames.add(obj.getString("name"));
                        branchLocationIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_dropdown_item_1line, branchLocationNames);
                    branchLocationDropdown.setAdapter(branchLocationAdapter);
                    branchLocationDropdown.setText("All Locations", false);
                    
                    // Set dropdown change listeners
                    agentTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
                        selectedAgentType = partnerTypeNames.get(position);
                        applyFilters();
                    });
                    
                    branchStateDropdown.setOnItemClickListener((parent, view, position, id) -> {
                        selectedBranchState = branchStateNames.get(position);
                        applyFilters();
                    });
                    
                    branchLocationDropdown.setOnItemClickListener((parent, view, position, id) -> {
                        selectedBranchLocation = branchLocationNames.get(position);
                        applyFilters();
                    });
                    
                    Log.d(TAG, "Loaded dropdowns: " + partnerTypeNames.size() + " partner types, " + 
                          branchStateNames.size() + " branch states, " + branchLocationNames.size() + " branch locations");
                    
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing dropdown response", e);
                    Toast.makeText(this, "Error loading dropdown options", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Error loading dropdown options", error);
                Toast.makeText(this, "Failed to load dropdown options", Toast.LENGTH_SHORT).show();
                // Fallback to sample data
                setupFallbackDropdowns();
            }
        );
        
        requestQueue.add(request);
    }
    
    private void setupFallbackDropdowns() {
        // Fallback sample data if API fails
        String[] agentTypes = {"All Types", "Individual", "Corporate", "Institutional", "Retail"};
        String[] branchStates = {"All States", "Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat", "Uttar Pradesh"};
        String[] branchLocations = {"All Locations", "Mumbai", "Delhi", "Bangalore", "Chennai", "Ahmedabad", "Lucknow"};
        
        ArrayAdapter<String> agentTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, agentTypes);
        agentTypeDropdown.setAdapter(agentTypeAdapter);
        agentTypeDropdown.setText("All Types", false);
        
        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, branchStates);
        branchStateDropdown.setAdapter(branchStateAdapter);
        branchStateDropdown.setText("All States", false);
        
        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, branchLocations);
        branchLocationDropdown.setAdapter(branchLocationAdapter);
        branchLocationDropdown.setText("All Locations", false);
        
        // Set listeners
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
    
    private void loadAgentData() {
        // Get logged-in user info
        String username = getLoggedInUsername();
        
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String url = AGENT_API_URL + "?username=" + username;
        
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
                    Toast.makeText(BusinessHeadMyAgentActivity.this, 
                        "Error loading agent data", Toast.LENGTH_SHORT).show();
                }
            });
        
        requestQueue.add(request);
    }
    
    private String getLoggedInUsername() {
        // Try to get from intent first
        if (userName != null && !userName.isEmpty()) {
            return userName;
        }
        
        // Try to get from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", "");
        
        if (!savedUsername.isEmpty()) {
            return savedUsername;
        }
        
        // Fallback to hardcoded value for testing
        return "94000";
    }
    
    private void parseApiResponse(JSONObject response) {
        try {
            boolean success = response.getBoolean("success");
            if (success) {
                JSONArray data = response.getJSONArray("data");
                
                List<AgentData> newAgentList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject agent = data.getJSONObject(i);
                    AgentData agentData = new AgentData();
                    
                    // Set fields from tbl_agent_data
                    agentData.setId(agent.optString("id", ""));
                    agentData.setFullName(agent.optString("full_name", ""));
                    agentData.setCompanyName(agent.optString("company_name", ""));
                    agentData.setPhoneNumber(agent.optString("Phone_number", ""));
                    agentData.setAlternativePhoneNumber(agent.optString("alternative_Phone_number", ""));
                    agentData.setEmailId(agent.optString("email_id", ""));
                    agentData.setPartnerType(agent.optString("partnerType", ""));
                    agentData.setState(agent.optString("state", ""));
                    agentData.setLocation(agent.optString("location", ""));
                    agentData.setAddress(agent.optString("address", ""));
                    agentData.setVisitingCard(agent.optString("visiting_card", ""));
                    agentData.setCreatedUser(agent.optString("created_user", ""));
                    agentData.setCreatedBy(agent.optString("createdBy", ""));
                    agentData.setStatus(agent.optString("status", ""));
                    agentData.setCreatedAt(agent.optString("created_at", ""));
                    agentData.setUpdatedAt(agent.optString("updated_at", ""));
                    
                    newAgentList.add(agentData);
                }
                
                runOnUiThread(() -> {
                    updateAgentList(newAgentList);
                });
            } else {
                String message = response.optString("message", "Unknown error");
                runOnUiThread(() -> {
                    Toast.makeText(BusinessHeadMyAgentActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing API response", e);
            runOnUiThread(() -> {
                Toast.makeText(BusinessHeadMyAgentActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void updateAgentList(List<AgentData> newAgentList) {
        agentList.clear();
        agentList.addAll(newAgentList);
        applyFilters();
    }
    
    private void applyFilters() {
        // Clear current list
        agentListContainer.removeAllViews();
        
        // Apply filters
        List<AgentData> filtered = new ArrayList<>();
        
        for (AgentData agent : agentList) {
            boolean matchesAgentType = selectedAgentType.equals("All Types") || 
                (agent.getPartnerType() != null && agent.getPartnerType().equals(selectedAgentType));
            
            boolean matchesBranchState = selectedBranchState.equals("All States") || 
                (agent.getState() != null && agent.getState().equals(selectedBranchState));
            
            boolean matchesBranchLocation = selectedBranchLocation.equals("All Locations") || 
                (agent.getLocation() != null && agent.getLocation().equals(selectedBranchLocation));
            
            if (matchesAgentType && matchesBranchState && matchesBranchLocation) {
                filtered.add(agent);
            }
        }
        
        filteredAgentList.clear();
        filteredAgentList.addAll(filtered);
        
        // Display filtered agents
        for (AgentData agent : filteredAgentList) {
            addAgentItem(agent);
        }
        
        // Show filter info
        String filterInfo = "Filters: " + selectedAgentType + ", " + selectedBranchState + ", " + selectedBranchLocation;
        Toast.makeText(this, filterInfo + " - Found " + filteredAgentList.size() + " agents", Toast.LENGTH_SHORT).show();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void addAgentItem(AgentData agent) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12);
        cardView.setCardElevation(4);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.background_light));

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(20, 20, 20, 20);

        // Add agent information
        addTextView(contentLayout, "Name: " + agent.getFullName());
        addTextView(contentLayout, "Company: " + agent.getCompanyName());
        addTextView(contentLayout, "Phone: " + agent.getPhoneNumber());
        if (agent.getAlternativePhoneNumber() != null && !agent.getAlternativePhoneNumber().isEmpty()) {
            addTextView(contentLayout, "Alt Phone: " + agent.getAlternativePhoneNumber());
        }
        addTextView(contentLayout, "Email: " + agent.getEmailId());
        addTextView(contentLayout, "Partner Type: " + agent.getPartnerType());
        addTextView(contentLayout, "State: " + agent.getState());
        addTextView(contentLayout, "Location: " + agent.getLocation());
        if (agent.getAddress() != null && !agent.getAddress().isEmpty()) {
            addTextView(contentLayout, "Address: " + agent.getAddress());
        }
        addTextView(contentLayout, "Status: " + agent.getStatus());
        addTextView(contentLayout, "Created At: " + agent.getCreatedAt());
        addTextView(contentLayout, "Created By: " + agent.getCreatedBy());

        cardView.addView(contentLayout);
        agentListContainer.addView(cardView);
    }
    
    private void addTextView(LinearLayout parent, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setPadding(0, 0, 0, 8);
        parent.addView(textView);
    }

    private void goBack() {
        finish();
    }
    
    // Agent Data Model Class
    private static class AgentData {
        private String id, fullName, companyName, phoneNumber, alternativePhoneNumber, emailId;
        private String partnerType, state, location, address, visitingCard, createdUser, createdBy, status, createdAt, updatedAt;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getAlternativePhoneNumber() { return alternativePhoneNumber; }
        public void setAlternativePhoneNumber(String alternativePhoneNumber) { this.alternativePhoneNumber = alternativePhoneNumber; }
        
        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }
        
        public String getPartnerType() { return partnerType; }
        public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getVisitingCard() { return visitingCard; }
        public void setVisitingCard(String visitingCard) { this.visitingCard = visitingCard; }
        
        public String getCreatedUser() { return createdUser; }
        public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
} 
package com.kfinone.app;

import android.content.Intent;
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
        loadActiveAgentList();
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
    
    private void applyFilters() {
        // Clear current list
        agentListContainer.removeAllViews();
        
        // Apply filters and reload agents
        loadActiveAgentList();
        
        // Show filter info
        String filterInfo = "Filters: " + selectedAgentType + ", " + selectedBranchState + ", " + selectedBranchLocation;
        Toast.makeText(this, filterInfo, Toast.LENGTH_SHORT).show();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void loadActiveAgentList() {
        // Sample agent data - in real implementation, this would come from API
        addAgentItem("John Doe", "1234567890", "john@example.com", "******", "Admin User", "Individual", "Maharashtra", "Mumbai");
        addAgentItem("Jane Smith", "9876543210", "jane@example.com", "******", "Business Head", "Corporate", "Delhi", "Delhi");
        addAgentItem("Mike Johnson", "5555555555", "mike@example.com", "******", "Manager", "Institutional", "Karnataka", "Bangalore");
        addAgentItem("Sarah Wilson", "1111111111", "sarah@example.com", "******", "Admin", "Retail", "Tamil Nadu", "Chennai");
        addAgentItem("David Brown", "2222222222", "david@example.com", "******", "Manager", "Individual", "Gujarat", "Ahmedabad");
    }

    private void addAgentItem(String name, String phone, String email, String password, String createdBy, 
                            String agentType, String branchState, String branchLocation) {
        
        // Apply filters
        if (!selectedAgentType.equals("All Types") && !selectedAgentType.equals(agentType)) {
            return;
        }
        if (!selectedBranchState.equals("All States") && !selectedBranchState.equals(branchState)) {
            return;
        }
        if (!selectedBranchLocation.equals("All Locations") && !selectedBranchLocation.equals(branchLocation)) {
            return;
        }
        
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

        TextView nameText = new TextView(this);
        nameText.setText("Name: " + name);
        nameText.setTextSize(16);
        nameText.setTextColor(getResources().getColor(R.color.black));
        nameText.setPadding(0, 0, 0, 8);
        contentLayout.addView(nameText);

        TextView phoneText = new TextView(this);
        phoneText.setText("Phone: " + phone);
        phoneText.setTextSize(16);
        phoneText.setTextColor(getResources().getColor(R.color.black));
        phoneText.setPadding(0, 0, 0, 8);
        contentLayout.addView(phoneText);

        TextView emailText = new TextView(this);
        emailText.setText("Email: " + email);
        emailText.setTextSize(16);
        emailText.setTextColor(getResources().getColor(R.color.black));
        emailText.setPadding(0, 0, 0, 8);
        contentLayout.addView(emailText);

        TextView passwordText = new TextView(this);
        passwordText.setText("Password: " + password);
        passwordText.setTextSize(16);
        passwordText.setTextColor(getResources().getColor(R.color.black));
        passwordText.setPadding(0, 0, 0, 8);
        contentLayout.addView(passwordText);

        TextView createdByText = new TextView(this);
        createdByText.setText("Created by: " + createdBy);
        createdByText.setTextSize(16);
        createdByText.setTextColor(getResources().getColor(R.color.black));
        createdByText.setPadding(0, 0, 0, 8);
        contentLayout.addView(createdByText);
        
        // Add agent type, branch state, and branch location info
        TextView agentTypeText = new TextView(this);
        agentTypeText.setText("Agent Type: " + agentType);
        agentTypeText.setTextSize(16);
        agentTypeText.setTextColor(getResources().getColor(R.color.black));
        agentTypeText.setPadding(0, 0, 0, 8);
        contentLayout.addView(agentTypeText);
        
        TextView branchStateText = new TextView(this);
        branchStateText.setText("Branch State: " + branchState);
        branchStateText.setTextSize(16);
        branchStateText.setTextColor(getResources().getColor(R.color.black));
        branchStateText.setPadding(0, 0, 0, 8);
        contentLayout.addView(branchStateText);
        
        TextView branchLocationText = new TextView(this);
        branchLocationText.setText("Branch Location: " + branchLocation);
        branchLocationText.setTextSize(16);
        branchLocationText.setTextColor(getResources().getColor(R.color.black));
        branchLocationText.setPadding(0, 0, 0, 8);
        contentLayout.addView(branchLocationText);

        cardView.addView(contentLayout);
        agentListContainer.addView(cardView);
    }

    private void goBack() {
        finish();
    }
} 
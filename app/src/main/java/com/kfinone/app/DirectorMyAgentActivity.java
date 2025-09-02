package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class DirectorMyAgentActivity extends AppCompatActivity {
    private Spinner spinnerPartnerType, spinnerBranchState, spinnerBranchLocation;
    private Button btnFilter, btnReset;
    private RequestQueue requestQueue;
    private RecyclerView agentsRecyclerView;
    private LinearLayout emptyStateContainer;
    private LinearLayout loadingContainer;
    private TextView agentCountText;
    private String userName;
    private String userId;

    // For mapping dropdown display names to IDs
    private List<String> partnerTypeNames = new ArrayList<>();
    private List<String> partnerTypeIds = new ArrayList<>();
    private List<String> branchStateNames = new ArrayList<>();
    private List<String> branchStateIds = new ArrayList<>();
    private List<String> branchLocationNames = new ArrayList<>();
    private List<String> branchLocationIds = new ArrayList<>();
    
    // For agents data
    private List<AgentData> allAgents = new ArrayList<>();
    private List<AgentData> filteredAgents = new ArrayList<>();
    private AgentAdapter agentAdapter;

    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_agent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Director My Agent");
        }

        spinnerPartnerType = findViewById(R.id.spinner_partner_type);
        spinnerBranchState = findViewById(R.id.spinner_branch_state);
        spinnerBranchLocation = findViewById(R.id.spinner_branch_location);
        btnFilter = findViewById(R.id.btn_filter);
        btnReset = findViewById(R.id.btn_reset);
        agentsRecyclerView = findViewById(R.id.agentsRecyclerView);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        loadingContainer = findViewById(R.id.loadingContainer);
        agentCountText = findViewById(R.id.agentCountText);

        requestQueue = Volley.newRequestQueue(this);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("userName");
        userId = getIntent().getStringExtra("userId");
        
        // Initialize agents data
        allAgents = new ArrayList<>();
        filteredAgents = new ArrayList<>();
        
        // Setup RecyclerView
        setupRecyclerView();

        loadDropdownOptions();
        loadMyAgents();

        btnFilter.setOnClickListener(v -> filterAgents());
        btnReset.setOnClickListener(v -> {
            spinnerPartnerType.setSelection(0);
            spinnerBranchState.setSelection(0);
            spinnerBranchLocation.setSelection(0);
            resetFilters();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadDropdownOptions() {
        // Load data from APIs
        loadPartnerTypes();
        loadBranchStates();
        loadBranchLocations();
    }
    
    private void loadPartnerTypes() {
        String url = BASE_URL + "get_director_partner_types.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray partnerTypesArray = data.getJSONArray("partner_types");
                            
                            partnerTypeNames.clear();
                            partnerTypeIds.clear();
                            partnerTypeNames.add("Select Agent Type");
                            
                            for (int i = 0; i < partnerTypesArray.length(); i++) {
                                JSONObject partnerType = partnerTypesArray.getJSONObject(i);
                                partnerTypeNames.add(partnerType.getString("partner_type"));
                                partnerTypeIds.add(partnerType.getString("id"));
                            }
                            
                            // Set up spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DirectorMyAgentActivity.this, 
                                android.R.layout.simple_spinner_item, partnerTypeNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPartnerType.setAdapter(adapter);
                            
                            Toast.makeText(DirectorMyAgentActivity.this, 
                                "Loaded " + partnerTypesArray.length() + " partner types", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(DirectorMyAgentActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(DirectorMyAgentActivity.this, "Error parsing partner types: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectorMyAgentActivity.this, "Failed to load partner types: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        
        requestQueue.add(request);
    }
    
    private void loadBranchStates() {
        String url = BASE_URL + "get_director_branch_states.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray branchStatesArray = data.getJSONArray("branch_states");
                            
                            branchStateNames.clear();
                            branchStateIds.clear();
                            branchStateNames.add("Select Branch State");
                            
                            for (int i = 0; i < branchStatesArray.length(); i++) {
                                JSONObject branchState = branchStatesArray.getJSONObject(i);
                                branchStateNames.add(branchState.getString("branch_state_name"));
                                branchStateIds.add(branchState.getString("id"));
                            }
                            
                            // Set up spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DirectorMyAgentActivity.this, 
                                android.R.layout.simple_spinner_item, branchStateNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerBranchState.setAdapter(adapter);
                            
                            Toast.makeText(DirectorMyAgentActivity.this, 
                                "Loaded " + branchStatesArray.length() + " branch states", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(DirectorMyAgentActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(DirectorMyAgentActivity.this, "Error parsing branch states: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectorMyAgentActivity.this, "Failed to load branch states: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        
        requestQueue.add(request);
    }
    
    private void loadBranchLocations() {
        String url = BASE_URL + "get_director_branch_locations.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray branchLocationsArray = data.getJSONArray("branch_locations");
                            
                            branchLocationNames.clear();
                            branchLocationIds.clear();
                            branchLocationNames.add("Select Branch Location");
                            
                            for (int i = 0; i < branchLocationsArray.length(); i++) {
                                JSONObject branchLocation = branchLocationsArray.getJSONObject(i);
                                branchLocationNames.add(branchLocation.getString("branch_location"));
                                branchLocationIds.add(branchLocation.getString("id"));
                            }
                            
                            // Set up spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DirectorMyAgentActivity.this, 
                                android.R.layout.simple_spinner_item, branchLocationNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerBranchLocation.setAdapter(adapter);
                            
                            Toast.makeText(DirectorMyAgentActivity.this, 
                                "Loaded " + branchLocationsArray.length() + " branch locations", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(DirectorMyAgentActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(DirectorMyAgentActivity.this, "Error parsing branch locations: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectorMyAgentActivity.this, "Failed to load branch locations: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        
        requestQueue.add(request);
    }

    private void filterAgents() {
        String selectedAgentType = spinnerPartnerType.getSelectedItem().toString();
        String selectedBranchState = spinnerBranchState.getSelectedItem().toString();
        String selectedBranchLocation = spinnerBranchLocation.getSelectedItem().toString();

        // Check if any filter is selected
        if (selectedAgentType.equals("Select Agent Type") && 
            selectedBranchState.equals("Select Branch State") && 
            selectedBranchLocation.equals("Select Branch Location")) {
            Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show filter applied message
        String filterMessage = "Filters applied: ";
        if (!selectedAgentType.equals("Select Agent Type")) {
            filterMessage += selectedAgentType;
        }
        if (!selectedBranchState.equals("Select Branch State")) {
            filterMessage += (filterMessage.endsWith(": ") ? "" : ", ") + selectedBranchState;
        }
        if (!selectedBranchLocation.equals("Select Branch Location")) {
            filterMessage += (filterMessage.endsWith(": ") ? "" : ", ") + selectedBranchLocation;
        }
        
        Toast.makeText(this, filterMessage, Toast.LENGTH_LONG).show();
        
        // TODO: Implement actual filtering logic here
        // For now, just show the message
    }
    
    private void setupRecyclerView() {
        agentAdapter = new AgentAdapter(filteredAgents);
        agentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        agentsRecyclerView.setAdapter(agentAdapter);
    }
    
    private void loadMyAgents() {
        if (userName == null || userName.isEmpty()) {
            Toast.makeText(this, "User information not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoadingState();
        
        String url = BASE_URL + "get_director_my_agents.php?username=" + userName;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoadingState();
                    try {
                        if (response.getBoolean("success")) {
                            parseAgentsData(response);
                            updateAgentsDisplay();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(DirectorMyAgentActivity.this, message, Toast.LENGTH_SHORT).show();
                            showEmptyState();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DirectorMyAgentActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoadingState();
                    Toast.makeText(DirectorMyAgentActivity.this, "Error loading agents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            });
        
        requestQueue.add(request);
    }
    
    private void parseAgentsData(JSONObject response) throws JSONException {
        allAgents.clear();
        
        JSONObject data = response.getJSONObject("data");
        JSONArray agentsArray = data.getJSONArray("agents");
        
        for (int i = 0; i < agentsArray.length(); i++) {
            JSONObject agentJson = agentsArray.getJSONObject(i);
            
            AgentData agent = new AgentData(
                agentJson.optString("id", ""),
                agentJson.optString("full_name", ""),
                agentJson.optString("company_name", ""),
                agentJson.optString("Phone_number", ""),
                agentJson.optString("alternative_Phone_number", ""),
                agentJson.optString("email_id", ""),
                agentJson.optString("partnerType", ""),
                agentJson.optString("state", ""),
                agentJson.optString("location", ""),
                agentJson.optString("address", ""),
                agentJson.optString("visiting_card", ""),
                agentJson.optString("created_user", ""),
                agentJson.optString("createdBy", ""),
                agentJson.optString("status", ""),
                agentJson.optString("created_at", ""),
                agentJson.optString("updated_at", ""),
                agentJson.optString("creator_first_name", ""),
                agentJson.optString("creator_last_name", ""),
                agentJson.optString("creator_username", ""),
                agentJson.optString("creator_full_name", "")
            );
            
            allAgents.add(agent);
        }
        
        // Initially show all agents
        filteredAgents.clear();
        filteredAgents.addAll(allAgents);
    }
    
    private void updateAgentsDisplay() {
        agentAdapter.notifyDataSetChanged();
        agentCountText.setText(filteredAgents.size() + " agents");
        
        if (filteredAgents.isEmpty()) {
            showEmptyState();
        } else {
            showAgentsList();
        }
    }
    
    private void showLoadingState() {
        loadingContainer.setVisibility(View.VISIBLE);
        agentsRecyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.GONE);
    }
    
    private void hideLoadingState() {
        loadingContainer.setVisibility(View.GONE);
    }
    
    private void showEmptyState() {
        emptyStateContainer.setVisibility(View.VISIBLE);
        agentsRecyclerView.setVisibility(View.GONE);
    }
    
    private void showAgentsList() {
        agentsRecyclerView.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);
    }
    
    private void resetFilters() {
        filteredAgents.clear();
        filteredAgents.addAll(allAgents);
        updateAgentsDisplay();
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }
} 
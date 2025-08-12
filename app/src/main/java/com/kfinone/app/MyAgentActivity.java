package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class MyAgentActivity extends AppCompatActivity {
    private static final String TAG = "MyAgentActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // UI Elements
    private TextView backButton;
    private Spinner agentTypeSpinner, branchStateSpinner, branchLocationSpinner;
    private Button filterButton, resetButton, loadAllButton;
    private RecyclerView agentRecyclerView;
    private RequestQueue requestQueue;

    // Data
    private List<AgentItem> agentList = new ArrayList<>();
    private AgentAdapter agentAdapter;
    
    // Background processing
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_agent);

        // Initialize background processing
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());

        initializeViews();
        setupVolley();
        setupClickListeners();
        setupRecyclerView();
        loadDropdownData();
        loadAgentData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        agentTypeSpinner = findViewById(R.id.agentTypeSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        loadAllButton = findViewById(R.id.loadAllButton);
        agentRecyclerView = findViewById(R.id.agentRecyclerView);
    }

    private void setupRecyclerView() {
        agentAdapter = new AgentAdapter(agentList);
        agentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        agentRecyclerView.setAdapter(agentAdapter);
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        filterButton.setOnClickListener(v -> {
            String selectedAgentType = agentTypeSpinner.getSelectedItem().toString();
            String selectedBranchState = branchStateSpinner.getSelectedItem().toString();
            String selectedBranchLocation = branchLocationSpinner.getSelectedItem().toString();
            
            if (selectedAgentType.equals("All") && selectedBranchState.equals("All") && selectedBranchLocation.equals("All")) {
                displayAllAgents();
            } else {
                // Get the IDs for server-side filtering
                String partnerTypeId = getPartnerTypeId(selectedAgentType);
                String stateId = getStateId(selectedBranchState);
                String locationId = getLocationId(selectedBranchLocation);
                
                filterAgentsFromServer(partnerTypeId, stateId, locationId);
            }
        });

        resetButton.setOnClickListener(v -> {
            agentTypeSpinner.setSelection(0);
            branchStateSpinner.setSelection(0);
            branchLocationSpinner.setSelection(0);
            displayAllAgents();
        });

        loadAllButton.setOnClickListener(v -> {
            loadAgentData();
        });
    }

    private void loadDropdownData() {
        loadAgentTypes();
        loadBranchStates();
        loadBranchLocations();
    }

    private void loadAgentTypes() {
        String url = BASE_URL + "get_partner_type_list.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> agentTypes = new ArrayList<>();
                        agentTypes.add("All");
                        
                        // Clear and populate the map
                        partnerTypeMap.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject typeObj = data.getJSONObject(i);
                            String name = typeObj.getString("name");
                            String id = typeObj.getString("id");
                            agentTypes.add(name);
                            partnerTypeMap.put(name, id);
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, agentTypes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        agentTypeSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackTypes = new ArrayList<>();
                        fallbackTypes.add("All");
                        fallbackTypes.add("Business");
                        fallbackTypes.add("Individual");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackTypes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        agentTypeSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing agent types: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading agent types: " + error.getMessage());
                // Fallback data
                List<String> fallbackTypes = new ArrayList<>();
                fallbackTypes.add("All");
                fallbackTypes.add("Business");
                fallbackTypes.add("Individual");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                agentTypeSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void loadBranchStates() {
        String url = BASE_URL + "get_branch_states_dropdown.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> branchStates = new ArrayList<>();
                        branchStates.add("All");
                        
                        // Clear and populate the map
                        stateMap.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject stateObj = data.getJSONObject(i);
                            String name = stateObj.getString("name");
                            String id = stateObj.getString("id");
                            branchStates.add(name);
                            stateMap.put(name, id);
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStates);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchStateSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackStates = new ArrayList<>();
                        fallbackStates.add("All");
                        fallbackStates.add("Maharashtra");
                        fallbackStates.add("Delhi");
                        fallbackStates.add("Karnataka");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackStates);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchStateSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing branch states: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading branch states: " + error.getMessage());
                // Fallback data
                List<String> fallbackStates = new ArrayList<>();
                fallbackStates.add("All");
                fallbackStates.add("Maharashtra");
                fallbackStates.add("Delhi");
                fallbackStates.add("Karnataka");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackStates);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchStateSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void loadBranchLocations() {
        String url = BASE_URL + "get_branch_locations_dropdown.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        List<String> branchLocations = new ArrayList<>();
                        branchLocations.add("All");
                        
                        // Clear and populate the map
                        locationMap.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject locationObj = data.getJSONObject(i);
                            String name = locationObj.getString("name");
                            String id = locationObj.getString("id");
                            branchLocations.add(name);
                            locationMap.put(name, id);
                        }
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocations);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchLocationSpinner.setAdapter(adapter);
                        
                    } else {
                        // Fallback data
                        List<String> fallbackLocations = new ArrayList<>();
                        fallbackLocations.add("All");
                        fallbackLocations.add("Mumbai Central");
                        fallbackLocations.add("Andheri West");
                        fallbackLocations.add("Bandra West");
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackLocations);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        branchLocationSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing branch locations: " + e.getMessage());
                }
            },
            error -> {
                Log.e(TAG, "Error loading branch locations: " + error.getMessage());
                // Fallback data
                List<String> fallbackLocations = new ArrayList<>();
                fallbackLocations.add("All");
                fallbackLocations.add("Mumbai Central");
                fallbackLocations.add("Andheri West");
                fallbackLocations.add("Bandra West");
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fallbackLocations);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchLocationSpinner.setAdapter(adapter);
            }
        );
        
        requestQueue.add(request);
    }

    private void loadAgentData() {
        // Show loading state
        showLoadingState();
        
        String url = BASE_URL + "get_all_agent_data_simple.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                // Process data in background thread
                executorService.execute(() -> {
                    try {
                        String status = response.getString("status");
                        if ("success".equals(status)) {
                            JSONArray data = response.getJSONArray("data");
                            parseAgentData(data);
                        } else {
                            // Show sample data if API fails
                            displaySampleData();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing agent data: " + e.getMessage());
                        displaySampleData();
                    }
                });
            },
            error -> {
                Log.e(TAG, "Error loading agent data: " + error.getMessage());
                displaySampleData();
            }
        );
        
        requestQueue.add(request);
    }

    private void parseAgentData(JSONArray response) {
        Log.d(TAG, "parseAgentData started with " + response.length() + " items");
        List<AgentItem> newAgentList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject agent = response.getJSONObject(i);
                
                // Log the raw data for debugging
                Log.d(TAG, "Raw agent data: " + agent.toString());
                
                // Try to get the data with fallbacks for different field names
                String fullName = agent.optString("full_name", agent.optString("Full_name", ""));
                String companyName = agent.optString("company_name", agent.optString("Company_name", ""));
                String phoneNumber = agent.optString("Phone_number", agent.optString("phone_number", ""));
                String alternativePhone = agent.optString("alternative_Phone_number", agent.optString("alternative_number", ""));
                String email = agent.optString("email_id", agent.optString("email", ""));
                String partnerType = agent.optString("partnerType", agent.optString("partner_type", ""));
                String state = agent.optString("state", agent.optString("branch_state", ""));
                String location = agent.optString("location", agent.optString("branch_location", ""));
                String address = agent.optString("address", "");
                String visitingCard = agent.optString("visiting_card", "");
                String createdUser = agent.optString("created_user", "");
                String createdBy = agent.optString("createdBy", agent.optString("created_by", ""));
                
                AgentItem agentItem = new AgentItem(
                    fullName,
                    companyName,
                    phoneNumber,
                    alternativePhone,
                    email,
                    partnerType,
                    state,
                    location,
                    address,
                    visitingCard,
                    createdUser,
                    createdBy
                );
                newAgentList.add(agentItem);
                Log.d(TAG, "Added agent: " + agentItem.getFullName());
            }
            
            // Update UI on main thread
            mainHandler.post(() -> {
                agentList.clear();
                agentList.addAll(newAgentList);
                agentAdapter.notifyDataSetChanged();
                
                Log.d(TAG, "Agent list updated, total items: " + agentList.size());
                
                if (newAgentList.isEmpty()) {
                    Log.w(TAG, "No agent data found");
                    Toast.makeText(this, "No agent data found", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Successfully loaded " + newAgentList.size() + " agents");
                    Toast.makeText(this, "Found " + newAgentList.size() + " agents!", Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing agent data: " + e.getMessage());
            mainHandler.post(this::displaySampleData);
        }
    }

    private void showLoadingState() {
        agentList.clear();
        agentAdapter.notifyDataSetChanged();
    }

    private void displayAllAgents() {
        agentAdapter.notifyDataSetChanged();
    }

    // Maps to store name to ID mappings
    private Map<String, String> partnerTypeMap = new HashMap<>();
    private Map<String, String> stateMap = new HashMap<>();
    private Map<String, String> locationMap = new HashMap<>();

    private String getPartnerTypeId(String partnerTypeName) {
        if ("All".equals(partnerTypeName)) return "0";
        return partnerTypeMap.getOrDefault(partnerTypeName, "0");
    }

    private String getStateId(String stateName) {
        if ("All".equals(stateName)) return "0";
        return stateMap.getOrDefault(stateName, "0");
    }

    private String getLocationId(String locationName) {
        if ("All".equals(locationName)) return "0";
        return locationMap.getOrDefault(locationName, "0");
    }

    private void filterAgentsFromServer(String partnerTypeId, String stateId, String locationId) {
        String url = BASE_URL + "filter_agents.php";
        
        // Create JSON object with filter parameters
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("partner_type_id", partnerTypeId);
            jsonBody.put("state_id", stateId);
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
            response -> {
                // Process data in background thread
                executorService.execute(() -> {
                    try {
                        String status = response.getString("status");
                        if ("success".equals(status)) {
                            JSONArray data = response.getJSONArray("data");
                            parseFilteredAgentData(data);
                        } else {
                            mainHandler.post(() -> 
                                Toast.makeText(MyAgentActivity.this, "No agents found with selected filters", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing filtered agent data: " + e.getMessage());
                        mainHandler.post(() -> 
                            Toast.makeText(MyAgentActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            },
            error -> {
                Log.e(TAG, "Error filtering agents: " + error.getMessage());
                Toast.makeText(MyAgentActivity.this, "Error filtering agents", Toast.LENGTH_SHORT).show();
            }
        );
        
        requestQueue.add(request);
    }

    private void parseFilteredAgentData(JSONArray response) {
        Log.d(TAG, "parseFilteredAgentData started with " + response.length() + " items");
        List<AgentItem> filteredList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject agent = response.getJSONObject(i);
                AgentItem agentItem = new AgentItem(
                    agent.optString("full_name", ""),
                    agent.optString("company_name", ""),
                    agent.optString("Phone_number", ""),
                    agent.optString("alternative_Phone_number", ""),
                    agent.optString("email_id", ""),
                    agent.optString("partnerType", ""),
                    agent.optString("state", ""),
                    agent.optString("location", ""),
                    agent.optString("address", ""),
                    agent.optString("visiting_card", ""),
                    agent.optString("created_user", ""),
                    agent.optString("createdBy", "")
                );
                filteredList.add(agentItem);
                Log.d(TAG, "Added filtered agent: " + agentItem.getFullName());
            }
            
            // Update UI on main thread
            mainHandler.post(() -> {
                agentList.clear();
                agentList.addAll(filteredList);
                agentAdapter.notifyDataSetChanged();
                
                Log.d(TAG, "Filtered agent list updated, total items: " + filteredList.size());
                
                if (filteredList.isEmpty()) {
                    Log.w(TAG, "No filtered agent data found");
                    Toast.makeText(this, "No agents found with selected filters", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Successfully filtered " + filteredList.size() + " agents");
                    Toast.makeText(this, "Found " + filteredList.size() + " matching agents!", Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing filtered agent data: " + e.getMessage());
            mainHandler.post(() -> 
                Toast.makeText(this, "Error parsing filtered data", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void filterAgents(String agentType, String branchState, String branchLocation) {
        List<AgentItem> filteredList = new ArrayList<>();
        
        for (AgentItem agent : agentList) {
            boolean matchesAgentType = agentType.equals("All") || agent.getPartnerType().equals(agentType);
            boolean matchesBranchState = branchState.equals("All") || agent.getState().equals(branchState);
            boolean matchesBranchLocation = branchLocation.equals("All") || agent.getLocation().equals(branchLocation);
            
            if (matchesAgentType && matchesBranchState && matchesBranchLocation) {
                filteredList.add(agent);
            }
        }
        
        agentList.clear();
        agentList.addAll(filteredList);
        agentAdapter.notifyDataSetChanged();
        
        Toast.makeText(this, "Found " + filteredList.size() + " matching agents", Toast.LENGTH_SHORT).show();
    }

    private void displaySampleData() {
        // Create sample data for testing
        List<AgentItem> sampleList = new ArrayList<>();
        sampleList.add(new AgentItem("John Doe", "ABC Corp", "1234567890", "0987654321", "john@abc.com", "Business", "Maharashtra", "Mumbai", "Mumbai Address", "", "Admin", "Admin"));
        sampleList.add(new AgentItem("Jane Smith", "XYZ Ltd", "9876543210", "0123456789", "jane@xyz.com", "Individual", "Delhi", "New Delhi", "Delhi Address", "", "Admin", "Admin"));
        
        agentList.clear();
        agentList.addAll(sampleList);
        agentAdapter.notifyDataSetChanged();
        
        Toast.makeText(this, "Loaded sample data", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 

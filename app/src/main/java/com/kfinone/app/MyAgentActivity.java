package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import android.content.Intent;

public class MyAgentActivity extends AppCompatActivity {
    private static final String TAG = "MyAgentActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // UI Elements
    private TextView backButton;
    private Spinner agentTypeSpinner, branchStateSpinner, branchLocationSpinner;
    private Button filterButton, resetButton, loadAllButton;
    private LinearLayout tableContent;
    private RequestQueue requestQueue;

    // Data
    private List<AgentItem> agentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_agent);

        initializeViews();
        setupVolley();
        setupClickListeners();
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
        tableContent = findViewById(R.id.tableContent);
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
        // Show loading message
        tableContent.removeAllViews();
        TextView loadingText = new TextView(this);
        loadingText.setText("Loading agent data...");
        loadingText.setTextSize(16);
        loadingText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        loadingText.setGravity(android.view.Gravity.CENTER);
        loadingText.setPadding(20, 50, 20, 50);
        tableContent.addView(loadingText);
        
        String url = BASE_URL + "get_all_agent_data_simple.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
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
            agentList.clear();
            agentList.addAll(newAgentList);
            displayAllAgents();
            
            Log.d(TAG, "Agent list updated, total items: " + agentList.size());
            
            if (newAgentList.isEmpty()) {
                Log.w(TAG, "No agent data found");
                Toast.makeText(this, "No agent data found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Successfully loaded " + newAgentList.size() + " agents");
                Toast.makeText(this, "Found " + newAgentList.size() + " agents!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing agent data: " + e.getMessage());
            displaySampleData();
        }
    }

    private void displayAllAgents() {
        tableContent.removeAllViews();
        addTableHeader();
        for (AgentItem agent : agentList) {
            addTableRow(agent);
        }
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
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        parseFilteredAgentData(data);
                    } else {
                        Toast.makeText(MyAgentActivity.this, "No agents found with selected filters", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing filtered agent data: " + e.getMessage());
                    Toast.makeText(MyAgentActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
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
            
            // Display filtered results
            tableContent.removeAllViews();
            addTableHeader();
            for (AgentItem agent : filteredList) {
                addTableRow(agent);
            }
            
            Log.d(TAG, "Filtered agent list updated, total items: " + filteredList.size());
            
            if (filteredList.isEmpty()) {
                Log.w(TAG, "No filtered agent data found");
                Toast.makeText(this, "No agents found with selected filters", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Successfully filtered " + filteredList.size() + " agents");
                Toast.makeText(this, "Found " + filteredList.size() + " matching agents!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing filtered agent data: " + e.getMessage());
            Toast.makeText(this, "Error parsing filtered data", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterAgents(String agentType, String branchState, String branchLocation) {
        tableContent.removeAllViews();
        List<AgentItem> filteredList = new ArrayList<>();
        
        for (AgentItem agent : agentList) {
            boolean matchesAgentType = agentType.equals("All") || agent.getPartnerType().equals(agentType);
            boolean matchesBranchState = branchState.equals("All") || agent.getState().equals(branchState);
            boolean matchesBranchLocation = branchLocation.equals("All") || agent.getLocation().equals(branchLocation);
            
            if (matchesAgentType && matchesBranchState && matchesBranchLocation) {
                filteredList.add(agent);
            }
        }
        
        tableContent.removeAllViews();
        addTableHeader();
        for (AgentItem agent : filteredList) {
            addTableRow(agent);
        }
        
        Toast.makeText(this, "Found " + filteredList.size() + " matching agents", Toast.LENGTH_SHORT).show();
    }

    private void addTableHeader() {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(8, 12, 8, 12);
        headerLayout.setBackgroundResource(R.drawable.table_header_background);

        // Header columns
        String[] headers = {"Name", "Company", "Phone", "Type", "State", "Location", "Action"};
        float[] weights = {1.5f, 1.5f, 1.2f, 1.2f, 1.2f, 1.2f, 1.0f};

        for (int i = 0; i < headers.length; i++) {
            TextView headerText = new TextView(this);
            LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
            headerParams.weight = weights[i];
            headerText.setLayoutParams(headerParams);
            headerText.setText(headers[i]);
            headerText.setTextSize(14);
            headerText.setTextColor(getResources().getColor(android.R.color.white));
            headerText.setGravity(android.view.Gravity.CENTER);
            headerText.setTypeface(null, android.graphics.Typeface.BOLD);
            headerLayout.addView(headerText);
        }

        tableContent.addView(headerLayout);
    }

    private void addTableRow(AgentItem agent) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(8, 12, 8, 12);
        
        // Alternate row colors for better readability
        if ((tableContent.getChildCount() - 1) % 2 == 0) { // -1 to account for header
            rowLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            rowLayout.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }

        // Add border to each row
        rowLayout.setPadding(8, 12, 8, 12);
        rowLayout.setBackgroundResource(R.drawable.table_row_background);

        // Full Name
        TextView fullNameText = createTableCell(agent.getFullName(), 1.5f);
        rowLayout.addView(fullNameText);

        // Company Name
        TextView companyText = createTableCell(agent.getCompanyName(), 1.5f);
        rowLayout.addView(companyText);

        // Mobile
        TextView mobileText = createTableCell(agent.getPhoneNumber(), 1.2f);
        rowLayout.addView(mobileText);

        // Agent Type
        TextView agentTypeText = createTableCell(agent.getPartnerType(), 1.2f);
        rowLayout.addView(agentTypeText);

        // Branch State
        TextView branchStateText = createTableCell(agent.getState(), 1.2f);
        rowLayout.addView(branchStateText);

        // Branch Location
        TextView branchLocationText = createTableCell(agent.getLocation(), 1.2f);
        rowLayout.addView(branchLocationText);

        // Action Button
        Button actionButton = new Button(this);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT);
        actionParams.weight = 1.0f;
        actionParams.setMargins(4, 4, 4, 4);
        actionButton.setLayoutParams(actionParams);
        actionButton.setText("View");
        actionButton.setTextSize(11);
        actionButton.setPadding(8, 6, 8, 6);
        actionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        actionButton.setTextColor(getResources().getColor(android.R.color.white));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgentDetails(agent);
            }
        });
        rowLayout.addView(actionButton);

        tableContent.addView(rowLayout);
    }

    private TextView createTableCell(String text, float weight) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = weight;
        params.setMargins(4, 4, 4, 4);
        textView.setLayoutParams(params);
        textView.setText(text.isEmpty() ? "-" : text);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(4, 4, 4, 4);
        return textView;
    }

    private void showAgentDetails(AgentItem agent) {
        // Create a dialog to show agent details
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Agent Details - " + agent.getFullName());
        
        String details = "Name: " + agent.getFullName() + "\n" +
                        "Company: " + agent.getCompanyName() + "\n" +
                        "Phone: " + agent.getPhoneNumber() + "\n" +
                        "Alternative Phone: " + agent.getAlternativePhoneNumber() + "\n" +
                        "Email: " + agent.getEmailId() + "\n" +
                        "Type: " + agent.getPartnerType() + "\n" +
                        "State: " + agent.getState() + "\n" +
                        "Location: " + agent.getLocation() + "\n" +
                        "Address: " + agent.getAddress() + "\n" +
                        "Created By: " + agent.getCreatedBy();
        
        builder.setMessage(details);
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void displaySampleData() {
        // Clear existing data
        tableContent.removeAllViews();
        
        // Add sample data for testing
        addTableHeader();
        
        // Sample agent data with all required parameters
        AgentItem sampleAgent1 = new AgentItem("John Doe", "ABC Corporation", "9876543210", "9876543211", "john@abc.com", "Business", "Maharashtra", "Mumbai Central", "123 Andheri West, Mumbai", "", "Admin", "System");
        AgentItem sampleAgent2 = new AgentItem("Jane Smith", "XYZ Industries", "8765432109", "8765432110", "jane@xyz.com", "Individual", "Delhi", "Connaught Place", "456 Bandra West, Delhi", "", "Admin", "System");
        AgentItem sampleAgent3 = new AgentItem("Bob Johnson", "Tech Solutions Ltd", "7654321098", "7654321099", "bob@tech.com", "Business", "Karnataka", "Bangalore Central", "789 Juhu Beach, Bangalore", "", "Admin", "System");
        
        addTableRow(sampleAgent1);
        addTableRow(sampleAgent2);
        addTableRow(sampleAgent3);
    }

    @Override
    public void onBackPressed() {
        // Check if we came from Director panel
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            // Navigate back to Director Agent Activity
            Intent intent = new Intent(this, DirectorAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default behavior
            super.onBackPressed();
        }
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
} 

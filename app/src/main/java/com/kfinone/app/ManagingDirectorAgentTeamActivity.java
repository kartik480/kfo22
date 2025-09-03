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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorAgentTeamActivity extends AppCompatActivity {

    private static final String TAG = "ManagingDirectorAgentTeam";

    // UI Elements
    private AutoCompleteTextView userDropdown;
    private Button showDataButton;
    private Button resetButton;
    private RecyclerView agentTeamRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextView dataCountText;

    // Data
    private List<AgentTeamUser> teamUsers = new ArrayList<>();
    private List<AgentData> allAgents = new ArrayList<>();
    private List<AgentData> filteredAgents = new ArrayList<>();
    private AgentAdapter agentAdapter;
    private ArrayAdapter<String> userDropdownAdapter;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    // Volley request queue
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_agent_team);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadTeamUsers();
        loadAgentData();
    }

    private void initializeViews() {
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        agentTeamRecyclerView = findViewById(R.id.agentTeamRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        dataCountText = findViewById(R.id.dataCountText);

        // Setup RecyclerView
        agentAdapter = new AgentAdapter(filteredAgents);
        agentTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        agentTeamRecyclerView.setAdapter(agentAdapter);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Show Data button
        showDataButton.setOnClickListener(v -> filterData());

        // Reset button
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void loadTeamUsers() {
        String url = "https://emp.kfinone.com/mobile/api/get_all_users_for_dropdown.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "Team users response: " + response.toString());
                    parseTeamUsersData(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading team users: " + error.getMessage());
                    Toast.makeText(ManagingDirectorAgentTeamActivity.this, "Error loading team users", Toast.LENGTH_SHORT).show();
                }
            }
        );

        requestQueue.add(request);
    }

    private void parseTeamUsersData(JSONArray response) {
        teamUsers.clear();
        try {
            Log.d(TAG, "Parsing team users data. Response length: " + response.length());
            for (int i = 0; i < response.length(); i++) {
                JSONObject user = response.getJSONObject(i);
                AgentTeamUser teamUser = new AgentTeamUser(
                    user.optString("id", ""),
                    user.optString("username", ""),
                    user.optString("firstName", ""),
                    user.optString("lastName", ""),
                    user.optString("designationId", ""),
                    user.optString("designationName", ""),
                    user.optString("emailId", ""),
                    user.optString("mobile", ""),
                    user.optString("status", "")
                );
                teamUsers.add(teamUser);
                Log.d(TAG, "Added user: " + teamUser.getDisplayName());
            }
            Log.d(TAG, "Total team users loaded: " + teamUsers.size());
            setupDropdown();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing team users data: " + e.getMessage());
        }
    }

    private void setupDropdown() {
        List<String> dropdownItems = new ArrayList<>();
        dropdownItems.add("All Users");
        
        for (AgentTeamUser user : teamUsers) {
            dropdownItems.add(user.getDisplayName());
        }

        Log.d(TAG, "Setting up dropdown with " + dropdownItems.size() + " items");
        Log.d(TAG, "Dropdown items: " + dropdownItems.toString());

        userDropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownItems);
        userDropdown.setAdapter(userDropdownAdapter);
        
        // Enable dropdown functionality
        userDropdown.setThreshold(0); // Show dropdown immediately when clicked
        userDropdown.setOnClickListener(v -> {
            Log.d(TAG, "Dropdown clicked, showing dropdown");
            userDropdown.showDropDown();
        });
        
        // Handle item selection
        userDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "User selected: " + selectedItem);
            userDropdown.setText(selectedItem);
            userDropdown.dismissDropDown();
        });
    }

    private void loadAgentData() {
        String url = "https://emp.kfinone.com/mobile/api/get_md_agent_team_data.php";
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "Agent data response: " + response.toString());
                    parseAgentData(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading agent data: " + error.getMessage());
                    Toast.makeText(ManagingDirectorAgentTeamActivity.this, "Error loading agent data", Toast.LENGTH_SHORT).show();
                }
            }
        );

        requestQueue.add(request);
    }

    private void parseAgentData(JSONArray response) {
        allAgents.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject agent = response.getJSONObject(i);
                AgentData agentData = new AgentData(
                    agent.optString("id", ""),
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
                    agent.optString("createdBy", ""),
                    agent.optString("status", ""),
                    agent.optString("created_at", ""),
                    agent.optString("updated_at", ""),
                    agent.optString("creator_first_name", ""),
                    agent.optString("creator_last_name", ""),
                    agent.optString("creator_username", ""),
                    agent.optString("creator_full_name", "")
                );
                allAgents.add(agentData);
            }
            filteredAgents.clear();
            filteredAgents.addAll(allAgents);
            updateDataDisplay();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing agent data: " + e.getMessage());
        }
    }

    private void filterData() {
        String selectedUser = userDropdown.getText().toString().trim();
        
        if (selectedUser.isEmpty() || selectedUser.equals("All Users")) {
            filteredAgents.clear();
            filteredAgents.addAll(allAgents);
        } else {
            AgentTeamUser selectedTeamUser = findUserByDisplayName(selectedUser);
            if (selectedTeamUser != null) {
                filteredAgents.clear();
                for (AgentData agent : allAgents) {
                    if (agent.getCreatedBy().equals(selectedTeamUser.getUsername())) {
                        filteredAgents.add(agent);
                    }
                }
            }
        }
        
        updateDataDisplay();
    }

    private AgentTeamUser findUserByDisplayName(String displayName) {
        for (AgentTeamUser user : teamUsers) {
            if (user.getDisplayName().equals(displayName)) {
                return user;
            }
        }
        return null;
    }

    private void resetFilters() {
        userDropdown.setText("");
        filteredAgents.clear();
        filteredAgents.addAll(allAgents);
        updateDataDisplay();
    }

    private void updateDataDisplay() {
        agentAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Agents: " + filteredAgents.size());
        
        if (filteredAgents.isEmpty()) {
            agentTeamRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            agentTeamRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    // Inner class for Agent Team User data
    public static class AgentTeamUser {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String designationId;
        private String designationName;
        private String emailId;
        private String mobile;
        private String status;

        public AgentTeamUser(String id, String username, String firstName, String lastName,
                           String designationId, String designationName, String emailId,
                           String mobile, String status) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.designationId = designationId;
            this.designationName = designationName;
            this.emailId = emailId;
            this.mobile = mobile;
            this.status = status;
        }

        public String getDisplayName() {
            return firstName + " " + lastName + " (" + designationName + " - ID: " + designationId + ")";
        }

        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getDesignationId() { return designationId; }
        public String getDesignationName() { return designationName; }
        public String getEmailId() { return emailId; }
        public String getMobile() { return mobile; }
        public String getStatus() { return status; }
    }
}

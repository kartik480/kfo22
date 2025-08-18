package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorEmpTeamActivity extends AppCompatActivity {
    private static final String TAG = "ManagingDirectorEmpTeamActivity";

    // UI Elements
    private Spinner userSpinner;
    private Button showDataButton;
    private Button resetButton;
    private TextView employeeTeamHeading;
    private TextView teamDataHeading;
    private TextView noDataMessage;
    private RecyclerView teamRecyclerView;
    
    // Data for dropdown
    private List<UserItem> userList;
    
    // Data for team members
    private List<TeamMember> teamMembers;
    private TeamMemberAdapter teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_emp_team);

        initializeViews();
        setupToolbar();
        setupClickListeners();
        loadUsersForDropdown();
        
        // Show all available users by default
        showAllAvailableUsers();
    }

    private void initializeViews() {
        // Initialize UI elements
        userSpinner = findViewById(R.id.userSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        employeeTeamHeading = findViewById(R.id.employeeTeamHeading);
        teamDataHeading = findViewById(R.id.teamDataHeading);
        noDataMessage = findViewById(R.id.noDataMessage);
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        
        // Initialize user list
        userList = new ArrayList<>();
        
        // Initialize team members list and adapter
        teamMembers = new ArrayList<>();
        teamAdapter = new TeamMemberAdapter(teamMembers, this);
        
        // Setup RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamRecyclerView.setAdapter(teamAdapter);
        
        // Show initial state - available users will be loaded
        teamDataHeading.setVisibility(View.VISIBLE);
        teamRecyclerView.setVisibility(View.VISIBLE);
        noDataMessage.setVisibility(View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Managing Director Employee Team");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupClickListeners() {
        showDataButton.setOnClickListener(v -> {
            int selectedPosition = userSpinner.getSelectedItemPosition();
            if (selectedPosition > 0 && selectedPosition < userList.size()) {
                UserItem selectedUser = userList.get(selectedPosition - 1); // -1 because first item is "Select User"
                fetchEmployeeTeamData(selectedUser.id, selectedUser.fullName);
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            userSpinner.setSelection(0);
            // Instead of hiding data, show all available users again
            displayAllAvailableUsers();
            Toast.makeText(this, "Showing all available users", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUsersForDropdown() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading Managing Director designated users for dropdown...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/ManagingDirectorUsers.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Managing Director designated users dropdown response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Managing Director designated users dropdown response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        List<String> userNames = new ArrayList<>();
                        userNames.add("Select User");
                        userList.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            String fullName = user.getString("fullName");
                            String userId = user.getString("id");
                            String designation = user.getString("designation_name");
                            
                            // Add designation to the display name for better identification
                            String displayName = fullName + " (" + designation + ")";
                            userNames.add(displayName);
                            userList.add(new UserItem(userId, fullName, designation));
                            
                            Log.d(TAG, "Added to Managing Director dropdown: " + displayName);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                ManagingDirectorEmpTeamActivity.this,
                                android.R.layout.simple_spinner_item,
                                userNames
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            userSpinner.setAdapter(spinnerAdapter);
                            Log.d(TAG, "Loaded " + userNames.size() + " Managing Director designated users in dropdown");
                            Toast.makeText(ManagingDirectorEmpTeamActivity.this, 
                                "Loaded " + (userNames.size() - 1) + " designated users", Toast.LENGTH_SHORT).show();
                            
                            // After loading users, automatically show all available users
                            if (userList.size() > 0) {
                                showAllAvailableUsers();
                            }
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Error loading users: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading Managing Director designated users for dropdown: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Error loading users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void fetchEmployeeTeamData(String managerId, String managerName) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching employee team data for manager ID: " + managerId + " (" + managerName + ")");
                URL url = new URL("https://emp.kfinone.com/mobile/api/ManagingDirector.php?manager_id=" + managerId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Managing Director team data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Managing Director team data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        runOnUiThread(() -> {
                            displayTeamData(data, managerName);
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception fetching Managing Director team data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(ManagingDirectorEmpTeamActivity.this, "Error fetching team data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void displayTeamData(JSONArray data, String managerName) {
        try {
            teamMembers.clear();
            
            for (int i = 0; i < data.length(); i++) {
                JSONObject member = data.getJSONObject(i);
                TeamMember teamMember = new TeamMember(
                    member.getString("id"),
                    member.getString("firstName"),
                    member.getString("lastName"),
                    member.getString("designation_name"),
                    member.getString("email_id"),
                    member.getString("mobile"),
                    member.getString("employee_no"),
                    managerName
                );
                teamMembers.add(teamMember);
            }
            
            teamAdapter.notifyDataSetChanged();
            
            if (teamMembers.isEmpty()) {
                showNoDataMessage();
            } else {
                showTeamData();
                // Update heading to show team members for specific manager
                teamDataHeading.setText("Team Members for " + managerName + " (" + teamMembers.size() + ")");
                Toast.makeText(this, "Found " + teamMembers.size() + " team members for " + managerName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing team data: " + e.getMessage(), e);
            Toast.makeText(this, "Error parsing team data", Toast.LENGTH_LONG).show();
        }
    }

    private void showTeamData() {
        teamDataHeading.setVisibility(View.VISIBLE);
        teamRecyclerView.setVisibility(View.VISIBLE);
        noDataMessage.setVisibility(View.GONE);
    }

    private void showNoDataMessage() {
        teamDataHeading.setVisibility(View.GONE);
        teamRecyclerView.setVisibility(View.GONE);
        noDataMessage.setVisibility(View.VISIBLE);
    }

    private void hideTeamData() {
        // Instead of hiding everything, show available users
        if (userList.size() > 0) {
            displayAllAvailableUsers();
        } else {
            showLoadingState();
        }
    }

    private void showAllAvailableUsers() {
        // Show all available users by default
        if (userList.size() > 0) {
            // Display all users as available users
            displayAllAvailableUsers();
        } else {
            // If no users loaded yet, show loading state
            showLoadingState();
        }
    }

    private void showLoadingState() {
        teamDataHeading.setText("Loading Available Users...");
        teamDataHeading.setVisibility(View.VISIBLE);
        teamRecyclerView.setVisibility(View.GONE);
        noDataMessage.setVisibility(View.GONE);
    }

    private void displayAllAvailableUsers() {
        try {
            teamMembers.clear();
            
            // Add all users from the dropdown as available users
            for (UserItem user : userList) {
                TeamMember teamMember = new TeamMember(
                    user.id,
                    user.fullName.split(" ")[0], // First name
                    user.fullName.split(" ").length > 1 ? user.fullName.split(" ")[1] : "", // Last name
                    user.designation,
                    "", // Email - not available in dropdown data
                    "", // Mobile - not available in dropdown data
                    "", // Employee no - not available in dropdown data
                    "Available User" // Manager name placeholder
                );
                teamMembers.add(teamMember);
            }
            
            teamAdapter.notifyDataSetChanged();
            
            if (teamMembers.isEmpty()) {
                showNoDataMessage();
            } else {
                showTeamData();
                // Update the heading to show "Available Users" instead of "Team Members"
                teamDataHeading.setText("Available Users (" + teamMembers.size() + ")");
                Toast.makeText(this, "Showing " + teamMembers.size() + " available users", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying all available users: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying available users", Toast.LENGTH_LONG).show();
        }
    }


    // User Item data class for dropdown
    private static class UserItem {
        String id;
        String fullName;
        String designation;

        UserItem(String id, String fullName, String designation) {
            this.id = id;
            this.fullName = fullName;
            this.designation = designation;
        }
    }
} 
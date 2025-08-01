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
            hideTeamData();
            Toast.makeText(this, "Data reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUsersForDropdown() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading designated users for dropdown...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_emp_panel_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Designated users dropdown response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Designated users dropdown response: " + responseString);

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
                            
                            Log.d(TAG, "Added to dropdown: " + displayName);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                ManagingDirectorEmpTeamActivity.this,
                                android.R.layout.simple_spinner_item,
                                userNames
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            userSpinner.setAdapter(spinnerAdapter);
                            Log.d(TAG, "Loaded " + userNames.size() + " designated users in dropdown");
                            Toast.makeText(ManagingDirectorEmpTeamActivity.this, 
                                "Loaded " + (userNames.size() - 1) + " designated users", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Exception loading designated users for dropdown: " + e.getMessage(), e);
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
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_team_members.php?manager_id=" + managerId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Employee team data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Employee team data response: " + responseString);

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
                Log.e(TAG, "Exception fetching employee team data: " + e.getMessage(), e);
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
        teamDataHeading.setVisibility(View.GONE);
        teamRecyclerView.setVisibility(View.GONE);
        noDataMessage.setVisibility(View.GONE);
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
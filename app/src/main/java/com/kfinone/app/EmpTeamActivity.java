package com.kfinone.app;

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
import androidx.appcompat.app.AppCompatActivity;
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

public class EmpTeamActivity extends AppCompatActivity {
    private static final String TAG = "EmpTeamActivity";

    // UI Elements
    private Spinner userSpinner;
    private Button showDataButton;
    private Button resetButton;
    private RecyclerView teamRecyclerView;
    private TeamAdapter adapter;
    private List<TeamMember> teamList;
    
    // Data for dropdown
    private List<UserItem> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_team);

        initializeViews();
        setupClickListeners();
        loadUsersForDropdown();
        loadAllTeamMembers(); // Automatically load all team members
    }

    private void initializeViews() {
        // Initialize Spinner
        userSpinner = findViewById(R.id.userSpinner);
        
        // Initialize Buttons
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        
        // Initialize RecyclerView
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        teamList = new ArrayList<>();
        adapter = new TeamAdapter(teamList);
        teamRecyclerView.setAdapter(adapter);
        
        // Initialize user list
        userList = new ArrayList<>();
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });

        showDataButton.setOnClickListener(v -> {
            int selectedPosition = userSpinner.getSelectedItemPosition();
            if (selectedPosition > 0 && selectedPosition < userList.size()) {
                UserItem selectedUser = userList.get(selectedPosition - 1); // -1 because first item is "Select User"
                fetchAgentData(selectedUser.id, selectedUser.fullName);
            } else {
                // If no user is selected, show all team members
                loadAllTeamMembers();
            }
        });

        resetButton.setOnClickListener(v -> {
            userSpinner.setSelection(0);
            teamList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Data reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUsersForDropdown() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading designated users for dropdown (Chief Business Officer, Regional Business Head, Director)...");
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
                            
                            // Debug logging
                            Log.d(TAG, "Processing user " + i + ": ID=" + userId + ", Name=" + fullName + ", Designation=" + designation);
                            
                            // Add designation to the display name for better identification
                            String displayName = fullName + " (" + designation + ")";
                            userNames.add(displayName);
                            userList.add(new UserItem(userId, fullName, designation));
                            
                            Log.d(TAG, "Added to dropdown: " + displayName);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                EmpTeamActivity.this,
                                android.R.layout.simple_spinner_item,
                                userNames
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            userSpinner.setAdapter(spinnerAdapter);
                            Log.d(TAG, "Loaded " + userNames.size() + " designated users in dropdown");
                            Toast.makeText(EmpTeamActivity.this, 
                                "Loaded " + (userNames.size() - 1) + " designated users", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(EmpTeamActivity.this, "Error loading users: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(EmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading designated users for dropdown: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(EmpTeamActivity.this, "Error loading users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadAllTeamMembers() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading all team members for Chief Business Officer, Regional Business Head, and Director...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_all_team_members.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "All team members response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "All team members response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        teamList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject member = data.getJSONObject(i);
                            teamList.add(new TeamMember(
                                member.getString("employee_no"),
                                member.getString("fullName"),
                                member.getString("mobile"),
                                member.getString("email_id"),
                                member.getString("manager_name"), // Manager name from API
                                member.getString("designation_name")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated team list with " + teamList.size() + " members");
                            Toast.makeText(EmpTeamActivity.this, 
                                "Showing " + teamList.size() + " team members", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(EmpTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(EmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading all team members: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(EmpTeamActivity.this, "Error loading team members: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void fetchTeamData(String managerId, String managerName) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching team data for manager ID: " + managerId + " (" + managerName + ")");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_team_members.php?manager_id=" + managerId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Team data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Team data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        teamList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject member = data.getJSONObject(i);
                            teamList.add(new TeamMember(
                                member.getString("employee_no"),
                                member.getString("fullName"),
                                member.getString("mobile"),
                                member.getString("email_id"),
                                managerName, // Reporting to the selected manager
                                member.getString("designation_name")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated team list with " + teamList.size() + " members");
                            Toast.makeText(EmpTeamActivity.this, 
                                "Showing " + teamList.size() + " team members for " + managerName, Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(EmpTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(EmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception fetching team data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(EmpTeamActivity.this, "Error fetching team data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void fetchAgentData(String userId, String userName) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching agent data for user ID: " + userId + " (" + userName + ")");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_agent_data_by_user.php?user_id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Agent data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Agent data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        teamList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject agent = data.getJSONObject(i);
                            teamList.add(new TeamMember(
                                agent.getString("id"),
                                agent.getString("full_name"),
                                agent.getString("Phone_number"),
                                agent.getString("email_id"),
                                agent.getString("created_by_name"), // Created by user name
                                agent.getString("company_name") // Using company name as designation
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated agent list with " + teamList.size() + " agents");
                            Toast.makeText(EmpTeamActivity.this, 
                                "Showing " + teamList.size() + " agents for " + userName, Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(EmpTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(EmpTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception fetching agent data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(EmpTeamActivity.this, "Error fetching agent data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
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

    // Team Member data class
    private static class TeamMember {
        String username;
        String fullName;
        String mobile;
        String email;
        String reportingTo;
        String designation;

        TeamMember(String username, String fullName, String mobile, String email, String reportingTo, String designation) {
            this.username = username;
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.reportingTo = reportingTo;
            this.designation = designation;
        }
    }

    // Team Adapter for RecyclerView
    private class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
        private List<TeamMember> teamMembers;

        TeamAdapter(List<TeamMember> teamMembers) {
            this.teamMembers = teamMembers;
            Log.d(TAG, "Team adapter created with " + teamMembers.size() + " members");
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emp_team_member, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TeamMember member = teamMembers.get(position);
            holder.usernameText.setText(member.username);
            holder.fullNameText.setText(member.fullName);
            holder.mobileText.setText(member.mobile);
            holder.emailText.setText(member.email);
            holder.reportingToText.setText(member.reportingTo);
            holder.designationText.setText(member.designation);

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(EmpTeamActivity.this, 
                    "Edit " + member.fullName, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(EmpTeamActivity.this, 
                    "Delete " + member.fullName, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return teamMembers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView usernameText;
            TextView fullNameText;
            TextView mobileText;
            TextView emailText;
            TextView reportingToText;
            TextView designationText;
            Button editButton;
            Button deleteButton;

            ViewHolder(View view) {
                super(view);
                usernameText = view.findViewById(R.id.usernameText);
                fullNameText = view.findViewById(R.id.fullNameText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                reportingToText = view.findViewById(R.id.reportingToText);
                designationText = view.findViewById(R.id.designationText);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 
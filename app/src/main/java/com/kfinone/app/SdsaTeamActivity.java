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

public class SdsaTeamActivity extends AppCompatActivity {
    private static final String TAG = "SdsaTeamActivity";

    // UI Elements
    private Spinner userDropdown;
    private Button showDataButton;
    private Button resetButton;
    private RecyclerView teamRecyclerView;
    private TeamAdapter adapter;
    private List<TeamMember> teamList;
    private List<UserItem> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdsa_team);

        initializeViews();
        setupClickListeners();
        loadUserDropdown();
        loadAllSdsaTeamMembers(); // Load all team members immediately
    }

    private void initializeViews() {
        // Initialize UI elements
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        
        // Initialize RecyclerView
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        teamList = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new TeamAdapter(teamList);
        teamRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });

        // Show Data button
        showDataButton.setOnClickListener(v -> {
            if (userDropdown.getSelectedItemPosition() > 0) {
                UserItem selectedUser = userList.get(userDropdown.getSelectedItemPosition() - 1);
                loadTeamData(selectedUser.id);
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            }
        });

        // Reset button
        resetButton.setOnClickListener(v -> {
            teamList.clear();
            adapter.notifyDataSetChanged();
            userDropdown.setSelection(0);
            Toast.makeText(this, "Data cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserDropdown() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading user dropdown data...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_sdsa_users_dropdown.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "User dropdown response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "User dropdown response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        userList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            userList.add(new UserItem(
                                user.getString("id"),
                                user.getString("fullName")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            setupUserDropdown();
                            Log.d(TAG, "Updated user dropdown with " + userList.size() + " users");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(SdsaTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(SdsaTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading user dropdown: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(SdsaTeamActivity.this, "Error loading user dropdown: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void setupUserDropdown() {
        List<String> dropdownItems = new ArrayList<>();
        dropdownItems.add("Select User");
        
        for (UserItem user : userList) {
            dropdownItems.add(user.name);
        }
        
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, dropdownItems);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userDropdown.setAdapter(dropdownAdapter);
    }

    private void loadAllSdsaTeamMembers() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading all SDSA team members...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_all_sdsa_team_members.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "All SDSA team data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "All SDSA team data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        teamList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject member = data.getJSONObject(i);
                            teamList.add(new TeamMember(
                                member.getString("fullName"),
                                member.getString("mobile"),
                                member.getString("email"),
                                member.getString("reportingTo"),
                                member.getString("id")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated team list with " + teamList.size() + " members");
                            Toast.makeText(SdsaTeamActivity.this, 
                                "Loaded " + teamList.size() + " SDSA team members", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(SdsaTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(SdsaTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading all SDSA team data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(SdsaTeamActivity.this, "Error loading SDSA team data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadTeamData(String managerId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading team data for manager ID: " + managerId);
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_team_members_by_manager.php?managerId=" + managerId);
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
                                member.getString("fullName"),
                                member.getString("mobile"),
                                member.getString("email"),
                                member.getString("reportingTo"),
                                member.getString("id")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated team list with " + teamList.size() + " members");
                            Toast.makeText(SdsaTeamActivity.this, 
                                "Loaded " + teamList.size() + " team members", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(SdsaTeamActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(SdsaTeamActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading team data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(SdsaTeamActivity.this, "Error loading team data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // User Item data class for dropdown
    private static class UserItem {
        String id;
        String name;

        UserItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // Team Member data class
    private static class TeamMember {
        String fullName;
        String mobile;
        String email;
        String reportingTo;
        String id;

        TeamMember(String fullName, String mobile, String email, String reportingTo, String id) {
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.reportingTo = reportingTo;
            this.id = id;
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
                    .inflate(R.layout.item_sdsa_team_new, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TeamMember member = teamMembers.get(position);
            holder.fullNameText.setText(member.fullName);
            holder.mobileText.setText(member.mobile);
            holder.emailText.setText(member.email);
            holder.reportingToText.setText(member.reportingTo);

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(SdsaTeamActivity.this, 
                    "Edit " + member.fullName, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(SdsaTeamActivity.this, 
                    "Delete " + member.fullName, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return teamMembers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView fullNameText;
            TextView mobileText;
            TextView emailText;
            TextView reportingToText;
            Button editButton;
            Button deleteButton;

            ViewHolder(View view) {
                super(view);
                fullNameText = view.findViewById(R.id.fullNameText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                reportingToText = view.findViewById(R.id.reportingToText);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 
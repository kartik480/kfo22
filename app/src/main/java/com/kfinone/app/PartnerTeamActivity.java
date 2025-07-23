package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartnerTeamActivity extends AppCompatActivity implements TeamMemberCardAdapter.OnTeamMemberActionListener {
    private static final String TAG = "PartnerTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private RecyclerView teamMembersRecyclerView;
    private TeamMemberCardAdapter teamAdapter;
    private List<PartnerTeamMember> teamList;
    private List<PartnerTeamMember> allTeamList;
    private RequestQueue requestQueue;
    private List<User> userList;

    // UI Elements
    private ImageButton backButton;
    private TextView totalTeamsText;
    private TextView totalMembersText;
    private AutoCompleteTextView userSpinner;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private MaterialButton filterButton;
    private LinearLayout noTeamMembersLayout;
    private LinearLayout loadingLayout;

    // Remove all references to selectedUserText and searchTeamInput
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_team);
        teamMembersRecyclerView = findViewById(R.id.teamMembersRecyclerView);
        teamList = new ArrayList<>();
        teamAdapter = new TeamMemberCardAdapter(this, teamList);
        teamMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamMembersRecyclerView.setAdapter(teamAdapter);
        // No selectedUserText, no searchTeamInput
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        totalTeamsText = findViewById(R.id.totalTeamsText);
        totalMembersText = findViewById(R.id.totalMembersText);
        userSpinner = findViewById(R.id.userSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        filterButton = findViewById(R.id.filterButton);
        noTeamMembersLayout = findViewById(R.id.noTeamMembersLayout);
        loadingLayout = findViewById(R.id.loadingLayout);

        backButton.setOnClickListener(v -> onBackPressed());
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void setupRecyclerView() {
        teamList = new ArrayList<>();
        allTeamList = new ArrayList<>();
        teamAdapter = new TeamMemberCardAdapter(this, teamList);
        teamAdapter.setActionListener(this);
        
        teamMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamMembersRecyclerView.setAdapter(teamAdapter);
    }

    private void setupSearchFunctionality() {
        // searchTeamInput.addTextChangedListener(new TextWatcher() {
        //     @Override
        //     public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        //     @Override
        //     public void onTextChanged(CharSequence s, int start, int before, int count) {
        //         filterTeamMembers(s.toString());
        //     }

        //     @Override
        //     public void afterTextChanged(Editable s) {}
        // });
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupClickListeners() {
        showDataButton.setOnClickListener(v -> {
            if (userSpinner.getText() != null && !userSpinner.getText().toString().isEmpty()) {
                String selectedUserId = getSelectedUserId();
                if (selectedUserId != null) {
                    loadPartnerTeamData(selectedUserId);
                } else {
                    Toast.makeText(this, "Please select a valid user first", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            userSpinner.setText("");
            teamList.clear();
            allTeamList.clear();
            teamAdapter.notifyDataSetChanged();
            updateStats();
            showNoTeamMembersMessage();
        });
    }

    private String getSelectedUserId() {
        String selectedText = userSpinner.getText().toString();
        for (User user : userList) {
            if (user.getName().equals(selectedText)) {
                return user.getId();
            }
        }
        return null;
    }

    private void loadUsersForDropdown() {
        String url = BASE_URL + "get_users_for_dropdown.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        userList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userObj = data.getJSONObject(i);
                            User user = new User(
                                userObj.getString("id"),
                                userObj.getString("name")
                            );
                            userList.add(user);
                        }
                        
                        ArrayAdapter<User> spinnerAdapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_dropdown_item_1line, userList
                        );
                        userSpinner.setAdapter(spinnerAdapter);
                        
                        updateStats();
                        
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Error fetching users: " + error.getMessage());
                Toast.makeText(this, "Error fetching users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
        
        requestQueue.add(request);
    }

    private void loadPartnerTeamData(String userId) {
        showLoading(true);
        String url = BASE_URL + "get_partner_team_data.php?user_id=" + userId;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                showLoading(false);
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        allTeamList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject teamObj = data.getJSONObject(i);
                            PartnerTeamMember member = new PartnerTeamMember(
                                teamObj.getString("id"),
                                teamObj.getString("full_name"),
                                teamObj.getString("mobile"),
                                teamObj.getString("email"),
                                teamObj.getString("created_by")
                            );
                            allTeamList.add(member);
                        }
                        
                        teamList.clear();
                        teamList.addAll(allTeamList);
                        teamAdapter.notifyDataSetChanged();
                        updateStats();
                        updateUI();
                        
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        showNoTeamMembersMessage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    showNoTeamMembersMessage();
                }
            },
            error -> {
                showLoading(false);
                Log.e(TAG, "Error fetching team data: " + error.getMessage());
                Toast.makeText(this, "Error fetching team data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                showNoTeamMembersMessage();
            }
        );
        
        requestQueue.add(request);
    }

    // Add this method to fetch and display the team partner list (copied and adapted from DirectorMyPartnerActivity)
    private void loadPartnerTeamData() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_get_partner_list.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        teamList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partner = data.getJSONObject(i);
                            teamList.add(new PartnerTeamMember(
                                partner.optString("id", ""),
                                partner.optString("first_name", "") + " " + partner.optString("last_name", ""),
                                partner.optString("Phone_number", ""),
                                partner.optString("email_id", ""),
                                partner.optString("createdBy", "")
                            ));
                        }
                        runOnUiThread(() -> teamAdapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(PartnerTeamActivity.this, "No team partners found.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(PartnerTeamActivity.this, "Failed to load team partners.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(PartnerTeamActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPartnerTeamData();
    }

    private void filterTeamMembers(String query) {
        if (query.isEmpty()) {
            teamList.clear();
            teamList.addAll(allTeamList);
        } else {
            List<PartnerTeamMember> filteredList = allTeamList.stream()
                .filter(member -> 
                    member.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    member.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    member.getMobile().contains(query) ||
                    member.getCreatedBy().toLowerCase().contains(query.toLowerCase())
                )
                .collect(Collectors.toList());
            
            teamList.clear();
            teamList.addAll(filteredList);
        }
        teamAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void showFilterDialog() {
        String[] filterOptions = {"All Members", "By Name", "By Email", "By Mobile"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Team Members")
               .setItems(filterOptions, (dialog, which) -> {
                   // For now, just show a message. In a real app, you'd implement specific filters
                   Toast.makeText(this, "Filter: " + filterOptions[which], Toast.LENGTH_SHORT).show();
               })
               .show();
    }

    private void updateStats() {
        int totalTeams = userList != null ? userList.size() : 0;
        int totalMembers = allTeamList.size();
        String selectedUser = userSpinner.getText().toString();
        
        if (selectedUser.isEmpty()) {
            selectedUser = "None";
        }

        totalTeamsText.setText(String.valueOf(totalTeams));
        totalMembersText.setText(String.valueOf(totalMembers));
        // selectedUserText.setText(selectedUser); // This line is removed
    }

    private void updateUI() {
        if (teamList.isEmpty()) {
            showNoTeamMembersMessage();
        } else {
            hideNoTeamMembersMessage();
        }
    }

    private void showLoading(boolean show) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        teamMembersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        noTeamMembersLayout.setVisibility(View.GONE);
    }

    private void showNoTeamMembersMessage() {
        noTeamMembersLayout.setVisibility(View.VISIBLE);
        teamMembersRecyclerView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }

    private void hideNoTeamMembersMessage() {
        noTeamMembersLayout.setVisibility(View.GONE);
        teamMembersRecyclerView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onEditTeamMember(PartnerTeamMember member) {
        Toast.makeText(this, "Edit team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to edit team member activity
    }

    @Override
    public void onDeleteTeamMember(PartnerTeamMember member) {
        new AlertDialog.Builder(this)
            .setTitle("Remove Team Member")
            .setMessage("Are you sure you want to remove " + member.getFullName() + " from the team?")
            .setPositiveButton("Remove", (dialog, which) -> {
                Toast.makeText(this, "Remove team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Implement remove functionality
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // User data class
    public static class User {
        private String id;
        private String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    // Partner Team Member data class
    public static class PartnerTeamMember {
        private String id;
        private String fullName;
        private String mobile;
        private String email;
        private String createdBy;

        public PartnerTeamMember(String id, String fullName, String mobile, String email, String createdBy) {
            this.id = id;
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.createdBy = createdBy;
        }

        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getMobile() { return mobile; }
        public String getEmail() { return email; }
        public String getCreatedBy() { return createdBy; }
    }
} 
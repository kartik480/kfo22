package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CBOTeamActivity extends AppCompatActivity {
    private static final String TAG = "CBOTeamActivity";
    
    // UI Components
    private TextView titleText;
    private TextView statsText;
    private RecyclerView teamRecyclerView;
    private Button backButton;
    private Button refreshButton;
    private Button addMemberButton;
    
    // Data
    private String userName;
    private String userId;
    private List<TeamMember> teamMembers;
    private TeamMemberAdapter teamAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_team);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        initializeViews();
        setupClickListeners();
        loadTeamData();
    }
    
    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        statsText = findViewById(R.id.statsText);
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addMemberButton = findViewById(R.id.addMemberButton);
        
        // Set title
        titleText.setText("Team Management - " + (userName != null ? userName : "CBO"));
        
        // Setup RecyclerView
        teamMembers = new ArrayList<>();
        teamAdapter = new TeamMemberAdapter(teamMembers, this);
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamRecyclerView.setAdapter(teamAdapter);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        refreshButton.setOnClickListener(v -> {
            loadTeamData();
        });
        
        addMemberButton.setOnClickListener(v -> {
            // TODO: Implement add team member functionality
            Toast.makeText(this, "Add team member functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadTeamData() {
        executor.execute(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/get_chief_business_officer_users.php";
                
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                
                if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    String responseBody = response.toString();
                    Log.d(TAG, "Response: " + responseBody);
                    
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray teamMembersArray = data.getJSONArray("team_members");
                        JSONObject statistics = data.getJSONObject("statistics");
                        
                        // Update UI on main thread
                        runOnUiThread(() -> {
                            updateStats(statistics);
                            updateTeamMembers(teamMembersArray);
                        });
                    } else {
                        String error = jsonResponse.optString("message", "Failed to load data");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Exception loading team data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateStats(JSONObject statistics) {
        try {
            int totalTeamMembers = statistics.optInt("total_team_members", 0);
            
            String statsMessage = String.format("Total Team Members: %d", totalTeamMembers);
            statsText.setText(statsMessage);
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating stats: " + e.getMessage());
        }
    }
    
    private void updateTeamMembers(JSONArray teamMembersArray) {
        try {
            teamMembers.clear();
            
            for (int i = 0; i < teamMembersArray.length(); i++) {
                JSONObject member = teamMembersArray.getJSONObject(i);
                
                TeamMember teamMember = new TeamMember();
                teamMember.setId(member.optString("id", ""));
                teamMember.setFirstName(member.optString("firstName", ""));
                teamMember.setLastName(member.optString("lastName", ""));
                teamMember.setFullName(member.optString("fullName", ""));
                teamMember.setDesignation(member.optString("designation_name", ""));
                teamMember.setEmail(member.optString("email_id", ""));
                teamMember.setMobile(member.optString("mobile", ""));
                teamMember.setEmployeeNo(member.optString("employee_no", ""));
                teamMember.setManagerName(member.optString("managerName", ""));
                
                teamMembers.add(teamMember);
            }
            
            teamAdapter.notifyDataSetChanged();
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating team members: " + e.getMessage());
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
} 
package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBHTeamActivity extends AppCompatActivity {
    private static final String TAG = "RBHTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private ImageButton backButton;
    private TextView titleText;
    private RecyclerView teamRecyclerView;
    private TextView noDataText;
    
    private List<TeamMember> teamMembers = new ArrayList<>();
    private TeamMemberAdapter teamAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_team);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupClickListeners();
        loadTeamMembers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        noDataText = findViewById(R.id.noDataText);
        
        titleText.setText("RBH Team Management");
        
        // Setup RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamAdapter = new TeamMemberAdapter(teamMembers, this);
        teamRecyclerView.setAdapter(teamAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void loadTeamMembers() {
        executor.execute(() -> {
            try {
                String urlString = BASE_URL + "get_rbh_users.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "Team API response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray teamMembersArray = data.getJSONArray("team_members");
                        
                        teamMembers.clear();
                        
                        for (int i = 0; i < teamMembersArray.length(); i++) {
                            JSONObject member = teamMembersArray.getJSONObject(i);
                            
                            TeamMember teamMember = new TeamMember();
                            teamMember.setId(member.getString("id"));
                            teamMember.setFullName(member.getString("fullName"));
                            teamMember.setDesignation(member.getString("designation_name"));
                            teamMember.setEmail(member.getString("email_id"));
                            teamMember.setMobile(member.getString("mobile"));
                            teamMember.setEmployeeNo(member.getString("employee_no"));
                            teamMember.setManagerName(member.getString("managerName"));
                            
                            teamMembers.add(teamMember);
                        }

                        runOnUiThread(() -> {
                            if (teamMembers.isEmpty()) {
                                noDataText.setVisibility(View.VISIBLE);
                                teamRecyclerView.setVisibility(View.GONE);
                            } else {
                                noDataText.setVisibility(View.GONE);
                                teamRecyclerView.setVisibility(View.VISIBLE);
                                teamAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        Log.e(TAG, "API returned error: " + jsonResponse.getString("message"));
                        runOnUiThread(() -> {
                            noDataText.setVisibility(View.VISIBLE);
                            teamRecyclerView.setVisibility(View.GONE);
                            noDataText.setText("Error loading team data");
                        });
                    }
                } else {
                    Log.e(TAG, "API failed with response code: " + responseCode);
                    runOnUiThread(() -> {
                        noDataText.setVisibility(View.VISIBLE);
                        teamRecyclerView.setVisibility(View.GONE);
                        noDataText.setText("Error loading team data");
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading team members: " + e.getMessage());
                runOnUiThread(() -> {
                    noDataText.setVisibility(View.VISIBLE);
                    teamRecyclerView.setVisibility(View.GONE);
                    noDataText.setText("Error loading team data");
                });
            }
        });
    }
} 
package com.kfinone.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class PortfolioTeamActivity extends AppCompatActivity {

    private RecyclerView teamRecyclerView;
    private View emptyStateLayout;
    private TextView backButton;
    private View addMemberButton;
    private TextView totalMembersText;
    private TextView activeMembersText;
    private AutoCompleteTextView userDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private TeamAdapter teamAdapter;
    private List<TeamMember> teamList;
    private List<TeamMember> allTeamList; // Store all team members for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_team);

        initializeViews();
        setupDropdowns();
        setupClickListeners();
        loadTeamData();
    }

    private void initializeViews() {
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        backButton = findViewById(R.id.backButton);
        addMemberButton = findViewById(R.id.addMemberButton);
        totalMembersText = findViewById(R.id.totalMembersText);
        activeMembersText = findViewById(R.id.activeMembersText);
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);

        // Setup RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamList = new ArrayList<>();
        allTeamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(teamList);
        teamRecyclerView.setAdapter(teamAdapter);
    }

    private void setupDropdowns() {
        // User options for filtering
        String[] users = {"All Users", "Admin", "Manager", "Supervisor", "Analyst", "Trader", "Research"};
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, users);
        userDropdown.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        addMemberButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add Team Member - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Add Team Member activity
        });

        showDataButton.setOnClickListener(v -> {
            filterTeamData();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
        });
    }

    private void loadTeamData() {
        // TODO: Load team data from server
        // For now, show sample data
        allTeamList.clear();
        
        // Add sample team members
        allTeamList.add(new TeamMember("1", "John Doe", "Portfolio Manager", "Active", "john.doe@email.com", "Admin"));
        allTeamList.add(new TeamMember("2", "Jane Smith", "Analyst", "Active", "jane.smith@email.com", "Manager"));
        allTeamList.add(new TeamMember("3", "Mike Johnson", "Trader", "Active", "mike.johnson@email.com", "Supervisor"));
        allTeamList.add(new TeamMember("4", "Sarah Wilson", "Research", "Active", "sarah.wilson@email.com", "Analyst"));
        allTeamList.add(new TeamMember("5", "David Brown", "Intern", "Inactive", "david.brown@email.com", "Manager"));
        allTeamList.add(new TeamMember("6", "Emily Davis", "Senior Analyst", "Active", "emily.davis@email.com", "Admin"));
        allTeamList.add(new TeamMember("7", "Robert Wilson", "Risk Manager", "Active", "robert.wilson@email.com", "Supervisor"));
        allTeamList.add(new TeamMember("8", "Lisa Anderson", "Data Analyst", "Active", "lisa.anderson@email.com", "Analyst"));
        
        // Initially show all data
        teamList.clear();
        teamList.addAll(allTeamList);
        teamAdapter.notifyDataSetChanged();
        updateStats();
        updateEmptyState();
    }

    private void filterTeamData() {
        String selectedUser = userDropdown.getText().toString().trim();
        
        if (TextUtils.isEmpty(selectedUser)) {
            Toast.makeText(this, "Please select a user to filter", Toast.LENGTH_SHORT).show();
            return;
        }

        teamList.clear();
        
        if ("All Users".equals(selectedUser)) {
            // Show all team members
            teamList.addAll(allTeamList);
        } else {
            // Filter by selected user type
            for (TeamMember member : allTeamList) {
                if (selectedUser.equals(member.getUserType())) {
                    teamList.add(member);
                }
            }
        }
        
        teamAdapter.notifyDataSetChanged();
        updateStats();
        updateEmptyState();
        
        Toast.makeText(this, "Showing data for: " + selectedUser, Toast.LENGTH_SHORT).show();
    }

    private void resetFilters() {
        userDropdown.setText("");
        teamList.clear();
        teamList.addAll(allTeamList);
        teamAdapter.notifyDataSetChanged();
        updateStats();
        updateEmptyState();
        
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }

    private void updateStats() {
        int totalMembers = teamList.size();
        int activeMembers = 0;
        
        for (TeamMember member : teamList) {
            if ("Active".equals(member.getStatus())) {
                activeMembers++;
            }
        }
        
        totalMembersText.setText(String.valueOf(totalMembers));
        activeMembersText.setText(String.valueOf(activeMembers));
    }

    private void updateEmptyState() {
        if (teamList.isEmpty()) {
            teamRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            teamRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    // Team Member class with additional user type field
    public static class TeamMember {
        private String id;
        private String name;
        private String role;
        private String status;
        private String email;
        private String userType;

        public TeamMember(String id, String name, String role, String status, String email, String userType) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.status = status;
            this.email = email;
            this.userType = userType;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public String getEmail() { return email; }
        public String getUserType() { return userType; }
    }
} 
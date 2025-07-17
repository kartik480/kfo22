package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InsuranceTeamActivity extends AppCompatActivity {

    private RecyclerView teamRecyclerView;
    private TeamMemberAdapter teamAdapter;
    private List<TeamMember> teamList;
    private List<TeamMember> filteredList;

    private Spinner userTypeSpinner;
    private Button showDataButton, resetButton;
    private LinearLayout emptyStateLayout, loadingLayout;
    private Button addTeamMemberButton;
    private TextView backButton;
    private View addMemberButton;

    private String selectedUserType = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_team);

        initializeViews();
        setupSpinner();
        setupClickListeners();
        loadTeamData();
    }

    private void initializeViews() {
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        addTeamMemberButton = findViewById(R.id.addTeamMemberButton);
        backButton = findViewById(R.id.backButton);
        addMemberButton = findViewById(R.id.addMemberButton);

        // Setup RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamList = new ArrayList<>();
        filteredList = new ArrayList<>();
        teamAdapter = new TeamMemberAdapter(filteredList, this);
        teamRecyclerView.setAdapter(teamAdapter);
    }

    private void setupSpinner() {
        String[] userTypes = {"All", "Agent", "Manager", "Supervisor", "Admin"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            userTypes
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(spinnerAdapter);

        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserType = userTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserType = "All";
            }
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        addMemberButton.setOnClickListener(v -> addNewMember());
        addTeamMemberButton.setOnClickListener(v -> addNewMember());
        showDataButton.setOnClickListener(v -> showData());
        resetButton.setOnClickListener(v -> resetFilter());
    }

    private void goBack() {
        Intent intent = new Intent(this, InsurancePanelActivity.class);
        startActivity(intent);
        finish();
    }

    private void addNewMember() {
        Toast.makeText(this, "Add Team Member - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to add team member activity
    }

    private void showData() {
        filterByUserType(selectedUserType);
    }

    private void resetFilter() {
        userTypeSpinner.setSelection(0); // Select "All"
        selectedUserType = "All";
        filteredList.clear();
        filteredList.addAll(teamList);
        teamAdapter.notifyDataSetChanged();
        updateEmptyState();
        Toast.makeText(this, "Filter reset", Toast.LENGTH_SHORT).show();
    }

    private void filterByUserType(String userType) {
        filteredList.clear();
        for (TeamMember member : teamList) {
            if (userType.equals("All") || member.getUserType().equals(userType)) {
                filteredList.add(member);
            }
        }
        teamAdapter.notifyDataSetChanged();
        updateEmptyState();
        Toast.makeText(this, "Showing " + userType + " members", Toast.LENGTH_SHORT).show();
    }

    private void loadTeamData() {
        showLoading(true);
        
        // Simulate API call delay
        teamRecyclerView.postDelayed(() -> {
            loadSampleData();
            showLoading(false);
            updateEmptyState();
        }, 1000);
    }

    private void loadSampleData() {
        teamList.clear();
        
        // Add sample team members
        teamList.add(new TeamMember(
            "John Smith",
            "Agent",
            "john.smith@example.com",
            "9876543210",
            "Mumbai",
            "Active"
        ));

        teamList.add(new TeamMember(
            "Sarah Johnson",
            "Manager",
            "sarah.johnson@example.com",
            "9876543211",
            "Delhi",
            "Active"
        ));

        teamList.add(new TeamMember(
            "Mike Davis",
            "Agent",
            "mike.davis@example.com",
            "9876543212",
            "Bangalore",
            "Active"
        ));

        teamList.add(new TeamMember(
            "Emily Wilson",
            "Supervisor",
            "emily.wilson@example.com",
            "9876543213",
            "Chennai",
            "Active"
        ));

        teamList.add(new TeamMember(
            "David Brown",
            "Agent",
            "david.brown@example.com",
            "9876543214",
            "Hyderabad",
            "Inactive"
        ));

        teamList.add(new TeamMember(
            "Lisa Anderson",
            "Admin",
            "lisa.anderson@example.com",
            "9876543215",
            "Pune",
            "Active"
        ));

        teamList.add(new TeamMember(
            "Robert Taylor",
            "Manager",
            "robert.taylor@example.com",
            "9876543216",
            "Kolkata",
            "Active"
        ));

        teamList.add(new TeamMember(
            "Jennifer Lee",
            "Agent",
            "jennifer.lee@example.com",
            "9876543217",
            "Ahmedabad",
            "Active"
        ));

        // Initially show all team members
        filteredList.clear();
        filteredList.addAll(teamList);
        teamAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            teamRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            teamRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            teamRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            teamRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Team Member data class
    public static class TeamMember {
        private String name;
        private String userType;
        private String email;
        private String phone;
        private String location;
        private String status;

        public TeamMember(String name, String userType, String email, String phone, String location, String status) {
            this.name = name;
            this.userType = userType;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.status = status;
        }

        // Getters
        public String getName() { return name; }
        public String getUserType() { return userType; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
    }
} 
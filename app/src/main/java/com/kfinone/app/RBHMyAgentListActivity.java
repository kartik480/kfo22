package com.kfinone.app;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RBHMyAgentListActivity extends AppCompatActivity {
    private TextView totalAgentCount, activeAgentCount, welcomeText;
    private Spinner spinnerAgentType, spinnerBranchState, spinnerBranchLocation;
    private Button btnFilter, btnReset;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_rbh_my_agent_list);
        
        String userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        initializeViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
        loadAgentData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        activeAgentCount = findViewById(R.id.activeAgentCount);
        spinnerAgentType = findViewById(R.id.spinnerAgentType);
        spinnerBranchState = findViewById(R.id.spinnerBranchState);
        spinnerBranchLocation = findViewById(R.id.spinnerBranchLocation);
        btnFilter = findViewById(R.id.btnFilter);
        btnReset = findViewById(R.id.btnReset);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Agent List");
        }
    }

    private void setupSpinners() {
        // Agent Type options
        String[] agentTypes = {"All Agent Types", "Individual Agent", "Corporate Agent", "Broker"};
        ArrayAdapter<String> agentTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, agentTypes);
        agentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgentType.setAdapter(agentTypeAdapter);

        // Branch State options
        String[] branchStates = {"All States", "Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat"};
        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStates);
        branchStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchState.setAdapter(branchStateAdapter);

        // Branch Location options
        String[] branchLocations = {"All Locations", "Mumbai", "Pune", "Nagpur", "Thane", "Nashik"};
        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocations);
        branchLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchLocation.setAdapter(branchLocationAdapter);
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> {
            String selectedAgentType = spinnerAgentType.getSelectedItem().toString();
            String selectedBranchState = spinnerBranchState.getSelectedItem().toString();
            String selectedBranchLocation = spinnerBranchLocation.getSelectedItem().toString();
            
            // TODO: Implement filter logic with API call
            Toast.makeText(this, "Filtering agents...", Toast.LENGTH_SHORT).show();
            applyFilters(selectedAgentType, selectedBranchState, selectedBranchLocation);
        });

        btnReset.setOnClickListener(v -> {
            spinnerAgentType.setSelection(0);
            spinnerBranchState.setSelection(0);
            spinnerBranchLocation.setSelection(0);
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
            // TODO: Reset to show all agents
        });
    }

    private void applyFilters(String agentType, String branchState, String branchLocation) {
        // TODO: Implement API call to filter agents based on selected criteria
        // For now, just show a message
        String filterMessage = "Applied filters:\n";
        if (!agentType.equals("All Agent Types")) filterMessage += "Agent Type: " + agentType + "\n";
        if (!branchState.equals("All States")) filterMessage += "Branch State: " + branchState + "\n";
        if (!branchLocation.equals("All Locations")) filterMessage += "Branch Location: " + branchLocation;
        
        Toast.makeText(this, filterMessage, Toast.LENGTH_LONG).show();
    }

    private void loadAgentData() {
        // TODO: Implement API call to fetch agent data
        totalAgentCount.setText("25");
        activeAgentCount.setText("18");
    }

    private void updateWelcomeMessage(String userName) {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 
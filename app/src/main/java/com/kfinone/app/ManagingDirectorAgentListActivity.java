package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorAgentListActivity extends AppCompatActivity {

    private static final String TAG = "ManagingDirectorAgentList";

    // UI Elements
    private Spinner agentTypeSpinner, branchStateSpinner, branchLocationSpinner;
    private Button filterButton, resetButton;
    private LinearLayout agentListContainer, emptyStateContainer, loadingContainer;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    // Volley request queue
    private RequestQueue requestQueue;

    // Sample data for spinners (replace with actual API calls)
    private List<String> agentTypes = new ArrayList<>();
    private List<String> branchStates = new ArrayList<>();
    private List<String> branchLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_agent_list);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadFilterData();
        showEmptyState();
    }

    private void initializeViews() {
        agentTypeSpinner = findViewById(R.id.agentTypeSpinner);
        branchStateSpinner = findViewById(R.id.branchStateSpinner);
        branchLocationSpinner = findViewById(R.id.branchLocationSpinner);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        agentListContainer = findViewById(R.id.agentListContainer);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        loadingContainer = findViewById(R.id.loadingContainer);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Filter Button
        filterButton.setOnClickListener(v -> {
            applyFilters();
        });

        // Reset Button
        resetButton.setOnClickListener(v -> {
            resetFilters();
        });
    }

    private void loadFilterData() {
        // Load sample data for spinners (replace with actual API calls)
        agentTypes.add("Select Agent Type");
        agentTypes.add("Individual");
        agentTypes.add("Corporate");
        agentTypes.add("Partner");

        branchStates.add("Select Branch State");
        branchStates.add("Maharashtra");
        branchStates.add("Delhi");
        branchStates.add("Karnataka");
        branchStates.add("Tamil Nadu");

        branchLocations.add("Select Branch Location");
        branchLocations.add("Mumbai");
        branchLocations.add("Pune");
        branchLocations.add("Delhi");
        branchLocations.add("Bangalore");

        // Set up spinners
        ArrayAdapter<String> agentTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, agentTypes);
        agentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agentTypeSpinner.setAdapter(agentTypeAdapter);

        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, branchStates);
        branchStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchStateSpinner.setAdapter(branchStateAdapter);

        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, branchLocations);
        branchLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchLocationSpinner.setAdapter(branchLocationAdapter);
    }

    private void applyFilters() {
        String selectedAgentType = agentTypeSpinner.getSelectedItem().toString();
        String selectedBranchState = branchStateSpinner.getSelectedItem().toString();
        String selectedBranchLocation = branchLocationSpinner.getSelectedItem().toString();

        // Check if any filter is selected
        if (selectedAgentType.equals("Select Agent Type") && 
            selectedBranchState.equals("Select Branch State") && 
            selectedBranchLocation.equals("Select Branch Location")) {
            Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        showLoadingState();

        // Simulate API call (replace with actual API call)
        // For now, we'll just show empty state after a delay
        new android.os.Handler().postDelayed(() -> {
            showEmptyState();
            Toast.makeText(this, "Filters applied: " + 
                (selectedAgentType.equals("Select Agent Type") ? "" : selectedAgentType + ", ") +
                (selectedBranchState.equals("Select Branch State") ? "" : selectedBranchState + ", ") +
                (selectedBranchLocation.equals("Select Branch Location") ? "" : selectedBranchLocation), 
                Toast.LENGTH_LONG).show();
        }, 2000);
    }

    private void resetFilters() {
        agentTypeSpinner.setSelection(0);
        branchStateSpinner.setSelection(0);
        branchLocationSpinner.setSelection(0);
        
        showEmptyState();
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }

    private void showLoadingState() {
        loadingContainer.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);
        agentListContainer.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        loadingContainer.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);
        agentListContainer.setVisibility(View.GONE);
    }

    private void showAgentList(List<AgentData> agents) {
        loadingContainer.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.GONE);
        agentListContainer.setVisibility(View.VISIBLE);

        // Clear existing agent list
        agentListContainer.removeAllViews();

        // Add agents to the list
        for (AgentData agent : agents) {
            View agentRow = createAgentRow(agent);
            agentListContainer.addView(agentRow);
        }
    }

    private View createAgentRow(AgentData agent) {
        // Create a simple row view for each agent
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundColor(android.graphics.Color.WHITE);
        row.setPadding(16, 12, 16, 12);
        row.setElevation(1);

        // Add agent data fields
        addField(row, agent.fullName, 2);
        addField(row, agent.companyName, 2);
        addField(row, agent.mobile, 1.5f);
        addField(row, agent.agentType, 1.5f);
        addField(row, agent.branchState, 1.5f);
        addField(row, agent.branchLocation, 1.5f);

        // Add actions
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setTextSize(10);
        editButton.setPadding(8, 4, 8, 4);
        editButton.setBackgroundResource(R.drawable.button_background_primary);
        editButton.setTextColor(android.graphics.Color.WHITE);
        editButton.setOnClickListener(v -> {
            Toast.makeText(this, "Edit " + agent.fullName, Toast.LENGTH_SHORT).show();
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(10);
        deleteButton.setPadding(8, 4, 8, 4);
        deleteButton.setBackgroundResource(R.drawable.button_background_red);
        deleteButton.setTextColor(android.graphics.Color.WHITE);
        deleteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Delete " + agent.fullName, Toast.LENGTH_SHORT).show();
        });

        actionsLayout.addView(editButton);
        actionsLayout.addView(deleteButton);
        row.addView(actionsLayout);

        return row;
    }

    private void addField(LinearLayout row, String text, float weight) {
        TextView field = new TextView(this);
        field.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        field.setText(text);
        field.setTextSize(12);
        field.setTextColor(android.graphics.Color.BLACK);
        field.setPadding(8, 0, 8, 0);
        field.setMaxLines(2);
        field.setEllipsize(android.text.TextUtils.TruncateAt.END);
        row.addView(field);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    // Data class for agent information
    private static class AgentData {
        String fullName;
        String companyName;
        String mobile;
        String agentType;
        String branchState;
        String branchLocation;

        AgentData(String fullName, String companyName, String mobile, 
                  String agentType, String branchState, String branchLocation) {
            this.fullName = fullName;
            this.companyName = companyName;
            this.mobile = mobile;
            this.agentType = agentType;
            this.branchState = branchState;
            this.branchLocation = branchLocation;
        }
    }
}

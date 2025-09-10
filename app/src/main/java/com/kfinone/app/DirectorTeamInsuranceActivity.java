package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DirectorTeamInsuranceActivity extends AppCompatActivity {

    private static final String TAG = "DirectorTeamInsurance";

    // UI Elements
    private ImageView backButton;
    private Spinner userSpinner;
    private Button showDataButton;
    private Button resetButton;
    private TextView dataCountText;
    private TextView emptyStateText;

    // Data
    private List<String> userList = new ArrayList<>();
    private ArrayAdapter<String> userAdapter;
    private String selectedUserId = "";

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_team_insurance);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        userSpinner = findViewById(R.id.userSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        dataCountText = findViewById(R.id.dataCountText);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Setup user spinner
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select User" option
                    selectedUserId = userList.get(position);
                    Log.d(TAG, "Selected user: " + selectedUserId);
                } else {
                    selectedUserId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserId = "";
            }
        });

        showDataButton.setOnClickListener(v -> {
            if (selectedUserId.isEmpty()) {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
                return;
            }
            loadTeamInsuranceData();
        });

        resetButton.setOnClickListener(v -> {
            userSpinner.setSelection(0);
            selectedUserId = "";
            resetDataDisplay();
        });
    }

    private void loadUserData() {
        // TODO: Replace with actual API call to fetch team users
        userList.clear();
        userList.add("Select User"); // Default option
        
        // Sample team users - replace with API call
        userList.add("Agent 001 - John Smith");
        userList.add("Agent 002 - Sarah Johnson");
        userList.add("Agent 003 - Mike Wilson");
        userList.add("Agent 004 - Lisa Brown");
        userList.add("Agent 005 - David Lee");

        userAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Users: " + (userList.size() - 1)); // Exclude "Select User"
        
        Log.d(TAG, "Loaded " + (userList.size() - 1) + " team users");
    }

    private void loadTeamInsuranceData() {
        if (selectedUserId.isEmpty()) {
            Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Replace with actual API call to fetch team insurance data
        Log.d(TAG, "Loading team insurance data for user: " + selectedUserId);
        
        // Simulate data loading
        Toast.makeText(this, "Loading insurance data for " + selectedUserId, Toast.LENGTH_SHORT).show();
        
        // For now, show empty state with message
        emptyStateText.setText("Team insurance data for " + selectedUserId + " will be displayed here.\n\nThis feature will be implemented with actual API integration.");
        emptyStateText.setVisibility(View.VISIBLE);
    }

    private void resetDataDisplay() {
        selectedUserId = "";
        emptyStateText.setText("No team insurance data available. Select a user and click 'Show Data' to load information.");
        emptyStateText.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Data reset. Please select a user to view team insurance data.", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

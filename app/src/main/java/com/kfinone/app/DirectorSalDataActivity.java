package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DirectorSalDataActivity extends AppCompatActivity implements SalariedListAdapter.OnSalariedActionClickListener {

    private static final String TAG = "DirectorSalData";

    // UI Elements
    private ImageView backButton;
    private TextView titleText;
    private ImageView accountIcon;
    private TextView welcomeText;
    private TextView descriptionText;
    private TextInputEditText mobileNumberInput;
    private Button filterButton;
    private Button resetButton;
    private TextView dataCountText;
    private RecyclerView salariedRecyclerView;
    private LinearLayout emptyStateLayout;

    // Bottom Navigation
    private LinearLayout dashboardButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;
    private LinearLayout profileButton;

    // Data
    private List<SalariedItem> salariedList = new ArrayList<>();
    private SalariedListAdapter salariedAdapter;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_sal_data);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        // Debug logging
        Log.d(TAG, "Received user data:");
        Log.d(TAG, "USER_ID: " + userId);
        Log.d(TAG, "USERNAME: " + username);
        Log.d(TAG, "FIRST_NAME: " + firstName);
        Log.d(TAG, "LAST_NAME: " + lastName);

        initializeViews();
        setupClickListeners();
        loadSalariedData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        accountIcon = findViewById(R.id.accountIcon);
        welcomeText = findViewById(R.id.welcomeText);
        descriptionText = findViewById(R.id.descriptionText);
        mobileNumberInput = findViewById(R.id.mobileNumberInput);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        dataCountText = findViewById(R.id.dataCountText);
        salariedRecyclerView = findViewById(R.id.salariedRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Bottom Navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        profileButton = findViewById(R.id.profileButton);

        // Setup RecyclerView
        salariedAdapter = new SalariedListAdapter(salariedList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        salariedRecyclerView.setLayoutManager(layoutManager);
        salariedRecyclerView.setAdapter(salariedAdapter);
        salariedRecyclerView.setHasFixedSize(true);
        salariedRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Account icon
        accountIcon.setOnClickListener(v -> {
            Log.d(TAG, "Account icon clicked");
            Toast.makeText(this, "Account settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Filter button
        filterButton.setOnClickListener(v -> {
            String mobileNumber = mobileNumberInput.getText().toString().trim();
            Log.d(TAG, "Filter button clicked with mobile: " + mobileNumber);
            
            if (mobileNumber.isEmpty()) {
                Toast.makeText(this, "Please enter a mobile number to filter", Toast.LENGTH_SHORT).show();
                return;
            }
            
            filterSalariedData(mobileNumber);
        });

        // Reset button
        resetButton.setOnClickListener(v -> {
            Log.d(TAG, "Reset button clicked");
            mobileNumberInput.setText("");
            loadSalariedData();
        });

        // Bottom Navigation
        dashboardButton.setOnClickListener(v -> {
            Log.d(TAG, "Dashboard button clicked");
            Intent intent = new Intent(this, User10002PanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Log.d(TAG, "Reports button clicked");
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "Settings button clicked");
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        profileButton.setOnClickListener(v -> {
            Log.d(TAG, "Profile button clicked");
            Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSalariedData() {
        // TODO: Replace with actual API call to fetch salaried data
        salariedList.clear();
        updateDataDisplay();
        
        if (salariedList.isEmpty()) {
            Toast.makeText(this, "No salaried records found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Loaded " + salariedList.size() + " salaried records", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterSalariedData(String mobileNumber) {
        // TODO: Replace with actual API call to filter salaried data by mobile number
        salariedList.clear();
        
        // For now, show a message
        Toast.makeText(this, "Filtering by mobile number: " + mobileNumber, Toast.LENGTH_SHORT).show();
        
        updateDataDisplay();
    }

    private void updateDataDisplay() {
        Log.d(TAG, "Updating data display. Salaried count: " + salariedList.size());
        salariedAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Records: " + salariedList.size());
        
        if (salariedList.isEmpty()) {
            Log.d(TAG, "No salaried records to display, showing empty state");
            salariedRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Showing " + salariedList.size() + " salaried records in RecyclerView");
            salariedRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewClick(SalariedItem salariedItem) {
        // Handle view click
        Toast.makeText(this, "Viewing: " + salariedItem.getLeadName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "View clicked for salaried: " + salariedItem.getLeadName());
        
        // TODO: Navigate to salaried detail view
        // Intent intent = new Intent(this, SalariedDetailActivity.class);
        // intent.putExtra("SALARIED_ID", salariedItem.getId());
        // startActivity(intent);
    }

    @Override
    public void onEditClick(SalariedItem salariedItem) {
        // Handle edit click
        Toast.makeText(this, "Editing: " + salariedItem.getLeadName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Edit clicked for salaried: " + salariedItem.getLeadName());
        
        // TODO: Navigate to salaried edit view
        // Intent intent = new Intent(this, SalariedEditActivity.class);
        // intent.putExtra("SALARIED_ID", salariedItem.getId());
        // startActivity(intent);
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

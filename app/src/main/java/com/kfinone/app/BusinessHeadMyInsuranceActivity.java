package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BusinessHeadMyInsuranceActivity extends AppCompatActivity implements BusinessHeadInsuranceListAdapter.OnInsuranceActionClickListener {

    private static final String TAG = "BHMyInsurance";

    // UI Elements
    private ImageView backButton;
    private TextView dataCountText;
    private RecyclerView insuranceRecyclerView;
    private LinearLayout emptyStateLayout;

    // Data
    private List<BusinessHeadInsuranceItem> insuranceList = new ArrayList<>();
    private BusinessHeadInsuranceListAdapter insuranceAdapter;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_insurance);

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
        loadInsuranceData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        dataCountText = findViewById(R.id.dataCountText);
        insuranceRecyclerView = findViewById(R.id.insuranceRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Setup RecyclerView
        insuranceAdapter = new BusinessHeadInsuranceListAdapter(insuranceList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        insuranceRecyclerView.setLayoutManager(layoutManager);
        insuranceRecyclerView.setAdapter(insuranceAdapter);
        insuranceRecyclerView.setHasFixedSize(true);
        insuranceRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadInsuranceData() {
        // TODO: Replace with actual API call to fetch insurance data
        insuranceList.clear();
        updateDataDisplay();
        
        if (insuranceList.isEmpty()) {
            Toast.makeText(this, "No insurance policies found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Loaded " + insuranceList.size() + " insurance policies", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDataDisplay() {
        Log.d(TAG, "Updating data display. Insurance count: " + insuranceList.size());
        insuranceAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Policies: " + insuranceList.size());
        
        if (insuranceList.isEmpty()) {
            Log.d(TAG, "No insurance policies to display, showing empty state");
            insuranceRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Showing " + insuranceList.size() + " insurance policies in RecyclerView");
            insuranceRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewClick(BusinessHeadInsuranceItem insuranceItem) {
        // Handle view click
        Toast.makeText(this, "Viewing: " + insuranceItem.getCustomerName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "View clicked for insurance: " + insuranceItem.getCustomerName());
        
        // TODO: Navigate to insurance detail view
        // Intent intent = new Intent(this, InsuranceDetailActivity.class);
        // intent.putExtra("INSURANCE_ID", insuranceItem.getId());
        // startActivity(intent);
    }

    @Override
    public void onEditClick(BusinessHeadInsuranceItem insuranceItem) {
        // Handle edit click
        Toast.makeText(this, "Editing: " + insuranceItem.getCustomerName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Edit clicked for insurance: " + insuranceItem.getCustomerName());
        
        // TODO: Navigate to insurance edit view
        // Intent intent = new Intent(this, InsuranceEditActivity.class);
        // intent.putExtra("INSURANCE_ID", insuranceItem.getId());
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

package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManagingDirectorMyPartnerUsersActivity extends AppCompatActivity implements PartnerUserAdapter.OnPartnerUserActionListener {
    private static final String TAG = "MDMyPartnerUsersActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private RecyclerView partnersRecyclerView;
    private PartnerUserAdapter partnerUserAdapter;
    private List<PartnerUser> partnerUserList;
    private List<PartnerUser> allPartnerUsersList;
    private RequestQueue requestQueue;

    // UI Elements
    private ImageButton backButton;
    private TextView totalPartnersText;
    private TextView activePartnersText;
    private TextView inactivePartnersText;
    private TextInputEditText searchPartnerInput;
    private MaterialButton filterButton;
    private LinearLayout noPartnersLayout;
    private LinearLayout loadingLayout;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partner);

        initializeViews();
        setupRecyclerView();
        setupSearchFunctionality();
        setupVolley();
        loadPartnerData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        totalPartnersText = findViewById(R.id.totalPartnersText);
        activePartnersText = findViewById(R.id.activePartnersText);
        inactivePartnersText = findViewById(R.id.inactivePartnersText);
        searchPartnerInput = findViewById(R.id.searchPartnerInput);
        filterButton = findViewById(R.id.filterButton);
        partnersRecyclerView = findViewById(R.id.partnersRecyclerView);
        noPartnersLayout = findViewById(R.id.noPartnersLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        titleText = findViewById(R.id.titleText);

        // Update title
        if (titleText != null) {
            titleText.setText("My Partner Users (Created by 10000)");
        }

        backButton.setOnClickListener(v -> onBackPressed());
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void setupRecyclerView() {
        partnerUserList = new ArrayList<>();
        allPartnerUsersList = new ArrayList<>();
        partnerUserAdapter = new PartnerUserAdapter(this, partnerUserList);
        partnerUserAdapter.setActionListener(this);
        
        partnersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partnersRecyclerView.setAdapter(partnerUserAdapter);
    }

    private void setupSearchFunctionality() {
        searchPartnerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPartners(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }
    
    private String getCurrentUsername() {
        // First try to get from intent extras
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        
        if (username != null && !username.isEmpty()) {
            return username;
        }
        
        // If not in intent, try to get from SharedPreferences
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            username = prefs.getString("USERNAME", "");
            if (!username.isEmpty()) {
                return username;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting username from SharedPreferences: " + e.getMessage());
        }
        
        // If still not found, try to get from global application state
        // This assumes you have a global application class or singleton
        try {
            // You might need to implement this based on your app's architecture
            // For now, return null if not found
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting username from global state: " + e.getMessage());
            return null;
        }
    }

    private void loadPartnerData() {
        showLoading(true);
        
        // Get the current user's username from intent or SharedPreferences
        String currentUsername = getCurrentUsername();
        if (currentUsername == null || currentUsername.isEmpty()) {
            showLoading(false);
            Toast.makeText(this, "Unable to identify current user", Toast.LENGTH_SHORT).show();
            showNoPartnersMessage();
            return;
        }
        
        String url = BASE_URL + "get_managing_director_partner_users_10000.php?username=" + currentUsername;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                showLoading(false);
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        allPartnerUsersList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partnerUserObj = data.getJSONObject(i);
                            PartnerUser partnerUser = new PartnerUser();
                            
                            // Set all fields using the getter/setter methods
                            partnerUser.setId(partnerUserObj.optString("id", ""));
                            partnerUser.setUsername(partnerUserObj.optString("username", ""));
                            partnerUser.setAliasName(partnerUserObj.optString("alias_name", ""));
                            partnerUser.setFirstName(partnerUserObj.optString("first_name", ""));
                            partnerUser.setLastName(partnerUserObj.optString("last_name", ""));
                            partnerUser.setPassword(partnerUserObj.optString("password", ""));
                            partnerUser.setPhoneNumber(partnerUserObj.optString("Phone_number", ""));
                            partnerUser.setEmailId(partnerUserObj.optString("email_id", ""));
                            partnerUser.setAlternativeMobileNumber(partnerUserObj.optString("alternative_mobile_number", ""));
                            partnerUser.setCompanyName(partnerUserObj.optString("company_name", ""));
                            partnerUser.setBranchStateNameId(partnerUserObj.optString("branch_state_name_id", ""));
                            partnerUser.setBranchLocationId(partnerUserObj.optString("branch_location_id", ""));
                            partnerUser.setBankId(partnerUserObj.optString("bank_id", ""));
                            partnerUser.setAccountTypeId(partnerUserObj.optString("account_type_id", ""));
                            partnerUser.setOfficeAddress(partnerUserObj.optString("office_address", ""));
                            partnerUser.setResidentialAddress(partnerUserObj.optString("residential_address", ""));
                            partnerUser.setAadhaarNumber(partnerUserObj.optString("aadhaar_number", ""));
                            partnerUser.setPanNumber(partnerUserObj.optString("pan_number", ""));
                            partnerUser.setAccountNumber(partnerUserObj.optString("account_number", ""));
                            partnerUser.setIfscCode(partnerUserObj.optString("ifsc_code", ""));
                            partnerUser.setRank(partnerUserObj.optString("rank", ""));
                            partnerUser.setStatus(partnerUserObj.optString("status", ""));
                            partnerUser.setReportingTo(partnerUserObj.optString("reportingTo", ""));
                            partnerUser.setEmployeeNo(partnerUserObj.optString("employee_no", ""));
                            partnerUser.setDepartment(partnerUserObj.optString("department", ""));
                            partnerUser.setDesignation(partnerUserObj.optString("designation", ""));
                            partnerUser.setBranchState(partnerUserObj.optString("branchstate", ""));
                            partnerUser.setBranchLocation(partnerUserObj.optString("branchloaction", ""));
                            partnerUser.setBankName(partnerUserObj.optString("bank_name", ""));
                            partnerUser.setAccountType(partnerUserObj.optString("account_type", ""));
                            partnerUser.setPartnerTypeId(partnerUserObj.optString("partner_type_id", ""));
                            partnerUser.setPanImg(partnerUserObj.optString("pan_img", ""));
                            partnerUser.setAadhaarImg(partnerUserObj.optString("aadhaar_img", ""));
                            partnerUser.setPhotoImg(partnerUserObj.optString("photo_img", ""));
                            partnerUser.setBankproofImg(partnerUserObj.optString("bankproof_img", ""));
                            partnerUser.setUserId(partnerUserObj.optString("user_id", ""));
                            partnerUser.setCreatedAt(partnerUserObj.optString("created_at", ""));
                            partnerUser.setCreatedBy(partnerUserObj.optString("createdBy", ""));
                            partnerUser.setUpdatedAt(partnerUserObj.optString("updated_at", ""));
                            partnerUser.setCreatorName(partnerUserObj.optString("creator_name", ""));
                            
                            allPartnerUsersList.add(partnerUser);
                        }
                        
                        // Log the filter information for debugging
                        if (response.has("filter_info")) {
                            JSONObject filterInfo = response.getJSONObject("filter_info");
                            String filterDescription = filterInfo.optString("filter_description", "");
                            Log.d(TAG, "Filter applied: " + filterDescription);
                        }
                        
                        partnerUserList.clear();
                        partnerUserList.addAll(allPartnerUsersList);
                        partnerUserAdapter.notifyDataSetChanged();
                        updateStats();
                        updateUI();
                        
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        showNoPartnersMessage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    showNoPartnersMessage();
                }
            },
            error -> {
                showLoading(false);
                Log.e(TAG, "Error fetching partner users: " + error.getMessage());
                Log.e(TAG, "Error details: " + error.toString());
                Log.e(TAG, "Network response: " + (error.networkResponse != null ? error.networkResponse.statusCode : "null"));
                
                String errorMessage = "Error fetching partner users";
                if (error.networkResponse != null) {
                    errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                }
                if (error.getMessage() != null) {
                    errorMessage += ": " + error.getMessage();
                }
                
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                showNoPartnersMessage();
            }
        );
        
        requestQueue.add(request);
    }

    private void filterPartners(String query) {
        if (query.isEmpty()) {
            partnerUserList.clear();
            partnerUserList.addAll(allPartnerUsersList);
        } else {
            List<PartnerUser> filteredList = allPartnerUsersList.stream()
                .filter(partnerUser -> 
                    partnerUser.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    partnerUser.getEmailId().toLowerCase().contains(query.toLowerCase()) ||
                    partnerUser.getPhoneNumber().contains(query) ||
                    partnerUser.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    partnerUser.getCompanyName().toLowerCase().contains(query.toLowerCase()) ||
                    partnerUser.getDepartment().toLowerCase().contains(query.toLowerCase()) ||
                    partnerUser.getDesignation().toLowerCase().contains(query.toLowerCase())
                )
                .collect(Collectors.toList());
            
            partnerUserList.clear();
            partnerUserList.addAll(filteredList);
        }
        partnerUserAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void showFilterDialog() {
        String[] filterOptions = {"All Partner Users", "Active Only", "Inactive Only"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Partner Users")
               .setItems(filterOptions, (dialog, which) -> {
                   switch (which) {
                       case 0: // All Partner Users
                           partnerUserList.clear();
                           partnerUserList.addAll(allPartnerUsersList);
                           break;
                       case 1: // Active Only
                           partnerUserList.clear();
                           partnerUserList.addAll(allPartnerUsersList.stream()
                               .filter(partnerUser -> "1".equals(partnerUser.getStatus()) || "Active".equalsIgnoreCase(partnerUser.getStatus()))
                               .collect(Collectors.toList()));
                           break;
                       case 2: // Inactive Only
                           partnerUserList.clear();
                           partnerUserList.addAll(allPartnerUsersList.stream()
                               .filter(partnerUser -> "0".equals(partnerUser.getStatus()) || "Inactive".equalsIgnoreCase(partnerUser.getStatus()))
                               .collect(Collectors.toList()));
                           break;
                   }
                   partnerUserAdapter.notifyDataSetChanged();
                   updateUI();
               })
               .show();
    }

    private void updateStats() {
        int total = allPartnerUsersList.size();
        int active = (int) allPartnerUsersList.stream().filter(p -> "1".equals(p.getStatus()) || "Active".equalsIgnoreCase(p.getStatus())).count();
        int inactive = total - active;

        totalPartnersText.setText(String.valueOf(total));
        activePartnersText.setText(String.valueOf(active));
        inactivePartnersText.setText(String.valueOf(inactive));
    }

    private void updateUI() {
        if (partnerUserList.isEmpty()) {
            showNoPartnersMessage();
        } else {
            hideNoPartnersMessage();
        }
    }

    private void showLoading(boolean show) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        partnersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        noPartnersLayout.setVisibility(View.GONE);
    }

    private void showNoPartnersMessage() {
        noPartnersLayout.setVisibility(View.VISIBLE);
        partnersRecyclerView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }

    private void hideNoPartnersMessage() {
        noPartnersLayout.setVisibility(View.GONE);
        partnersRecyclerView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onEditPartnerUser(PartnerUser partnerUser) {
        Toast.makeText(this, "Edit partner user: " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to edit partner user activity
    }

    @Override
    public void onDeletePartnerUser(PartnerUser partnerUser) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Partner User")
            .setMessage("Are you sure you want to delete " + partnerUser.getFullName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                Toast.makeText(this, "Delete partner user: " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Implement delete functionality
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onViewDetails(PartnerUser partnerUser) {
        Toast.makeText(this, "View details for: " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to partner user details activity
    }
} 